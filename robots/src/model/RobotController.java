package model;

/**
 * Контроллер для управления роботом
 */
public class RobotController {
    private final RobotModel model;
    private static final double UPDATE_DURATION = 10; // мс
    private java.util.Timer timer;

    public RobotController(RobotModel model) {
        this.model = model;
    }

    /**
     * Обновляет состояние робота (вызывается по таймеру)
     */
    public void update() {
        double distance = model.getDistanceToTarget();

        // Если робот близко к цели, останавливаемся
        if (distance < 10.0) {
            return;
        }

        double velocity = 0.5; // Максимальная скорость
        double angularVelocity = model.calculateAngularVelocity();

        model.update(velocity, angularVelocity, UPDATE_DURATION);
    }

    /**
     * Устанавливает новую цель
     */
    public void setTargetPosition(double x, double y) {
        model.setTarget(x, y);
    }

    public RobotModel getModel() {
        return model;
    }

    /**
     * Запускает контроллер (если нужно)
     */
    public void start() {
        // Если нужен отдельный таймер в контроллере
    }

    /**
     * Останавливает контроллер
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}