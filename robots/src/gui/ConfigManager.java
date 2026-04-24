package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import log.Logger;

public class ConfigManager
{
    private static final String CONFIG_FILE = System.getProperty("user.home") +
            File.separator + ".robots_config.properties";
    private Properties properties;

    public ConfigManager()
    {
        properties = new Properties();
    }

    public void load()
    {
        try
        {
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists())
            {
                Logger.debug("Файл конфигурации не найден");
                return;
            }

            FileInputStream fis = new FileInputStream(configFile);
            properties.load(fis);
            fis.close();

            Logger.debug("Конфигурация загружена из " + CONFIG_FILE);
        }
        catch (Exception e)
        {
            Logger.error("Ошибка при загрузке конфигурации: " + e.getMessage());
        }
    }

    public void save()
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
            properties.store(fos, "Robots Application Configuration");
            fos.close();

            Logger.debug("Конфигурация сохранена в " + CONFIG_FILE);
        }
        catch (Exception e)
        {
            Logger.error("Ошибка при сохранении конфигурации: " + e.getMessage());
        }
    }

    public String getString(String key, String defaultValue)
    {
        return properties.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue)
    {
        try
        {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue)
    {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    public void setString(String key, String value)
    {
        properties.setProperty(key, value);
    }

    public void setInt(String key, int value)
    {
        properties.setProperty(key, String.valueOf(value));
    }

    public void setBoolean(String key, boolean value)
    {
        properties.setProperty(key, String.valueOf(value));
    }
}