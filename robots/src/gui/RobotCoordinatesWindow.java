package gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class RobotCoordinatesWindow extends JInternalFrame implements RobotModelListener {
  private final RobotModel m_model;
  private final JTextArea m_textArea;

  public RobotCoordinatesWindow(RobotModel model) {
    super("Информация о роботе", true, true, true, true);
    m_model = model;
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
        "РОБОТ\n" +
            "Позиция: X = " + String.format("%.1f", robotX) + ", Y = " + String.format("%.1f", robotY) + "\n" +
            "Направление: " + String.format("%.1f", directionDeg) + "°\n\n" +
            "ЦЕЛЬ\n" +
            "Позиция: X = " + targetX + ", Y = " + targetY + "\n" +
            "Расстояние до цели: " + String.format("%.1f", distance) + "\n" +
            "Угол до цели: " + String.format("%.1f", angleToTargetDeg) + "°";

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
}