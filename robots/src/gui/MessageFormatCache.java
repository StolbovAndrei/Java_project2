package gui;

import java.text.MessageFormat;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class MessageFormatCache {
  private static final Map<String, MessageFormat> cache = new ConcurrentHashMap<>();

  public static String getFormatted(ResourceBundle rb, String key, Object... args) {
    String pattern = rb.getString(key);
    MessageFormat formatter = cache.computeIfAbsent(pattern, MessageFormat::new);
    return formatter.format(args);
  }
}