package gui;

import model.RobotModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final ConfigManager configManager;
    private final WindowStateManager windowStateManager;
    private final LocalizationManager localizationManager;

    private GameWindow gameWindow;
    private LogWindow logWindow;
    private CoordinatesWindow coordinatesWindow;

    private JMenu fileMenu;
    private JMenu viewMenu;
    private JMenu languageMenu;
    private JMenu testMenu;
    private JMenuItem exitItem;
    private JMenuItem logMessageItem;
    private JMenuItem russianItem;
    private JMenuItem englishItem;

    public MainApplicationFrame() {
        localizationManager = LocalizationManager.getInstance();
        configManager = new ConfigManager();
        windowStateManager = new WindowStateManager(configManager);

        configManager.load();

        setupFonts();

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        gameWindow = new GameWindow();
        addWindow(gameWindow);

        logWindow = new LogWindow(Logger.getDefaultLogSource());
        addWindow(logWindow);

        RobotModel model = gameWindow.getModel();
        coordinatesWindow = new CoordinatesWindow(model);
        addWindow(coordinatesWindow);

        setJMenuBar(createMenuBar());
        updateUITexts();

        windowStateManager.restoreMainWindowState(this);
        windowStateManager.restoreAllWindowsState(desktopPane);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    private void setupFonts() {
        Font defaultFont = new Font("Dialog", Font.PLAIN, 12);

        UIManager.put("Menu.font", defaultFont);
        UIManager.put("MenuItem.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Panel.font", defaultFont);
    }

    private void updateUITexts() {
        setTitle(localizationManager.getString("app.title"));

        if (fileMenu != null) fileMenu.setText(localizationManager.getString("menu.file"));
        if (exitItem != null) exitItem.setText(localizationManager.getString("menu.file.exit"));

        if (viewMenu != null) viewMenu.setText(localizationManager.getString("menu.view"));
        if (languageMenu != null) languageMenu.setText(localizationManager.getString("menu.view.language"));

        if (testMenu != null) testMenu.setText(localizationManager.getString("menu.tests"));
        if (logMessageItem != null) logMessageItem.setText(localizationManager.getString("menu.tests.logMessage"));

        if (russianItem != null) russianItem.setText(localizationManager.getString("menu.view.language.ru"));
        if (englishItem != null) englishItem.setText(localizationManager.getString("menu.view.language.en"));

        if (gameWindow != null) gameWindow.setTitle(localizationManager.getString("window.game"));
        if (logWindow != null) logWindow.setTitle(localizationManager.getString("window.log"));
        if (coordinatesWindow != null) {
            coordinatesWindow.setTitle(localizationManager.getString("window.coordinates"));
            coordinatesWindow.updateLabels();
        }

        SwingUtilities.updateComponentTreeUI(this);
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        fileMenu = new JMenu();
        exitItem = new JMenuItem();
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitItem);

        viewMenu = new JMenu();
        languageMenu = new JMenu();

        russianItem = new JMenuItem();
        russianItem.addActionListener(e -> {
            localizationManager.setLocale(new Locale("ru", "RU"));
            updateUITexts();
            SwingUtilities.updateComponentTreeUI(this);
        });
        languageMenu.add(russianItem);

        englishItem = new JMenuItem();
        englishItem.addActionListener(e -> {
            localizationManager.setLocale(Locale.ENGLISH);
            updateUITexts();
            SwingUtilities.updateComponentTreeUI(this);
        });
        languageMenu.add(englishItem);

        viewMenu.add(languageMenu);

        testMenu = new JMenu();
        logMessageItem = new JMenuItem();
        logMessageItem.addActionListener(e -> Logger.debug(localizationManager.getString("log.newMessage")));
        testMenu.add(logMessageItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(testMenu);

        return menuBar;
    }

    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
                this,
                localizationManager.getString("dialog.exit.message"),
                localizationManager.getString("dialog.exit.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            if (gameWindow != null) {
                gameWindow.stop();
            }

            windowStateManager.saveMainWindowState(this);
            windowStateManager.saveAllWindowsState(desktopPane);
            configManager.save();

            Logger.debug(localizationManager.getString("log.closing"));
            dispose();
        }
    }
}