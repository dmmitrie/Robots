package gui;

import javax.swing.JInternalFrame;

import log.Logger;

/**
 * Класс для управления состоянием окон
 * Отвечает за сохранение и восстановление позиций, размеров и состояния окон
 */
public class WindowStateManager
{
    private final ConfigManager configManager;

    public WindowStateManager(ConfigManager configManager)
    {
        this.configManager = configManager;
    }

    /**
     * Сохраняет состояние окна с указанным префиксом
     */
    public void saveWindowState(String prefix, JInternalFrame frame)
    {
        if (frame == null)
        {
            return;
        }

        configManager.setInt(prefix + ".x", frame.getX());
        configManager.setInt(prefix + ".y", frame.getY());
        configManager.setInt(prefix + ".width", frame.getWidth());
        configManager.setInt(prefix + ".height", frame.getHeight());
        configManager.setBoolean(prefix + ".isIcon", frame.isIcon());

        try
        {
            configManager.setBoolean(prefix + ".isSelected", frame.isSelected());
        }
        catch (Exception e)
        {
            configManager.setBoolean(prefix + ".isSelected", false);
        }

        Logger.debug("Состояние окна '" + prefix + "' сохранено");
    }

    /**
     * Восстанавливает состояние окна с указанным префиксом
     */
    public void restoreWindowState(String prefix, JInternalFrame frame)
    {
        if (frame == null)
        {
            return;
        }

        int x = configManager.getInt(prefix + ".x", frame.getX());
        int y = configManager.getInt(prefix + ".y", frame.getY());
        int width = configManager.getInt(prefix + ".width", frame.getWidth());
        int height = configManager.getInt(prefix + ".height", frame.getHeight());
        boolean isIcon = configManager.getBoolean(prefix + ".isIcon", false);

        frame.setBounds(x, y, width, height);

        if (isIcon)
        {
            try
            {
                frame.setIcon(true);
            }
            catch (Exception e)
            {
                // ignore
            }
        }

        Logger.debug("Состояние окна '" + prefix + "' восстановлено");
    }

    /**
     * Сохраняет состояние главного окна
     */
    public void saveMainWindowState(MainApplicationFrame frame)
    {
        configManager.setInt("main.x", frame.getX());
        configManager.setInt("main.y", frame.getY());
        configManager.setInt("main.width", frame.getWidth());
        configManager.setInt("main.height", frame.getHeight());
        configManager.setInt("main.extendedState", frame.getExtendedState());

        Logger.debug("Состояние главного окна сохранено");
    }

    /**
     * Восстанавливает состояние главного окна
     */
    public void restoreMainWindowState(MainApplicationFrame frame)
    {
        int x = configManager.getInt("main.x", frame.getX());
        int y = configManager.getInt("main.y", frame.getY());
        int width = configManager.getInt("main.width", frame.getWidth());
        int height = configManager.getInt("main.height", frame.getHeight());
        int extendedState = configManager.getInt("main.extendedState", 0);

        frame.setBounds(x, y, width, height);
        frame.setExtendedState(extendedState);

        Logger.debug("Состояние главного окна восстановлено");
    }
}