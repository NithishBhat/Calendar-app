package view.components;

import controller.EventController;
import view.utils.CommandGenerator;

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

    System.out.println("MonthViewPanel initializeUI");
    System.out.println("MonthDate: " + currentMonth);
    if (currentMonth == null) {
      currentMonth = YearMonth.now();
      System.out.println("MonthDate: " + currentMonth);
    }

    JLabel headerLabel = new JLabel(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            + " " + currentMonth.getYear(), JLabel.CENTER);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
    add(headerLabel, BorderLayout.NORTH);

    JPanel calendarPanel = new JPanel(new BorderLayout());

    JPanel headerRow = new JPanel(new GridLayout(1, 7));
    for (DayOfWeek day : DayOfWeek.values()) {
      JLabel dayLabel = new JLabel(day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), JLabel.CENTER);
      dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
      headerRow.add(dayLabel);
    }
    calendarPanel.add(headerRow, BorderLayout.NORTH);

    JPanel calendarGrid = new JPanel(new GridLayout(0, 7)); // auto rows

    LocalDate firstOfMonth = currentMonth.atDay(1);
    int lengthOfMonth = currentMonth.lengthOfMonth();
    int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 = Monday ... 7 = Sunday

    int shift = firstDayOfWeek % 7;

    for (int i = 0; i < shift; i++) {
      calendarGrid.add(new JPanel());
    }

    eventAreas = new JTextArea[lengthOfMonth];

    // Fill in the days
    for (int day = 1; day <= lengthOfMonth; day++) {
      LocalDate date = currentMonth.atDay(day);
      JPanel dayPanel = createDayPanel(date, day - 1);
      calendarGrid.add(dayPanel);
    }

    calendarPanel.add(calendarGrid, BorderLayout.CENTER);
    add(new JScrollPane(calendarPanel), BorderLayout.CENTER);

    showEventsForMonth();
  }

  /**
   * Creates a panel for a specific date with events.
   */
  private JPanel createDayPanel(LocalDate date, int index) {
    JPanel dayPanel = new JPanel();
    dayPanel.setLayout(new BorderLayout());
    dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    JLabel dateLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
    dateLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
    dayPanel.add(dateLabel, BorderLayout.NORTH);

    JTextArea eventArea = new JTextArea();
    eventArea.setFont(new Font("SansSerif", Font.PLAIN, 10));
    eventArea.setEditable(false);
    eventArea.setLineWrap(true);
    eventArea.setWrapStyleWord(true);

    eventAreas[index] = eventArea;

    JScrollPane scrollPane = new JScrollPane(eventArea);
    scrollPane.setBorder(null);
    scrollPane.setPreferredSize(new Dimension(100, 60));

    dayPanel.add(scrollPane, BorderLayout.CENTER);

    if (date.equals(LocalDate.now())) {
      dayPanel.setBackground(new Color(220, 245, 255));
    }
    dayPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {  // Detect right-click
          showContextMenu(e, date);
        }
      }
    });

    return dayPanel;
  }

  private void showContextMenu(MouseEvent e, LocalDate date) {
    JPopupMenu contextMenu = new JPopupMenu();

    JMenuItem createItem = new JMenuItem("Create Event");
    JMenuItem editItem = new JMenuItem("Edit Event");

    createItem.addActionListener(ae -> redirectToCreateEventTab(date));
    editItem.addActionListener(ae -> redirectToEditEventTab(date));

    contextMenu.add(createItem);

    LocalDateTime startDateTime = date.atStartOfDay();
    LocalDateTime endDateTime = date.atTime(23, 59, 59);
    List<String> eventStrings = controller.getEventsBetween(startDateTime, endDateTime);
    if (!eventStrings.isEmpty()) {
      contextMenu.add(editItem);
    }
    contextMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  private void redirectToCreateEventTab(LocalDate date) {
    int createEventTabIndex = 3;
    tabbedPane.setSelectedIndex(createEventTabIndex);
  }

  // Redirect to Edit Event Tab with selected date pre-filled
  private void redirectToEditEventTab(LocalDate date) {

    int editEventTabIndex = 4;
    tabbedPane.setSelectedIndex(editEventTabIndex);
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

      eventsByDate
              .computeIfAbsent(eventDate, k -> new StringBuilder())
              .append(eventName)
              .append(" (")
              .append(parts[1].substring(11)) // Time only from start
              .append(" - ")
              .append(parts[2].substring(11)) // Time only from end
              .append(")\n");
    }

    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      LocalDate date = currentMonth.atDay(day);
      JTextArea eventArea = eventAreas[day - 1];

      if (eventsByDate.containsKey(date)) {
        eventArea.setText(eventsByDate.get(date).toString());
      } else {
        eventArea.setText("");
      }
    }
  }

  public void refreshMonth() {
    showEventsForMonth();
    revalidate();
    repaint();              //force redraw
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