package controller;

import java.time.ZoneId;

import model.CalenderCollection;
import model.EventCollectionModel;
import model.EventModel;
import model.RecurringEventModel;

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

  /**
   * Constructor initializes the event collection model.
   */
  public EventHandler() {
    calenderCollection = new CalenderCollection();
    createCalender("\"default\"", ZoneId.of("UTC"));
    eventCollection = calenderCollection.getEventCollection("default");
  }

  /**
   * Sets the event collection for a specific calendar.
   *
   * @param calenderName the name of the calendar to use.
   */
  public void useCalender(String calenderName) {
    eventCollection = calenderCollection.getEventCollection(calenderName);
  }

  /**
   * Processes an event copy operation on a specific date, copying an event from one calendar
   * to another at a specified time.
   *
   * @param matcher the matcher object containing the event details, including event name,
   *                event date, target calendar, and target calendar date.
   */
  public void processCopyEventOn(Matcher matcher) {
    String eventName = matcher.group(1);
    LocalDateTime eventDateTime = LocalDateTime.parse(matcher.group(2));
    String tarCalName = matcher.group(3);
    LocalDateTime tarCalDateTime = LocalDateTime.parse(matcher.group(4));

    String sourceCalendar = calenderCollection.getCalendarName(eventCollection);

    calenderCollection.copyCalendarEventOn(sourceCalendar, eventName,
            eventDateTime, tarCalName, tarCalDateTime);
  }

  /**
   * Processes an operation to copy events between two specified dates from one calendar to another.
   *
   * @param matcher the matcher object containing the date range and target calendar details.
   */
  public void processCopyCalenderBetweenDates(Matcher matcher) {
    LocalDate fromDate = LocalDate.parse(matcher.group(1));
    LocalDate toDate = LocalDate.parse(matcher.group(2));
    String targetCalendarName = matcher.group(3);
    LocalDate targetStartDate = LocalDate.parse(matcher.group(4));

    String sourceCalendar = calenderCollection.getCalendarName(eventCollection);

    calenderCollection.copyCalenderBetweenDates(fromDate, toDate, sourceCalendar,
            targetStartDate, targetCalendarName);
  }

  /**
   * Processes an operation to copy all events from a specific day in one calendar to another.
   *
   * @param matcher the matcher object containing the event date and target calendar details.
   */
  public void processCopyEventsOn(Matcher matcher) {
    LocalDate eventDateTime = LocalDate.parse(matcher.group(1));
    String targetCalendar = matcher.group(2);
    LocalDate targetDateTime = LocalDate.parse(matcher.group(3));

    String sourceCalendar = calenderCollection.getCalendarName(eventCollection);

    calenderCollection.copyCalenderEntireDay(eventDateTime, sourceCalendar,
            targetDateTime, targetCalendar);
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
    calenderCollection.copyEventsWithSameNameBetweenDates(eventName, fromDate, toDate,
            sourceCalendarName, targetStartDate, targetCalendarName);
  }

  /**
   * Processes an operation to edit a calendar's properties such as name or time zone.
   *
   * @param matcher the matcher object containing the calendar name,
   *                property to edit, and new value.
   */
  public void processEditCalendar(Matcher matcher) {
    String calName = matcher.group(1);
    String property = matcher.group(2);
    String propertyValue = matcher.group(3);

    switch (property) {
      case "name":
        calenderCollection.changeCalenderName(calName, propertyValue);
        break;

      case "time-zone":
        ZoneId newZone = ZoneId.of(propertyValue);
        calenderCollection.changeCalendarTimeZone(calName, newZone);
        break;

      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }
  }

  /**
   * Processes the operation to switch to a specified calendar.
   *
   * @param matcher the matcher object containing the calendar name to switch to.
   */
  public void processUseCalendar(Matcher matcher) {
    String calName = matcher.group(1);
    useCalender(calName);
    System.out.println("Changed to calendar: " + calName);

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
    calenderCollection.addCalender(calenderName, timezone);
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
      System.out.println("Calender: " + calName + " created in Timezone: " + timeZoneString);
    } catch (Exception e) {
      System.out.println("Invalid time zone: " + timeZoneString);
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
    eventCollection.checkForCollision(event, autoDecline);
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

    eventCollection.checkForCollisionRecurringEvent(recurringEvent, autoDecline);
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

    eventCollection.checkForCollisionRecurringEvent(recurringEvent, autoDecline);
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
    eventCollection.checkForCollision(event, autoDecline);
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
    eventCollection.checkForCollisionRecurringEvent(recurringEvent, autoDecline);
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
    eventCollection.checkForCollisionRecurringEvent(recurringEvent, autoDecline);
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

    LocalDateTime startDateTime = LocalDateTime.parse(startDate);
    LocalDateTime endDateTime = LocalDateTime.parse(endDate);
    if (isEndBeforeStart(startDateTime, endDateTime)) {
      throw new IllegalArgumentException("End time cannot be before" +
              " start time, Event was not created");
    }
    editEvents(property, newProperty, eventName, startDateTime, endDateTime);
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

    LocalDateTime startDateTime = LocalDateTime.parse(startDate);
    editEvents(property, newProperty, eventName, startDateTime, null);
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

    editEvents(property, newProperty, eventName, null, null);
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
    String startDate = matcher.group(1);
    LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
    LocalDateTime endDateTime =
            startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));

    List<EventModel> printList = new ArrayList<>();
    printList = eventCollection.searchEventByTimeFrame(startDateTime, endDateTime);
    System.out.println(eventCollection.printAssortments(printList));
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
    System.out.println(eventCollection.printAssortments(printList));
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

    System.out.println("Exporting to " + fileName + "...");
    File exportFile = new File(fileName);

    if (!exportFile.exists()) {
      try {
        if (exportFile.createNewFile()) {
          System.out.println("File "
                  + fileName
                  + " did not exist. A new file has been created at "
                  + exportFile.getAbsolutePath());
        }
      } catch (IOException e) {
        System.out.println("Error creating file: " + exportFile.getAbsolutePath());
        return;
      }
    }

    List<EventModel> eventList = eventCollection.getEventList();
    try {
      ExportToCSV.generateCSV(fileName, eventList);
    } catch (Exception e) {
      System.out.println("Could not complete Export: " + e.getMessage());
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
    System.out.println(eventCollection.checkStatus(startDateTime,
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
      if (event.getEventName().equals(eventName) && checkEventBetweenDates(event,
              startDateTime, endDateTime)) {
        event.setStartDateTime(newProperty);
      }
    }
    return null;
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
      if (event.getEventName().equals(eventName) && checkEventBetweenDates(event, startDateTime,
              endDateTime)) {
        event.setEndDateTime(newProperty);
      }
    }
    return null;
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
    return null;
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
    return null;
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
    return null;
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
    return null;
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
}