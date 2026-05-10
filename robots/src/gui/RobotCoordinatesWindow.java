package gui;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class RobotCoordinatesWindow extends JInternalFrame implements RobotModelListener, LocalizableComponent {
  private final RobotModel m_model;
  private final JTextArea m_textArea;
  private ResourceBundle currentBundle;

  public RobotCoordinatesWindow(RobotModel model, ResourceBundle bundle) {
    super("", true, true, true, true);
    m_model = model;
    this.currentBundle = bundle;
    m_model.addListener(this);
    m_textArea = new JTextArea();
    m_textArea.setEditable(false);
    updateInfoDisplay();
    getContentPane().add(m_textArea, BorderLayout.CENTER);
    setSize(260, 160);
    setLocation(320, 10);
  }

  private void updateInfoDisplay() {
    double robotX = m_model.getRobotPositionX();
    double robotY = m_model.getRobotPositionY();
    double directionDeg = Math.toDegrees(m_model.getRobotDirection());
    int targetX = m_model.getTargetPositionX();
    int targetY = m_model.getTargetPositionY();
    double distance = Math.hypot(targetX - robotX, targetY - robotY);
    double angleToTargetDeg = Math.toDegrees(angleTo(robotX, robotY, targetX, targetY));

    String text =
        MessageFormatCache.getFormatted(currentBundle, "robotPosition", robotX, robotY) + "\n" +
            MessageFormatCache.getFormatted(currentBundle, "robotDirection", directionDeg) + "\n\n" +
            MessageFormatCache.getFormatted(currentBundle, "targetPosition", targetX, targetY) + "\n" +
            MessageFormatCache.getFormatted(currentBundle, "distanceToTarget", distance) + "\n" +
            MessageFormatCache.getFormatted(currentBundle, "angleToTarget", angleToTargetDeg);

    m_textArea.setText(text);
  }

  private double angleTo(double fromX, double fromY, double toX, double toY) {
    double diffX = toX - fromX;
    double diffY = toY - fromY;
    return Math.atan2(diffY, diffX);
  }

  @Override
  public void onModelUpdated() {
    SwingUtilities.invokeLater(this::updateInfoDisplay);
  }

  @Override
  public void updateText(ResourceBundle rb) {
    this.currentBundle = rb;
    setTitle(rb.getString("coordinatesWindow"));
    updateInfoDisplay();
  }
}