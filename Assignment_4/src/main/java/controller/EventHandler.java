package controller;

import java.time.ZoneId;

import model.CalenderCollection;
import model.EventCollectionModel;
import model.EventModel;
import model.RecurringEventModel;
import view.componentsGui.CalendarUIListener;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Handles event creation, modification, and retrieval.
 * This class provides methods for processing various event-related commands
 * such as creating, editing, exporting, and printing events.
 */
public class EventHandler {

  private EventCollectionModel eventCollection;
  private CalenderCollection calenderCollection;
  private List<CalendarUIListener> uiListeners = new ArrayList<>();

  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_CYAN = "\u001B[36m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String errorString = ANSI_RED + "[ERROR] " + ANSI_RESET;
  private static final String doneString = ANSI_GREEN + "[SUCCESS] " + ANSI_RESET;

  private static final String infoString = ANSI_YELLOW + "[INFO] " + ANSI_RESET;
  private static final String warningString = ANSI_RED + "[WARNING] " + ANSI_RESET;
  private static final String successString = ANSI_CYAN + "[INFO] " + ANSI_RESET;

  /**
   * Constructor initializes the event collection model.
   */
  public EventHandler() {
    calenderCollection = new CalenderCollection();
    createCalender("\"default\"", ZoneId.of("UTC"));
    eventCollection = calenderCollection.getEventCollection("default");
  }

  /**
   * Adds a UI listener to receive notifications about model changes.
   *
   * @param listener The UI listener to add
   */
  public void addUIListener(CalendarUIListener listener) {
    uiListeners.add(listener);
  }

  /**
   * Removes a UI listener.
   *
   * @param listener The UI listener to remove
   */
  public void removeUIListener(CalendarUIListener listener) {
    uiListeners.remove(listener);
  }

  /**
   * Notifies all registered UI listeners that the current calendar has changed.
   *
   * @param calendarName The name of the current calendar
   */
  private void notifyCalendarChanged(String calendarName) {
    for (CalendarUIListener listener : uiListeners) {
      listener.onCalendarChanged(calendarName);
    }
  }

  /**
   * Notifies all registered UI listeners that the events have changed.
   */
  private void notifyEventsChanged() {
    for (CalendarUIListener listener : uiListeners) {
      listener.onEventsChanged(calenderCollection);
    }
  }

  /**
   * Notifies all registered UI listeners that the calendar list has changed.
   */
  private void notifyCalendarListChanged() {
    for (CalendarUIListener listener : uiListeners) {
      listener.onCalendarListChanged();
    }
  }

  /**
   * Notifies all registered UI listeners with a user-friendly message.
   *
   * @param message The message to display to the user
   */
  private void notifyUserMessage(String message) {
    for (CalendarUIListener listener : uiListeners) {
      listener.onCommandOutput(message);
    }
  }

  /**
   * Sets the event collection for a specific calendar.
   *
   * @param calenderName the name of the calendar to use.
   */
  public void useCalender(String calenderName) {
    eventCollection = calenderCollection.getEventCollection(calenderName);
    notifyCalendarChanged(calenderName);
    notifyEventsChanged();
  }

  /**
   * Processes an event copy operation on a specific date, copying an event from one calendar
   * to another at a specified time.
   *
   * @param matcher the matcher object containing the event details, including event name,
   *                event date, target calendar, and target calendar date.
   */
  public void processCopyEventOn(Matcher matcher) {
    try {
      String eventName = matcher.group(1);
      LocalDateTime eventDateTime = LocalDateTime.parse(matcher.group(2));
      String tarCalName = matcher.group(3);
      LocalDateTime tarCalDateTime = LocalDateTime.parse(matcher.group(4));

      String sourceCalendar = calenderCollection.getCalendarName(eventCollection);

      calenderCollection.copyCalendarEventOn(sourceCalendar, eventName,
              eventDateTime, tarCalName, tarCalDateTime);
      System.out.println(successString + eventName + " copied to calendar");
      notifyEventsChanged();
    } catch (Exception e) {
      System.out.println(warningString + "Error copying events: " + e.getMessage());
    }
  }

  /**
   * Processes an operation to copy events between two specified dates from one calendar to another.
   *
   * @param matcher the matcher object containing the date range and target calendar details.
   */
  public void processCopyCalenderBetweenDates(Matcher matcher) {
    try {
      LocalDate fromDate = LocalDate.parse(matcher.group(1));
      LocalDate toDate = LocalDate.parse(matcher.group(2));
      String targetCalendarName = matcher.group(3);
      LocalDate targetStartDate = LocalDate.parse(matcher.group(4));

      String sourceCalendar = calenderCollection.getCalendarName(eventCollection);

      calenderCollection.copyCalenderBetweenDates(fromDate, toDate, sourceCalendar,
              targetStartDate, targetCalendarName);

      System.out.println(successString + " Events from " + fromDate + " to "
              + toDate + " copied to calendar " + targetCalendarName);
      notifyEventsChanged();
    } catch (Exception e) {
      System.out.println(warningString + "Error copying events: " + e.getMessage());
    }
  }

