package gui;

import log.Logger;

import javax.swing.*;

public class RobotsProgram
{
  public static void main(String[] args)
  {
    try
    {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
      UIManager. put("OptionPane.yesButtonText", "Да");
      UIManager.put("OptionPane.noButtonText", "Нет");
      UIManager.put("OptionPane.cancelButtonText", "Отмена");
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