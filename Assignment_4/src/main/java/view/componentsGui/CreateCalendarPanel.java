package view.componentsGui;

import controller.EventController;
import view.utilsGui.CommandGenerator;
import view.utilsGui.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Panel for creating a new calendar
 */
public class CreateCalendarPanel extends AbstractCalendarPanel {

  private CommandGenerator commandGenerator;
  private JTextField nameField;
  private JComboBox<String> timezoneCombo;


  /**
   * Constructor
   *
   * @param controller The event controller
   * @param commandPreviewArea The shared command preview area
   * @param commandGenerator The command generator
   */
  public CreateCalendarPanel(EventController controller, JTextArea commandPreviewArea,
                             CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;

  }

  @Override
  protected void initializeUI() {
    // Calendar Details Section
    JPanel detailsPanel = createSectionPanel("Calendar Details", 2, 1);

    // Calendar Name field
    nameField = new JTextField();
    applySize(nameField, UIUtils.getFieldSize());
    addFormField("Calendar Name:", nameField);
    detailsPanel.add(createRow("Calendar Name:", nameField));

    // Timezone field
    timezoneCombo = UIUtils.createTimeZoneComboBox();
    applySize(timezoneCombo, UIUtils.getFieldSize());
    addFormField("Time Zone (area/location):", timezoneCombo);
    detailsPanel.add(createRow("Time Zone:", timezoneCombo));

    add(detailsPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));

    // Create calendar button
    JButton createButton = new JButton("Create Calendar");
    applySize(createButton, UIUtils.getButtonSize());
    createButton.addActionListener(e -> executeAction());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(createButton);
    add(buttonPanel);
  }

  @Override
  protected String generateCommand() {
    Map<String, String> values = getFormValues();
    System.out.println(values);
    return commandGenerator.createCalendarCommand(values);
  }

  @Override
  protected String getButtonText() {
    return "Create Calendar";
  }
}