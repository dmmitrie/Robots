package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final ConfigManager configManager;
    private final WindowStateManager windowStateManager;
    private LogWindow logWindow;
    private GameWindow gameWindow;

    public MainApplicationFrame()
    {
        // Инициализируем менеджеры
        configManager = new ConfigManager();
        windowStateManager = new WindowStateManager(configManager);

        // Загружаем конфигурацию
        configManager.load();

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = new GameWindow();
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());

        // Восстанавливаем состояния окон
        windowStateManager.restoreMainWindowState(this);
        windowStateManager.restoreWindowState("game", gameWindow);
        windowStateManager.restoreWindowState("log", logWindow);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createFileMenu()
    {
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(
                "Меню файла с командой выхода");

        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        exitItem.addActionListener((event) -> {
            exitApplication();
        });
        fileMenu.add(exitItem);

        return fileMenu;
    }

    private JMenu createLookAndFeelMenu()
    {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        return lookAndFeelMenu;
    }

    private JMenu createTestMenu()
    {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        return testMenu;
    }

    private void exitApplication()
    {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти из приложения?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION)
        {
            // Останавливаем все окна
            if (gameWindow != null)
            {
                gameWindow.stop();
            }

            // Сохраняем состояния окон
            windowStateManager.saveMainWindowState(this);
            windowStateManager.saveWindowState("game", gameWindow);
            windowStateManager.saveWindowState("log", logWindow);

            // Сохраняем конфигурацию
            configManager.save();

            Logger.debug("Приложение закрывается пользователем");
            dispose();
        }
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            Logger.error("Failed to set look and feel: " + e.getMessage());
        }
    }
}