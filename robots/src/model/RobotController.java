package model;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Контроллер управляет моделью и циклом обновлений.
 * Скрывает модель от внешнего мира.
 */
public class RobotController
{
    private final RobotModel model = new RobotModel();
    private final Timer timer = new Timer("robot-update-timer", true);
    private long lastTickTime = 0;

    public void start()
    {
        lastTickTime = System.currentTimeMillis();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                long now = System.currentTimeMillis();
                double dt = now - lastTickTime; // время в мс
                lastTickTime = now;
                tick(dt);
            }
        }, 0, 10);
    }

    public void stop()
    {
        timer.cancel();
        timer.purge();
    }

    private void tick(double dt)
    {
        double distance = model.getDistanceToTarget();
        if (distance < 10.0) return;

        double velocity = 0.1;
        double angularVelocity = model.calculateAngularVelocity();
        model.update(velocity, angularVelocity, dt);
    }

    public void setTarget(double x, double y)
    {
        model.setTarget(x, y);
    }

    public void addObserver(RobotModelObserver observer) { model.addObserver(observer); }
    public void removeObserver(RobotModelObserver observer) { model.removeObserver(observer); }
}