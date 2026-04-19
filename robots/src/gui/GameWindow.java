package gui;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame implements LocalizableComponent
{
  private final GameVisualizer m_visualizer;

  public GameWindow(RobotModel model)
  {
    super("", true, true, true, true);
    m_visualizer = new GameVisualizer(model);
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(m_visualizer, BorderLayout.CENTER);
    getContentPane().add(panel);
    pack();
  }

  @Override
  public void updateText(ResourceBundle rb)
  {
    setTitle(rb.getString("gameWindow"));
  }
}
