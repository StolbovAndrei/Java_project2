package robot;

public interface RobotBehavior {
  RobotState step(double currentX, double currentY, double currentDir,
      double targetX, double targetY, double dt);
}