package gui;

import model.RobotModel;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class CoordinatesWindow extends JInternalFrame implements RobotModel.RobotModelListener
{
    private final RobotModel model;
    private JLabel xLabel;
    private JLabel yLabel;
    private JLabel directionLabel;
    private JLabel targetLabel;
    private JLabel distanceLabel;

    private static final int DEFAULT_X = 600;
    private static final int DEFAULT_Y = 10;
    private static final int DEFAULT_WIDTH = 250;
    private static final int DEFAULT_HEIGHT = 150;

    public CoordinatesWindow(RobotModel model)
    {
        super("Координаты робота", true, true, true, true);

        this.model = model;
        model.addListener(this);

        setBounds(DEFAULT_X, DEFAULT_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Позиция X:"));
        xLabel = new JLabel("0.00");
        panel.add(xLabel);

        panel.add(new JLabel("Позиция Y:"));
        yLabel = new JLabel("0.00");
        panel.add(yLabel);

        panel.add(new JLabel("Направление:"));
        directionLabel = new JLabel("0.00 рад");
        panel.add(directionLabel);

        panel.add(new JLabel("Цель:"));
        targetLabel = new JLabel("(0, 0)");
        panel.add(targetLabel);

        // Расстояние
        panel.add(new JLabel("Расстояние:"));
        distanceLabel = new JLabel("0.00");
        panel.add(distanceLabel);

        getContentPane().add(panel, BorderLayout.CENTER);
        pack();

        // Перехват закрытия окна
        addInternalFrameListener(new InternalFrameAdapter()
        {
            @Override
            public void internalFrameClosing(InternalFrameEvent e)
            {
                model.removeListener(CoordinatesWindow.this);
            }
        });

        // Первичное обновление
        updateLabels();
    }

    @Override
    public void onRobotStateChanged(double x, double y, double direction)
    {
        SwingUtilities.invokeLater(this::updateLabels);
    }

    @Override
    public void onTargetChanged(double x, double y)
    {
        SwingUtilities.invokeLater(this::updateLabels);
    }

    private void updateLabels()
    {
        xLabel.setText(String.format("%.2f", model.getRobotPositionX()));
        yLabel.setText(String.format("%.2f", model.getRobotPositionY()));
        directionLabel.setText(String.format("%.2f рад", model.getRobotDirection()));
        targetLabel.setText(String.format("(%.2f, %.2f)",
                model.getTargetPositionX(), model.getTargetPositionY()));
        distanceLabel.setText(String.format("%.2f", model.getDistanceToTarget()));
    }
}