package view.components;

import controller.EventController;
import model.EventCollectionModel;
import model.EventModel;
import view.CalendarUIListener;
import view.utils.CommandGenerator;
import view.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for editing existing events
 */
public class EditEventPanel extends AbstractCalendarPanel implements CalendarUIListener {

  private CommandGenerator commandGenerator;
  private JComboBox<String> eventNameCombo;
  private JComboBox<String> propertyCombo;
  private JPanel valuePanel;
  private JTextField textValueField;
  private JTextField dateField;
  private JTextField timeField;
  private JComboBox<String> privacyCombo;

  private JRadioButton specificEventRB;
  private JRadioButton eventsFromRB;
  private JRadioButton allEventsRB;

  private JTextField fromDateField;
  private JTextField fromTimeField;
  private JTextField toDateField;
  private JTextField toTimeField;
  private JTextField fromOnlyDateField;
  private JTextField fromOnlyTimeField;

  private JPanel dateTimeFieldsPanel;
  private String currentCalendarName;

  public EditEventPanel(EventController controller, JTextArea commandPreviewArea,
                        CommandGenerator commandGenerator) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
    controller.getEventHandler().addUIListener(this);
  }

  public JComboBox<String> getEventNameCombo() {
    return eventNameCombo;
  }

  @Override
  protected void initializeUI() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(2, 2, 2, 2));

    JPanel selectionPanel = createSectionPanel("Event & Property", 2, 1);

    eventNameCombo = new JComboBox<>();
    eventNameCombo.setEditable(true);
    applySize(eventNameCombo, new Dimension(250, 25));
    addFormField("Event Name", eventNameCombo);
    selectionPanel.add(createRow("Event Name:", eventNameCombo));

    JTextField eventNameField = (JTextField) eventNameCombo.getEditor().getEditorComponent();
    eventNameField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        String text = eventNameField.getText();
        if (text.length() == 0) {
          return;
        }
        filterEventNameComboBox(text);
      }
    });

    propertyCombo = new JComboBox<>(new String[] {
            "eventName", "startDateTime", "endDateTime", "location", "description", "privacyType"
    });
    addFormField("Property to Edit", propertyCombo);
    selectionPanel.add(createRow("Property:", propertyCombo));

    add(selectionPanel);
    add(Box.createRigidArea(new Dimension(0, 1)));

    JPanel newValuePanel = new JPanel();
    newValuePanel.setBorder(BorderFactory.createTitledBorder("New Value"));
    newValuePanel.setLayout(new BorderLayout());

    valuePanel = new JPanel(new CardLayout());

    textValueField = new JTextField();
    JPanel textPanel = new JPanel(new BorderLayout());
    textPanel.add(textValueField, BorderLayout.CENTER);
    valuePanel.add(textPanel, "text");
    addFormField("TextValue", textValueField);

    JPanel dateTimePanel = new JPanel(new BorderLayout(5, 0));
    dateField = new JTextField("2025-04-01");
    timeField = new JTextField("09:00");
    applySize(dateField, new Dimension(140, 25));
    applySize(timeField, new Dimension(80, 25));

    JPanel innerDateTimePanel = new JPanel(new BorderLayout(5, 0));
    innerDateTimePanel.add(dateField, BorderLayout.CENTER);
    innerDateTimePanel.add(timeField, BorderLayout.EAST);
    dateTimePanel.add(innerDateTimePanel, BorderLayout.CENTER);
    valuePanel.add(dateTimePanel, "dateTime");
    addFormField("DateValue", dateField);
    addFormField("TimeValue", timeField);

    privacyCombo = new JComboBox<>(new String[] {"Public", "Private"});
    JPanel privacyPanel = new JPanel(new BorderLayout());
    privacyPanel.add(privacyCombo, BorderLayout.CENTER);
    valuePanel.add(privacyPanel, "privacy");
    addFormField("PrivacyValue", privacyCombo);

    propertyCombo.addActionListener(e -> {
      CardLayout cl = (CardLayout) valuePanel.getLayout();
      String selectedProperty = (String) propertyCombo.getSelectedItem();

      if (selectedProperty.equals("startDateTime") || selectedProperty.equals("endDateTime")) {
        cl.show(valuePanel, "dateTime");
      } else if (selectedProperty.equals("privacyType")) {
        cl.show(valuePanel, "privacy");
      } else {
        cl.show(valuePanel, "text");
      }
    });

    JPanel valueRow = new JPanel(new BorderLayout(5, 0));
    JLabel valueLabel = new JLabel("New Value:");
    applySize(valueLabel, UIUtils.getLabelSize());
    valueRow.add(valueLabel, BorderLayout.WEST);
    applySize(valuePanel, UIUtils.getFieldSize());
    valueRow.add(valuePanel, BorderLayout.CENTER);
    newValuePanel.add(valueRow, BorderLayout.CENTER);

    add(newValuePanel);
    add(Box.createRigidArea(new Dimension(0, 1)));

    JPanel modePanel = new JPanel();
    modePanel.setBorder(BorderFactory.createTitledBorder("Edit Mode"));
    modePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));

    specificEventRB = new JRadioButton("Edit Specific Event (from-to)");
    specificEventRB.setSelected(true);
    addFormField("ThisEventOnlyRB", specificEventRB);

    eventsFromRB = new JRadioButton("Edit Events From Date");
    addFormField("ThisEventLaterRB", eventsFromRB);

    allEventsRB = new JRadioButton("Edit All Events with Name");
    addFormField("AllEventsRB", allEventsRB);

    ButtonGroup radioGroup = new ButtonGroup();
    radioGroup.add(specificEventRB);
    radioGroup.add(eventsFromRB);
    radioGroup.add(allEventsRB);

    modePanel.add(specificEventRB);
    modePanel.add(eventsFromRB);
    modePanel.add(allEventsRB);

    add(modePanel);
    add(Box.createRigidArea(new Dimension(0, 1)));

    JPanel dateFieldsContainer = new JPanel(new BorderLayout());
    dateFieldsContainer.setBorder(BorderFactory.createTitledBorder("Date Range"));

    dateTimeFieldsPanel = new JPanel(new CardLayout());

    JPanel fromToPanel = new JPanel(new GridLayout(2, 1, 0, 5));

    fromDateField = new JTextField("2025-04-01");
    fromTimeField = new JTextField("09:00");
    toDateField = new JTextField("2025-04-01");
    toTimeField = new JTextField("10:00");

    applySize(fromDateField, new Dimension(110, 25));
    applySize(fromTimeField, new Dimension(70, 25));
    applySize(toDateField, new Dimension(110, 25));
    applySize(toTimeField, new Dimension(70, 25));

    addFormField("FromDate", fromDateField);
    addFormField("FromTime", fromTimeField);
    addFormField("ToDate", toDateField);
    addFormField("ToTime", toTimeField);

    JPanel fromRow = new JPanel(new BorderLayout(5, 0));
    JLabel fromLabel = new JLabel("From:");
    applySize(fromLabel, new Dimension(80, 25));
    fromRow.add(fromLabel, BorderLayout.WEST);

    JPanel fromFields = new JPanel(new BorderLayout(5, 0));
    fromFields.add(fromDateField, BorderLayout.CENTER);
    fromFields.add(fromTimeField, BorderLayout.EAST);
    fromRow.add(fromFields, BorderLayout.CENTER);

    JPanel toRow = new JPanel(new BorderLayout(5, 0));
    JLabel toLabel = new JLabel("To:");
    applySize(toLabel, new Dimension(80, 25));
    toRow.add(toLabel, BorderLayout.WEST);

    JPanel toFields = new JPanel(new BorderLayout(5, 0));
    toFields.add(toDateField, BorderLayout.CENTER);
    toFields.add(toTimeField, BorderLayout.EAST);
    toRow.add(toFields, BorderLayout.CENTER);

    fromToPanel.add(fromRow);
    fromToPanel.add(toRow);
    dateTimeFieldsPanel.add(fromToPanel, "fromTo");

    JPanel fromPanel = new JPanel(new BorderLayout(5, 0));
    JPanel fromOnlyRow = new JPanel(new BorderLayout(5, 0));
    JLabel fromOnlyLabel = new JLabel("From:");
    applySize(fromOnlyLabel, new Dimension(80, 25));
    fromOnlyRow.add(fromOnlyLabel, BorderLayout.WEST);

    fromOnlyDateField = new JTextField("2025-04-01");
    fromOnlyTimeField = new JTextField("09:00");

    applySize(fromOnlyDateField, new Dimension(110, 25));
    applySize(fromOnlyTimeField, new Dimension(70, 25));

    addFormField("FromOnlyDate", fromOnlyDateField);
    addFormField("FromOnlyTime", fromOnlyTimeField);

    JPanel fromOnlyFields = new JPanel(new BorderLayout(5, 0));
    fromOnlyFields.add(fromOnlyDateField, BorderLayout.CENTER);
    fromOnlyFields.add(fromOnlyTimeField, BorderLayout.EAST);
    fromOnlyRow.add(fromOnlyFields, BorderLayout.CENTER);

    fromPanel.add(fromOnlyRow, BorderLayout.NORTH);
    dateTimeFieldsPanel.add(fromPanel, "from");

    JPanel noDatesPanel = new JPanel();
    dateTimeFieldsPanel.add(noDatesPanel, "none");

    specificEventRB.addActionListener(e -> {
      CardLayout cl = (CardLayout) dateTimeFieldsPanel.getLayout();
      cl.show(dateTimeFieldsPanel, "fromTo");
    });

    eventsFromRB.addActionListener(e -> {
      CardLayout cl = (CardLayout) dateTimeFieldsPanel.getLayout();
      cl.show(dateTimeFieldsPanel, "from");
    });

    allEventsRB.addActionListener(e -> {
      CardLayout cl = (CardLayout) dateTimeFieldsPanel.getLayout();
      cl.show(dateTimeFieldsPanel, "none");
    });

    CardLayout cl = (CardLayout) dateTimeFieldsPanel.getLayout();
    cl.show(dateTimeFieldsPanel, "fromTo");

    dateFieldsContainer.add(dateTimeFieldsPanel, BorderLayout.CENTER);
    add(dateFieldsContainer);
    add(Box.createRigidArea(new Dimension(0, 5)));

    JButton runBtn = new JButton(getButtonText());
    applySize(runBtn, UIUtils.getButtonSize());
    runBtn.addActionListener(e -> executeAction());

    JPanel runPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    runPanel.add(runBtn);
    add(runPanel);
  }

  @Override
  protected String generateCommand() {
    Map<String, String> values = getFormValues();

    String eventName = eventNameCombo.getSelectedItem().toString();
    if (eventName.endsWith(" (recurring)")) {
      eventName = eventName.substring(0, eventName.length() - " (recurring)".length());
    }
    values.put("Event Name", eventName);

    String property = propertyCombo.getSelectedItem().toString();
    values.put("Property to Edit", property);

    String newValue;
    if (property.equals("startDateTime") || property.equals("endDateTime")) {
      newValue = dateField.getText() + "T" + timeField.getText();
    } else if (property.equals("privacyType")) {
      newValue = privacyCombo.getSelectedItem().toString().equals("Private") ? "true" : "false";
    } else {
      newValue = textValueField.getText();
    }
    values.put("New Value", newValue);

    if (specificEventRB.isSelected()) {
      values.put("FromDate", fromDateField.getText());
      values.put("FromTime", fromTimeField.getText());
      values.put("ToDate", toDateField.getText());
      values.put("ToTime", toTimeField.getText());
    } else if (eventsFromRB.isSelected()) {
      values.put("FromDate", fromOnlyDateField.getText());
      values.put("FromTime", fromOnlyTimeField.getText());
    }

    return commandGenerator.editEventCommand(values);
  }

  @Override
  protected String getButtonText() {
    return "Run Edit Event";
  }

  private void filterEventNameComboBox(String text) {
    if (currentCalendarName == null || currentCalendarName.isEmpty()) {
      Map<String, model.CalenderModel> calendars =
              controller.getEventHandler().getCalenderCollection().getCalenderCollection();
      if (!calendars.isEmpty()) {
        currentCalendarName = calendars.keySet().iterator().next();
      } else {
        return;
      }
    }

    try {
      String currentText = text;
      JTextField textField = (JTextField) eventNameCombo.getEditor().getEditorComponent();
      int selectionStart = textField.getSelectionStart();
      int selectionEnd = textField.getSelectionEnd();

      EventCollectionModel eventCollection =
              controller.getEventHandler().getCalenderCollection().getEventCollection(currentCalendarName);

      List<EventModel> events = eventCollection.getEventList();

      DefaultComboBoxModel<String> filteredModel = new DefaultComboBoxModel<>();

      Map<String, Integer> eventNameCount = new HashMap<>();

      for (EventModel event : events) {
        String name = event.getEventName();
        if (name.toLowerCase().contains(text.toLowerCase())) {
          eventNameCount.put(name, eventNameCount.getOrDefault(name, 0) + 1);
        }
      }

      for (String name : eventNameCount.keySet()) {
        if (eventNameCount.get(name) > 1) {
          filteredModel.addElement(name + " (recurring)");
        } else {
          filteredModel.addElement(name);
        }
      }

      if (filteredModel.getSize() > 0) {
        eventNameCombo.setModel(filteredModel);
        eventNameCombo.getEditor().setItem(currentText);
        textField.setSelectionStart(selectionStart);
        textField.setSelectionEnd(selectionEnd);

        if (!eventNameCombo.isPopupVisible()) {
          eventNameCombo.showPopup();
        }
      }
    } catch (Exception e) {
      System.err.println("Error filtering event names: " + e.getMessage());
    }
  }

  @Override
  public void onCalendarChanged(String calendarName) {
    this.currentCalendarName = calendarName;
  }

  @Override
  public void onCommandOutput(String output) {}

  @Override
  public void onEventsChanged(model.CalenderCollection calenderCollection) {}

  @Override
  public void onCalendarListChanged() {}
}
