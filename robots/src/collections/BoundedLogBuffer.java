package collections;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Потокобезопасный кольцевой буфер с ограниченным размером.
 * Реализует интерфейс Queue для стандартного API работы с очередью.
 */
public class BoundedLogBuffer<T> implements Queue<T> {

    private final Object[] buffer;
    private final int capacity;
    private int head;
    private int size;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public BoundedLogBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.capacity = capacity;
        this.buffer = new Object[capacity];
        this.head = 0;
        this.size = 0;
    }

    @Override
    public boolean add(T item) {
        if (item == null) {
            throw new NullPointerException("Null elements not allowed");
        }

        lock.writeLock().lock();
        try {
            if (size == capacity) {
                head = (head + 1) % capacity;
            } else {
                size++;
            }

            int writeIdx = (head + size - 1) % capacity;
            buffer[writeIdx] = item;
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean offer(T item) {
        return add(item);
    }

    @Override
    public T remove() {
        lock.writeLock().lock();
        try {
            if (size == 0) {
                throw new NoSuchElementException("Queue is empty");
            }

            @SuppressWarnings("unchecked")
            T result = (T) buffer[head];
            buffer[head] = null; // Помогаем GC
            head = (head + 1) % capacity;
            size--;
            return result;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public T poll() {
        lock.writeLock().lock();
        try {
            if (size == 0) {
                return null;
            }

            @SuppressWarnings("unchecked")
            T result = (T) buffer[head];
            buffer[head] = null;
            head = (head + 1) % capacity;
            size--;
            return result;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public T element() {
        lock.readLock().lock();
        try {
            if (size == 0) {
                throw new NoSuchElementException("Queue is empty");
            }
            @SuppressWarnings("unchecked")
            T result = (T) buffer[head];
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public T peek() {
        lock.readLock().lock();
        try {
            if (size == 0) {
                return null;
            }
            @SuppressWarnings("unchecked")
            T result = (T) buffer[head];
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        lock.readLock().lock();
        try {
            for (int i = 0; i < size; i++) {
                int idx = (head + i) % capacity;
                if (o.equals(buffer[idx])) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {


        return new BoundedLogIterator();
    }

    @Override
    public Object[] toArray() {
        lock.readLock().lock();
        try {
            Object[] result = new Object[size];
            for (int i = 0; i < size; i++) {
                int idx = (head + i) % capacity;
                result[i] = buffer[idx];
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        lock.readLock().lock();
        try {
            if (a.length < size) {
                a = Arrays.copyOf(a, size);
            }
            for (int i = 0; i < size; i++) {
                int idx = (head + i) % capacity;
                @SuppressWarnings("unchecked")
                T1 element = (T1) buffer[idx];
                a[i] = element;
            }
            if (a.length > size) {
                a[size] = null;
            }
            return a;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Remove by object not supported");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (T e : c) {
            add(e);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("RemoveAll not supported");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("RetainAll not supported");
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            Arrays.fill(buffer, null);
            head = 0;
            size = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Возвращает сегмент записей по индексам [from, to)
     */
    public List<T> getSegment(int from, int to) {
        lock.readLock().lock();
        try {

            if (from < 0 || from >= size) {
                return Collections.emptyList();
            }


            int end = Math.min(to, size);

            if (from >= end) {
                return Collections.emptyList();
            }

            List<T> result = new ArrayList<>(end - from);
            for (int i = from; i < end; i++) {
                @SuppressWarnings("unchecked")
                T item = (T) buffer[(head + i) % capacity];
                result.add(item);
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int capacity() {
        return capacity;
    }

    /**
     * Итератор без копирования - держит read lock
     */
    private class BoundedLogIterator implements Iterator<T> {
        private int cursor = 0;
        private final int snapshotSize;
        private final int snapshotHead;

        BoundedLogIterator() {
            lock.readLock().lock();
            try {
                this.snapshotSize = size;
                this.snapshotHead = head;
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public boolean hasNext() {
            return cursor < snapshotSize;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            lock.readLock().lock();
            try {
                int idx = (snapshotHead + cursor) % capacity;
                cursor++;
                return (T) buffer[idx];
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
    }
}