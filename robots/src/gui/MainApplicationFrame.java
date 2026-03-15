package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

  public MainApplicationFrame() {
    int inset = 50;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds(inset, inset,
        screenSize.width  - inset*2,
        screenSize.height - inset*2);

    setContentPane(desktopPane);

    LogWindow logWindow = createLogWindow();
    addWindow(logWindow);

    GameWindow gameWindow = new GameWindow();
    gameWindow.setSize(400,  400);
    addWindow(gameWindow);

    setJMenuBar(generateMenuBar());
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        exitApplication();
      }
    });
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
    Object[] options = {"Да", "Нет"};
    int result = JOptionPane.showOptionDialog(this,
        "Вы действительно хотите выйти?",
        "Подтверждение выхода",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]);
    if (result == 0) {
      System.exit(0);
    }
  }

  private JMenuBar generateMenuBar()
  {
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("Файл");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    fileMenu.getAccessibleContext().setAccessibleDescription("Управление файлом");

    fileMenu.add(createMenuItem("Выход", KeyEvent.VK_Q,
        (event) -> exitApplication()));

    menuBar.add(fileMenu);

    JMenu lookAndFeelMenu = new JMenu("Режим отображения");
    lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
    lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
        "Управление режимом отображения приложения");

    lookAndFeelMenu.add(createMenuItem("Системная схема", KeyEvent.VK_S,
        (event) -> {
          setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          this.invalidate();
        }));

    lookAndFeelMenu.add(createMenuItem("Универсальная схема", KeyEvent.VK_S,
        (event) -> {
          setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
          this.invalidate();
        }));

    menuBar.add(lookAndFeelMenu);

    JMenu testMenu = new JMenu("Тесты");
    testMenu.setMnemonic(KeyEvent.VK_T);
    testMenu.getAccessibleContext().setAccessibleDescription(
        "Тестовые команды");

    testMenu.add(createMenuItem("Сообщение в лог", KeyEvent.VK_S,
        (event) -> Logger.debug("Новая строка")));

    testMenu.add(createMenuItem("Тест локализации", KeyEvent.VK_L,
        (event) -> {
          JOptionPane.showConfirmDialog(MainApplicationFrame.this,
              "Это тестовое сообщение",
              "Заголовок",
              JOptionPane.YES_NO_CANCEL_OPTION);
        }));

    menuBar.add(testMenu);

    return menuBar;
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
      // just ignore
    }
  }
}