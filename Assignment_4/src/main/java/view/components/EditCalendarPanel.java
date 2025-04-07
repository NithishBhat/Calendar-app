package view.components;

import controller.EventController;
import model.CalenderModel;
import view.CalendarUIListener;
import view.utils.CommandGenerator;
import view.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Panel for editing an existing calendar
 */
public class EditCalendarPanel extends AbstractCalendarPanel implements CalendarUIListener {

  private CommandGenerator commandGenerator;
  private JComboBox<String> calendarNameSelector;
  private JComboBox<String> propertyCombo;
  private JPanel valueCardPanel;
  private JTextField nameField;
  private JComboBox<String> timezoneCombo;

  /**
   * Constructor
   *
   * @param controller The event controller
   * @param commandPreviewArea The shared command preview area
   * @param commandGenerator The command generator
   */
  public EditCalendarPanel(EventController controller, JTextArea commandPreviewArea,
                           CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;

    // Register as a listener to receive calendar updates
    controller.getEventHandler().addUIListener(this);
  }

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

  @Override
  protected String getButtonText() {
    return "Run Edit Calendar";
  }

  /**
   * Update the calendar selector with current calendars
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

  @Override
  public void onCalendarChanged(String calendarName) {
    // No action needed for this panel
  }

  @Override
  public void onCommandOutput(String output) {
    // No action needed for this panel
  }

  @Override
  public void onEventsChanged(model.CalenderCollection calenderCollection) {
    // No action needed for this panel
  }

  @Override
  public void onCalendarListChanged() {
    updateCalendarList();
  }
}