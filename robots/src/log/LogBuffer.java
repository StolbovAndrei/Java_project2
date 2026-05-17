package log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LogBuffer {
  private final LogEntry[] entries;
  private final int capacity;
  private int head;
  private int tail;
  private int size;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public LogBuffer(int capacity) {
    this.capacity = capacity;
    this.entries = new LogEntry[capacity];
    this.head = 0;
    this.tail = 0;
    this.size = 0;
  }

  public void append(LogLevel level, String message) {
    LogEntry entry = new LogEntry(level, message);
    lock.writeLock().lock();
    try {
      if (size == capacity) {
        entries[tail] = entry;
        tail = (tail + 1) % capacity;
        head = (head + 1) % capacity;
      } else {
        entries[tail] = entry;
        tail = (tail + 1) % capacity;
        size++;
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  public int size() {
    lock.readLock().lock();
    try {
      return size;
    } finally {
      lock.readLock().unlock();
    }
  }

  public List<LogEntry> range(int startFrom, int count) {
    lock.readLock().lock();
    try {
      if (startFrom < 0 || startFrom >= size) {
        return new ArrayList<>();
      }
      int actualCount = Math.min(count, size - startFrom);
      List<LogEntry> result = new ArrayList<>(actualCount);
      for (int i = 0; i < actualCount; i++) {
        int index = (head + startFrom + i) % capacity;
        result.add(entries[index]);
      }
      return result;
    } finally {
      lock.readLock().unlock();
    }
  }

  public List<LogEntry> all() {
    return range(0, size());
  }
}