package model;

import java.util.ArrayList;
import java.util.List;

public class RobotModel
{
    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100;
    private volatile double robotDirection = 0;

    private volatile double targetPositionX = 150;
    private volatile double targetPositionY = 100;

    private final List<RobotModelListener> listeners = new ArrayList<>();

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    public interface RobotModelListener
    {
        void onRobotStateChanged(double x, double y, double direction);
        void onTargetChanged(double x, double y);
    }

    public void addListener(RobotModelListener listener)
    {
        listeners.add(listener);
    }

    public void removeListener(RobotModelListener listener)
    {
        listeners.remove(listener);
    }

    private void notifyRobotStateChanged()
    {
        for (RobotModelListener listener : new ArrayList<>(listeners))
        {
            listener.onRobotStateChanged(robotPositionX, robotPositionY, robotDirection);
        }
    }

    private void notifyTargetChanged()
    {
        for (RobotModelListener listener : new ArrayList<>(listeners))
        {
            listener.onTargetChanged(targetPositionX, targetPositionY);
        }
    }

    public double getRobotPositionX()
    {
        return robotPositionX;
    }

    public double getRobotPositionY()
    {
        return robotPositionY;
    }

    public double getRobotDirection()
    {
        return robotDirection;
    }

    public double getTargetPositionX()
    {
        return targetPositionX;
    }

    public double getTargetPositionY()
    {
        return targetPositionY;
    }

    public void setTargetPosition(double x, double y)
    {
        this.targetPositionX = x;
        this.targetPositionY = y;
        notifyTargetChanged();
    }

    public double getDistanceToTarget()
    {
        double diffX = robotPositionX - targetPositionX;
        double diffY = robotPositionY - targetPositionY;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public double getAngleToTarget()
    {
        double diffX = targetPositionX - robotPositionX;
        double diffY = targetPositionY - robotPositionY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    public void updatePosition(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX;
        double newY;

        if (Math.abs(angularVelocity) < 1e-9)
        {
            newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
        }
        else
        {
            newX = robotPositionX + velocity / angularVelocity *
                    (Math.sin(robotDirection + angularVelocity * duration) - Math.sin(robotDirection));
            newY = robotPositionY - velocity / angularVelocity *
                    (Math.cos(robotDirection + angularVelocity * duration) - Math.cos(robotDirection));
        }

        if (Double.isFinite(newX))
            robotPositionX = newX;
        if (Double.isFinite(newY))
            robotPositionY = newY;

        robotDirection = asNormalizedRadians(robotDirection + angularVelocity * duration);

        notifyRobotStateChanged();
    }

    public double calculateAngularVelocity()
    {
        double angleToTarget = getAngleToTarget();
        double angleDiff = angleToTarget - robotDirection;

        // Нормализуем разницу углов (-PI до PI)
        while (angleDiff > Math.PI)
            angleDiff -= 2 * Math.PI;
        while (angleDiff < -Math.PI)
            angleDiff += 2 * Math.PI;

        // Мёртвая зона
        if (Math.abs(angleDiff) < 0.2)
            return 0;

        return angleDiff > 0 ? MAX_ANGULAR_VELOCITY : -MAX_ANGULAR_VELOCITY;
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
            angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI)
            angle -= 2 * Math.PI;
        return angle;
    }
}