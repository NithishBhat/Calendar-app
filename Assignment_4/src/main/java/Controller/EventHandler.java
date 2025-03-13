package Controller;

import Model.EventCollectionModel;
import Model.EventModel;
import Model.ExportToCSV;
import Model.RecurringEventModel;

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

  /**
   * Constructor initializes the event collection model.
   *
   * @param eventCollection The event collection model to manage events.
   */
  public EventHandler(EventCollectionModel eventCollection) {
    this.eventCollection = eventCollection;
  }


  /**
   * Processes the creation of an event from a regex matcher.
   * Format: create event --autoDecline <eventName> from
   * <dateStringTtimeString> to <dateStringTtimeString>
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateEvent(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);
    String endDateTime = matcher.group(4);

    String description = matcher.group(5);
    String location = matcher.group(6);
    boolean privacy = matcher.group(7) != null ? Boolean.parseBoolean(matcher.group(7)) : false;

    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    LocalDateTime endDate = LocalDateTime.parse(endDateTime);
    if (isEndBeforeStart(startDate, endDate)) {
      System.out.println("End DateTime: " + endDate + " is before Start DateTime: " + startDate);
      System.out.println("Hence Event was not created");
      return;
    }

    EventModel event = new EventModel(eventName, startDate, endDate, description,
            privacy, autoDecline, location);
    checkForCollision(event, autoDecline, startDate, endDate);
  }

  /**
   * Processes the creation of a recurring event for a specified number of times.
   * Format: create event <eventName> from <dateStringTtimeString> to
   * <dateStringTtimeString> repeats <weekdays> for <N> times
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateRecurringEventN(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);
    String endDateTime = matcher.group(4);
    String weekDay = matcher.group(5);
    Integer numOfTimes = Integer.valueOf(matcher.group(6));

    String description = matcher.group(7);
    String location = matcher.group(8);
    Boolean privacy = matcher.group(9) != null ? Boolean.parseBoolean(matcher.group(9)) : false;

    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    LocalDateTime endDate = LocalDateTime.parse(endDateTime);
    if (isEndBeforeStart(startDate, endDate)) {
      System.out.println("End Date: " + endDate + " is before Start Date: " + startDate);
      System.out.println("Event was not created");
      return;
    }
    List<Character> recurringWeekDay = StringToDayOfWeek(weekDay);
    RecurringEventModel recurringEvent = new RecurringEventModel(eventName,
            startDate, endDate, description,
            privacy, autoDecline, location,
            recurringWeekDay, numOfTimes,
            null);

    checkForCollisionRecurringEvent(recurringEvent, autoDecline);
  }

  /**
   * Processes the creation of a recurring event that repeats until a specified end date.
   * Format: create event --autoDecline <eventName> from <dateStringTtimeString> to
   * <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateRecurringEventEndDate(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);
    String endDateTime = matcher.group(4);
    String weekDay = matcher.group(5);
    String recurrernceEndDate = matcher.group(6);

    String description = matcher.group(7);
    String location = matcher.group(8);
    Boolean privacy = matcher.group(9) != null ? Boolean.parseBoolean(matcher.group(9)) : false;

    List<Character> recurringWeekDay = StringToDayOfWeek(weekDay);
    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    LocalDateTime endDate = LocalDateTime.parse(endDateTime);


    if (isEndBeforeStart(startDate, endDate)) {
      System.out.println("End Date: " + endDate + " is before Start Date: " + startDate);
      System.out.println("Event was not created");
      return;
    }

    LocalDateTime recurrernceEndDateTime = LocalDateTime.parse(recurrernceEndDate);

    RecurringEventModel recurringEvent = new RecurringEventModel(eventName,
            startDate, endDate, description,
            privacy, autoDecline, location,
            recurringWeekDay, null,
            recurrernceEndDateTime);

    checkForCollisionRecurringEvent(recurringEvent, autoDecline);
  }

  /**
   * Processes the creation of an all-day event.
   * Format: create event --autoDecline <eventName> on <dateStringTtimeString>
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateAllDayEvent(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);

    String description = matcher.group(4);
    String location = matcher.group(5);
    Boolean privacy = matcher.group(6) != null ? Boolean.parseBoolean(matcher.group(6)) : false;

    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    EventModel event = new EventModel(eventName, startDate, null, description
            , privacy, autoDecline, location);
    checkForCollision(event, autoDecline, event.getEndDateTime(), event.getEndDateTime());
  }

  /**
   * Processes the creation of an all-day recurring event that
   * repeats for a specified number of times.
   * Format: create event <eventName> on <dateString> repeats <weekdays> for <N> times
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateAllDayRecurring(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);

    String weekDay = matcher.group(4);
    Integer numOfTimes = Integer.valueOf(matcher.group(5));

    String description = matcher.group(6);
    String location = matcher.group(7);
    Boolean privacy = matcher.group(8) != null ? Boolean.parseBoolean(matcher.group(8)) : false;

    LocalDateTime startDate = LocalDate.parse(startDateTime).atStartOfDay();
    List<Character> recurringWeekDay = StringToDayOfWeek(weekDay);


    RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDate,
            null, description,
            privacy, autoDecline, location,
            recurringWeekDay, numOfTimes,
            null);
    checkForCollisionRecurringEvent(recurringEvent, autoDecline);
  }

  /**
   * Processes the creation of an all-day recurring event that repeats until a specified end date.
   * Format: create event <eventName> on <dateString> repeats <weekdays> until <dateString>
   *
   * @param matcher The regex matcher containing event details.
   */
  void processCreateRecurringWholeDayEventEnd(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDate = matcher.group(3);

    String weekDay = matcher.group(4);
    String recurrernceEndDate = matcher.group(5);

    String description = matcher.group(6);
    String location = matcher.group(7);
    Boolean privacy = matcher.group(8) != null ? Boolean.parseBoolean(matcher.group(8)) : false;

    List<Character> recurringWeekDay = StringToDayOfWeek(weekDay);
    LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
    LocalDateTime recurrernceEndDateTime = LocalDate.parse(recurrernceEndDate).atStartOfDay();


    RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDateTime,
            null, description,
            privacy, autoDecline, location,
            recurringWeekDay, null,
            recurrernceEndDateTime);
    checkForCollisionRecurringEvent(recurringEvent, autoDecline);
  }

  /**
   * Processes the editing of a specific event property between two dates.
   * Format: edit event <property> <eventName> from <dateStringTtimeString> to
   * <dateStringTtimeString> with <NewPropertyValue>
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
      System.out.println("End Date: " + endDate + " is before Start Date: " + startDate);
      System.out.println("Event was not created");
      return;
    }
    editEvents(property, newProperty, eventName, startDateTime, endDateTime);
  }

  /**
   * Processes the editing of a property for all events with the specified name from a start date.
   * Format: edit events <property> <eventName> from
   * <dateStringTtimeString> with <NewPropertyValue>
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
   * Format: edit events <property> <eventName> <NewPropertyValue>
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
   * Format: print events on <dateString>
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
   * Format: print events from <dateStringTtimeString> to <dateStringTtimeString>
   *
   * @param matcher The regex matcher containing date range details.
   */
  void processPrintEventsBetween(Matcher matcher) {
    String startDate = matcher.group(1);
    String endDate = matcher.group(2);
    LocalDateTime startDateTime = LocalDateTime.parse(startDate);
    LocalDateTime endDateTime = LocalDateTime.parse(endDate);

    if (isEndBeforeStart(startDateTime, endDateTime)) {
      System.out.println("End Date: " + endDate + " is before Start Date: " + startDate);
      System.out.println("Event was not created");
      return;
    }

    List<EventModel> printList = new ArrayList<>();
    printList = eventCollection.searchEventByTimeFrame(startDateTime, endDateTime);
    System.out.println(eventCollection.printAssortments(printList));
  }

  /**
   * Exports all calendar events to a CSV file.
   * Format: export cal fileName.csv
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
   * Format: show status on <dateStringTtimeString>
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
   * @param weekDay String representation of weekdays (e.g., "MT" for Monday and Tuesday)
   * @return List of characters representing each day of the week
   */
  private List<Character> StringToDayOfWeek(String weekDay) {
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
   * @param StartDateTime The start date-time to filter events (can be null)
   * @param EndDateTime   The end date-time to filter events (can be null)
   */
  private void editEvents(String property, String newProperty, String eventName,
                          LocalDateTime StartDateTime, LocalDateTime EndDateTime) {
    if (property.contains("startDateTime")) {
      setStartDateTime(LocalDateTime.parse(newProperty), eventName, StartDateTime, EndDateTime);
    } else if (property.contains("endDateTime")) {
      setEndDateTime(LocalDateTime.parse(newProperty), eventName, StartDateTime, EndDateTime);
    } else if (property.contains("location")) {
      setLocation(newProperty, eventName, StartDateTime, EndDateTime);
    } else if (property.contains("description")) {
      setDescription(eventName, newProperty, StartDateTime, EndDateTime);
    } else if (newProperty.contains("private") && property.contains("privacyType")) {
      setIsPrivate(eventName, true, StartDateTime, EndDateTime);
    } else if (property.contains("public") && property.contains("privacyType")) {
      setIsPrivate(newProperty, false, StartDateTime, EndDateTime);
    } else if (property.contains("eventName")) {
      setEventName(eventName, newProperty, StartDateTime, EndDateTime);
    }
  }

  /**
   * Sets the start date-time for events matching the criteria.
   *
   * @param newProperty   The new start date-time
   * @param eventName     The name of the event to update
   * @param startDateTime The start date-time filter (can be null)
   * @param endDateTime   The end date-time filter (can be null)
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
   * @param newProperty   The new end date-time
   * @param eventName     The name of the event to update
   * @param startDateTime The start date-time filter (can be null)
   * @param endDateTime   The end date-time filter (can be null)
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
   * @param location      The new location
   * @param eventName     The name of the event to update
   * @param startDateTime The start date-time filter (can be null)
   * @param endDateTime   The end date-time filter (can be null)
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
   * @param eventName     The current name of the event
   * @param newEventName  The new name for the event
   * @param startDateTime The start date-time filter (can be null)
   * @param endDateTime   The end date-time filter (can be null)
   * @return Void (null)
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
   * Checks for collision with existing events and adds the event if allowed.
   *
   * @param event         The event to check and potentially add
   * @param autoDecline   Whether to automatically decline events with conflicts
   * @param startDateTime The start date-time of the event
   * @param endDateTime   The end date-time of the event
   * @return true if event was added, false if there was a conflict and autoDecline is true
   */
  private boolean checkForCollision(EventModel event, boolean autoDecline,
                                    LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (autoDecline == true) {
      if (!eventCollection.checkConflict(startDateTime, endDateTime)) {
        eventCollection.addEvent(event);
        System.out.println("Event \"" + event.getEventName() + "\" was created");
        return false;
      } else {
        System.out.println("Event \"" + event.getEventName()
                + "\" was not created due to conflict");
        return true;
      }
    } else {
      eventCollection.addEvent(event);
      System.out.println("Event \"" + event.getEventName() + "\" was created");
      return true;
    }
  }

  /**
   * Checks for collision with existing events for recurring events and adds if allowed.
   *
   * @param recurringEvent The recurring event to check and potentially add
   * @param autoDecline    Whether to automatically decline events with conflicts
   * @return true if event was added, false if there was a conflict and autoDecline is true
   */
  private boolean checkForCollisionRecurringEvent(RecurringEventModel recurringEvent,
                                                  boolean autoDecline) {
    if (autoDecline == true) {
      List<EventModel> recurringEventsList = eventCollection.generateOccurrences(recurringEvent);
      for (EventModel event : recurringEventsList) {
        if (eventCollection.checkConflict(event.getStartDateTime(), event.getEndDateTime())) {
          System.out.println("Conflict detected, your recurring event \""
                  + recurringEvent.getEventName() + "\" was not created");
          return true;
        }
      }

      eventCollection.addRecurringEvent(recurringEvent);
      System.out.println("Event \"" + recurringEvent.getEventName() + "\" was created");
      return false;

    } else {
      eventCollection.addRecurringEvent(recurringEvent);
      System.out.println("Event \"" + recurringEvent.getEventName() + "\" was created");
      return false;
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
    if (endDateTime.isBefore(startDateTime)) {
      return true;
    }
    return false;
  }
}