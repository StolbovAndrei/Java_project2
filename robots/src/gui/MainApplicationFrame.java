package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

import javax.swing.*;
import log.Logger;
import robot.*;

public class MainApplicationFrame extends JFrame {
  private final JDesktopPane desktopPane = new JDesktopPane();
  private LogWindow logWindow;
  private GameWindow gameWindow;
  private RobotCoordinatesWindow coordinatesWindow;
  private RobotModel robotModel;

  private final LanguageManager languageManager = new LanguageManager();
  private ResourceBundle currentBundle;
  private JMenuBar menuBar;

  public MainApplicationFrame() {
    int inset = 50;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds(inset, inset,
        screenSize.width  - inset*2,
        screenSize.height - inset*2);

    setContentPane(desktopPane);

    Locale.setDefault(new Locale("ru", "RU"));
    currentBundle = ResourceBundle.getBundle("resources.ComponentsMenu_ru");

    robotModel = new RobotModel();

    logWindow = createLogWindow();
    addWindow(logWindow);
    languageManager.registerComponent(logWindow);

    gameWindow = new GameWindow(robotModel);
    gameWindow.setSize(400, 400);
    addWindow(gameWindow);
    languageManager.registerComponent(gameWindow);

    coordinatesWindow = new RobotCoordinatesWindow(robotModel);
    addWindow(coordinatesWindow);
    languageManager.registerComponent(coordinatesWindow);

    setJMenuBar(generateMenuBar());
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        exitApplication();
      }
    });

    ConfigManager.loadWindowsState(this, logWindow, gameWindow, coordinatesWindow);
    languageManager.switchLanguage(currentBundle, this);
  }

  protected LogWindow createLogWindow() {
    LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
    logWindow.setLocation(10,10);
    logWindow.setSize(300, 800);
    setMinimumSize(logWindow.getSize());
    logWindow.pack();
    Logger.debug("Протокол работает");
    return logWindow;
  }

  protected void addWindow(JInternalFrame frame) {
    desktopPane.add(frame);
    frame.setVisible(true);
  }

  private void exitApplication() {
    ConfigManager.saveWindowsState(this, logWindow, gameWindow, coordinatesWindow);
    int result = JOptionPane.showConfirmDialog(this,
        currentBundle.getString("exitInDialog"),
        currentBundle.getString("exitName"),
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);
    if (result == JOptionPane.YES_OPTION) {
      System.exit(0);
    }
  }

  private JMenuBar generateMenuBar() {
    menuBar = new JMenuBar();

    LocalizedJMenu languageMenu = addLocalizedMenu("language", KeyEvent.VK_L);
    addLocalizedMenuItem("russian", e -> switchLanguage(new Locale("ru", "RU"), "resources.ComponentsMenu_ru"), languageMenu);
    addLocalizedMenuItem("english", e -> switchLanguage(new Locale("en", "US"), "resources.ComponentsMenu_en_US"), languageMenu);

    LocalizedJMenu lookAndFeelMenu = addLocalizedMenu("scheme", KeyEvent.VK_V);
    addLocalizedMenuItem("systemScheme", event -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()), lookAndFeelMenu);
    addLocalizedMenuItem("crossplatformScheme", event -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()), lookAndFeelMenu);

    LocalizedJMenu testMenu = addLocalizedMenu("tests", KeyEvent.VK_T);
    addLocalizedMenuItem("message", event -> Logger.debug(currentBundle.getString("messageInLog")), testMenu);

    LocalizedJMenu robotMenu = addLocalizedMenu("robotMenu", KeyEvent.VK_R);
    addLocalizedMenuItem("loadRobot", e -> loadRobotFromJar(), robotMenu);
    addLocalizedMenuItem("defaultRobot", e -> setDefaultRobot(), robotMenu);

    LocalizedJMenuItem exitItem = addLocalizedMenuItem("exit", event -> exitApplication(), menuBar);
    exitItem.setMnemonic(KeyEvent.VK_Q);

    return menuBar;
  }

  private void loadRobotFromJar() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
      }
      public String getDescription() {
        return "JAR files (*.jar)";
      }
    });
    if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File jarFile = chooser.getSelectedFile();
    if (!isJarFile(jarFile)) {
      JOptionPane.showMessageDialog(this,
          currentBundle.getString("notJar"),
          currentBundle.getString("errorTitle"),
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    try {
      URLClassLoader loader = new URLClassLoader(
          new URL[] { jarFile.toURI().toURL() },
          getClass().getClassLoader());
      ServiceLoader<RobotFactory> factoryLoader = ServiceLoader.load(RobotFactory.class, loader);
      Iterator<RobotFactory> it = factoryLoader.iterator();
      if (!it.hasNext()) {
        JOptionPane.showMessageDialog(this,
            currentBundle.getString("noFactory"),
            currentBundle.getString("errorTitle"),
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      RobotFactory factory = it.next();
      RobotBehavior behavior = factory.createBehavior();
      RobotRenderer renderer = factory.createRenderer();
      robotModel.setRobotBehavior(behavior);
      gameWindow.setRobotRenderer(renderer);
      Logger.debug("Загружен робот из " + jarFile.getAbsolutePath());
    } catch (Exception e) {
      Logger.error("Ошибка загрузки робота: " + e.getMessage());
      JOptionPane.showMessageDialog(this,
          currentBundle.getString("loadError") + "\n" + e.getMessage(),
          currentBundle.getString("errorTitle"),
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void setDefaultRobot() {
    robotModel.setRobotBehavior(new DefaultRobotBehavior());
    gameWindow.setRobotRenderer(new DefaultRobotRenderer());
    Logger.debug("Установлен стандартный робот");
  }

  private boolean isJarFile(File file) {
    try (InputStream in = new FileInputStream(file)) {
      byte[] magic = new byte[4];
      if (in.read(magic) != 4) return false;
      return (magic[0] == (byte)0x50 && magic[1] == (byte)0x4B &&
          magic[2] == (byte)0x03 && magic[3] == (byte)0x04);
    } catch (IOException e) {
      return false;
    }
  }

  private LocalizedJMenu addLocalizedMenu(String key, int mnemonic) {
    LocalizedJMenu menu = new LocalizedJMenu(key, currentBundle);
    menu.setMnemonic(mnemonic);
    languageManager.registerComponent(menu);
    menuBar.add(menu);
    return menu;
  }

  private LocalizedJMenuItem addLocalizedMenuItem(String key, ActionListener listener, Container parent) {
    LocalizedJMenuItem item = new LocalizedJMenuItem(key, currentBundle);
    item.addActionListener(listener);
    languageManager.registerComponent(item);
    parent.add(item);
    return item;
  }

  private void switchLanguage(Locale locale, String baseName) {
    currentBundle = ResourceBundle.getBundle(baseName, locale);
    languageManager.switchLanguage(currentBundle, this);
  }

  private void setLookAndFeel(String className) {
    try {
      UIManager.setLookAndFeel(className);
      SwingUtilities.updateComponentTreeUI(this);
    } catch (ClassNotFoundException | InstantiationException
             | IllegalAccessException | UnsupportedLookAndFeelException e) {
    }
  }
}