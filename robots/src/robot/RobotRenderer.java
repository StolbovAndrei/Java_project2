package robot;

import java.awt.Graphics2D;

public interface RobotRenderer {
  void drawRobot(Graphics2D g, double x, double y, double direction);
}