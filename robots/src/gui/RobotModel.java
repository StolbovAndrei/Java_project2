package gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import robot.RobotBehavior;
import robot.DefaultRobotBehavior;
import robot.RobotState;

public class RobotModel {
  private volatile double m_robotPositionX = 100;
  private volatile double m_robotPositionY = 100;
  private volatile double m_robotDirection = 0;
  private volatile int m_targetPositionX = 150;
  private volatile int m_targetPositionY = 100;

  private final List<RobotModelListener> m_listeners = new ArrayList<>();
  private java.util.Timer m_timer;
  private RobotBehavior behavior;

  public RobotModel() {
    behavior = new DefaultRobotBehavior();
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

  public double getRobotPositionX() { return m_robotPositionX; }
  public double getRobotPositionY() { return m_robotPositionY; }
  public double getRobotDirection() { return m_robotDirection; }
  public int getTargetPositionX() { return m_targetPositionX; }
  public int getTargetPositionY() { return m_targetPositionY; }

  public void setRobotBehavior(RobotBehavior b) {
    this.behavior = b;
  }

  protected void onModelUpdateEvent() {
    RobotState newState = behavior.step(
        m_robotPositionX, m_robotPositionY, m_robotDirection,
        m_targetPositionX, m_targetPositionY, 0.01);
    m_robotPositionX = newState.x;
    m_robotPositionY = newState.y;
    m_robotDirection = newState.direction;
    notifyListeners();
  }
}