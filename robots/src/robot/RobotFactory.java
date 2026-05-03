package robot;

public interface RobotFactory {
  RobotBehavior createBehavior();
  RobotRenderer createRenderer();
}