package view.componentsGui;

import controller.EventController;
import view.utilsGui.CommandGenerator;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;

public class MonthViewPanel extends AbstractCalendarPanel {
  private YearMonth currentMonth;
  private CommandGenerator commandGenerator;
  private JTextArea[] eventAreas;
  private Map<LocalDate, String> eventsMap;
  private JTabbedPane tabbedPane;
  private JLabel headerLabel;
  private JPanel calendarGrid;
  private JScrollPane calendarScrollPane;

  /**
   * Constructor
   *
   * @param controller The event controller (not directly used here)
   * @param commandPreviewArea The shared command preview area
   * @param commandGenerator The command generator
   */
  public MonthViewPanel(EventController controller, JTextArea commandPreviewArea,
                        CommandGenerator commandGenerator, JTabbedPane tabbedPane) {
    super(controller, commandPreviewArea);
    this.commandGenerator = commandGenerator;
    this.currentMonth = YearMonth.now();
    this.eventAreas = new JTextArea[this.currentMonth.lengthOfMonth()];
    this.eventsMap = new HashMap<>();
    this.tabbedPane = tabbedPane;
    initializeUI();
  }

  @Override
  protected void initializeUI() {
    setLayout(new BorderLayout());

    // Ensure currentMonth is initialized
    if (currentMonth == null) {
      currentMonth = YearMonth.now();
    }

    // Create the main calendar panel
    JPanel calendarPanel = new JPanel(new BorderLayout());

    // Set a preferred size for the entire calendar panel to make it larger
    // This will make the whole calendar take up more space vertically
    calendarPanel.setPreferredSize(new Dimension(800, 700));

    // Create the month navigation panel (header)
    JPanel navigationPanel = createNavigationPanel();
    calendarPanel.add(navigationPanel, BorderLayout.NORTH);

    // Create inner panel for day headers and grid
    JPanel innerCalendarPanel = new JPanel(new BorderLayout());

    // Create day of week header
    JPanel headerRow = new JPanel(new GridLayout(1, 7));
    for (DayOfWeek day : DayOfWeek.values()) {
      JLabel dayLabel = new JLabel(day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), JLabel.CENTER);
      dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
      // Add some padding
      dayLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
      headerRow.add(dayLabel);
    }
    innerCalendarPanel.add(headerRow, BorderLayout.NORTH);

    // Create calendar grid (will be populated in refreshCalendarGrid)
    // Setting a fixed row count ensures consistent cell sizes
    // For a month view, 6 rows is enough to handle all month layouts
    calendarGrid = new JPanel(new GridLayout(6, 7));
    innerCalendarPanel.add(calendarGrid, BorderLayout.CENTER);

    calendarPanel.add(innerCalendarPanel, BorderLayout.CENTER);

    add(calendarPanel, BorderLayout.CENTER);

