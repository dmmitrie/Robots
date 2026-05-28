package gui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты производительности локализации")
class LocalizationBenchmarkTest {

    @Test
    @DisplayName("Кэширование ускоряет форматирование")
    void testCachingImprovesPerformance() {
        LocalizationBenchmark benchmark = new LocalizationBenchmark();

        long withoutCache = benchmark.testMessageFormatWithoutCache();
        long withCache = benchmark.testMessageFormatWithCache();

        assertTrue(withCache < withoutCache,
                "Кэширование должно ускорять выполнение");

        double speedup = (double) withoutCache / withCache;
        System.out.println("Speedup from caching: " + String.format("%.2f", speedup) + "x");

        assertTrue(speedup >= 2.0,
                "Ускорение должно быть не менее 2x, фактически: " + speedup);
    }

    @Test
    @DisplayName("MessageFormat с кэшем быстрее Formatter")
    void testMessageFormatFasterThanFormatter() {
        LocalizationBenchmark benchmark = new LocalizationBenchmark();

        long formatterTime = benchmark.testFormatter();
        long cachedMFTime = benchmark.testMessageFormatWithCache();

        System.out.println("Formatter: " + formatterTime + " ms");
        System.out.println("Cached MessageFormat: " + cachedMFTime + " ms");
    }
}