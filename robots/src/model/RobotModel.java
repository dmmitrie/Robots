package model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Модель робота. Хранит состояние и физику движения.
 * Не зависит от Swing/GUI.
 */
public class RobotModel
{
    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100;
    private volatile double robotDirection = 0;

    private volatile double targetPositionX = 150;
    private volatile double targetPositionY = 100;

    // Синхронизированный список для потокобезопасного доступа из разных потоков
    private final List<RobotModelObserver> listeners = new CopyOnWriteArrayList<>();

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;

    public void addObserver(RobotModelObserver observer)
    {
        listeners.add(observer);
    }

    public void removeObserver(RobotModelObserver observer)
    {
        listeners.remove(observer);
    }

    private void notifyRobotStateChanged()
    {
        for (RobotModelObserver listener : listeners)
        {
            listener.onRobotStateChanged(robotPositionX, robotPositionY, robotDirection);
        }
    }

    private void notifyTargetChanged()
    {
        for (RobotModelObserver listener : listeners)
        {
            listener.onTargetChanged(targetPositionX, targetPositionY);
        }
    }

    public double getX() { return robotPositionX; }
    public double getY() { return robotPositionY; }
    public double getDirection() { return robotDirection; }
    public double getTargetX() { return targetPositionX; }
    public double getTargetY() { return targetPositionY; }

    public double getDistanceToTarget()
    {
        double diffX = robotPositionX - targetPositionX;
        double diffY = robotPositionY - targetPositionY;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public void setTarget(double x, double y)
    {
        this.targetPositionX = x;
        this.targetPositionY = y;
        notifyTargetChanged();
    }

    public void update(double velocity, double angularVelocity, double dt)
    {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX;
        double newY;

        if (Math.abs(angularVelocity) < 1e-9)
        {
            newX = robotPositionX + velocity * dt * Math.cos(robotDirection);
            newY = robotPositionY + velocity * dt * Math.sin(robotDirection);
        }
        else
        {
            newX = robotPositionX + velocity / angularVelocity *
                    (Math.sin(robotDirection + angularVelocity * dt) - Math.sin(robotDirection));
            newY = robotPositionY - velocity / angularVelocity *
                    (Math.cos(robotDirection + angularVelocity * dt) - Math.cos(robotDirection));
        }

        if (Double.isFinite(newX)) robotPositionX = newX;
        if (Double.isFinite(newY)) robotPositionY = newY;

        robotDirection = asNormalizedRadians(robotDirection + angularVelocity * dt);
        notifyRobotStateChanged();
    }

    public double calculateAngularVelocity()
    {
        double angleToTarget = asNormalizedRadians(Math.atan2(
                targetPositionY - robotPositionY, targetPositionX - robotPositionX));
        double angleDiff = angleToTarget - robotDirection;

        while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
        while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

        if (Math.abs(angleDiff) < 0.2) return 0;
        return angleDiff > 0 ? MAX_ANGULAR_VELOCITY : -MAX_ANGULAR_VELOCITY;
    }

    private static double applyLimits(double value, double min, double max)
    {
        return Math.max(min, Math.min(max, value));
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}