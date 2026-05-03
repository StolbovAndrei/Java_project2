package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;
import robot.RobotRenderer;
import robot.DefaultRobotRenderer;

public class GameVisualizer extends JPanel implements RobotModelListener {
  private final RobotModel m_model;
  private RobotRenderer renderer;

  public GameVisualizer(RobotModel model) {
    m_model = model;
    renderer = new DefaultRobotRenderer();
    m_model.addListener(this);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        m_model.setTargetPosition(e.getPoint());
        repaint();
      }
    });
    setDoubleBuffered(true);
  }

  public void setRobotRenderer(RobotRenderer r) {
    this.renderer = r;
  }

  @Override
  public void onModelUpdated() {
    EventQueue.invokeLater(this::repaint);
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2d = (Graphics2D) g;
    int robotCenterX = round(m_model.getRobotPositionX());
    int robotCenterY = round(m_model.getRobotPositionY());
    double direction = m_model.getRobotDirection();
    drawTarget(g2d, m_model.getTargetPositionX(), m_model.getTargetPositionY());

    AffineTransform old = g2d.getTransform();
    g2d.translate(robotCenterX, robotCenterY);
    g2d.rotate(direction);
    renderer.drawRobot(g2d, 0, 0, direction);
    g2d.setTransform(old);
  }

  private void drawTarget(Graphics2D g, int x, int y) {
    g.setColor(Color.GREEN);
    g.fillOval(x - 2, y - 2, 5, 5);
    g.setColor(Color.BLACK);
    g.drawOval(x - 2, y - 2, 5, 5);
  }

  private static int round(double value) {
    return (int)(value + 0.5);
  }
}