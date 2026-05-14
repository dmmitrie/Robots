package gui;

import model.RobotController;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class GameWindow extends JInternalFrame
{
    private final RobotController controller;
    private final GameVisualizer visualizer;

    public GameWindow(RobotController controller)
    {
        super("Игровое поле", true, true, true, true);
        this.controller = controller;
        this.visualizer = new GameVisualizer(controller);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        setBounds(150, 100, 420, 450);
        pack();

        addInternalFrameListener(new InternalFrameAdapter()
        {
            @Override
            public void internalFrameClosing(InternalFrameEvent e)
            {
                controller.stop();
            }
        });
    }

    public void startSimulation()
    {
        controller.start();
    }
}