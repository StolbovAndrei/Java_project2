package log;

import java.util.ArrayList;

public class LogWindowSource {
  private final LogBuffer buffer;
  private final ArrayList<LogChangeListener> m_listeners;
  private volatile LogChangeListener[] m_activeListeners;

  public LogWindowSource(int iQueueLength) {
    buffer = new LogBuffer(iQueueLength);
    m_listeners = new ArrayList<LogChangeListener>();
  }

  public void registerListener(LogChangeListener listener) {
    synchronized (m_listeners) {
      m_listeners.add(listener);
      m_activeListeners = null;
    }
  }

  public void unregisterListener(LogChangeListener listener) {
    synchronized (m_listeners) {
      m_listeners.remove(listener);
      m_activeListeners = null;
    }
  }

  public void append(LogLevel logLevel, String strMessage) {
    buffer.append(logLevel, strMessage);
    LogChangeListener[] activeListeners = m_activeListeners;
    if (activeListeners == null) {
      synchronized (m_listeners) {
        if (m_activeListeners == null) {
          activeListeners = m_listeners.toArray(new LogChangeListener[0]);
          m_activeListeners = activeListeners;
        }
      }
    }
    for (LogChangeListener listener : activeListeners) {
      listener.onLogChanged();
    }
  }

  public int size() {
    return buffer.size();
  }

  public Iterable<LogEntry> range(int startFrom, int count) {
    return buffer.range(startFrom, count);
  }

  public Iterable<LogEntry> all() {
    return buffer.all();
  }
}