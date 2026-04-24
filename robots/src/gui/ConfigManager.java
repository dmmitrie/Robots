package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import log.Logger;

/**
 * Класс для управления файлом конфигурации
 * Отвечает за чтение и запись свойств
 */
public class ConfigManager
{
    private static final String CONFIG_FILE = System.getProperty("user.home") +
            File.separator + ".robots_config.properties";
    private Properties properties;

    public ConfigManager()
    {
        properties = new Properties();
    }

    /**
     * Загружает конфигурацию из файла
     */
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

    /**
     * Сохраняет конфигурацию в файл
     */
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

    /**
     * Получает строковое значение по ключу
     */
    public String getString(String key, String defaultValue)
    {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Получает целочисленное значение по ключу
     */
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

    /**
     * Получает логическое значение по ключу
     */
    public boolean getBoolean(String key, boolean defaultValue)
    {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    /**
     * Устанавливает строковое значение
     */
    public void setString(String key, String value)
    {
        properties.setProperty(key, value);
    }

    /**
     * Устанавливает целочисленное значение
     */
    public void setInt(String key, int value)
    {
        properties.setProperty(key, String.valueOf(value));
    }

    /**
     * Устанавливает логическое значение
     */
    public void setBoolean(String key, boolean value)
    {
        properties.setProperty(key, String.valueOf(value));
    }
}