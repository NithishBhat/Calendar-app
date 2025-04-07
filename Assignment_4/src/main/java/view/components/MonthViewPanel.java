package view.components;

import controller.EventController;
import view.utils.CommandGenerator;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for the Month View - currently a placeholder
 * that demonstrates the component structure
 */
public class MonthViewPanel extends AbstractCalendarPanel {

  /**
   * Constructor
   *
   * @param controller The event controller
   * @param commandPreviewArea The shared command preview area
   * @param commandGenerator The command generator
   */
  public MonthViewPanel(EventController controller, JTextArea commandPreviewArea,
                        CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
  }

  @Override
  protected void initializeUI() {
    // Simple placeholder for now
    JLabel placeholderLabel = new JLabel("Month View Coming Soon...");
    placeholderLabel.setFont(new Font(placeholderLabel.getFont().getName(), Font.BOLD, 14));

    // Center the label
    JPanel centerPanel = new JPanel(new GridBagLayout());
    centerPanel.add(placeholderLabel);

    add(centerPanel);
  }

  @Override
  protected String generateCommand() {
    // No command generated for this panel currently
    return "";
  }

  @Override
  protected String getButtonText() {
    return "Refresh View";
  }
}