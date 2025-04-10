package view.componentsgui;

import controller.EventController;
import model.CalenderModel;
import view.utilsgui.CommandGenerator;
import view.utilsgui.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;

/**
 * Panel for editing an existing calendar in the application.
 * Provides UI components for selecting a calendar and modifying its properties.
 * Extends AbstractCalendarPanel to inherit common panel functionality and
 * implements CalendarUIListener to receive notifications about changes in the model.
 */
public class EditCalendarPanel extends AbstractCalendarPanel implements CalendarUIListener {

  /** Generator for creating formatted command strings. */
  private CommandGenerator commandGenerator;

  /** Dropdown for selecting the calendar to edit. */
  private JComboBox<String> calendarNameSelector;

  /** Dropdown for selecting which property to edit. */
  private JComboBox<String> propertyCombo;

  /** Panel containing different input components based on selected property. */
  private JPanel valueCardPanel;

  /** Text field for editing the calendar name. */
  private JTextField nameField;

  /** Dropdown for selecting a timezone. */
  private JComboBox<String> timezoneCombo;

  /**
   * Constructor for the edit calendar panel.
   *
   * @param controller The event controller that processes commands
   * @param commandPreviewArea The shared text area for displaying command output
   * @param commandGenerator The generator for creating formatted command strings
   */
  public EditCalendarPanel(EventController controller, JTextArea commandPreviewArea,
                           CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;

    // Register as a listener to receive calendar updates
    controller.getEventHandler().addUIListener(this);
  }

  /**
   * Initializes the UI components for this panel.
   * Creates and arranges fields for calendar selection, property selection,
   * and value input, and adds a button to submit the edit request.
   */
  @Override
  protected void initializeUI() {
    // Calendar Selection Section
    JPanel selectPanel = createSectionPanel("Select Calendar", 1, 1);

    calendarNameSelector = new JComboBox<>();
    updateCalendarList();
    applySize(calendarNameSelector, UIUtils.getFieldSize());
    addFormField("Calendar to Edit", calendarNameSelector);
    selectPanel.add(createRow("Calendar to Edit:", calendarNameSelector));

    add(selectPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));

    // Property Section
    JPanel propertyPanel = createSectionPanel("Edit Property", 1, 1);

    propertyCombo = new JComboBox<>(new String[]{"name", "timezone"});
    applySize(propertyCombo, UIUtils.getFieldSize());
    addFormField("Property to Edit", propertyCombo);
    propertyPanel.add(createRow("Property to Edit:", propertyCombo));

    add(propertyPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));

    // Value Section with CardLayout
    valueCardPanel = new JPanel(new CardLayout());
    valueCardPanel.setBorder(BorderFactory.createTitledBorder("New Value"));

    // Name value input
    nameField = new JTextField();
    applySize(nameField, UIUtils.getFieldSize());
    addFormField("NameValue", nameField);
    JPanel nameCard = new JPanel(new FlowLayout(FlowLayout.LEFT));
    nameCard.add(nameField);

    // Timezone combo input
    timezoneCombo = UIUtils.createTimeZoneComboBox();
    applySize(timezoneCombo, UIUtils.getFieldSize());
    addFormField("TimezoneValue", timezoneCombo);
    JPanel tzCard = new JPanel(new FlowLayout(FlowLayout.LEFT));
    tzCard.add(timezoneCombo);

    valueCardPanel.add(nameCard, "name");
    valueCardPanel.add(tzCard, "timezone");

    propertyCombo.addActionListener(e -> {
      CardLayout cl = (CardLayout) valueCardPanel.getLayout();
      cl.show(valueCardPanel, propertyCombo.getSelectedItem().toString());
    });

    // Show name by default
    ((CardLayout) valueCardPanel.getLayout()).show(valueCardPanel, "name");
    add(valueCardPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));

    // Run button
    JButton runButton = new JButton(getButtonText());
    applySize(runButton, UIUtils.getButtonSize());
    runButton.addActionListener(e -> executeAction());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(runButton);
    add(buttonPanel);
  }

  /**
   * Generates a command string for editing a calendar based on the current field values.
   * Collects the input values and uses the command generator to format them into a valid command.
   *
   * @return A formatted command string for editing a calendar with the specified changes
   */
  @Override
  protected String generateCommand() {
    Map<String, String> values = getFormValues();

    // Add specific values from the card layout selection
    String property = propertyCombo.getSelectedItem().toString();
    String newValue = property.equals("name")
            ? nameField.getText()
            : timezoneCombo.getSelectedItem().toString();

    values.put("Calendar Name:", calendarNameSelector.getSelectedItem().toString());
    values.put("Property to Edit:", property);
    values.put("New Value:", newValue);

    return commandGenerator.editCalendarCommand(values);
  }

  /**
   * Returns the text to display on this panel's action button.
   *
   * @return The button text ("Run Edit Calendar")
   */
  @Override
  protected String getButtonText() {
    return "Run Edit Calendar";
  }

  /**
   * Updates the calendar selector dropdown with the current list of calendars.
   * Preserves the current selection if possible.
   */
  private void updateCalendarList() {
    SwingUtilities.invokeLater(() -> {
      if (calendarNameSelector != null) {
        // Save current selection
        Object currentSelection = calendarNameSelector.getSelectedItem();

        // Update the list
        calendarNameSelector.removeAllItems();

        // Get calendars from controller
        if (controller != null) {
          Map<String, CalenderModel> calendars =
                  controller.getEventHandler().getCalenderCollection().getCalenderCollection();

          // Add each calendar to the selector
          for (String calName : calendars.keySet()) {
            calendarNameSelector.addItem(calName);
          }

          // Restore selection if it still exists
          if (currentSelection != null &&
                  calendars.containsKey(currentSelection.toString())) {
            calendarNameSelector.setSelectedItem(currentSelection);
          }
        }
      }
    });
  }

  // CalendarUIListener implementation

  /**
   * Called when the current calendar changes.
   * This panel does not need to take any action for this event.
   *
   * @param calendarName The name of the newly selected calendar
   */
  @Override
  public void onCalendarChanged(String calendarName) {
    // No action needed for this panel
  }

  /**
   * Called when a command produces output.
   * This panel does not need to take any action for this event.
   *
   * @param output The command output text
   */
  @Override
  public void onCommandOutput(String output) {
    // No action needed for this panel
  }

  /**
   * Called when the events in any calendar change.
   * This panel does not need to take any action for this event.
   *
   * @param calenderCollection The updated calendar collection
   */
  @Override
  public void onEventsChanged(model.CalenderCollection calenderCollection) {
    // No action needed for this panel
  }

  /**
   * Called when the list of available calendars changes.
   * Updates the calendar selector dropdown in response.
   */
  @Override
  public void onCalendarListChanged() {
    updateCalendarList();
  }
}