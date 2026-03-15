package gui;

import javax.swing.UIManager;

public class UIManagerLocalization {

  public static void apply() {
    UIManager.put("OptionPane.yesButtonText", "Да");
    UIManager.put("OptionPane.noButtonText", "Нет");
    UIManager.put("OptionPane.cancelButtonText", "Отмена");
    UIManager.put("OptionPane.okButtonText", "OK");

    UIManager.put("FileChooser.openButtonText", "Открыть");
    UIManager.put("FileChooser.saveButtonText", "Сохранить");
    UIManager.put("FileChooser.cancelButtonText", "Отмена");
    UIManager.put("FileChooser.directoryOpenButtonText", "Открыть папку");
    UIManager.put("FileChooser.updateButtonText", "Обновить");
    UIManager.put("FileChooser.helpButtonText", "Справка");
    UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
    UIManager.put("FileChooser.listViewButtonToolTipText", "Список");

    UIManager.put("FileChooser.desktopName", "Рабочий стол");
    UIManager.put("FileChooser.fileNameHeaderText", "Имя файла");
    UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
    UIManager.put("FileChooser.fileTypeHeaderText", "Тип");
    UIManager.put("FileChooser.fileDateHeaderText", "Дата изменения");
    UIManager.put("FileChooser.lookInLabelText", "Папка");
    UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
    UIManager.put("FileChooser.newFolderErrorText", "Ошибка создания папки");
    UIManager.put("FileChooser.newFolderErrorSeparator", ":");
    UIManager.put("FileChooser.fileNameLabelText", "Имя файла:");
    UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов:");
    UIManager.put("FileChooser.saveInLabelText", "Сохранить в папке:");
    UIManager.put("FileChooser.folderNameLabelText", "Имя папки:");

    UIManager.put("ColorChooser.okText", "OK");
    UIManager.put("ColorChooser.cancelText", "Отмена");
    UIManager.put("ColorChooser.resetText", "Сброс");
    UIManager.put("ColorChooser.sampleText", "Образец текста");

    UIManager.put("ColorChooser.swatchesNameText", "Образцы");
    UIManager.put("ColorChooser.hsvNameText", "HSV");
    UIManager.put("ColorChooser.hslNameText", "HSL");
    UIManager.put("ColorChooser.rgbNameText", "RGB");
    UIManager.put("ColorChooser.cmykNameText", "CMYK");

    UIManager.put("OptionPane.inputDialogTitle", "Ввод");
    UIManager.put("OptionPane.messageDialogTitle", "Сообщение");

    UIManager.put("InternalFrame.closeButtonToolTip", "Закрыть");
    UIManager.put("InternalFrame.iconButtonToolTip", "Свернуть");
    UIManager.put("InternalFrame.maxButtonToolTip", "Развернуть");
    UIManager.put("InternalFrame.restoreButtonToolTip", "Восстановить");
    UIManager.put("InternalFrame.moveButtonToolTip", "Переместить");
    UIManager.put("InternalFrame.sizeButtonToolTip", "Размер");

    UIManager.put("ProgressMonitor.progressText", "Выполняется...");
  }
}