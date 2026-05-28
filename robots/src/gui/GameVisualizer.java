package gui;

import model.RobotModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

public class GameVisualizer extends JPanel implements model.RobotModelObserver {
    private final RobotModel model;

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    public GameVisualizer(RobotModel model) {
        this.model = model;
        model.addObserver(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTarget(e.getX(), e.getY());
            }
        });

        setDoubleBuffered(true);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    @Override
    public void onRobotStateChanged(double x, double y, double direction) {
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    public void onTargetChanged(double x, double y) {
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        drawRobot(g2d);
        drawTarget(g2d);

        g2d.setTransform(originalTransform);
    }

    private void drawRobot(Graphics2D g) {
        int robotCenterX = round(model.getX());
        int robotCenterY = round(model.getY());
        double direction = model.getDirection();

        AffineTransform robotTransform = g.getTransform();
        g.rotate(direction, robotCenterX, robotCenterY);

        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);

        g.setTransform(robotTransform);
    }

    private void drawTarget(Graphics2D g) {
        int x = round(model.getTargetX());
        int y = round(model.getTargetY());

        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }
}