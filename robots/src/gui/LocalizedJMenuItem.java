package gui;

import javax.swing.*;
import java.util.ResourceBundle;

public class LocalizedJMenuItem extends JMenuItem implements LocalizableComponent {
  private final String key;

  public LocalizedJMenuItem(String key, ResourceBundle rb) {
    this.key = key;
    updateText(rb);
  }

  @Override
  public void updateText(ResourceBundle rb) {
    setText(rb.getString(key));
  }
}
