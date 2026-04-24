package gui;

import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    private static final int DEFAULT_X = 150;
    private static final int DEFAULT_Y = 100;
    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    public GameWindow()
    {
        super("Игровое поле", true, true, true, true);

        // Устанавливаем значения по умолчанию
        setBounds(DEFAULT_X, DEFAULT_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        // Перехватываем событие закрытия окна
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
        m_visualizer.stop();
    }
}