  /**
   * Processes an operation to copy all events from a specific day in one calendar to another.
   *
   * @param matcher the matcher object containing the event date and target calendar details.
   */
  public void processCopyEventsOn(Matcher matcher) {
    try {
      LocalDate eventDateTime = LocalDate.parse(matcher.group(1));
      String targetCalendar = matcher.group(2);
      LocalDate targetDateTime = LocalDate.parse(matcher.group(3));

      String sourceCalendar = calenderCollection.getCalendarName(eventCollection);

      calenderCollection.copyCalenderEntireDay(eventDateTime, sourceCalendar,
              targetDateTime, targetCalendar);
      System.out.println(successString + "Events on " + eventDateTime + " copied to calendar");
      notifyEventsChanged();
    } catch (Exception e) {
      System.out.println(warningString + "Error copying events: " + e.getMessage());
    }
  }

  /**
   * Processes an operation to copy events with the same name between specified date ranges
   * from one calendar to another.
   *
   * @param eventName          the name of the event to copy.
   * @param fromDate           the start date for event copying.
   * @param toDate             the end date for event copying.
   * @param sourceCalendarName the name of the source calendar.
   * @param targetStartDate    the start date for the target calendar.
   * @param targetCalendarName the name of the target calendar.
   */
  public void processCopyCalendarBetweenDatesWithName(String eventName, LocalDateTime fromDate,
                                                      LocalDateTime toDate,
                                                      String sourceCalendarName,
                                                      LocalDateTime targetStartDate,
                                                      String targetCalendarName) {
    try {
      calenderCollection.copyEventsWithSameNameBetweenDates(eventName, fromDate, toDate,
              sourceCalendarName, targetStartDate, targetCalendarName);
      System.out.println(successString + "Events " + eventName + " copied to calendar");
      notifyEventsChanged();
    } catch (Exception e) {
      System.out.println(warningString + "Error copying events: " + e.getMessage());
    }
  }

  /**
   * Processes an operation to edit a calendar's properties such as name or time zone.
   *
   * @param matcher the matcher object containing the calendar name,
   *                property to edit, and new value.
   */
  public void processEditCalendar(Matcher matcher) {
    try {
      String calName = matcher.group(1);
      String property = matcher.group(2);
      String propertyValue = matcher.group(3);

      switch (property) {
        case "name":
          calenderCollection.changeCalenderName(calName, propertyValue);
          notifyCalendarChanged(propertyValue);
          notifyCalendarListChanged();
          break;

        case "timezone":
          ZoneId newZone = ZoneId.of(propertyValue);
          calenderCollection.changeCalendarTimeZone(calName, newZone);
          notifyCalendarChanged(calName);
          break;

        default:
          throw new IllegalArgumentException("Invalid property: " + property);
      }
      System.out.println(successString + calName + " property: "
              +  property + " changed to: " + propertyValue);
    } catch (Exception e) {
      System.out.println(warningString + "Error editing events: " + e.getMessage());
    }
  }

  /**
   * Processes the operation to switch to a specified calendar.
   *
   * @param matcher the matcher object containing the calendar name to switch to.
   */
  void processUseCalendar(Matcher matcher) {
    try {
      String calName = matcher.group(1);
      useCalender(calName);
      System.out.println(successString + "Changed to calendar: " + calName);
      // No need to print success message here as it's done in useCalender method
    } catch (Exception e) {
      String errorMsg = "Error changing calendar: " + e.getMessage();
      System.out.println(warningString + "Error changing events: " + e.getMessage());
      notifyUserMessage(errorMsg);
    }
  }
  /**
   * Retrieves the event collection managed by the controller.
   *
   * @return The EventCollectionModel instance.
   */
  public EventCollectionModel getEventCollection(String calenderName) {
    return calenderCollection.getEventCollection(calenderName);
  }

  public EventCollectionModel getEventCollection() {
    return eventCollection;
  }

  public CalenderCollection getCalenderCollection() {
    return calenderCollection;
  }

  public int getCalenderCollectionSize() {
    return calenderCollection.getCalenderCollectionSize();
  }

  /**
   * Creates a new calendar with the specified name and time zone.
   *
   * @param calenderName the name of the new calendar.
   * @param timezone     the time zone to associate with the calendar.
   */
  void createCalender(String calenderName, ZoneId timezone) {
    try {
      calenderCollection.addCalender(calenderName, timezone);
      notifyCalendarListChanged();

      // Send user-friendly message
      String message = "Calendar \"" + calenderName + "\" created with timezone " + timezone;
      System.out.println(successString + message);
      notifyUserMessage(message);
    } catch (Exception e) {
      String errorMsg = "Error creating calendar: " + e.getMessage();
      System.out.println(warningString + errorMsg);
      notifyUserMessage(errorMsg);
    }
  }

  /**
   * Processes the creation of a new calendar by parsing the input matcher and
   * handling the creation with the specified name and time zone.
   *
   * @param matcher the matcher object containing the calendar name and time zone as strings.
   */
  void processCreateCalendar(Matcher matcher) {
    String calName = matcher.group(1);
    String timeZoneString = matcher.group(2);

    try {
      ZoneId timeZone = ZoneId.of(timeZoneString);
      createCalender(calName, timeZone);
      // No need to print success message here as it's done in createCalender method
    } catch (Exception e) {
      String errorMsg = "Failed to create calendar: Invalid time zone: " + timeZoneString;
      System.out.println(warningString + errorMsg);
      notifyUserMessage(errorMsg);
    }
  }

