package gui;

import log.Logger;
import javax.swing.*;

public class RobotsProgram
{
  public static void main(String[] args)
  {
    UIManager.put("OptionPane.okButtonText", "OK");
    UIManager.put("OptionPane.yesButtonText", "Да");
    UIManager.put("OptionPane.noButtonText", "Нет");
    UIManager.put("OptionPane.cancelButtonText", "Отмена");
    UIManager.put("OptionPane.titleText", "Подтверждение");
    UIManager.put("OptionPane.messageDialogTitle", "Подтверждение действия");

    try
    {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    }
    catch (Exception e)
    {
      Logger.error("Failed to set look and feel: " + e.getMessage());
    }

    SwingUtilities.invokeLater(() -> {
      MainApplicationFrame frame = new MainApplicationFrame();
      frame.setVisible(true);
    });
  }
}