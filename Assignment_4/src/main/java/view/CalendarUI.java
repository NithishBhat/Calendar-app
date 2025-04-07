package view;

import controller.EventController;
import model.CalenderCollection;
import model.EventCollectionModel;
import model.EventModel;
import view.components.*;
import view.utils.CommandGenerator;
import view.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Main UI class for the Calendar application.
 * Manages the UI components and handles events.
 */
public class CalendarUI implements CalendarUIListener {

  // Shared components
  private JTextArea commandPreviewArea;
  private JComboBox<String> calendarSelector;
  private EventController controller;
  private String lastCommand = ""; // Track last command to prevent infinite loops
  private CommandGenerator commandGenerator;

  // Reference to the edit event panel's event name combo box
  private JComboBox<String> editEventNameCombo;

  /**
   * Main method - application entry point
   */
  public static void main(String[] args) {
    // Use SwingUtilities.invokeLater to ensure UI creation happens on EDT
    SwingUtilities.invokeLater(() -> {
      // Initialize controller first
      EventController controller = new EventController();

      // Initialize command generator
      CommandGenerator commandGenerator = new CommandGenerator();

      // Create UI instance and register as a listener
      CalendarUI ui = new CalendarUI(controller, commandGenerator);
      controller.getEventHandler().addUIListener(ui);

      // Start the UI
      ui.createAndShowGUI();
    });
  }

  /**
   * Constructor
   *
   * @param controller The event controller
   * @param commandGenerator The command generator
   */
  public CalendarUI(EventController controller, CommandGenerator commandGenerator) {
    this.controller = controller;
    this.commandGenerator = commandGenerator;
  }

  /**
   * Create and show the main UI
   */
  private void createAndShowGUI() {
    // Create the main frame
    JFrame frame = new JFrame("Calendar Command UI");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(1200, 700));
    frame.setMinimumSize(new Dimension(800, 500));
    frame.setResizable(true);

    // Initialize the command preview area (shared across panels)
    commandPreviewArea = new JTextArea(30, 80);
    commandPreviewArea.setEditable(false);
    commandPreviewArea.setBorder(new EmptyBorder(5, 5, 5, 5));

    // Initialize calendar selector dropdown
    calendarSelector = new JComboBox<>();
    updateCalendarList();

    // Add an action listener to switch calendars
    calendarSelector.addActionListener(e -> {
      // Only react to user actions, not programmatic changes
      if (e.getActionCommand() != null && e.getActionCommand().equals("comboBoxChanged")
              && calendarSelector.getSelectedItem() != null) {
        String selectedCalendar = calendarSelector.getSelectedItem().toString();
        String command = String.format("use calendar --name \"%s\"", selectedCalendar);
        // Prevent infinite loop by keeping track of the last command
        if (!command.equals(lastCommand)) {
          lastCommand = command;
          controller.filterCommand(command);
          commandPreviewArea.append(command + "\n");
        }
      }
    });

    // Create tabs with the component panels
    JTabbedPane tabbedPane = new JTabbedPane();

    // Add the tab panels using our new component classes
    tabbedPane.addTab("Month View", UIUtils.wrapPanel(new MonthViewPanel(controller, commandPreviewArea, commandGenerator)));
    tabbedPane.addTab("Create Calendar", UIUtils.wrapPanel(new CreateCalendarPanel(controller, commandPreviewArea, commandGenerator)));
    tabbedPane.addTab("Edit Calendar", UIUtils.wrapPanel(new EditCalendarPanel(controller, commandPreviewArea, commandGenerator)));
    tabbedPane.addTab("Create Event", UIUtils.wrapPanel(new CreateEventPanel(controller, commandPreviewArea, commandGenerator)));

    // Create EditEventPanel instance and store reference to its event name combo box
    EditEventPanel editEventPanel = new EditEventPanel(controller, commandPreviewArea, commandGenerator);
    editEventNameCombo = editEventPanel.getEventNameCombo();
    tabbedPane.addTab("Edit Event", UIUtils.wrapPanel(editEventPanel));

    tabbedPane.addTab("Print Events", UIUtils.wrapPanel(new PrintEventsPanel(controller, commandPreviewArea, commandGenerator)));
    tabbedPane.addTab("Export Calendar", UIUtils.wrapPanel(new ExportCalendarPanel(controller, commandPreviewArea, commandGenerator)));
    tabbedPane.addTab("Show Status", UIUtils.wrapPanel(new ShowStatusPanel(controller, commandPreviewArea, commandGenerator)));

