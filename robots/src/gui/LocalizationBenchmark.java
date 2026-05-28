package gui;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationBenchmark {

    private static final int ITERATIONS = 10000;
    private final ResourceBundle bundle;
    private final Locale locale;

    public LocalizationBenchmark() {
        this.locale = new Locale("ru", "RU");
        // Исправлено: просто "messages", так как папка resources - это Resources Root
        this.bundle = ResourceBundle.getBundle("messages", this.locale);
    }

    public long testFormatter() {
        String pattern = bundle.getString("coords.positionX");

        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String result = String.format(locale, pattern, i);
        }
        long end = System.nanoTime();

        return (end - start) / 1_000_000; // миллисекунды
    }

    public long testMessageFormatWithoutCache() {
        String pattern = bundle.getString("coords.positionX");

        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            MessageFormat mf = new MessageFormat(pattern, locale);
            String result = mf.format(new Object[]{i});
        }
        long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    public long testMessageFormatWithCache() {
        String pattern = bundle.getString("coords.positionX");
        MessageFormat cachedMF = new MessageFormat(pattern, locale);

        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String result = cachedMF.format(new Object[]{i});
        }
        long end = System.nanoTime();

        return (end - start) / 1_000_000;
    }

    public void runBenchmark() {
        System.out.println("=== BENCHMARK RESULTS ===");
        System.out.println("Iterations: " + ITERATIONS);
        System.out.println();

        long formatterTime = testFormatter();
        System.out.println("1. Formatter:                    " + formatterTime + " ms");

        long mfWithoutCacheTime = testMessageFormatWithoutCache();
        System.out.println("2. MessageFormat (no cache):     " + mfWithoutCacheTime + " ms");

        long mfWithCacheTime = testMessageFormatWithCache();
        System.out.println("3. MessageFormat (with cache):   " + mfWithCacheTime + " ms");

        System.out.println();
        System.out.println("Speedup (cached vs no cache): " +
                String.format("%.2f", (double)mfWithoutCacheTime / mfWithCacheTime) + "x");
        System.out.println("Speedup (cached vs formatter): " +
                String.format("%.2f", (double)formatterTime / mfWithCacheTime) + "x");
    }

    public static void main(String[] args) {
        LocalizationBenchmark benchmark = new LocalizationBenchmark();
        benchmark.runBenchmark();
    }
}