package robot;

import java.awt.Color;
import java.awt.Graphics2D;

public class DefaultRobotRenderer implements RobotRenderer {
  @Override
  public void drawRobot(Graphics2D g, double x, double y, double direction) {
    g.setColor(Color.MAGENTA);
    fillOval(g, 0, 0, 30, 10);
    g.setColor(Color.BLACK);
    drawOval(g, 0, 0, 30, 10);
    g.setColor(Color.WHITE);
    fillOval(g, 10, 0, 5, 5);
    g.setColor(Color.BLACK);
    drawOval(g, 10, 0, 5, 5);
  }

  private void fillOval(Graphics2D g, int cx, int cy, int w, int h) {
    g.fillOval(cx - w/2, cy - h/2, w, h);
  }

  private void drawOval(Graphics2D g, int cx, int cy, int w, int h) {
    g.drawOval(cx - w/2, cy - h/2, w, h);
  }
}