  /**
   * Processes the creation of an event from a regex matcher.
   * Format: create event --autoDecline {@code <eventName>} from
   * {@code <dateString>T<timeString>} to {@code <dateString>T<timeString>}.
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateEvent(Matcher matcher) {
    String autoDeclineString = matcher.group(1);
    Boolean autoDecline = autoDeclineString != null;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);
    String endDateTime = matcher.group(4);

    String description = matcher.group(5);
    String location = matcher.group(6);
    boolean privacy = matcher.group(7) != null && Boolean.parseBoolean(matcher.group(7));

    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    LocalDateTime endDate = LocalDateTime.parse(endDateTime);
    if (isEndBeforeStart(startDate, endDate)) {
      throw new IllegalArgumentException("End time cannot be before " +
              "start time, Event was not created");
    }

    EventModel event = new EventModel(eventName, startDate, endDate, description,
            privacy, autoDecline, location);
    addEvent(event);
    notifyEventsChanged();
  }

  /**
   * Processes the creation of a recurring event for a specified number of times.
   * Format: create event {@code <eventName>} from {@code <dateString>T<timeString>} to
   * {@code <dateString>T<timeString>} repeats {@code <weekdays>} for {@code <N>} times.
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateRecurringEventN(Matcher matcher) {
    String autoDeclineString = matcher.group(1);
    Boolean autoDecline = autoDeclineString != null;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);
    String endDateTime = matcher.group(4);
    String weekDay = matcher.group(5);
    Integer numOfTimes = Integer.valueOf(matcher.group(6));

    String description = matcher.group(7);
    String location = matcher.group(8);
    Boolean privacy = matcher.group(9) != null && Boolean.parseBoolean(matcher.group(9));

    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    LocalDateTime endDate = LocalDateTime.parse(endDateTime);
    if (isEndBeforeStart(startDate, endDate)) {
      throw new IllegalArgumentException("End time cannot be before " +
              "start time, Event was not created");
    }

    List<Character> recurringWeekDay = stringToDayOfWeek(weekDay);
    RecurringEventModel recurringEvent = new RecurringEventModel(eventName,
            startDate, endDate, description,
            privacy, autoDecline, location,
            recurringWeekDay, numOfTimes,
            null);

    addRecurringEvent(recurringEvent);
    notifyEventsChanged();
  }

  /**
   * Processes the creation of a recurring event that repeats until a specified end date.
   * Format: create event --autoDecline {@code <eventName>} from
   * {@code <dateString>T<timeString>} to
   * {@code <dateString>T<timeString>} repeats {@code <weekdays>} until
   * {@code <dateString>T<timeString>}.
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateRecurringEventEndDate(Matcher matcher) {
    String autoDeclineString = matcher.group(1);
    Boolean autoDecline = autoDeclineString != null;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);
    String endDateTime = matcher.group(4);
    String weekDay = matcher.group(5);
    String recurrernceEndDate = matcher.group(6);

    String description = matcher.group(7);
    String location = matcher.group(8);
    Boolean privacy = matcher.group(9) != null && Boolean.parseBoolean(matcher.group(9));

    List<Character> recurringWeekDay = stringToDayOfWeek(weekDay);
    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    LocalDateTime endDate = LocalDateTime.parse(endDateTime);

    if (isEndBeforeStart(startDate, endDate)) {
      throw new IllegalArgumentException("End time cannot be before " +
              "start time, Event was not created");
    }

    LocalDateTime recurrernceEndDateTime = LocalDateTime.parse(recurrernceEndDate);

    RecurringEventModel recurringEvent = new RecurringEventModel(eventName,
            startDate, endDate, description,
            privacy, autoDecline, location,
            recurringWeekDay, null,
            recurrernceEndDateTime);

    addRecurringEvent(recurringEvent);
    notifyEventsChanged();
  }

  /**
   * Processes the creation of an all-day event.
   * Format: create event --autoDecline {@code <eventName>} on {@code <dateString>T<timeString>}.
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateAllDayEvent(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString != null;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);

    String description = matcher.group(4);
    String location = matcher.group(5);
    Boolean privacy = matcher.group(6) != null && Boolean.parseBoolean(matcher.group(6));

    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    EventModel event = new EventModel(eventName, startDate, null, description
            , privacy, autoDecline, location);
    addEvent(event);
    notifyEventsChanged();
  }

  /**
   * Processes the creation of an all-day recurring event that
   * repeats for a specified number of times.
   * Format: create event {@code <eventName>} on {@code <dateString>} repeats {@code <weekdays>}
   * for {@code <N>} times.
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateAllDayRecurring(Matcher matcher) {
    String autoDeclineString = matcher.group(1);
    Boolean autoDecline = autoDeclineString != null;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);

    String weekDay = matcher.group(4);
    Integer numOfTimes = Integer.valueOf(matcher.group(5));

    String description = matcher.group(6);
    String location = matcher.group(7);
    Boolean privacy = matcher.group(8) != null && Boolean.parseBoolean(matcher.group(8));

    LocalDateTime startDate = LocalDate.parse(startDateTime).atStartOfDay();
    List<Character> recurringWeekDay = stringToDayOfWeek(weekDay);

    RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDate,
            null, description,
            privacy, autoDecline, location,
            recurringWeekDay, numOfTimes,
            null);
    addRecurringEvent(recurringEvent);
    notifyEventsChanged();
  }

  /**
   * Processes the creation of an all-day recurring event that repeats until a specified end date.
   * Format: create event {@code <eventName>} on {@code <dateString>} repeats {@code <weekdays>}
   * until {@code <dateString>}.
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateRecurringWholeDayEventEnd(Matcher matcher) {
    String autoDeclineString = matcher.group(1);
    Boolean autoDecline = autoDeclineString != null;
    String eventName = matcher.group(2);
    String startDate = matcher.group(3);

    String weekDay = matcher.group(4);
    String recurrernceEndDate = matcher.group(5);

    String description = matcher.group(6);
    String location = matcher.group(7);
    Boolean privacy = matcher.group(8) != null && Boolean.parseBoolean(matcher.group(8));

    List<Character> recurringWeekDay = stringToDayOfWeek(weekDay);
    LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
    LocalDateTime recurrernceEndDateTime = LocalDate.parse(recurrernceEndDate).atStartOfDay();

    RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDateTime,
            null, description,
            privacy, autoDecline, location,
            recurringWeekDay, null,
            recurrernceEndDateTime);
    addRecurringEvent(recurringEvent);
    notifyEventsChanged();
  }

  /**
   * Processes the editing of a specific event property between two dates.
   * Format: edit event {@code <property>} {@code <eventName>} from
   * {@code <dateString>T<timeString>} to
   * {@code <dateString>T<timeString>} with {@code <NewPropertyValue>}.
   *
   * @param matcher The regex matcher containing event details.
   */
  void processEditEvent(Matcher matcher) {
    String property = matcher.group(1);
    String eventName = matcher.group(2);
    String startDate = matcher.group(3);
    String endDate = matcher.group(4);
    String newProperty = matcher.group(5);

    try {
      LocalDateTime startDateTime = LocalDateTime.parse(startDate);
      LocalDateTime endDateTime = LocalDateTime.parse(endDate);
      if (isEndBeforeStart(startDateTime, endDateTime)) {
        String errorMsg = "End time cannot be before start time, event was not edited";
        System.out.println(warningString + errorMsg);
        notifyUserMessage(errorMsg);
        return;
      }

      // Count affected events
      int count = countEventsForEdit(property, eventName, startDateTime, endDateTime);

      if (count == 0) {
        String errorMsg = "No events named \"" + eventName + "\" found between "
                + startDate + " and " + endDate;
        System.out.println(warningString + errorMsg);
        notifyUserMessage(errorMsg);
        return;
      }

      editEvents(property, newProperty, eventName, startDateTime, endDateTime);
      notifyEventsChanged();

      // Send user-friendly message
      String message = "Successfully edited " + property + " to \"" + newProperty + "\" for ";
      if (count == 1) {
        message += "1 occurrence of event \"" + eventName + "\"";
      } else {
        message += count + " occurrences of event \"" + eventName + "\"";
      }
      message += " between " + startDate + " and " + endDate;

      System.out.println(successString + message);
      notifyUserMessage(message);
    } catch (Exception e) {
      String errorMsg = "Error editing event: " + e.getMessage();
      System.out.println(warningString + errorMsg);
      notifyUserMessage(errorMsg);
    }
  }

