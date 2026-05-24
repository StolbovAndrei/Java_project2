package log;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class LogBufferTest {

  @Test
  void testAppendAndSize() {
    LogBuffer buffer = new LogBuffer(5);
    assertEquals(0, buffer.size());
    buffer.append(LogLevel.Debug, "msg1");
    assertEquals(1, buffer.size());
  }

  @Test
  void testCapacityOverflow() {
    LogBuffer buffer = new LogBuffer(3);
    buffer.append(LogLevel.Info, "A");
    buffer.append(LogLevel.Info, "B");
    buffer.append(LogLevel.Info, "C");
    buffer.append(LogLevel.Info, "D");
    buffer.append(LogLevel.Info, "E");

    assertEquals(3, buffer.size());
    List<LogEntry> all = buffer.all();
    assertEquals(3, all.size());
    assertEquals("C", all.get(0).getMessage());
    assertEquals("D", all.get(1).getMessage());
    assertEquals("E", all.get(2).getMessage());
  }

  @Test
  void testRange() {
    LogBuffer buffer = new LogBuffer(5);
    buffer.append(LogLevel.Debug, "0");
    buffer.append(LogLevel.Debug, "1");
    buffer.append(LogLevel.Debug, "2");
    buffer.append(LogLevel.Debug, "3");
    buffer.append(LogLevel.Debug, "4");

    List<LogEntry> slice = buffer.range(0, 3);
    assertEquals(3, slice.size());
    assertEquals("0", slice.get(0).getMessage());
    assertEquals("1", slice.get(1).getMessage());
    assertEquals("2", slice.get(2).getMessage());

    slice = buffer.range(2, 3);
    assertEquals(3, slice.size());
    assertEquals("2", slice.get(0).getMessage());
    assertEquals("3", slice.get(1).getMessage());
    assertEquals("4", slice.get(2).getMessage());

    slice = buffer.range(0, 10);
    assertEquals(5, slice.size());

    slice = buffer.range(10, 2);
    assertTrue(slice.isEmpty());
  }

  @Test
  void testConcurrentAccess() throws InterruptedException {
    LogBuffer buffer = new LogBuffer(100);
    int numWriters = 4;
    int writesPerWriter = 1000;
    CountDownLatch latch = new CountDownLatch(numWriters);
    AtomicInteger errors = new AtomicInteger(0);

    for (int i = 0; i < numWriters; i++) {
      final int writerId = i;
      new Thread(() -> {
        try {
          for (int j = 0; j < writesPerWriter; j++) {
            buffer.append(LogLevel.Debug, "writer" + writerId + "-" + j);
          }
        } catch (Exception e) {
          errors.incrementAndGet();
        } finally {
          latch.countDown();
        }
      }).start();
    }

    Thread reader = new Thread(() -> {
      while (latch.getCount() > 0) {
        try {
          List<LogEntry> all = buffer.all();
          if (all.size() > 100) errors.incrementAndGet();
        } catch (Exception e) {
          errors.incrementAndGet();
        }
      }
    });
    reader.start();

    latch.await();
    reader.join(1000);
    assertEquals(0, errors.get());
    assertTrue(buffer.size() <= 100);
  }
}