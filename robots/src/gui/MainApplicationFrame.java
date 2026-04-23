package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

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
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private static final String CONFIG_FILE = System.getProperty("user.home") +
            File.separator + ".robots_config.properties";

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        loadWindowState();

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
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
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

    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти из приложения?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            if (gameWindow != null) {
                gameWindow.stop();
            }
            saveWindowState();

            dispose();
        }
    }

    private void saveWindowState() {
        try {
            Properties props = new Properties();

            props.setProperty("main.x", String.valueOf(getX()));
            props.setProperty("main.y", String.valueOf(getY()));
            props.setProperty("main.width", String.valueOf(getWidth()));
            props.setProperty("main.height", String.valueOf(getHeight()));
            props.setProperty("main.extendedState", String.valueOf(getExtendedState()));

            if (gameWindow != null) {
                props.setProperty("game.x", String.valueOf(gameWindow.getX()));
                props.setProperty("game.y", String.valueOf(gameWindow.getY()));
                props.setProperty("game.width", String.valueOf(gameWindow.getWidth()));
                props.setProperty("game.height", String.valueOf(gameWindow.getHeight()));
                props.setProperty("game.isIcon", String.valueOf(gameWindow.isIcon()));
                try {
                    props.setProperty("game.isSelected", String.valueOf(gameWindow.isSelected()));
                } catch (Exception e) {
                    props.setProperty("game.isSelected", "false");
                }
            }

            if (logWindow != null) {
                props.setProperty("log.x", String.valueOf(logWindow.getX()));
                props.setProperty("log.y", String.valueOf(logWindow.getY()));
                props.setProperty("log.width", String.valueOf(logWindow.getWidth()));
                props.setProperty("log.height", String.valueOf(logWindow.getHeight()));
                props.setProperty("log.isIcon", String.valueOf(logWindow.isIcon()));
                try {
                    props.setProperty("log.isSelected", String.valueOf(logWindow.isSelected()));
                } catch (Exception e) {
                    props.setProperty("log.isSelected", "false");
                }
            }

            FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
            props.store(fos, "Robots Application Configuration");
            fos.close();

            Logger.debug("Состояние окон сохранено в " + CONFIG_FILE);
        } catch (Exception e) {
            Logger.error("Ошибка при сохранении состояния окон: " + e.getMessage());
        }
    }

    private void loadWindowState() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                Logger.debug("Файл конфигурации не найден, используем значения по умолчанию");
                return;
            }

            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(configFile);
            props.load(fis);
            fis.close();

            int mainX = Integer.parseInt(props.getProperty("main.x", String.valueOf(getX())));
            int mainY = Integer.parseInt(props.getProperty("main.y", String.valueOf(getY())));
            int mainWidth = Integer.parseInt(props.getProperty("main.width", String.valueOf(getWidth())));
            int mainHeight = Integer.parseInt(props.getProperty("main.height", String.valueOf(getHeight())));
            int mainExtendedState = Integer.parseInt(props.getProperty("main.extendedState", "0"));

            setBounds(mainX, mainY, mainWidth, mainHeight);
            setExtendedState(mainExtendedState);

            if (gameWindow != null) {
                int gameX = Integer.parseInt(props.getProperty("game.x", String.valueOf(gameWindow.getX())));
                int gameY = Integer.parseInt(props.getProperty("game.y", String.valueOf(gameWindow.getY())));
                int gameWidth = Integer.parseInt(props.getProperty("game.width", String.valueOf(gameWindow.getWidth())));
                int gameHeight = Integer.parseInt(props.getProperty("game.height", String.valueOf(gameWindow.getHeight())));
                boolean gameIsIcon = Boolean.parseBoolean(props.getProperty("game.isIcon", "false"));

                gameWindow.setBounds(gameX, gameY, gameWidth, gameHeight);
                if (gameIsIcon) {
                    try {
                        gameWindow.setIcon(true);
                    } catch (Exception e) {
                    }
                }
            }

            if (logWindow != null) {
                int logX = Integer.parseInt(props.getProperty("log.x", String.valueOf(logWindow.getX())));
                int logY = Integer.parseInt(props.getProperty("log.y", String.valueOf(logWindow.getY())));
                int logWidth = Integer.parseInt(props.getProperty("log.width", String.valueOf(logWindow.getWidth())));
                int logHeight = Integer.parseInt(props.getProperty("log.height", String.valueOf(logWindow.getHeight())));
                boolean logIsIcon = Boolean.parseBoolean(props.getProperty("log.isIcon", "false"));

                logWindow.setBounds(logX, logY, logWidth, logHeight);
                if (logIsIcon) {
                    try {
                        logWindow.setIcon(true);
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }

            Logger.debug("Состояние окон восстановлено из " + CONFIG_FILE);
        } catch (Exception e) {
            Logger.error("Ошибка при загрузке состояния окон: " + e.getMessage());
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