  /**
   * Processes the editing of a property for all events with the specified name from a start date.
   * Format: edit events {@code <property>} {@code <eventName>} from
   * {@code <dateString>T<timeString>} with {@code <NewPropertyValue>}.
   *
   * @param matcher The regex matcher containing event details.
   */
  void processEditEvents(Matcher matcher) {
    String property = matcher.group(1);
    String eventName = matcher.group(2);
    String startDate = matcher.group(3);
    String newProperty = matcher.group(4);

    try {
      LocalDateTime startDateTime = LocalDateTime.parse(startDate);

      // Count affected events
      int count = countEventsForEdit(property, eventName, startDateTime, null);

      if (count == 0) {
        String errorMsg = "No events named \"" + eventName + "\" found from " + startDate + " onwards";
        System.out.println(warningString + errorMsg);
        notifyUserMessage(errorMsg);
        return;
      }

      editEvents(property, newProperty, eventName, startDateTime, null);
      notifyEventsChanged();

      // Send user-friendly message
      String message = "Successfully edited " + property + " to \"" + newProperty + "\" for ";
      if (count == 1) {
        message += "1 occurrence of event \"" + eventName + "\"";
      } else {
        message += count + " occurrences of event \"" + eventName + "\"";
      }
      message += " from " + startDate + " onwards";

      System.out.println(successString + message);
      notifyUserMessage(message);
    } catch (Exception e) {
      String errorMsg = "Error editing events: " + e.getMessage();
      System.out.println(warningString + errorMsg);
      notifyUserMessage(errorMsg);
    }
  }
  /**
   * Processes the editing of a property for all events with the specified name.
   * Format: edit events {@code <property>} {@code <eventName>} {@code <NewPropertyValue>}.
   *
   * @param matcher The regex matcher containing event details.
   */
  void processEditEventsByName(Matcher matcher) {
    String property = matcher.group(1);
    String eventName = matcher.group(2);
    String newProperty = matcher.group(3);

    try {
      // Count affected events
      int count = countEventsForEdit(property, eventName, null, null);

      if (count == 0) {
        String errorMsg = "No events named \"" + eventName + "\" found";
        System.out.println(warningString + errorMsg);
        notifyUserMessage(errorMsg);
        return;
      }

      editEvents(property, newProperty, eventName, null, null);
      notifyEventsChanged();

      // Send user-friendly message
      String message = "Successfully edited " + property + " to \"" + newProperty + "\" for ";
      if (count == 1) {
        message += "1 occurrence of event \"" + eventName + "\"";
      } else {
        message += count + " occurrences of event \"" + eventName + "\"";
      }

      System.out.println(successString + message);
      notifyUserMessage(message);
    } catch (Exception e) {
      String errorMsg = "Error editing events: " + e.getMessage();
      System.out.println(warningString + errorMsg);
      notifyUserMessage(errorMsg);
    }
  }

