package view.componentsGui;

import controller.EventController;
import view.utilsGui.CommandGenerator;
import view.utilsGui.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;

/**
 * Panel for creating new events in the calendar application.
 * Provides UI components for entering event details including name, time, location,
 * and recurrence settings. Extends AbstractCalendarPanel to inherit common panel functionality.
 */
public class CreateEventPanel extends AbstractCalendarPanel {

  /**
   * Generator for creating formatted command strings.
   */
  private CommandGenerator commandGenerator;




  private LocalDate selectedDate;

  /**
   * Constructor for the create event panel.
   *
   * @param controller         The event controller that processes commands
   * @param commandPreviewArea The shared text area for displaying command output
   * @param commandGenerator   The generator for creating formatted command strings
   * @param selectedDate       The date initially selected (if any),
   *                          used to pre-populate date fields
   */
  public CreateEventPanel(EventController controller, JTextArea commandPreviewArea,
                          CommandGenerator commandGenerator, LocalDate selectedDate) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
    this.selectedDate = selectedDate;
  }

  /**
   * Initializes the UI components for this panel.
   * Creates and arranges fields for event details including basic information,
   * date and time, settings, and recurrence options. Also adds a button to submit
   * the creation request.
   */
  @Override
  protected void initializeUI() {

    JTextField repeatUntilDateField;
    JTextField eventNameField;
    JTextField locationField;
    JTextArea descriptionArea;
    JTextField startDateField;
    JTextField startTimeField;
    JTextField endDateField;
    JTextField endTimeField;
    JCheckBox allDayCB;
    JCheckBox autoDeclineCB;
    JCheckBox recurringCB;
    JCheckBox[] dayCheckboxes;
    JRadioButton repeatNTimesRB;
    JTextField repeatCountField;
    JRadioButton repeatUntilRB;
    JComboBox<String> privacyCombo;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(5, 5, 5, 5));

    // --- Basic Event Info Section ---
    JPanel basicInfoPanel = createSectionPanel("Event Information", 3, 1);

    // Event Name
    eventNameField = new JTextField();
    addFormField("Event Name", eventNameField);
    basicInfoPanel.add(createRow("Event Name:", eventNameField));

    // Location
    locationField = new JTextField();
    addFormField("Location", locationField);
    basicInfoPanel.add(createRow("Location:", locationField));

    // Description
    descriptionArea = new JTextArea(3, 20);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
    basicInfoPanel.add(createRow("Description:", descriptionScroll));
    addFormField("Description", descriptionArea);

    add(basicInfoPanel);
    add(Box.createRigidArea(new Dimension(0, 5)));

    // --- Date & Time Section ---
    JPanel dateTimePanel = createSectionPanel("Date & Time", 2, 1);

    // Start Date & Time
    JPanel startPanel = new JPanel(new BorderLayout(5, 0));
    JLabel startLabel = new JLabel("Start:");
    applySize(startLabel, new Dimension(80, 25));
    startPanel.add(startLabel, BorderLayout.WEST);

    JPanel startFieldsPanel = new JPanel(new BorderLayout(5, 0));
    startDateField = (selectedDate == null ? new JTextField(LocalDate.now().
            toString()) : new JTextField(selectedDate.toString()));
    applySize(startDateField, new Dimension(110, 25));
    addFormField("StartDate", startDateField);

    startTimeField = new JTextField("09:00");
    applySize(startTimeField, new Dimension(70, 25));
    addFormField("StartTime", startTimeField);

    startFieldsPanel.add(startDateField, BorderLayout.CENTER);
    startFieldsPanel.add(startTimeField, BorderLayout.EAST);
    startPanel.add(startFieldsPanel, BorderLayout.CENTER);
    dateTimePanel.add(startPanel);

    // End Date & Time
    JPanel endPanel = new JPanel(new BorderLayout(5, 0));
    JLabel endLabel = new JLabel("End:");
    applySize(endLabel, new Dimension(80, 25));
    endPanel.add(endLabel, BorderLayout.WEST);

    JPanel endFieldsPanel = new JPanel(new BorderLayout(5, 0));
    endDateField = (selectedDate == null ? new JTextField(LocalDate.now().toString())
            : new JTextField(selectedDate.toString()));
    applySize(endDateField, new Dimension(110, 25));
    addFormField("EndDate", endDateField);

    endTimeField = new JTextField("10:00");
    applySize(endTimeField, new Dimension(70, 25));
    addFormField("EndTime", endTimeField);

    endFieldsPanel.add(endDateField, BorderLayout.CENTER);
    endFieldsPanel.add(endTimeField, BorderLayout.EAST);
    endPanel.add(endFieldsPanel, BorderLayout.CENTER);
    dateTimePanel.add(endPanel);

    add(dateTimePanel);
    add(Box.createRigidArea(new Dimension(0, 5)));

    // --- Settings Section ---
    JPanel settingsPanel = new JPanel();
    settingsPanel.setBorder(BorderFactory.createTitledBorder("Event Settings"));
    settingsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

    // Privacy
    JPanel privacyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    privacyPanel.add(new JLabel("Privacy:"));
    privacyCombo = new JComboBox<>(new String[]{"Public", "Private"});
    applySize(privacyCombo, new Dimension(90, 25));
    privacyPanel.add(privacyCombo);
    addFormField("Privacy", privacyCombo);
    settingsPanel.add(privacyPanel);

    // All Day
    allDayCB = new JCheckBox("All Day");
    addFormField("AllDay", allDayCB);
    settingsPanel.add(allDayCB);

    // Auto Decline
    autoDeclineCB = new JCheckBox("Auto Decline");
    addFormField("AutoDecline", autoDeclineCB);
    settingsPanel.add(autoDeclineCB);

    add(settingsPanel);
    add(Box.createRigidArea(new Dimension(0, 5)));

    // --- Recurring Section ---
    JPanel recurringPanel = new JPanel();
    recurringPanel.setBorder(BorderFactory.createTitledBorder("Recurrence"));
    recurringPanel.setLayout(new BoxLayout(recurringPanel, BoxLayout.Y_AXIS));

    // Recurring checkbox
    JPanel recurringRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    recurringCB = new JCheckBox("Recurring Event");
    addFormField("Recurring", recurringCB);
    recurringRow.add(recurringCB);
    recurringPanel.add(recurringRow);

    // Repeat days
    JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    daysPanel.add(new JLabel("Repeat on:"));

    // Clear mapping between UI labels and command codes
    String[][] dayMappings = {
            {"Mon", "M"}, {"Tue", "T"}, {"Wed", "W"},
            {"Thu", "R"}, {"Fri", "F"}, {"Sat", "S"}, {"Sun", "U"}
    };

    dayCheckboxes = new JCheckBox[dayMappings.length];
    for (int i = 0; i < dayMappings.length; i++) {
      String label = dayMappings[i][0];
      String code = dayMappings[i][1];
      JCheckBox cb = new JCheckBox(label);
      cb.setMargin(new Insets(0, 3, 0, 3));
      dayCheckboxes[i] = cb;
      addFormField("RepeatDay_" + code, cb);
      daysPanel.add(cb);
    }
    recurringPanel.add(daysPanel);

    // Repeat options
    JPanel repeatOptionsPanel = new JPanel(new GridLayout(2, 1));

    // Repeat N times option
    JPanel repeatTimesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    repeatNTimesRB = new JRadioButton("Repeat for");
    addFormField("RepeatNTimesRB", repeatNTimesRB);
    repeatTimesPanel.add(repeatNTimesRB);

    repeatCountField = new JTextField("5", 3);
    applySize(repeatCountField, new Dimension(40, 25));
    addFormField("RepeatCount", repeatCountField);
    repeatTimesPanel.add(repeatCountField);

    repeatTimesPanel.add(new JLabel("times"));
    repeatOptionsPanel.add(repeatTimesPanel);

    // Repeat until date option
    JPanel repeatUntilPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    repeatUntilRB = new JRadioButton("Repeat until");
    addFormField("RepeatUntilRB", repeatUntilRB);
    repeatUntilPanel.add(repeatUntilRB);

    repeatUntilDateField = new JTextField(LocalDate.now()
            .plusMonths(1).toString(), 10);
    applySize(repeatUntilDateField, new Dimension(110, 25));
    addFormField("RepeatUntilDate", repeatUntilDateField);
    repeatUntilPanel.add(repeatUntilDateField);

    repeatOptionsPanel.add(repeatUntilPanel);

    // Group radio buttons
    ButtonGroup radioGroup = new ButtonGroup();
    radioGroup.add(repeatNTimesRB);
    radioGroup.add(repeatUntilRB);
    repeatNTimesRB.setSelected(true);

    recurringPanel.add(repeatOptionsPanel);
    add(recurringPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));

    // Button panel
    JButton createButton = new JButton(getButtonText());
    applySize(createButton, UIUtils.getButtonSize());
    createButton.addActionListener(e -> executeAction());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(createButton);
    add(buttonPanel);
  }

  /**
   * Generates a command string for creating an event based on the current field values.
   * Collects the input values and uses the command generator to format them into a valid command.
   *
   * @return A formatted command string for creating an event with the specified details
   */
  @Override
  protected String generateCommand() {
    return commandGenerator.createEventCommand(getFormValues());
  }

  /**
   * Returns the text to display on this panel's action button.
   *
   * @return The button text ("Create Event")
   */
  @Override
  protected String getButtonText() {
    return "Create Event";
  }
}