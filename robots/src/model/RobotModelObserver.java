package model;

/**
 * Интерфейс наблюдателя для модели робота
 */
public interface RobotModelObserver
{
    void onRobotStateChanged(double x, double y, double direction);
    void onTargetChanged(double x, double y);
}