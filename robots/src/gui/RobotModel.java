package gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class RobotModel {
  private volatile double m_robotPositionX = 100;
  private volatile double m_robotPositionY = 100;
  private volatile double m_robotDirection = 0;
  private volatile int m_targetPositionX = 150;
  private volatile int m_targetPositionY = 100;

  private static final double maxVelocity = 0.1;
  private static final double maxAngularVelocity = 0.001;

  private final List<RobotModelListener> m_listeners = new ArrayList<>();
  private java.util.Timer m_timer;

  public RobotModel() {
    m_timer = new java.util.Timer("RobotModelTimer", true);
    m_timer.schedule(new java.util.TimerTask() {
      @Override
      public void run() {
        onModelUpdateEvent();
      }
    }, 0, 10);
  }

  public void addListener(RobotModelListener listener) {
    synchronized (m_listeners) {
      m_listeners.add(listener);
    }
  }

  public void removeListener(RobotModelListener listener) {
    synchronized (m_listeners) {
      m_listeners.remove(listener);
    }
  }

  private void notifyListeners() {
    List<RobotModelListener> copy;
    synchronized (m_listeners) {
      copy = new ArrayList<>(m_listeners);
    }
    for (RobotModelListener listener : copy) {
      listener.onModelUpdated();
    }
  }

  public void setTargetPosition(Point p) {
    m_targetPositionX = p.x;
    m_targetPositionY = p.y;
  }

  public double getRobotPositionX() {
    return m_robotPositionX;
  }

  public double getRobotPositionY() {
    return m_robotPositionY;
  }

  public double getRobotDirection() {
    return m_robotDirection;
  }

  public int getTargetPositionX() {
    return m_targetPositionX;
  }

  public int getTargetPositionY() {
    return m_targetPositionY;
  }

  protected void onModelUpdateEvent() {
    double distance = distance(m_targetPositionX, m_targetPositionY,
        m_robotPositionX, m_robotPositionY);
    if (distance < 0.5) {
      return;
    }
    double velocity = maxVelocity;
    double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
    double angularVelocity = calculateAngularVelocity(angleToTarget, m_robotDirection);
    moveRobot(velocity, angularVelocity, 10);
    notifyListeners();
  }

  private double calculateAngularVelocity(double angleToTarget, double currentAngle) {
    double diff = angleToTarget - currentAngle;
    diff = asNormalizedRadians(diff);
    if (diff > Math.PI) {
      diff = diff - 2 * Math.PI;
    }
    if (diff < -Math.PI) {
      diff = diff + 2 * Math.PI;
    }
    if (Math.abs(diff) < 1e-6) {
      return 0;
    }
    if (diff > 0) {
      return maxAngularVelocity;
    } else {
      return -maxAngularVelocity;
    }
  }

  private static double applyLimits(double value, double min, double max) {
    if (value < min)
      return min;
    if (value > max)
      return max;
    return value;
  }

  private void moveRobot(double velocity, double angularVelocity, double duration) {
    velocity = applyLimits(velocity, 0, maxVelocity);
    angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
    double newX = m_robotPositionX + velocity / angularVelocity *
        (Math.sin(m_robotDirection + angularVelocity * duration) -
            Math.sin(m_robotDirection));
    if (!Double.isFinite(newX)) {
      newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
    }
    double newY = m_robotPositionY - velocity / angularVelocity *
        (Math.cos(m_robotDirection + angularVelocity * duration) -
            Math.cos(m_robotDirection));
    if (!Double.isFinite(newY)) {
      newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
    }
    m_robotPositionX = newX;
    m_robotPositionY = newY;
    double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
    m_robotDirection = newDirection;
  }

  private static double distance(double x1, double y1, double x2, double y2) {
    double diffX = x1 - x2;
    double diffY = y1 - y2;
    return Math.sqrt(diffX * diffX + diffY * diffY);
  }

  private static double angleTo(double fromX, double fromY, double toX, double toY) {
    double diffX = toX - fromX;
    double diffY = toY - fromY;
    return asNormalizedRadians(Math.atan2(diffY, diffX));
  }

  private static double asNormalizedRadians(double angle) {
    while (angle < 0) {
      angle += 2 * Math.PI;
    }
    while (angle >= 2 * Math.PI) {
      angle -= 2 * Math.PI;
    }
    return angle;
  }
}
