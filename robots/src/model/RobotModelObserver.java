package model;

/**
 * Интерфейс наблюдателя для обновления состояния робота
 * Вынесен из модели, чтобы не привязывать контракт к реализации
 */
public interface RobotModelObserver
{
    void onRobotStateChanged(double x, double y, double direction);
    void onTargetChanged(double x, double y);
}