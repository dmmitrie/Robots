package log;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Потокобезопасный кольцевой буфер для хранения логов с ограниченным размером.
 * Реализует все требования задания, включая поддержку темпоральных (snapshot) итераторов.
 */
public class BoundedLogBuffer<T> implements Iterable<T> {

    private final T[] buffer;
    private final int capacity;
    private int head; // Индекс самой старой записи
    private int size; // Текущее количество записей

    // ReadWriteLock позволяет множественным потокам читать лог одновременно,
    // но блокирует запись, пока идет чтение (и наоборот).
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @SuppressWarnings("unchecked")
    public BoundedLogBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
        this.head = 0;
        this.size = 0;
    }

    /**
     * Добавляет запись в буфер. Если буфер полон, вытесняет самую старую запись.
     * Сложность: O(1)
     */
    public void add(T item) {
        lock.writeLock().lock();
        try {
            if (size == capacity) {
                // Вытесняем старую запись, сдвигая head
                head = (head + 1) % capacity;
            } else {
                size++;
            }
            // Записываем в следующую свободную ячейку
            int writeIdx = (head + size - 1) % capacity;
            buffer[writeIdx] = item;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Возвращает сегмент записей по логическим индексам [from, to).
     * Позволяет окну протоколирования загружать только видимую часть, а не весь лог.
     * Сложность: O(K), где K = to - from
     */
    public List<T> getSegment(int from, int to) {
        lock.readLock().lock();
        try {
            int start = Math.max(0, from);
            int end = Math.min(size, to);

            if (start >= end) {
                return Collections.emptyList();
            }

            List<T> result = new ArrayList<>(end - start);
            for (int i = start; i < end; i++) {
                // Маппинг логического индекса на физический в кольцевом массиве
                result.add(buffer[(head + i) % capacity]);
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try { return size; }
        finally { lock.readLock().unlock(); }
    }

    public int capacity() {
        return capacity;
    }

    /**
     * Возвращает потокобезопасный темпоральный итератор.
     * Итератор фиксирует состояние буфера на момент создания (snapshot),
     * поэтому добавление новых записей во время итерации не ломает его и не вызывает ConcurrentModificationException.
     */
    @Override
    public Iterator<T> iterator() {
        lock.readLock().lock();
        try {
            // Создаем моментальный снимок состояния для изоляции итератора
            int snapshotHead = head;
            int snapshotSize = size;
            T[] snapshotBuffer = buffer.clone();
            return new TemporalSnapshotIterator<>(snapshotHead, snapshotSize, snapshotBuffer);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Темпоральный итератор, работающий с независимым снимком данных.
     * Гарантирует стабильность обхода даже при активной записи в основной буфер.
     */
    private static class TemporalSnapshotIterator<T> implements Iterator<T> {
        private final T[] snapshotBuffer;
        private final int head;
        private final int size;
        private int cursor;

        TemporalSnapshotIterator(int head, int size, T[] snapshotBuffer) {
            this.head = head;
            this.size = size;
            this.snapshotBuffer = snapshotBuffer;
            this.cursor = 0;
        }

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int idx = (head + cursor) % snapshotBuffer.length;
            cursor++;
            return snapshotBuffer[idx];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Log buffer does not support removal via iterator");
        }
    }
}