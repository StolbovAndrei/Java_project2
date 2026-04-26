package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
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
    gameWindow.setSize(400,  400);
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

  protected LogWindow createLogWindow()
  {
    LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
    logWindow.setLocation(10,10);
    logWindow.setSize(300, 800);
    setMinimumSize(logWindow.getSize());
    logWindow.pack();
    Logger.debug("Протокол работает");
    return logWindow;
  }

  protected void addWindow(JInternalFrame frame)
  {
    desktopPane.add(frame);
    frame.setVisible(true);
  }

  private void exitApplication()
  {
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

  private JMenuBar generateMenuBar()
  {
    menuBar = new JMenuBar();

    LocalizedJMenu languageMenu = addLocalizedMenu("language", KeyEvent.VK_L);
    addLocalizedMenuItem("russian", e -> switchLanguage(new Locale("ru", "RU"), "resources.ComponentsMenu_ru"), languageMenu);
    addLocalizedMenuItem("english", e -> switchLanguage(new Locale("en", "US"), "resources.ComponentsMenu_en_US"), languageMenu);

    LocalizedJMenu lookAndFeelMenu = addLocalizedMenu("scheme", KeyEvent.VK_V);
    addLocalizedMenuItem("systemScheme", event -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()), lookAndFeelMenu);
    addLocalizedMenuItem("crossplatformScheme", event -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()), lookAndFeelMenu);

    LocalizedJMenu testMenu = addLocalizedMenu("tests", KeyEvent.VK_T);
    addLocalizedMenuItem("message", event -> Logger.debug(currentBundle.getString("messageInLog")), testMenu);

    LocalizedJMenuItem exitItem = addLocalizedMenuItem("exit", event -> exitApplication(), menuBar);
    exitItem.setMnemonic(KeyEvent.VK_Q);

    return menuBar;
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

  private void switchLanguage(Locale locale, String baseName)
  {
    currentBundle = ResourceBundle.getBundle(baseName, locale);
    languageManager.switchLanguage(currentBundle, this);
  }

  private void setLookAndFeel(String className)
  {
    try
    {
      UIManager.setLookAndFeel(className);
      SwingUtilities.updateComponentTreeUI(this);
    }
    catch (ClassNotFoundException | InstantiationException
           | IllegalAccessException | UnsupportedLookAndFeelException e)
    {
      // ignore
    }
  }
}