package model;

public class RobotController
{
    private final RobotModel model;
    private static final double UPDATE_DURATION = 10;

    public RobotController(RobotModel model)
    {
        this.model = model;
    }

    public void update()
    {
        double distance = model.getDistanceToTarget();

        if (distance < 10.0)
        {
            return;
        }

        double velocity = 0.5;
        double angularVelocity = model.calculateAngularVelocity();

        model.updatePosition(velocity, angularVelocity, UPDATE_DURATION);
    }

    public void setTargetPosition(double x, double y)
    {
        model.setTargetPosition(x, y);
    }

    public RobotModel getModel()
    {
        return model;
    }
}