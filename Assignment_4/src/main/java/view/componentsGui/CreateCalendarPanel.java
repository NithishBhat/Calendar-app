package view.componentsGui;

import controller.EventController;
import view.utilsGui.CommandGenerator;
import view.utilsGui.UIUtils;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;

/**
 * Panel for creating a new calendar in the application.
 * Provides UI components for entering calendar details and submitting the creation request.
 * Extends AbstractCalendarPanel to inherit common panel functionality.
 */
public class CreateCalendarPanel extends AbstractCalendarPanel {

  /** Generator for creating formatted command strings. */
  private CommandGenerator commandGenerator;

  /** Text field for entering the calendar name. */
  private JTextField nameField;

  /** Dropdown for selecting the calendar's timezone. */
  private JComboBox<String> timezoneCombo;

  /**
   * Constructor for the create calendar panel.
   *
   * @param controller The event controller that processes commands
   * @param commandPreviewArea The shared text area for displaying command output
   * @param commandGenerator The generator for creating formatted command strings
   */
  public CreateCalendarPanel(EventController controller, JTextArea commandPreviewArea,
                             CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
  }

  /**
   * Initializes the UI components for this panel.
   * Creates and arranges fields for calendar name and timezone selection,
   * and adds a button to submit the creation request.
   */
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

  /**
   * Generates a command string for creating a calendar based on the current field values.
   * Collects the input values and uses the command generator to format them into a valid command.
   *
   * @return A formatted command string for creating a calendar with the specified details
   */
  @Override
  protected String generateCommand() {
    Map<String, String> values = getFormValues();
    return commandGenerator.createCalendarCommand(values);
  }

  /**
   * Returns the text to display on this panel's action button.
   *
   * @return The button text ("Create Calendar")
   */
  @Override
  protected String getButtonText() {
    return "Create Calendar";
  }
}