package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LanguageManager {
  private final List<LocalizableComponent> components = new ArrayList<>();

  public void registerComponent(LocalizableComponent component) {
    components.add(component);
  }

  public void switchLanguage(ResourceBundle rb, Frame frame) {
    for (LocalizableComponent comp : components) {
      comp.updateText(rb);
    }
    applyUIManagerStrings(rb);
    SwingUtilities.updateComponentTreeUI(frame);
  }

  private void applyUIManagerStrings(ResourceBundle rb) {
    UIManager.put("InternalFrame.closeButtonToolTip", rb.getString("InternalFrame.closeButtonToolTip"));
    UIManager.put("InternalFrame.iconButtonToolTip", rb.getString("InternalFrame.iconButtonToolTip"));
    UIManager.put("InternalFrame.maxButtonToolTip", rb.getString("InternalFrame.maxButtonToolTip"));
    UIManager.put("InternalFrame.restoreButtonToolTip", rb.getString("InternalFrame.restoreButtonToolTip"));
    UIManager.put("InternalFrameTitlePane.closeButtonAccessibleName", rb.getString("InternalFrameTitlePane.closeButtonAccessibleName"));
    UIManager.put("InternalFrameTitlePane.closeButtonText", rb.getString("InternalFrameTitlePane.closeButtonText"));
    UIManager.put("InternalFrameTitlePane.iconifyButtonAccessibleName", rb.getString("InternalFrameTitlePane.iconifyButtonAccessibleName"));
    UIManager.put("InternalFrameTitlePane.maximizeButtonAccessibleName", rb.getString("InternalFrameTitlePane.maximizeButtonAccessibleName"));
    UIManager.put("InternalFrameTitlePane.maximizeButtonText", rb.getString("InternalFrameTitlePane.maximizeButtonText"));
    UIManager.put("InternalFrameTitlePane.minimizeButtonText", rb.getString("InternalFrameTitlePane.minimizeButtonText"));
    UIManager.put("InternalFrameTitlePane.moveButtonText", rb.getString("InternalFrameTitlePane.moveButtonText"));
    UIManager.put("InternalFrameTitlePane.restoreButtonText", rb.getString("InternalFrameTitlePane.restoreButtonText"));
    UIManager.put("InternalFrameTitlePane.sizeButtonText", rb.getString("InternalFrameTitlePane.sizeButtonText"));
    UIManager.put("OptionPane.yesButtonText", rb.getString("OptionPane.yesButtonText"));
    UIManager.put("OptionPane.noButtonText", rb.getString("OptionPane.noButtonText"));
  }
}
