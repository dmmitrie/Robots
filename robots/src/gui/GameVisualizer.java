package gui;

import model.RobotController;
import model.RobotModelObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 *View: отображает состояние. Не держит ссылку на модель.
 */
public class GameVisualizer extends JPanel implements RobotModelObserver
{
    private final RobotController controller;

    // Локальное состояние для отрисовки (обновляется через коллбэки)
    private double robotX = 100, robotY = 100, robotDir = 0;
    private double targetX = 150, targetY = 100;

    public GameVisualizer(RobotController controller)
    {
        this.controller = controller;
        this.controller.addObserver(this);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // Взаимодействие только через контроллер
                controller.setTarget(e.getX(), e.getY());
            }
        });

        setDoubleBuffered(true);
        setPreferredSize(new Dimension(400, 400));
    }

    @Override
    public void onRobotStateChanged(double x, double y, double direction)
    {
        this.robotX = x;
        this.robotY = y;
        this.robotDir = direction;
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    public void onTargetChanged(double x, double y)
    {
        this.targetX = x;
        this.targetY = y;
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform original = g2d.getTransform();

        drawRobot(g2d);
        drawTarget(g2d);

        g2d.setTransform(original);
    }

    private void drawRobot(Graphics2D g)
    {
        int cx = round(robotX);
        int cy = round(robotY);
        AffineTransform t = g.getTransform();
        g.rotate(robotDir, cx, cy);

        g.setColor(Color.MAGENTA);
        g.fillOval(cx - 15, cy - 5, 30, 10);
        g.setColor(Color.BLACK);
        g.drawOval(cx - 15, cy - 5, 30, 10);
        g.setTransform(t);
    }

    private void drawTarget(Graphics2D g)
    {
        int cx = round(targetX);
        int cy = round(targetY);
        g.setColor(Color.GREEN);
        g.fillOval(cx - 2, cy - 2, 5, 5);
        g.setColor(Color.BLACK);
        g.drawOval(cx - 2, cy - 2, 5, 5);
    }

    private static int round(double v) { return (int)(v + 0.5); }
}