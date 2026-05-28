package gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты LocalizationManager")
class LocalizationManagerTest {

    private LocalizationManager localizationManager;

    @BeforeEach
    void setUp() {
        localizationManager = LocalizationManager.getInstance();
        localizationManager.setLocale(new Locale("ru", "RU"));
    }

    @Test
    @DisplayName("Загрузка русской локали")
    void testRussianLocale() {
        localizationManager.setLocale(new Locale("ru", "RU"));

        assertEquals("Файл", localizationManager.getString("menu.file"));
        assertEquals("Игровое поле", localizationManager.getString("window.game"));
    }

    @Test
    @DisplayName("Загрузка английской локали")
    void testEnglishLocale() {
        localizationManager.setLocale(Locale.ENGLISH);

        assertEquals("File", localizationManager.getString("menu.file"));
        assertEquals("Game Field", localizationManager.getString("window.game"));
    }

    @Test
    @DisplayName("Заглушка для несуществующего ключа")
    void testMissingKey() {
        String result = localizationManager.getString("non.existent.key");
        assertEquals("!non.existent.key!", result);
    }

    @Test
    @DisplayName("Форматирование без кэша")
    void testFormattedMessage() {
        localizationManager.setLocale(Locale.ENGLISH);

        assertNotNull(localizationManager.getFormattedMessage("coords.positionX", 100));
    }

    @Test
    @DisplayName("Форматирование с кэшем")
    void testCachedFormattedMessage() {
        localizationManager.setLocale(Locale.ENGLISH);

        String result1 = localizationManager.getCachedFormattedMessage("coords.positionX", 100);
        String result2 = localizationManager.getCachedFormattedMessage("coords.positionX", 200);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(1, localizationManager.getCacheSize());
    }

    @Test
    @DisplayName("Кэш очищается при смене языка")
    void testCacheClearedOnLocaleChange() {
        localizationManager.setLocale(Locale.ENGLISH);
        localizationManager.getCachedFormattedMessage("coords.positionX", 100);

        assertEquals(1, localizationManager.getCacheSize());

        localizationManager.setLocale(new Locale("ru", "RU"));

        assertEquals(0, localizationManager.getCacheSize());
    }

    @Test
    @DisplayName("Очистка кэша")
    void testClearCache() {
        localizationManager.getCachedFormattedMessage("coords.positionX", 100);
        localizationManager.getCachedFormattedMessage("coords.positionY", 200);

        assertEquals(2, localizationManager.getCacheSize());

        localizationManager.clearCache();

        assertEquals(0, localizationManager.getCacheSize());
    }

    @Test
    @DisplayName("Singleton паттерн")
    void testSingleton() {
        LocalizationManager instance1 = LocalizationManager.getInstance();
        LocalizationManager instance2 = LocalizationManager.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Текущая локаль")
    void testGetCurrentLocale() {
        Locale testLocale = new Locale("en", "US");
        localizationManager.setLocale(testLocale);

        assertEquals(testLocale, localizationManager.getCurrentLocale());
    }
}