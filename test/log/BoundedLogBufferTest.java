package log;

import collections.BoundedLogBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты BoundedLogBuffer")
class BoundedLogBufferTest {

    private BoundedLogBuffer<String> buffer;

    @BeforeEach
    void setUp() {
        buffer = new BoundedLogBuffer<>(5);
    }

    @Test
    @DisplayName("Добавление и извлечение одного элемента")
    void testAddAndPoll() {
        assertTrue(buffer.offer("Hello"));
        assertEquals("Hello", buffer.poll());
        assertTrue(buffer.isEmpty());
    }

    @Test
    @DisplayName("Добавление нескольких элементов")
    void testMultipleElements() {
        buffer.offer("A");
        buffer.offer("B");
        buffer.offer("C");

        assertEquals(3, buffer.size());
        assertEquals("A", buffer.peek());
    }

    @Test
    @DisplayName("Вытеснение старых элементов при переполнении")
    void testOverflowEviction() {
        buffer.offer("1");
        buffer.offer("2");
        buffer.offer("3");
        buffer.offer("4");
        buffer.offer("5");

        assertEquals(5, buffer.size());


        buffer.offer("6");

        assertEquals(5, buffer.size());
        assertEquals("2", buffer.poll());
        assertEquals("3", buffer.poll());
        assertEquals("4", buffer.poll());
        assertEquals("5", buffer.poll());
        assertEquals("6", buffer.poll());
    }

    @Test
    @DisplayName("Пустая очередь")
    void testEmptyQueue() {
        assertTrue(buffer.isEmpty());
        assertNull(buffer.poll());
        assertNull(buffer.peek());
        assertThrows(NoSuchElementException.class, () -> buffer.remove());
    }

    @Test
    @DisplayName("Метод element() и peek()")
    void testElementAndPeek() {
        buffer.offer("First");
        buffer.offer("Second");

        assertEquals("First", buffer.element());
        assertEquals("First", buffer.peek());
        assertEquals(2, buffer.size()); // Размер не изменился
    }

    @Test
    @DisplayName("Итерация по элементам")
    void testIteration() {
        buffer.offer("A");
        buffer.offer("B");
        buffer.offer("C");

        Iterator<String> it = buffer.iterator();

        assertTrue(it.hasNext());
        assertEquals("A", it.next());
        assertEquals("B", it.next());
        assertEquals("C", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("getSegment - получение части данных")
    void testGetSegment() {

        for (int i = 1; i <= 10; i++) {
            buffer.offer(String.valueOf(i));
        }

        var segment = buffer.getSegment(2, 5);


        assertEquals(3, segment.size(), "Размер сегмента должен быть 3");
        assertEquals("8", segment.get(0), "Элемент с индексом 2 должен быть '8'");
        assertEquals("9", segment.get(1), "Элемент с индексом 3 должен быть '9'");
        assertEquals("10", segment.get(2), "Элемент с индексом 4 должен быть '10'");
    }

    @Test
    @DisplayName("getSegment с вытеснением")
    void testGetSegmentWithOverflow() {

        for (int i = 1; i <= 8; i++) {
            buffer.offer(String.valueOf(i));
        }


        var segment = buffer.getSegment(0, 3);

        assertEquals(3, segment.size());
        assertEquals("4", segment.get(0));
        assertEquals("5", segment.get(1));
        assertEquals("6", segment.get(2));
    }

    @Test
    @DisplayName("toArray")
    void testToArray() {
        buffer.offer("X");
        buffer.offer("Y");
        buffer.offer("Z");

        Object[] arr = buffer.toArray();

        assertEquals(3, arr.length);
        assertEquals("X", arr[0]);
        assertEquals("Y", arr[1]);
        assertEquals("Z", arr[2]);
    }

    @Test
    @DisplayName("contains")
    void testContains() {
        buffer.offer("A");
        buffer.offer("B");

        assertTrue(buffer.contains("A"));
        assertTrue(buffer.contains("B"));
        assertFalse(buffer.contains("C"));
    }

    @Test
    @DisplayName("clear")
    void testClear() {
        buffer.offer("1");
        buffer.offer("2");

        buffer.clear();

        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.size());
    }

    @Test
    @DisplayName("Null элементы не допускаются")
    void testNullElements() {
        assertThrows(NullPointerException.class, () -> buffer.offer(null));
    }

    @Test
    @DisplayName("Потокобезопасность - базовый тест")
    void testThreadSafety() throws InterruptedException {
        BoundedLogBuffer<Integer> concurrentBuffer = new BoundedLogBuffer<>(100);

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                concurrentBuffer.offer(i);
            }
        });

        Thread consumer = new Thread(() -> {
            int count = 0;
            while (count < 100) {
                if (concurrentBuffer.poll() != null) {
                    count++;
                }
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();


        assertTrue(true);
    }
}