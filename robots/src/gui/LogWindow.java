package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener, LocalizableComponent {
  private static final int VISIBLE_COUNT = 5;
  private LogWindowSource m_logSource;
  private TextArea m_logContent;
  private int scrollOffset;
  private JScrollBar scrollBar;

  public LogWindow(LogWindowSource logSource) {
    super("", true, true, true, true);
    m_logSource = logSource;
    m_logSource.registerListener(this);
    m_logContent = new TextArea("");
    m_logContent.setSize(200, 500);
    scrollOffset = 0;

    scrollBar = new JScrollBar(JScrollBar.VERTICAL);
    scrollBar.addAdjustmentListener(new AdjustmentListener() {
      @Override
      public void adjustmentValueChanged(AdjustmentEvent e) {
          scrollOffset = e.getValue();
          updateLogContent();
      }
    });

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(m_logContent, BorderLayout.CENTER);
    panel.add(scrollBar, BorderLayout.EAST);
    getContentPane().add(panel);
    pack();

    m_logContent.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        int total = m_logSource.size();
        int maxOffset = Math.max(0, total - VISIBLE_COUNT);
        if (e.getWheelRotation() > 0) {
          scrollOffset = Math.min(scrollOffset + 1, maxOffset);
        } else {
          scrollOffset = Math.max(scrollOffset - 1, 0);
        }
        scrollBar.setValue(scrollOffset);
        updateLogContent();
        e.consume();
      }
    });

    updateLogContent();
  }

  private void updateScrollBar() {
    int total = m_logSource.size();
    int maxOffset = Math.max(0, total - VISIBLE_COUNT);
    scrollBar.setMinimum(0);
    scrollBar.setMaximum(maxOffset + VISIBLE_COUNT);
    scrollBar.setVisibleAmount(VISIBLE_COUNT);
    scrollBar.setValue(Math.min(scrollOffset, maxOffset));
  }

  private void updateLogContent() {
    int total = m_logSource.size();
    int startFrom = Math.max(0, total - VISIBLE_COUNT - scrollOffset);
    int count = Math.min(VISIBLE_COUNT, total - startFrom);
    List<LogEntry> entries = (List<LogEntry>) m_logSource.range(startFrom, count);

    StringBuilder content = new StringBuilder();
    for (LogEntry entry : entries) {
      content.append(entry.getMessage()).append("\n");
    }
    m_logContent.setText(content.toString());
    m_logContent.invalidate();
    updateScrollBar();
  }

  @Override
  public void onLogChanged() {
    EventQueue.invokeLater(() -> {
      scrollOffset = 0;
      updateLogContent();
    });
  }

  @Override
  public void updateText(ResourceBundle rb) {
    setTitle(rb.getString("logWindow"));
  }
}