  /**
   * Prints all events scheduled on a specific date.
   * Format: print events on {@code <dateString>}.
   * Prints a bulleted list of all events on that day along with
   * their start and end time and location.
   *
   * @param matcher The regex matcher containing date details.
   */
  void processPrintEventsOn(Matcher matcher) {
    try {
      String startDate = matcher.group(1);
      LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
      LocalDateTime endDateTime = startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));

      // Search for events in the timeframe
      List<EventModel> printList = eventCollection.searchEventByTimeFrame(startDateTime, endDateTime);

      // Get calendar name and count events
      String calendarName = calenderCollection.getCalendarName(eventCollection);
      int eventCount = printList.size();

      // Send user-friendly message
      String message = "Found " + eventCount + " event" + (eventCount != 1 ? "s" : "") +
              " on " + startDate + " in calendar \"" + calendarName + "\"";
      System.out.println(infoString + message);

      // Print the events to console
      String printOutput = eventCollection.printAssortments(printList);
      System.out.println(printOutput);

      // Send both the message and the events list to the UI separately
      for (CalendarUIListener listener : uiListeners) {
        listener.onCommandOutput(message);
        if (!printList.isEmpty()) {
          listener.onCommandOutput(printOutput);
        }
      }
    } catch (Exception e) {
      String errorMsg = "Error printing events: " + e.getMessage();
      System.out.println(warningString + errorMsg);
      notifyUserMessage(errorMsg);
    }
  }

  /**
   * Prints all events scheduled between two dates and times.
   * Format: print events from {@code <dateString>T<timeString>} to
   * {@code <dateString>T<timeString>}.
   *
   * @param matcher The regex matcher containing date range details.
   */
  void processPrintEventsBetween(Matcher matcher) {
    String startDate = matcher.group(1);
    String endDate = matcher.group(2);
    LocalDateTime startDateTime = LocalDateTime.parse(startDate);
    LocalDateTime endDateTime = LocalDateTime.parse(endDate);

    if (isEndBeforeStart(startDateTime, endDateTime)) {
      throw new IllegalArgumentException("End time cannot be before" +
              " start time, Event was not created");
    }

    List<EventModel> printList = new ArrayList<>();
    printList = eventCollection.searchEventByTimeFrame(startDateTime, endDateTime);
    System.out.println(infoString + "Printing events on " + startDate + " to " + endDate + "...");
    System.out.println(eventCollection.printAssortments(printList));
    for (CalendarUIListener listener : uiListeners) {
      listener.onCommandOutput("Printing events on " + startDate + " to " + endDate
              + "...\n"+eventCollection.printAssortments(printList));
    }
  }

  void processPrintCalendars() {
    String printList = calenderCollection.printCalendars();
    System.out.println(printList);
  }

  /**
   * Exports all calendar events to a CSV file.
   * Format: export cal fileName.csv.
   *
   * @param matcher The regex matcher containing the file name.
   */
  void processExport(Matcher matcher) {
    String fileName = matcher.group(1);

    System.out.println(infoString + "Exporting to " + fileName + "...");

    try {
      File exportFile = new File(fileName);

      if (!exportFile.exists()) {
        try {
          if (exportFile.createNewFile()) {
            String fileCreatedMsg = "File \"" + fileName + "\" did not exist. A new file has been created at "
                    + exportFile.getAbsolutePath();
            System.out.println(warningString + fileCreatedMsg);
            notifyUserMessage(fileCreatedMsg);
          }
        } catch (IOException e) {
          String errorMsg = "Error creating file: " + exportFile.getAbsolutePath() + " - " + e.getMessage();
          System.out.println(warningString + errorMsg);
          notifyUserMessage(errorMsg);
          return;
        }
      }

      List<EventModel> eventList = eventCollection.getEventList();
      String calendarName = calenderCollection.getCalendarName(eventCollection);

      ExportToCSV.generateCSV(fileName, eventList);

      // Send user-friendly message
      String message = "Successfully exported " + eventList.size() + " events from calendar \""
              + calendarName + "\" to file \"" + exportFile.getAbsolutePath() + "\"";
      System.out.println(doneString + message);
      notifyUserMessage(message);
    } catch (Exception e) {
      String errorMsg = "Error exporting calendar: " + e.getMessage();
      System.out.println(errorString + errorMsg);
      notifyUserMessage(errorMsg);
    }
  }

  void processImport(Matcher matcher) {
    String fileName = matcher.group(1);

    System.out.println(infoString + "Importing from " + fileName + "...");

    File importFile = new File(fileName);

    if (!importFile.exists()) {
      String errorMsg = "File " + importFile.getAbsolutePath() + " does not exist.";
      System.out.println(errorString + errorMsg);
      notifyUserMessage(errorMsg);
      return;
    }

    String lowerFileName = fileName.toLowerCase();
    ImportInterface importer;

    if (lowerFileName.endsWith(".csv")) {
      importer = new ImportFromCSV();
    } else if (lowerFileName.endsWith(".ics")) {
      return;
      //importer = new ImportFromICS();
    } else {
      String errorMsg = "File \"" + fileName + "\" is not a supported filetype";
      System.out.println(errorMsg);
      notifyUserMessage(errorMsg);
      return;
    }

    try {
      List<EventModel> importedEvents = importer.importEvents(importFile);
      if (importedEvents.isEmpty()) {
        String errorMsg = "No events found in " + importFile.getAbsolutePath();
        System.out.println(errorMsg);
        notifyUserMessage(errorMsg);
      } else {
        for (EventModel importedEvent : importedEvents) {
          eventCollection.addEvent(importedEvent);
        }

        String message = "Successfully imported " + importedEvents.size() + " events";
        System.out.println(doneString + message);
        notifyUserMessage(message);
      }
    } catch (Exception e) {
      String errorMsg = "Error importing calendar: " + e.getMessage();
      System.out.println(errorString + errorMsg);
      notifyUserMessage(errorMsg);
    }

  }

  /**
   * Shows availability status at a specific date and time.
   * Format: show status on {@code <dateString>T<timeString>}.
   * Prints busy status if the user has events scheduled
   * on a given day and time, otherwise, available.
   *
   * @param matcher The regex matcher containing date and time details.
   */
  void processShowStatus(Matcher matcher) {
    String startDate = matcher.group(1);
    LocalDateTime startDateTime = LocalDateTime.parse(startDate);
    System.out.println(infoString + eventCollection.checkStatus(startDateTime,
            startDateTime) + " on " + startDate);
  }

  /**
   * Converts a string representation of weekdays to a list of characters.
   * Example: "MT" would be converted to a list containing 'M' and 'T'.
   *
   * @param weekDay String representation of weekdays (e.g., "MT" for Monday and Tuesday).
   * @return List of characters representing each day of the week.
   */
  private List<Character> stringToDayOfWeek(String weekDay) {
    List<Character> dayOfWeek = new ArrayList<>();
    for (int i = 0; i < weekDay.length(); i++) {
      dayOfWeek.add(weekDay.charAt(i));
    }
    return dayOfWeek;
  }

  /**
   * Edits event properties based on the specified criteria.
   * Supports editing properties like startDateTime, endDateTime, location, description,
   * privacyType, and eventName.
   *
   * @param property      The property to edit
   * @param newProperty   The new value for the property
   * @param eventName     The name of the event to edit
   * @param startDateTime The start date-time to filter events (can be null)
   * @param endDateTime   The end date-time to filter events (can be null)
   */
  private void editEvents(String property, String newProperty, String eventName,
                          LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (property.contains("startDateTime")) {
      setStartDateTime(LocalDateTime.parse(newProperty), eventName, startDateTime, endDateTime);
    } else if (property.contains("endDateTime")) {
      setEndDateTime(LocalDateTime.parse(newProperty), eventName, startDateTime, endDateTime);
    } else if (property.contains("location")) {
      setLocation(newProperty, eventName, startDateTime, endDateTime);
    } else if (property.contains("description")) {
      setDescription(eventName, newProperty, startDateTime, endDateTime);
    } else if (newProperty.contains("private") && property.contains("privacyType")) {
      setIsPrivate(eventName, true, startDateTime, endDateTime);
    } else if (newProperty.contains("public") && property.contains("privacyType")) {
      setIsPrivate(eventName, false, startDateTime, endDateTime);
    } else if (property.contains("eventName")) {
      setEventName(eventName, newProperty, startDateTime, endDateTime);
    }
  }

  /**
   * Sets the start date-time for events matching the criteria.
   *
   * @param newProperty   The new start date-time.
   * @param eventName     The name of the event to update.
   * @param startDateTime The start date-time filter (can be null).
   * @param endDateTime   The end date-time filter (can be null).
   * @return Void (null)
   */
  private Void setStartDateTime(LocalDateTime newProperty, String eventName,
                                LocalDateTime startDateTime, LocalDateTime endDateTime) {
    for (EventModel event : eventCollection.getEventList()) {
      // if start and enddate is specified then we need to check
      // if the event given is between the two dates
      if (isEndBeforeStart(newProperty,event.getEndDateTime())){
        throw new RuntimeException("End date should be before start date");
      }

      if (event.getEventName().equals(eventName) && checkEventBetweenDates(event,
              startDateTime, endDateTime) ) {
        event.setStartDateTime(newProperty);

      }
    }
    throw new RuntimeException("Event " + eventName + " not found");


  }

  /**
   * Sets the end date-time for events matching the criteria.
   *
   * @param newProperty   The new end date-time.
   * @param eventName     The name of the event to update.
   * @param startDateTime The start date-time filter (can be null).
   * @param endDateTime   The end date-time filter (can be null).
   * @return Void (null)
   */
  private Void setEndDateTime(LocalDateTime newProperty, String eventName,
                              LocalDateTime startDateTime, LocalDateTime endDateTime) {
    for (EventModel event : eventCollection.getEventList()) {
      if (isEndBeforeStart(newProperty,event.getEndDateTime())){
        throw new RuntimeException("End date should be before start date");
      }
      if (event.getEventName().equals(eventName) && checkEventBetweenDates(event, startDateTime,
              endDateTime)&& isEndBeforeStart(event.getStartDateTime(),newProperty)) {
        event.setEndDateTime(newProperty);
      }
    }
    throw new RuntimeException("Event " + eventName + " not found");
  }

  /**
   * Sets the location for events matching the criteria.
   *
   * @param location      The new location.
   * @param eventName     The name of the event to update.
   * @param startDateTime The start date-time filter (can be null).
   * @param endDateTime   The end date-time filter (can be null).
   * @return Void (null)
   */
  private Void setLocation(String location, String eventName, LocalDateTime startDateTime,
                           LocalDateTime endDateTime) {
    for (EventModel event : eventCollection.getEventList()) {
      if (event.getEventName().equals(eventName) && checkEventBetweenDates(event,
              startDateTime, endDateTime)) {
        event.setLocation(location);
      }
    }
    throw new RuntimeException("Event " + eventName + " not found");
  }

  /**
   * Sets the event name for events matching the criteria.
   *
   * @param eventName     The current name of the event.
   * @param newEventName  The new name for the event.
   * @param startDateTime The start date-time filter (can be null).
   * @param endDateTime   The end date-time filter (can be null).
   * @return Void (null).
   */
  private Void setEventName(String eventName, String newEventName, LocalDateTime startDateTime,
                            LocalDateTime endDateTime) {
    for (EventModel event : eventCollection.getEventList()) {
      if (event.getEventName().equals(eventName) && checkEventBetweenDates(event,
              startDateTime, endDateTime)) {
        event.setEventName(newEventName);
      }
    }
    throw new RuntimeException("Event " + eventName + " not found");
  }

  /**
   * Sets the privacy status for events matching the criteria.
   *
   * @param eventName     The name of the event to update
   * @param isPrivate     The new privacy status
   * @param startDateTime The start date-time filter (can be null)
   * @param endDateTime   The end date-time filter (can be null)
   * @return Void (null)
   */
  private Void setIsPrivate(String eventName, boolean isPrivate, LocalDateTime startDateTime,
                            LocalDateTime endDateTime) {
    for (EventModel event : eventCollection.getEventList()) {
      if (event.getEventName().equals(eventName) && checkEventBetweenDates(event,
              startDateTime, endDateTime)) {
        event.setIsPrivate(isPrivate);
      }
    }
    throw new RuntimeException("Event " + eventName + " not found");
  }

  /**
   * Sets the description for events matching the criteria.
   *
   * @param eventName     The name of the event to update
   * @param description   The new description
   * @param startDateTime The start date-time filter (can be null)
   * @param endDateTime   The end date-time filter (can be null)
   * @return Void (null)
   */
  private Void setDescription(String eventName, String description, LocalDateTime startDateTime,
                              LocalDateTime endDateTime) {
    for (EventModel event : eventCollection.getEventList()) {
      if (event.getEventName().equals(eventName) && checkEventBetweenDates(event, startDateTime,
              endDateTime)) {
        event.setDescription(description);
      }
    }
    throw new RuntimeException("Event " + eventName + " not found");
  }

  /**
   * Checks if an event falls between the specified date-time filters.
   *
   * @param event         The event to check
   * @param startDateTime The start date-time filter (can be null)
   * @param endDateTime   The end date-time filter (can be null)
   * @return true if the event matches the filter criteria, false otherwise
   */
  private boolean checkEventBetweenDates(EventModel event, LocalDateTime startDateTime,
                                         LocalDateTime endDateTime) {
    if (startDateTime == null && endDateTime == null) {
      return true;
    } else if (startDateTime != null && endDateTime == null) {
      return event.isAfterDateTime(startDateTime);
    } else {
      return event.isEventBetweenDates(startDateTime, endDateTime);
    }
  }

  /**
   * Checks if the end date-time is before the start date-time.
   *
   * @param startDateTime The start date-time to check
   * @param endDateTime   The end date-time to check
   * @return true if end date-time is before start date-time, false otherwise
   */
  public boolean isEndBeforeStart(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return endDateTime.isBefore(startDateTime);
  }

  /**
   * Adds an event to the current calendar's event collection.
   * By default, autoDecline is set to true, which means events with time conflicts
   * will not be created.
   *
   * @param event The event to be added.
   * @return true if the event was added successfully, false if there was a conflict.
   */
  public boolean addEvent(EventModel event) {
    // Force autoDecline to true by default
    boolean autoDecline = true;

    // Check for conflicts
    boolean hasConflict = eventCollection.checkConflict(event.getStartDateTime(), event.getEndDateTime());

    if (autoDecline && hasConflict) {
      String errorMsg = "Event \"" + event.getEventName() +
              "\" was not created due to time conflict with an existing event";
      System.out.println(warningString + errorMsg);
      notifyUserMessage(errorMsg);
      return false;
    }

    // Add event if no conflict or autoDecline is false
    eventCollection.addEvent(event);
    String dateTime = formatDateTime(event);
    String calendarName = calenderCollection.getCalendarName(eventCollection);

    String successMsg = "Event \"" + event.getEventName() + "\" " + dateTime +
            " successfully created in calendar \"" + calendarName + "\"";
    System.out.println(doneString + successMsg);
    notifyUserMessage(successMsg);
    notifyEventsChanged();
    return true;
  }

  /**
   * Adds a recurring event to the current calendar's event collection.
   * By default, autoDecline is set to true, which means events with time conflicts
   * will not be created.
   *
   * @param recurringEvent The recurring event to be added.
   * @return true if the recurring event was added successfully, false if there was a conflict.
   */
  public boolean addRecurringEvent(RecurringEventModel recurringEvent) {
    // Force autoDecline to true by default
    boolean autoDecline = true;

    // Generate all occurrences to check for conflicts
    List<EventModel> recurringEventsList = eventCollection.generateOccurrences(recurringEvent);

    // Check each occurrence for conflicts
    for (EventModel event : recurringEventsList) {
      if (eventCollection.checkConflict(event.getStartDateTime(), event.getEndDateTime())) {
        String conflictDateTime = "on " + event.getStartDateTime().toLocalDate() +
                " at " + event.getStartDateTime().toLocalTime();

        String errorMsg = "Recurring event \"" + recurringEvent.getEventName() +
                "\" was not created due to time conflict " + conflictDateTime;
        System.out.println(warningString + errorMsg);
        notifyUserMessage(errorMsg);
        return false;
      }
    }

    // If no conflicts or autoDecline is false, add all occurrences individually
    for (EventModel event : recurringEventsList) {
      eventCollection.addEvent(event);
    }
    String recurrenceDesc = buildRecurrenceDescription(recurringEvent);
    String calendarName = calenderCollection.getCalendarName(eventCollection);

    String successMsg = "Created recurring event \"" + recurringEvent.getEventName() + "\" " +
            recurrenceDesc + " in calendar \"" + calendarName + "\" (" +
            recurringEventsList.size() + " occurrences)";

    System.out.println(doneString + successMsg);
    notifyUserMessage(successMsg);
    notifyEventsChanged();
    return true;
  }
  /**
   * Formats a datetime display string for an event based on whether it's all-day or timed.
   *
   * @param event The event model to format
   * @return A user-friendly date/time string
   */
  private String formatDateTime(EventModel event) {
    if (event.getEndDateTime() == null) {
      // All-day event
      return "on " + event.getStartDateTime().toLocalDate();
    } else {
      // Timed event
      return "from " + event.getStartDateTime().toLocalDate() +
              " " + event.getStartDateTime().toLocalTime() +
              " to " + event.getEndDateTime().toLocalDate() +
              " " + event.getEndDateTime().toLocalTime();
    }
  }
  /**
   * Builds a user-friendly description of a recurring event pattern.
   *
   * @param recurringEvent The recurring event model
   * @return A string describing the recurrence pattern
   */
  private String buildRecurrenceDescription(RecurringEventModel recurringEvent) {
    StringBuilder builder = new StringBuilder();

    // Add basic datetime info
    if (recurringEvent.getEndDateTime() == null) {
      // All-day event
      builder.append("on ")
              .append(recurringEvent.getStartDateTime().toLocalDate());
    } else {
      // Timed event
      builder.append("from ")
              .append(recurringEvent.getStartDateTime().toLocalDate())
              .append(" ")
              .append(recurringEvent.getStartDateTime().toLocalTime())
              .append(" to ")
              .append(recurringEvent.getEndDateTime().toLocalDate())
              .append(" ")
              .append(recurringEvent.getEndDateTime().toLocalTime());
    }

    // Add recurrence pattern
    builder.append(" repeating on ");
    List<Character> weekdays = recurringEvent.getRecurringDays();
    for (int i = 0; i < weekdays.size(); i++) {
      if (i > 0) {
        if (i == weekdays.size() - 1) {
          builder.append(" and ");
        } else {
          builder.append(", ");
        }
      }

      switch (weekdays.get(i)) {
        case 'M': builder.append("Monday"); break;
        case 'T': builder.append("Tuesday"); break;
        case 'W': builder.append("Wednesday"); break;
        case 'R': builder.append("Thursday"); break;
        case 'F': builder.append("Friday"); break;
        case 'S': builder.append("Saturday"); break;
        case 'U': builder.append("Sunday"); break;
        default: builder.append(weekdays.get(i));
      }
    }

    // Add termination condition
    if (recurringEvent.getOccurrences() != null) {
      builder.append(" for ")
              .append(recurringEvent.getOccurrences())
              .append(" times");
    } else if (recurringEvent.getRecurrenceEndDateTime() != null) {
      builder.append(" until ")
              .append(recurringEvent.getRecurrenceEndDateTime().toLocalDate());
    }

    return builder.toString();
  }
  /**
   * Counts the number of events that would be affected by an edit operation.
   * Used to provide more informative feedback to users.
   *
   * @param property      The property to be edited
   * @param eventName     The name of the target events
   * @param startDateTime The start date-time filter (can be null)
   * @param endDateTime   The end date-time filter (can be null)
   * @return The number of events that match the criteria
   */
  private int countEventsForEdit(String property, String eventName,
                                 LocalDateTime startDateTime, LocalDateTime endDateTime) {
    int count = 0;
    for (EventModel event : eventCollection.getEventList()) {
      if (event.getEventName().equals(eventName) &&
              checkEventBetweenDates(event, startDateTime, endDateTime)) {
        count++;
      }
    }
    return count;
  }

  private List<EventModel> getEventsBetween(String cal, LocalDateTime start, LocalDateTime end) {
    eventCollection = calenderCollection.getEventCollection(cal);
    List<EventModel> events = eventCollection.getEventList();
    List<EventModel> filteredEvents = new ArrayList<>();
    for (EventModel event : events) {
      if (checkEventBetweenDates(event, start, end)) {
        filteredEvents.add(event);
      }
    }
    return filteredEvents;
  }

  /**
   * Returns a list of events for a specific calendar between a specific start and end time.
   * @param start LocalDateTime starting time
   * @param end LocalDateTime Ending time
   * @return a List of Events
   */
  public List<String> processGetEventsBetween(LocalDateTime start, LocalDateTime end) {
    String calendarName = this.calenderCollection.getCalendarName(this.eventCollection);
    List<EventModel> filteredEvents = getEventsBetween(calendarName, start, end);
    List<String> eventStrings = new ArrayList<>();
    for (EventModel event : filteredEvents) {
      String formattedEventString = formatEvent(event);
      eventStrings.add(formattedEventString);
    }
    return eventStrings;
  }

  private String formatEvent(EventModel event) {
    return String.format(
            "%s, %s, %s",
            event.getEventName(),
            event.getStartDateTime(),
            event.getEndDateTime()
    );
  }
}