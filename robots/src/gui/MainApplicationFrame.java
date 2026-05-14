package gui;

import model.RobotController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final ConfigManager configManager;
    private final WindowStateManager windowStateManager;
    private GameWindow gameWindow;
    private LogWindow logWindow;
    private CoordinatesWindow coordinatesWindow;
    private final RobotController controller = new RobotController();

    public MainApplicationFrame()
    {
        configManager = new ConfigManager();
        windowStateManager = new WindowStateManager(configManager);
        configManager.load();

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset*2, screenSize.height - inset*2);
        setContentPane(desktopPane);

        // Создаём окна, передавая ТОЛЬКО контроллер
        gameWindow = new GameWindow(controller);
        addWindow(gameWindow);

        logWindow = new LogWindow(Logger.getDefaultLogSource());
        addWindow(logWindow);

        coordinatesWindow = new CoordinatesWindow(controller);
        addWindow(coordinatesWindow);

        setJMenuBar(generateMenuBar());
        windowStateManager.restoreMainWindowState(this);
        windowStateManager.restoreAllWindowsState(desktopPane);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { exitApplication(); }
        });

        // Запускаем симуляцию после инициализации UI
        gameWindow.startSimulation();
    }

    protected LogWindow createLogWindow() { return logWindow; }
    protected void addWindow(JInternalFrame frame) { desktopPane.add(frame); frame.setVisible(true); }

    private JMenuBar generateMenuBar()
    {
        JMenuBar bar = new JMenuBar();
        bar.add(createFileMenu());
        bar.add(createLookAndFeelMenu());
        bar.add(createTestMenu());
        return bar;
    }

    private JMenu createFileMenu()
    {
        JMenu menu = new JMenu("Файл");
        menu.setMnemonic(KeyEvent.VK_F);
        JMenuItem exit = new JMenuItem("Выход", KeyEvent.VK_X);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        exit.addActionListener(e -> exitApplication());
        menu.add(exit);
        return menu;
    }

    private JMenu createLookAndFeelMenu()
    {
        JMenu menu = new JMenu("Режим отображения");
        menu.setMnemonic(KeyEvent.VK_V);
        JMenuItem sys = new JMenuItem("Системная схема", KeyEvent.VK_S);
        sys.addActionListener(e -> { setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); invalidate(); });
        menu.add(sys);
        JMenuItem cross = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        cross.addActionListener(e -> { setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); invalidate(); });
        menu.add(cross);
        return menu;
    }

    private JMenu createTestMenu()
    {
        JMenu menu = new JMenu("Тесты");
        menu.setMnemonic(KeyEvent.VK_T);
        JMenuItem logMsg = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        logMsg.addActionListener(e -> Logger.debug("Новая строка"));
        menu.add(logMsg);
        return menu;
    }

    private void exitApplication()
    {
        int res = JOptionPane.showConfirmDialog(this, "Выйти?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION)
        {
            controller.stop();
            windowStateManager.saveMainWindowState(this);
            windowStateManager.saveAllWindowsState(desktopPane);
            configManager.save();
            Logger.debug("Приложение закрыто");
            dispose();
        }
    }

    private void setLookAndFeel(String cls)
    {
        try { UIManager.setLookAndFeel(cls); SwingUtilities.updateComponentTreeUI(this); }
        catch (Exception e) { Logger.error("L&F error: " + e.getMessage()); }
    }
}