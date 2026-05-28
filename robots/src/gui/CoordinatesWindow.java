package gui;

import model.RobotModel;
import model.RobotModelObserver;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class CoordinatesWindow extends JInternalFrame implements RobotModelObserver {
    private final RobotModel model;
    private JLabel xLabel;
    private JLabel yLabel;
    private JLabel directionLabel;
    private JLabel targetLabel;
    private JLabel distanceLabel;

    private JLabel lblPosX;
    private JLabel lblPosY;
    private JLabel lblDir;
    private JLabel lblTarget;
    private JLabel lblDist;

    private static final int DEFAULT_X = 600;
    private static final int DEFAULT_Y = 10;
    private static final int DEFAULT_WIDTH = 250;
    private static final int DEFAULT_HEIGHT = 150;

    public CoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);

        this.model = model;
        model.addObserver(this);

        setBounds(DEFAULT_X, DEFAULT_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblPosX = new JLabel();
        panel.add(lblPosX);
        xLabel = new JLabel("0.00");
        panel.add(xLabel);

        lblPosY = new JLabel();
        panel.add(lblPosY);
        yLabel = new JLabel("0.00");
        panel.add(yLabel);

        lblDir = new JLabel();
        panel.add(lblDir);
        directionLabel = new JLabel("0.00 рад");
        panel.add(directionLabel);

        lblTarget = new JLabel();
        panel.add(lblTarget);
        targetLabel = new JLabel("(0, 0)");
        panel.add(targetLabel);

        lblDist = new JLabel();
        panel.add(lblDist);
        distanceLabel = new JLabel("0.00");
        panel.add(distanceLabel);

        getContentPane().add(panel, BorderLayout.CENTER);
        pack();

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                model.removeObserver(CoordinatesWindow.this);
            }
        });

        updateLabels();
    }

    public void updateLabels() {
        LocalizationManager lm = LocalizationManager.getInstance();
        lblPosX.setText(lm.getString("coords.positionX"));
        lblPosY.setText(lm.getString("coords.positionY"));
        lblDir.setText(lm.getString("coords.direction"));
        lblTarget.setText(lm.getString("coords.target"));
        lblDist.setText(lm.getString("coords.distance"));

        onRobotStateChanged(model.getX(), model.getY(), model.getDirection());
    }

    @Override
    public void onRobotStateChanged(double x, double y, double direction) {
        SwingUtilities.invokeLater(() -> {
            xLabel.setText(String.format("%.2f", x));
            yLabel.setText(String.format("%.2f", y));

            String radUnit = LocalizationManager.getInstance().getString("unit.rad");
            directionLabel.setText(String.format("%.2f " + radUnit, direction));

            updateDistance();
        });
    }

    @Override
    public void onTargetChanged(double x, double y) {
        SwingUtilities.invokeLater(() -> {
            targetLabel.setText(String.format("(%.2f, %.2f)", x, y));
            updateDistance();
        });
    }

    private void updateDistance() {
        double distance = model.getDistanceToTarget();
        distanceLabel.setText(String.format("%.2f", distance));
    }
}