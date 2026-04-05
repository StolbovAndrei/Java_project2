package gui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties;

public class ConfigManager {
  private static final String CONFIG_FILE = System.getProperty("user.home") + File.separator + "robots_config.properties";

  public static void saveWindowsState(JFrame mainFrame, JInternalFrame logWindow, JInternalFrame gameWindow, JInternalFrame coordinatesWindow) {
    Properties props = new Properties();

    props.setProperty("mainFrame.bounds", mainFrame.getBounds().x + "," + mainFrame.getBounds().y + "," +
        mainFrame.getBounds().width + "," + mainFrame.getBounds().height);
    props.setProperty("mainFrame.extendedState", String.valueOf(mainFrame.getExtendedState()));

    saveInternalFrameProps(props, logWindow, "logWindow");
    saveInternalFrameProps(props, gameWindow, "gameWindow");
    saveInternalFrameProps(props, coordinatesWindow, "coordinatesWindow");

    try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
      props.store(out, "Robots Application Configuration");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void loadWindowsState(JFrame mainFrame, JInternalFrame logWindow, JInternalFrame gameWindow, JInternalFrame coordinatesWindow) {
    Properties props = new Properties();
    File file = new File(CONFIG_FILE);
    if (!file.exists()) {
      return;
    }

    try (InputStream in = new FileInputStream(file)) {
      props.load(in);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    String mainBounds = props.getProperty("mainFrame.bounds");
    if (mainBounds != null) {
      String[] parts = mainBounds.split(",");
      if (parts.length == 4) {
        try {
          int x = Integer.parseInt(parts[0]);
          int y = Integer.parseInt(parts[1]);
          int w = Integer.parseInt(parts[2]);
          int h = Integer.parseInt(parts[3]);
          mainFrame.setBounds(x, y, w, h);
        } catch (NumberFormatException ignored) {}
      }
    }
    String mainState = props.getProperty("mainFrame.extendedState");
    if (mainState != null) {
      try {
        mainFrame.setExtendedState(Integer.parseInt(mainState));
      } catch (NumberFormatException ignored) {}
    }

    loadInternalFrameProps(props, logWindow, "logWindow");
    loadInternalFrameProps(props, gameWindow, "gameWindow");
    loadInternalFrameProps(props, coordinatesWindow, "coordinatesWindow");
  }

  private static void saveInternalFrameProps(Properties props, JInternalFrame frame, String prefix) {
    props.setProperty(prefix + ".bounds", frame.getBounds().x + "," + frame.getBounds().y + "," +
        frame.getBounds().width + "," + frame.getBounds().height);
    props.setProperty(prefix + ".icon", String.valueOf(frame.isIcon()));
    props.setProperty(prefix + ".maximum", String.valueOf(frame.isMaximum()));
  }

  private static void loadInternalFrameProps(Properties props, JInternalFrame frame, String prefix) {
    String bounds = props.getProperty(prefix + ".bounds");
    if (bounds != null) {
      String[] parts = bounds.split(",");
      if (parts.length == 4) {
        try {
          int x = Integer.parseInt(parts[0]);
          int y = Integer.parseInt(parts[1]);
          int w = Integer.parseInt(parts[2]);
          int h = Integer.parseInt(parts[3]);
          frame.setBounds(x, y, w, h);
        } catch (NumberFormatException ignored) {}
      }
    }

    String icon = props.getProperty(prefix + ".icon");
    if (icon != null) {
      try {
        frame.setIcon(Boolean.parseBoolean(icon));
      } catch (Exception ignored) {}
    }

    String max = props.getProperty(prefix + ".maximum");
    if (max != null) {
      try {
        frame.setMaximum(Boolean.parseBoolean(max));
      } catch (Exception ignored) {}
    }
  }
}
