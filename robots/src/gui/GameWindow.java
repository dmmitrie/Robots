package gui;

import model.RobotController;
import model.RobotModel;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Окно с игровым полем
 * Содержит контроллер и таймер обновлений
 */
public class GameWindow extends JInternalFrame
{
    private final RobotModel model;
    private final RobotController controller;
    private final GameVisualizer visualizer;
    private final Timer timer;

    private static final int DEFAULT_X = 150;
    private static final int DEFAULT_Y = 100;
    private static final int DEFAULT_WIDTH = 420;
    private static final int DEFAULT_HEIGHT = 450;

    public GameWindow()
    {
        super("Игровое поле", true, true, true, true);

        // Создаём модель и контроллер
        model = new RobotModel();
        controller = new RobotController(model);

        // Создаём визуализатор и передаём ему модель
        visualizer = new GameVisualizer(model);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        setBounds(DEFAULT_X, DEFAULT_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        pack();

        // Запускаем таймер обновлений
        timer = new Timer("game-timer", true);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                controller.update();
            }
        }, 0, 10);

        // Перехват закрытия окна
        addInternalFrameListener(new InternalFrameAdapter()
        {
            @Override
            public void internalFrameClosing(InternalFrameEvent e)
            {
                stop();
            }
        });
    }

    public void stop()
    {
        timer.cancel();
        timer.purge();
    }

    public RobotModel getModel()
    {
        return model;
    }
}