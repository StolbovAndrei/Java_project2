package gui;

import javax.swing.*;
import java.util.ResourceBundle;

public class LocalizedJMenu extends JMenu implements LocalizableComponent {
  private final String key;

  public LocalizedJMenu(String key, ResourceBundle rb) {
    this.key = key;
    updateText(rb);
  }

  @Override
  public void updateText(ResourceBundle rb) {
    setText(rb.getString(key));
  }
}
