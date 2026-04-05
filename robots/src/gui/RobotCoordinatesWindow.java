package gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class RobotCoordinatesWindow extends JInternalFrame implements RobotModelListener {
  private final RobotModel m_model;
  private final JLabel m_coordinatesLabel;

  public RobotCoordinatesWindow(RobotModel model) {
    super("Координаты робота", true, true, true, true);
    m_model = model;
    m_model.addListener(this);
    m_coordinatesLabel = new JLabel();
    updateCoordinatesDisplay();
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(m_coordinatesLabel, BorderLayout.CENTER);
    getContentPane().add(panel);
    setSize(200, 100);
    setLocation(320, 10);
  }

  private void updateCoordinatesDisplay() {
    double x = m_model.getRobotPositionX();
    double y = m_model.getRobotPositionY();
    m_coordinatesLabel.setText(String.format("X: %.1f, Y: %.1f", x, y));
  }

  @Override
  public void onModelUpdated() {
    SwingUtilities.invokeLater(this::updateCoordinatesDisplay);
  }
}
