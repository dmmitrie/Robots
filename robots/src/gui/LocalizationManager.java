package gui;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.UIManager;

public class LocalizationManager {

    private static LocalizationManager instance;
    private ResourceBundle bundle;
    private Locale currentLocale;

    private final ConcurrentHashMap<String, MessageFormat> messageFormatCache;

    private LocalizationManager() {
        messageFormatCache = new ConcurrentHashMap<>();
        setLocale(new Locale("ru", "RU"));
    }

    public static synchronized LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle("messages", locale);
        messageFormatCache.clear();

        updateSwingDefaults();
    }

    private void updateSwingDefaults() {
        UIManager.put("OptionPane.yesButtonText", getString("dialog.yes"));
        UIManager.put("OptionPane.noButtonText", getString("dialog.no"));
        UIManager.put("OptionPane.cancelButtonText", getString("dialog.cancel"));
        UIManager.put("OptionPane.okButtonText", getString("dialog.ok"));
    }

    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!"; // Заглушка если ключ не найден
        }
    }

    public String getFormattedMessage(String key, Object... args) {
        String pattern = getString(key);
        MessageFormat mf = new MessageFormat(pattern, currentLocale);
        return mf.format(args);
    }

    public String getCachedFormattedMessage(String key, Object... args) {
        String pattern = getString(key);

        MessageFormat mf = messageFormatCache.computeIfAbsent(
                key,
                k -> new MessageFormat(pattern, currentLocale)
        );

        return mf.format(args);
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void clearCache() {
        messageFormatCache.clear();
    }

    public int getCacheSize() {
        return messageFormatCache.size();
    }
}