    // Initial population of the calendar grid
    refreshCalendarGrid();
  }

  /**
   * Creates the navigation panel with month/year display and navigation buttons
   */
  private JPanel createNavigationPanel() {
    JPanel navigationPanel = new JPanel(new BorderLayout());
    navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // Ensure currentMonth is initialized
    if (currentMonth == null) {
      currentMonth = YearMonth.now();
    }

    // Month and year label
    headerLabel = new JLabel(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            + " " + currentMonth.getYear(), JLabel.CENTER);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));

    // Navigation buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());

    JButton todayButton = new JButton("Today");
    JButton prevButton = new JButton("←");
    JButton nextButton = new JButton("→");

    // Add action listeners
    todayButton.addActionListener(e -> {
      currentMonth = YearMonth.now();
      refreshCalendarGrid();
    });

    prevButton.addActionListener(e -> {
      currentMonth = currentMonth.minusMonths(1);
      refreshCalendarGrid();
    });

    nextButton.addActionListener(e -> {
      currentMonth = currentMonth.plusMonths(1);
      refreshCalendarGrid();
    });

    // Add buttons to panel
    buttonPanel.add(todayButton);
    buttonPanel.add(prevButton);
    buttonPanel.add(nextButton);

    // Add components to navigation panel
    navigationPanel.add(headerLabel, BorderLayout.CENTER);
    navigationPanel.add(buttonPanel, BorderLayout.EAST);

    return navigationPanel;
  }

  /**
   * Refreshes the calendar grid for the current month
   */
  private void refreshCalendarGrid() {
    // Update header label if it exists
    if (headerLabel != null) {
      headerLabel.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
              + " " + currentMonth.getYear());
    }

    // Ensure calendarGrid is initialized
    if (calendarGrid == null) {
      calendarGrid = new JPanel(new GridLayout(0, 7));
    }

    // Clear the grid
    calendarGrid.removeAll();

    // Calculate first day position
    LocalDate firstOfMonth = currentMonth.atDay(1);
    int lengthOfMonth = currentMonth.lengthOfMonth();
    // Get day of week (1 = Monday, 7 = Sunday)
    int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue();

    // Adjust for week starting with Sunday in the UI
    int shift = (firstDayOfWeek + 6) % 7;

    // Add empty cells for days before the 1st of the month
    for (int i = 0; i < shift; i++) {
      calendarGrid.add(new JPanel());
    }

    // Create array for event areas
    eventAreas = new JTextArea[lengthOfMonth];

    // Fill in the days for this month
    for (int day = 1; day <= lengthOfMonth; day++) {
      LocalDate date = currentMonth.atDay(day);
      JPanel dayPanel = createDayPanel(date, day - 1);
      calendarGrid.add(dayPanel);
    }

    // Fetch and display events
    showEventsForMonth();

    // Refresh UI
    calendarGrid.revalidate();
    calendarGrid.repaint();
  }

  /**
   * Creates a panel for a specific date with events.
   */
  private JPanel createDayPanel(LocalDate date, int index) {
    JPanel dayPanel = new JPanel();
    dayPanel.setLayout(new BorderLayout(0, 5)); // Add vertical gap
    dayPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
    ));

    // Date label with better formatting
    JPanel dateHeaderPanel = new JPanel(new BorderLayout());
    dateHeaderPanel.setOpaque(false);

    JLabel dateLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
    dateLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
    dateLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    dateHeaderPanel.add(dateLabel, BorderLayout.WEST);

    dayPanel.add(dateHeaderPanel, BorderLayout.NORTH);

    // Events area
    JTextArea eventArea = new JTextArea();
    eventArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
    eventArea.setEditable(false);
    eventArea.setLineWrap(true);
    eventArea.setWrapStyleWord(true);
    eventArea.setMargin(new Insets(3, 3, 3, 3));

    eventAreas[index] = eventArea;

    JScrollPane scrollPane = new JScrollPane(eventArea);
    scrollPane.setBorder(null);
    // Increase the height of the scroll pane to show more events
    scrollPane.setPreferredSize(new Dimension(100, 100));

    dayPanel.add(scrollPane, BorderLayout.CENTER);

    // Highlight current day
    if (date.equals(LocalDate.now())) {
      dayPanel.setBackground(new Color(220, 245, 255));
      dateHeaderPanel.setBackground(new Color(200, 235, 255));
      dateLabel.setForeground(new Color(0, 90, 150));
      eventArea.setBackground(new Color(235, 250, 255));
    }

    // Add mouse listeners for user interactions
    dayPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {  // Right-click
          showContextMenu(e, date);
        } else if (e.getClickCount() == 2) {  // Double-click
          redirectToCreateEventTab(date);
        }
      }
    });

    // Add mouse listener for the event text area
    eventArea.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // If there's text at the point clicked (i.e., clicking on an event)
        try {
          int pos = eventArea.viewToModel(e.getPoint());
          if (pos >= 0) {
            int line = eventArea.getLineOfOffset(pos);
            int start = eventArea.getLineStartOffset(line);
            int end = eventArea.getLineEndOffset(line);
            String clickedEventText = eventArea.getText(start, end - start).trim();

            if (!clickedEventText.isEmpty()) {
              // Extract event name from the text (assuming format is "EventName (time)")
              String eventName = extractEventName(clickedEventText);
              if (eventName != null && !eventName.isEmpty()) {
                redirectToEditEventTab(date, eventName);
              }
            }
          }
        } catch (Exception ex) {
          // Ignore errors in text parsing
          System.err.println("Error handling event click: " + ex.getMessage());
        }
      }
    });

    return dayPanel;
  }

  /**
   * Extracts the event name from the event text displayed in the calendar
   */
  private String extractEventName(String clickedEventText) {
    int openParenIndex = clickedEventText.indexOf(" (");
    if (openParenIndex > 0) {
      return clickedEventText.substring(0, openParenIndex);
    }
    return clickedEventText; // Return full text if no parenthesis found
  }

  private void showContextMenu(MouseEvent e, LocalDate date) {
    JPopupMenu contextMenu = new JPopupMenu();

    JMenuItem createItem = new JMenuItem("Create Event");
    JMenuItem editItem = new JMenuItem("Edit Event");

    createItem.addActionListener(ae -> redirectToCreateEventTab(date));

    // For edit, we need a sub-menu if there are multiple events on this day
    LocalDateTime startDateTime = date.atStartOfDay();
    LocalDateTime endDateTime = date.atTime(23, 59, 59);
    List<String> eventStrings = controller.getEventsBetween(startDateTime, endDateTime);

    if (!eventStrings.isEmpty()) {
      if (eventStrings.size() == 1) {
        // Just one event, extract its name
        String eventStr = eventStrings.get(0);
        String[] parts = eventStr.split(",\\s*");
        if (parts.length >= 1) {
          String eventName = parts[0];
          editItem.addActionListener(ae -> redirectToEditEventTab(date, eventName));
          contextMenu.add(editItem);
        }
      } else {
        // Multiple events, create a submenu
        JMenu editMenu = new JMenu("Edit Event");
        for (String eventStr : eventStrings) {
          String[] parts = eventStr.split(",\\s*");
          if (parts.length >= 1) {
            String eventName = parts[0];
            JMenuItem eventItem = new JMenuItem(eventName);
            eventItem.addActionListener(ae -> redirectToEditEventTab(date, eventName));
            editMenu.add(eventItem);
          }
        }
        contextMenu.add(editMenu);
      }
    }

    contextMenu.add(createItem);
    contextMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  private void redirectToCreateEventTab(LocalDate date) {
    try {
      // Default tab index for create event
      int createEventTabIndex = 3;

      // Try to find the CreateEventPanel tab more intelligently
      for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        if (tabbedPane.getTitleAt(i).equals("Create Event")) {
          createEventTabIndex = i;
          break;
        }
      }

      // Select the tab
      tabbedPane.setSelectedIndex(createEventTabIndex);

      // Get the selected component
      Component currentTab = tabbedPane.getSelectedComponent();

      // Find text fields for dates within the component hierarchy
      JTextField startDateField = null;
      JTextField endDateField = null;

      // First try to find fields by traversing all components
      Component[] components = getAllComponents(currentTab);
      for (Component comp : components) {
        if (comp instanceof JTextField) {
          JTextField textField = (JTextField) comp;
          String fieldText = textField.getText();

          // Try to identify date fields by their format (YYYY-MM-DD)
          if (fieldText != null && fieldText.matches("\\d{4}-\\d{2}-\\d{2}")) {
            // If we haven't found a start date field yet, this is probably it
            if (startDateField == null) {
              startDateField = textField;
            }
            // If we already found a start date field, this is probably the end date
            else if (endDateField == null) {
              endDateField = textField;
            }
          }
        }
      }

      // Update the date fields if found
      if (startDateField != null) {
        startDateField.setText(date.toString());
      }
      if (endDateField != null) {
        endDateField.setText(date.toString());
      }

    } catch (Exception ex) {
      System.err.println("Error redirecting to Create Event tab: " + ex.getMessage());
      ex.printStackTrace();

      // Fallback to default behavior
      try {
        tabbedPane.setSelectedIndex(3);
      } catch (Exception e) {
        // Ignore if we can't even select the tab
      }
    }
  }

  // Helper method to get all components in a container and its nested containers
  private Component[] getAllComponents(Component container) {
    java.util.List<Component> componentList = new java.util.ArrayList<>();
    addComponents(container, componentList);
    return componentList.toArray(new Component[0]);
  }

  private void addComponents(Component container, java.util.List<Component> componentList) {
    componentList.add(container);
    if (container instanceof Container) {
      Container cont = (Container) container;
      Component[] components = cont.getComponents();
      for (Component component : components) {
        addComponents(component, componentList);
      }
    }
  }

  // Redirect to Edit Event Tab with selected date and event pre-filled
  private void redirectToEditEventTab(LocalDate date, String eventName) {
    try {
      // Default tab index for edit event
      int editEventTabIndex = 4;

      // Try to find the EditEventPanel tab more intelligently
      for (int i = 0; i < tabbedPane.getTabCount(); i++) {
        if (tabbedPane.getTitleAt(i).equals("Edit Event")) {
          editEventTabIndex = i;
          break;
        }
      }

      // Select the tab
      tabbedPane.setSelectedIndex(editEventTabIndex);

      // Get the selected component
      Component currentTab = tabbedPane.getSelectedComponent();

      // Find all combo boxes and date fields within the component hierarchy
      Component[] components = getAllComponents(currentTab);

      // Find event name combo box
      JComboBox<String> eventNameCombo = null;
      for (Component comp : components) {
        if (comp instanceof JComboBox) {
          JComboBox<?> comboBox = (JComboBox<?>) comp;
          // Try to identify the event name combo box
          if (comboBox.isEditable()) {
            eventNameCombo = (JComboBox<String>) comboBox;
            break;
          }
        }
      }

      // Find date fields
      java.util.List<JTextField> dateFields = new java.util.ArrayList<>();
      for (Component comp : components) {
        if (comp instanceof JTextField) {
          JTextField textField = (JTextField) comp;
          String text = textField.getText();
          // Identify date fields by their format (YYYY-MM-DD)
          if (text != null && text.matches("\\d{4}-\\d{2}-\\d{2}")) {
            dateFields.add(textField);
          }
        }
      }

      // Set event name in combo box
      if (eventNameCombo != null) {
        // Find the matching event name item
        boolean found = false;
        for (int i = 0; i < eventNameCombo.getItemCount(); i++) {
          String item = eventNameCombo.getItemAt(i).toString();
          if (item.startsWith(eventName)) {
            eventNameCombo.setSelectedIndex(i);
            found = true;
            break;
          }
        }

        // If no matching item, try to set the text directly
        if (!found && eventNameCombo.isEditable()) {
          eventNameCombo.setSelectedItem(eventName);
        }
      }

      // Update date fields if found
      if (!dateFields.isEmpty()) {
        // First two date fields are usually the from/to date fields
        int count = Math.min(2, dateFields.size());
        for (int i = 0; i < count; i++) {
          dateFields.get(i).setText(date.toString());
        }
      }

    } catch (Exception ex) {
      System.err.println("Error redirecting to Edit Event tab: " + ex.getMessage());
      ex.printStackTrace();

      // Fallback to default behavior
      try {
        tabbedPane.setSelectedIndex(4);
      } catch (Exception e) {
        // Ignore if we can't even select the tab
      }
    }
  }

  // Helper method to find a component by name in the hierarchy
  private Component findComponentByName(Container container, String name) {
    for (Component component : container.getComponents()) {
      // Check if this component has the form field name we're looking for
      if (component instanceof JComponent) {
        JComponent jComponent = (JComponent) component;
        String compName = jComponent.getName();
        if (name.equals(compName)) {
          return component;
        }
      }

      // Check child components if this is a container
      if (component instanceof Container) {
        Component found = findComponentByName((Container) component, name);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }

  // Helper method to find the index of a tab by class name
  private int findTabIndexByClass(Class<?> panelClass) {
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
      Component comp = tabbedPane.getComponentAt(i);
      if (findPanelByClass(comp, panelClass) != null) {
        return i;
      }
    }
    return -1;
  }

  // Helper method to find a panel of specific class in component hierarchy
  private <T> T findPanelByClass(Component component, Class<T> panelClass) {
    if (panelClass.isInstance(component)) {
      return panelClass.cast(component);
    }

    if (component instanceof Container) {
      Container container = (Container) component;
      for (Component child : container.getComponents()) {
        T found = findPanelByClass(child, panelClass);
        if (found != null) {
          return found;
        }
      }
    }

    return null;
  }

  // Helper methods to find specific panel types
  private CreateEventPanel findCreateEventPanel(Component component) {
    return findPanelByClass(component, CreateEventPanel.class);
  }

  private EditEventPanel findEditEventPanel(Component component) {
    return findPanelByClass(component, EditEventPanel.class);
  }

  /**
   * Method to show events for the entire month
   */
  public void showEventsForMonth() {
    LocalDateTime startDateTime = currentMonth.atDay(1).atStartOfDay();
    LocalDateTime endDateTime = currentMonth.atEndOfMonth().atTime(23, 59);

    List<String> eventStrings = controller.getEventsBetween(startDateTime, endDateTime);

    Map<LocalDate, StringBuilder> eventsByDate = new HashMap<>();

    for (String eventStr : eventStrings) {
      String[] parts = eventStr.split(",\\s*");
      if (parts.length < 3) continue;

      String eventName = parts[0];
      LocalDateTime startDateTimeParsed = LocalDateTime.parse(parts[1]);
      LocalDate eventDate = startDateTimeParsed.toLocalDate();

      // Format with more information - e.g., "2:30 PM - 3:30 PM" instead of "14:30 - 15:30"
      String startTime = startDateTimeParsed.toLocalTime().toString();
      String endTime = "";
      if (parts.length > 2) {
        try {
          LocalDateTime endDateTimeParsed = LocalDateTime.parse(parts[2]);
          endTime = endDateTimeParsed.toLocalTime().toString();
        } catch (Exception e) {
          // Handle parsing errors
          endTime = parts[2].contains("T") ? parts[2].substring(parts[2].indexOf("T") + 1) : parts[2];
        }
      }

      eventsByDate
              .computeIfAbsent(eventDate, k -> new StringBuilder())
              .append(eventName)
              .append(" (")
              .append(startTime)
              .append(" - ")
              .append(endTime)
              .append(")\n");
    }

    // Clear all event areas first
    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      if (day-1 < eventAreas.length) {
        JTextArea eventArea = eventAreas[day - 1];
        if (eventArea != null) {
          eventArea.setText("");
        }
      }
    }

    // Now populate with events
    for (LocalDate date : eventsByDate.keySet()) {
      if (date.getMonth() == currentMonth.getMonth() &&
              date.getYear() == currentMonth.getYear()) {
        int day = date.getDayOfMonth();
        if (day-1 < eventAreas.length) {
          JTextArea eventArea = eventAreas[day - 1];
          if (eventArea != null) {
            eventArea.setText(eventsByDate.get(date).toString());
          }
        }
      }
    }
  }

  public void refreshMonth() {
    // Rebuild the calendar grid completely
    refreshCalendarGrid();

    // Update just the events in the existing grid
    showEventsForMonth();

    // Refresh UI
    revalidate();
    repaint();
  }

  @Override
  protected String generateCommand() {
    return "";
  }

  @Override
  protected String getButtonText() {
    return "Refresh View";
  }
}