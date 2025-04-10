package view.componentsGui;

import controller.EventController;
import model.CalenderCollection;
import model.EventCollectionModel;
import model.EventModel;
import view.utilsGui.CommandGenerator;
import view.utilsGui.UIUtils;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main UI class for the Calendar application.
 * Manages the UI components and handles events.
 * Implements CalendarUIListener to receive notifications about changes in the model.
 */
public class CalendarUI implements CalendarUIListener {

  /** Text area to display command history and output. */
  private JTextArea commandPreviewArea;

  /** Dropdown to select the current calendar. */
  private JComboBox<String> calendarSelector;

  /** Reference to the event controller for executing commands. */
  private EventController controller;

  /** Tracks the last executed command to prevent infinite loops. */
  private String lastCommand = "";

  /** Helper to generate command strings. */
  private CommandGenerator commandGenerator;

  /** Reference to the month view panel for refreshing the calendar display. */
  private MonthViewPanel monthViewPanel;

  /** Reference to the edit event panel's event name combo box for updating event lists. */
  private JComboBox<String> editEventNameCombo;

  /**
   * Main method - application entry point.
   * Creates and initializes the UI on the Event Dispatch Thread.
   */
  public void start() {
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
   * Constructor for the Calendar UI.
   *
   * @param controller The event controller that processes commands
   * @param commandGenerator The command generator for creating command strings
   */
  public CalendarUI(EventController controller, CommandGenerator commandGenerator) {
    this.controller = controller;
    this.commandGenerator = commandGenerator;
  }

  /**
   * Creates and displays the main application window.
   * Sets up all UI components, panels, and event handlers.
   */
  private void createAndShowGUI() {
    // Create the main frame
    JFrame frame = new JFrame("Calendar Command UI");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(1200, 750));
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
    monthViewPanel = new MonthViewPanel(controller, commandPreviewArea,
            commandGenerator, tabbedPane);
    tabbedPane.addTab("Month View", UIUtils.wrapPanel(monthViewPanel));

    tabbedPane.addTab("Create Calendar", UIUtils.wrapPanel(new CreateCalendarPanel(controller,
            commandPreviewArea, commandGenerator)));
    tabbedPane.addTab("Edit Calendar", UIUtils.wrapPanel(new EditCalendarPanel(controller,
            commandPreviewArea, commandGenerator)));

    tabbedPane.addTab("Create Event", UIUtils.wrapPanel(new CreateEventPanel(controller,
            commandPreviewArea, commandGenerator, null)));

    // Create EditEventPanel instance and store reference to its event name combo box
    EditEventPanel editEventPanel = new EditEventPanel(controller, commandPreviewArea,
            commandGenerator);
    editEventNameCombo = editEventPanel.getEventNameCombo();
    tabbedPane.addTab("Edit Event", UIUtils.wrapPanel(editEventPanel));

    tabbedPane.addTab("Print Events", UIUtils.wrapPanel(new PrintEventsPanel(controller,
            commandPreviewArea, commandGenerator)));
    tabbedPane.addTab("Export Calendar", UIUtils.wrapPanel(new ExportCalendarPanel(controller,
            commandPreviewArea, commandGenerator)));
    tabbedPane.addTab("Import Calendar", UIUtils.wrapPanel(new ImportCalendarPanel(controller,
            commandPreviewArea, commandGenerator)));
    tabbedPane.addTab("Show Status", UIUtils.wrapPanel(new ShowStatusPanel(controller,
            commandPreviewArea, commandGenerator)));


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
   * Called when the current calendar changes.
   * Updates the UI to reflect the new calendar selection.
   *
   * @param calendarName The name of the newly selected calendar
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

  /**
   * Called when a command produces output.
   * Appends the formatted output to the command preview area.
   *
   * @param output The command output text to display
   */
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
   * Formats command output to be more user-friendly.
   * Adds a timestamp to the output message.
   *
   * @param output The raw command output
   * @return Formatted user-friendly message with timestamp
   */
  private String formatCommandOutput(String output) {
    // Add timestamp
    String timestamp = LocalTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm:ss"));

    return "[" + timestamp + "] " + output;
  }

  /**
   * Called when the events in a calendar change.
   * Updates event-related UI components.
   *
   * @param calenderCollection The updated calendar collection
   */
  @Override
  public void onEventsChanged(CalenderCollection calenderCollection) {
    SwingUtilities.invokeLater(() -> {
      updateEventNameComboBox();
      if (monthViewPanel != null) {
        monthViewPanel.refreshMonth();
      }
    });
  }

  /**
   * Called when the list of available calendars changes.
   * Updates the calendar selector dropdown.
   */
  @Override
  public void onCalendarListChanged() {
    updateCalendarList();
  }

  /**
   * Updates the calendar selector dropdown with the current list of calendars.
   * Preserves the current selection if possible.
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
   * Updates the event name combo box with the current events from the selected calendar.
   * Identifies and marks recurring events.
   */
  private void updateEventNameComboBox() {
    if (editEventNameCombo == null) {
      return;
    }

    // Get current calendar
    if (calendarSelector.getSelectedItem() != null) {
      String currentCalendarName = calendarSelector.getSelectedItem().toString();

      try {
        // Clear current items
        editEventNameCombo.removeAllItems();

        // Get events from this calendar
        EventCollectionModel eventCollection =
                controller.getEventHandler().getCalenderCollection()
                        .getEventCollection(currentCalendarName);

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