    // Calendar selector panel
    JPanel calendarSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    calendarSelectorPanel.add(new JLabel("Current Calendar:"));
    calendarSelectorPanel.add(calendarSelector);

    // Right panel: command preview area
    JPanel commandPanel = new JPanel(new BorderLayout(10, 10));
    commandPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    commandPanel.add(calendarSelectorPanel, BorderLayout.NORTH);
    commandPanel.add(new JScrollPane(commandPreviewArea), BorderLayout.CENTER);

    // Main panel with fixed layout
    JPanel mainPanel = new JPanel(new GridLayout(1, 2));  // 1 row, 2 columns
    mainPanel.add(tabbedPane);
    mainPanel.add(commandPanel);

    frame.add(mainPanel, BorderLayout.CENTER);
    frame.pack();
    frame.setLocationRelativeTo(null); // Center on screen
    frame.setVisible(true);
  }

  /**
   * CalendarUIListener implementation methods
   */
  @Override
  public void onCalendarChanged(String calendarName) {
    SwingUtilities.invokeLater(() -> {
      if (calendarSelector != null && !calendarName.equals(calendarSelector.getSelectedItem())) {
        // Set a flag to indicate this is a programmatic change
        calendarSelector.setActionCommand("programmaticChange");
        calendarSelector.setSelectedItem(calendarName);
        // Reset the action command
        calendarSelector.setActionCommand("comboBoxChanged");
      }

      // Update event names in the edit panel if it exists
      if (editEventNameCombo != null) {
        updateEventNameComboBox();
      }
    });
  }

  @Override
  public void onCommandOutput(String output) {
    SwingUtilities.invokeLater(() -> {
      if (commandPreviewArea != null) {
        // Format the output to be more user-friendly
        String formattedOutput = formatCommandOutput(output);
        commandPreviewArea.append(formattedOutput + "\n");
      }
    });
  }

  /**
   * Formats command output to be more user-friendly
   * @param output The raw command output
   * @return Formatted user-friendly message
   */
  private String formatCommandOutput(String output) {

    // Add timestamp
    String timestamp = LocalTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm:ss"));

    return "[" + timestamp + "] " + output;
  }

  @Override
  public void onEventsChanged(CalenderCollection calenderCollection) {
    // Update event names in the edit panel
    SwingUtilities.invokeLater(this::updateEventNameComboBox);
  }

  @Override
  public void onCalendarListChanged() {
    updateCalendarList();
  }

  /**
   * Update the calendar selector with current calendars
   */
  private void updateCalendarList() {
    SwingUtilities.invokeLater(() -> {
      if (calendarSelector != null) {
        // Save current selection
        Object currentSelection = calendarSelector.getSelectedItem();

        // Update the list
        calendarSelector.removeAllItems();

        // Get calendars from controller
        Map<String, model.CalenderModel> calendars =
                controller.getEventHandler().getCalenderCollection().getCalenderCollection();

        // Add each calendar to the selector
        for (String calName : calendars.keySet()) {
          calendarSelector.addItem(calName);
        }

        // Restore selection if it still exists
        if (currentSelection != null &&
                calendars.containsKey(currentSelection.toString())) {
          calendarSelector.setSelectedItem(currentSelection);
        }
      }
    });
  }

  /**
   * Updates the event name combo box with current events
   */
  private void updateEventNameComboBox() {
    if (editEventNameCombo == null) return;

    // Get current calendar
    if (calendarSelector.getSelectedItem() != null) {
      String currentCalendarName = calendarSelector.getSelectedItem().toString();

      try {
        // Clear current items
        editEventNameCombo.removeAllItems();

        // Get events from this calendar
        EventCollectionModel eventCollection =
                controller.getEventHandler().getCalenderCollection().getEventCollection(currentCalendarName);

        // Get all events
        List<EventModel> events = eventCollection.getEventList();

        // Create map to track recurring events
        Map<String, Integer> eventNameCount = new HashMap<>();

        // Count occurrences of each event name to identify recurring events
        for (EventModel event : events) {
          String name = event.getEventName();
          eventNameCount.put(name, eventNameCount.getOrDefault(name, 0) + 1);
        }

        // Add each unique event name to the combo box, marking recurring events
        for (String name : eventNameCount.keySet()) {
          if (eventNameCount.get(name) > 1) {
            // This is a recurring event - mark it
            editEventNameCombo.addItem(name + " (recurring)");
          } else {
            // Single event
            editEventNameCombo.addItem(name);
          }
        }
      } catch (Exception e) {
        System.err.println("Error populating event names: " + e.getMessage());
      }
    }
  }
}