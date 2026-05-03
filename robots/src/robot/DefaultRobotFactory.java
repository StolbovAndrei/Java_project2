package robot;

public class DefaultRobotFactory implements RobotFactory {
  @Override
  public RobotBehavior createBehavior() {
    return new DefaultRobotBehavior();
  }

  @Override
  public RobotRenderer createRenderer() {
    return new DefaultRobotRenderer();
  }
}