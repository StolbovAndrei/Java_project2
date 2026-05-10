package gui;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageFormatCacheTest {

  private static ResourceBundle rb;
  private static final int ITERATIONS = 100000;
  private static final String KEY = "messageInLog";

  @BeforeAll
  static void setup() {
    Locale locale = new Locale("ru", "RU");
    rb = ResourceBundle.getBundle("resources.ComponentsMenu_ru", locale);
  }

  @Test
  void testFormattingCorrectness() {
    String result = MessageFormatCache.getFormatted(rb, KEY, 1, "Test");
    assertEquals("Запись в лог 1 : Test", result);
  }

  @Test
  void benchmarkFormatting() {
    String pattern = rb.getString(KEY);
    String formatterPattern = pattern.replace("{0}", "%1$s").replace("{1}", "%2$s");
    Object[] args = {99, "Performance Test"};

    long startFormatter = System.nanoTime();
    for (int i = 0; i < ITERATIONS; i++) {
      String.format(formatterPattern, args[0], args[1]);
    }
    long endFormatter = System.nanoTime();
    long durationFormatter = (endFormatter - startFormatter) / 1_000_000;

    long startNoCache = System.nanoTime();
    for (int i = 0; i < ITERATIONS; i++) {
      MessageFormat.format(pattern, args);
    }
    long endNoCache = System.nanoTime();
    long durationNoCache = (endNoCache - startNoCache) / 1_000_000;

    long startCache = System.nanoTime();
    for (int i = 0; i < ITERATIONS; i++) {
      MessageFormatCache.getFormatted(rb, KEY, args);
    }
    long endCache = System.nanoTime();
    long durationCache = (endCache - startCache) / 1_000_000;

    System.out.println("Formatter: " + durationFormatter + " ms");
    System.out.println("MessageFormat without cache: " + durationNoCache + " ms");
    System.out.println("MessageFormat with cache: " + durationCache + " ms");
  }
}