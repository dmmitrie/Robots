package gui;

import model.RobotController;
import model.RobotModel;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class GameWindow extends JInternalFrame {
    private final RobotModel model;
    private final RobotController controller;
    private final GameVisualizer visualizer;
    private final Timer timer;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);

        this.model = new RobotModel();
        this.controller = new RobotController(model);
        this.visualizer = new GameVisualizer(model);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        setBounds(150, 100, 420, 450);
        pack();

        timer = new Timer("game-timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                controller.update();
            }
        }, 0, 10);

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                stop();
            }
        });
    }

    public RobotModel getModel() {
        return model;
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}