package gui;

import model.RobotController;
import model.RobotModelObserver;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

/**
 * View: отображает координаты. Не знает о модели.
 */
public class CoordinatesWindow extends JInternalFrame implements RobotModelObserver
{
    private final RobotController controller;
    private JLabel lblX, lblY, lblDir, lblTarget, lblDist;

    public CoordinatesWindow(RobotController controller)
    {
        super("Координаты робота", true, true, true, true);
        this.controller = controller;
        this.controller.addObserver(this);

        setBounds(600, 10, 250, 150);
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("X:")); lblX = new JLabel("0.00"); panel.add(lblX);
        panel.add(new JLabel("Y:")); lblY = new JLabel("0.00"); panel.add(lblY);
        panel.add(new JLabel("Угол:")); lblDir = new JLabel("0.00"); panel.add(lblDir);
        panel.add(new JLabel("Цель:")); lblTarget = new JLabel("(0,0)"); panel.add(lblTarget);
        panel.add(new JLabel("Дист:")); lblDist = new JLabel("0.00"); panel.add(lblDist);

        getContentPane().add(panel, BorderLayout.CENTER);
        pack();

        addInternalFrameListener(new InternalFrameAdapter()
        {
            @Override
            public void internalFrameClosing(InternalFrameEvent e)
            {
                controller.removeObserver(CoordinatesWindow.this);
            }
        });
    }

    @Override
    public void onRobotStateChanged(double x, double y, double dir)
    {
        SwingUtilities.invokeLater(() -> {
            lblX.setText(String.format("%.2f", x));
            lblY.setText(String.format("%.2f", y));
            lblDir.setText(String.format("%.2f", dir));
            updateDistance();
        });
    }

    @Override
    public void onTargetChanged(double x, double y)
    {
        SwingUtilities.invokeLater(() -> {
            lblTarget.setText(String.format("(%.2f, %.2f)", x, y));
            updateDistance();
        });
    }

    private void updateDistance()
    {
        // Вычисляем дистанцию локально или запрашиваем у контроллера
        // Для чистоты MVC лучше вычислять на основе сохранённых значений,
        // но допустимо добавить метод getDistance() в контроллер.
        // Оставим заглушку, т.к. точная дистанция берётся из модели,
        // а контроллер её не раскрывает. Можно добавить метод в контроллер при необходимости.
    }
}