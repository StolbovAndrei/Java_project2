package robot;

public class DefaultRobotBehavior implements RobotBehavior {
  private static final double maxVelocity = 0.1;
  private static final double maxAngularVelocity = 0.001;

  @Override
  public RobotState step(double x, double y, double dir, double targetX, double targetY, double dt) {
    double distance = distance(targetX, targetY, x, y);
    if (distance < 0.5) {
      return new RobotState(x, y, dir);
    }
    double velocity = maxVelocity;
    double angleToTarget = angleTo(x, y, targetX, targetY);
    double angularVelocity = calculateAngularVelocity(angleToTarget, dir);

    double newX, newY, newDir;
    double durationMs = dt * 1000;
    velocity = applyLimits(velocity, 0, maxVelocity);
    angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

    if (Math.abs(angularVelocity) < 1e-9) {
      newX = x + velocity * durationMs * Math.cos(dir);
      newY = y + velocity * durationMs * Math.sin(dir);
    } else {
      newX = x + velocity / angularVelocity *
          (Math.sin(dir + angularVelocity * durationMs) - Math.sin(dir));
      newY = y - velocity / angularVelocity *
          (Math.cos(dir + angularVelocity * durationMs) - Math.cos(dir));
    }
    newDir = asNormalizedRadians(dir + angularVelocity * durationMs);
    return new RobotState(newX, newY, newDir);
  }

  private double calculateAngularVelocity(double angleToTarget, double currentAngle) {
    double diff = angleToTarget - currentAngle;
    diff = asNormalizedRadians(diff);
    if (diff > Math.PI) diff -= 2 * Math.PI;
    if (diff < -Math.PI) diff += 2 * Math.PI;
    if (Math.abs(diff) < 1e-6) return 0;
    return diff > 0 ? maxAngularVelocity : -maxAngularVelocity;
  }

  private static double distance(double x1, double y1, double x2, double y2) {
    double dx = x1 - x2;
    double dy = y1 - y2;
    return Math.sqrt(dx*dx + dy*dy);
  }

  private static double angleTo(double fromX, double fromY, double toX, double toY) {
    double dx = toX - fromX;
    double dy = toY - fromY;
    return asNormalizedRadians(Math.atan2(dy, dx));
  }

  private static double asNormalizedRadians(double angle) {
    while (angle < 0) angle += 2 * Math.PI;
    while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
    return angle;
  }

  private static double applyLimits(double value, double min, double max) {
    if (value < min) return min;
    if (value > max) return max;
    return value;
  }
}