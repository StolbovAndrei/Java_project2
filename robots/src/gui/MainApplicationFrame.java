package gui;

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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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

  private JMenuItem createMenuItem(String text, int mnemonic, ActionListener listener)
  {
    JMenuItem item = new JMenuItem(text, mnemonic);
    item.addActionListener(listener);
    return item;
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
    JMenuBar menuBar = new JMenuBar();

    LocalizedJMenu languageMenu = new LocalizedJMenu("language", currentBundle);
    languageMenu.setMnemonic(KeyEvent.VK_L);
    languageManager.registerComponent(languageMenu);

    LocalizedJMenuItem russianItem = new LocalizedJMenuItem("russian", currentBundle);
    russianItem.addActionListener(e -> switchLanguage(new Locale("ru", "RU"), "resources.ComponentsMenu_ru"));
    languageManager.registerComponent(russianItem);
    languageMenu.add(russianItem);

    LocalizedJMenuItem englishItem = new LocalizedJMenuItem("english", currentBundle);
    englishItem.addActionListener(e -> switchLanguage(new Locale("en", "US"), "resources.ComponentsMenu_en_US"));
    languageManager.registerComponent(englishItem);
    languageMenu.add(englishItem);

    menuBar.add(languageMenu);

    LocalizedJMenu lookAndFeelMenu = new LocalizedJMenu("scheme", currentBundle);
    lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
    languageManager.registerComponent(lookAndFeelMenu);

    LocalizedJMenuItem systemSchemeItem = new LocalizedJMenuItem("systemScheme", currentBundle);
    systemSchemeItem.addActionListener(event -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
    languageManager.registerComponent(systemSchemeItem);
    lookAndFeelMenu.add(systemSchemeItem);

    LocalizedJMenuItem crossplatformSchemeItem = new LocalizedJMenuItem("crossplatformScheme", currentBundle);
    crossplatformSchemeItem.addActionListener(event -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
    languageManager.registerComponent(crossplatformSchemeItem);
    lookAndFeelMenu.add(crossplatformSchemeItem);

    menuBar.add(lookAndFeelMenu);

    LocalizedJMenu testMenu = new LocalizedJMenu("tests", currentBundle);
    testMenu.setMnemonic(KeyEvent.VK_T);
    languageManager.registerComponent(testMenu);

    LocalizedJMenuItem messageItem = new LocalizedJMenuItem("message", currentBundle);
    messageItem.addActionListener(event -> Logger.debug(currentBundle.getString("messageInLog")));
    languageManager.registerComponent(messageItem);
    testMenu.add(messageItem);

    menuBar.add(testMenu);

    LocalizedJMenuItem exitItem = new LocalizedJMenuItem("exit", currentBundle);
    exitItem.setMnemonic(KeyEvent.VK_Q);
    exitItem.addActionListener(event -> exitApplication());
    languageManager.registerComponent(exitItem);
    menuBar.add(exitItem);

    return menuBar;
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
