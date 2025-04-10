package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manages a collection of calendars with support for complex calendar operations.
 * This class provides functionality to:
 * - Add and remove calendars
 * - Manage calendar properties (name, time zone)
 * - Copy events between calendars
 * - Handle event scheduling and collision detection
 * The class uses a HashMap to store multiple CalenderModel instances,
 * each representing a distinct calendar with its own events and time zone.
 */
public class CalenderCollection implements CalendarCollectionInterface {

  /**
   * Stores the collection of calendars, where the key is the calendar name
   * and the value is the corresponding CalenderModel.
   */
  private HashMap<String, CalenderModel> calenderCollection;

  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";

  private static final String warningString = ANSI_RED + "[WARNING] " + ANSI_RESET;
  private static final String successString = ANSI_GREEN + "[SUCCESS] " + ANSI_RESET;

  /**
   * Initializes an empty calendar collection.
   * Creates a new HashMap to store calendar models, preparing
   * the collection for adding calendars.
   */
  public CalenderCollection() {
    calenderCollection = new HashMap<>();
  }

  /**
   * Retrieves the entire collection of calendars.
   *
   * @return A HashMap containing all calendars, with calendar names as keys
   *         and CalenderModel instances as values.
   */
  public HashMap<String, CalenderModel> getCalenderCollection() {
    return calenderCollection;
  }

  /**
   * Adds a new calendar to the collection.
   * Creates a new CalenderModel with an empty event collection
   * and the specified time zone, then stores it in the collection.
   *
   * @param name     The name of the calendar to be added
   * @param timeZone The time zone associated with the calendar
   */
  public void addCalender(String name, ZoneId timeZone) {
    calenderCollection.put(name.replace("\"", ""),
            new CalenderModel(new EventCollectionModel(), timeZone));
  }

  /**
   * Retrieves the event collection for a specific calendar.
   *
   * @param name The name of the calendar to retrieve events from
   * @return The EventCollectionModel containing events for the specified calendar
   * @throws IllegalArgumentException If the specified calendar does not exist
   */
  public EventCollectionModel getEventCollection(String name) {
    if (!calenderCollection.containsKey(name)) {
      throw new IllegalArgumentException("Calendar does not exist: " + name);
    }
    return calenderCollection.get(name).getEventCollection();
  }

  /**
   * Returns the total number of calendars in the collection.
   *
   * @return The number of calendars currently stored in the collection
   */
  public int getCalenderCollectionSize() {
    return calenderCollection.size();
  }

  /**
   * Removes a calendar from the collection by its name.
   *
   * @param name The name of the calendar to be removed
   */
  public void removeCalenderByName(String name) {
    calenderCollection.remove(name);
  }

  /**
   * Renames an existing calendar in the collection.
   * Moves the CalenderModel to a new key in the HashMap,
   * effectively changing its name while preserving its content.
   *
   * @param oldName The current name of the calendar
   * @param newName The new name to be assigned to the calendar
   */
  public void changeCalenderName(String oldName, String newName) {
    calenderCollection.put(newName.replace("\"", ""),
            calenderCollection.remove(oldName));
  }

  /**
   * Finds the name of the calendar containing a specific event collection.
   * Iterates through the calendar collection to find the calendar
   * associated with the given event collection.
   *
   * @param eventCollection The EventCollectionModel to search for
   * @return The name of the calendar containing the event collection
   * @throws IllegalArgumentException If no calendar contains the event collection
   */
  public String getCalendarName(EventCollectionModel eventCollection) {
    for (HashMap.Entry<String, CalenderModel> entry : calenderCollection.entrySet()) {
      if (entry.getValue().getEventCollection() == eventCollection) {
        return entry.getKey();
      }
    }
    throw new IllegalArgumentException("Event collection does not belong to a calendar collection");
  }

  /**
   * Updates the time zone for a specific calendar.
   *
   * @param calName  The name of the calendar to update
   * @param timeZone The new time zone to be assigned to the calendar
   */
  public void changeCalendarTimeZone(String calName, ZoneId timeZone) {
    calenderCollection.get(calName).setTimeZone(timeZone);
  }

  /**
   * Copies a specific event from one calendar to another at a specified time.
   * Locates the event in the source calendar, creates a copy,
   * and adds it to the target calendar with adjusted timing.
   *
   * @param calName    Source calendar name
   * @param eventName  Name of the event to copy
   * @param calDate    Original start time of the event
   * @param tarCalName Target calendar name
   * @param tarCalDate Start time for the event in the target calendar
   * @throws IllegalArgumentException If the event cannot be found in the source calendar
   */
  public void copyCalendarEventOn(String calName, String eventName,
                                  LocalDateTime calDate, String tarCalName,
                                  LocalDateTime tarCalDate) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    // Attempt to find the event in the source calendar
    EventModel eventToCopy =
            sourceEventCollection.searchEventByStartTimeAndName(eventName, calDate);

    if (eventToCopy == null) {
      throw new IllegalArgumentException("Error: Event " + eventName
              + " not found on " + calDate
              + " in calendar " + tarCalName);
    }

    // Calculate original event duration
    Duration duration =
            Duration.between(eventToCopy.getStartDateTime(), eventToCopy.getEndDateTime());

    // Create a copy of the event
    EventModel copiedEvent = new EventModel(eventToCopy);
    copiedEvent.setStartDateTime(tarCalDate);

    // Set new end time preserving original duration
    LocalDateTime newEndDateTime = tarCalDate.plus(duration);
    copiedEvent.setEndDateTime(newEndDateTime);

    // Check for conflicts and add event
    addEventWithCollisionCheck(targetEventCollection, copiedEvent);
  }

  /**
   * Copies all events from an entire day in one calendar to another.
   * Handles time zone conversions and adjusts event timings accordingly.
   *
   * @param fromDate   The source date to copy events from
   * @param calName    Source calendar name
   * @param tarCalDate Target date to copy events to
   * @param tarCalName Target calendar name
   */
  public void copyCalenderEntireDay(LocalDate fromDate, String calName,
                                    LocalDate tarCalDate, String tarCalName) {
    // Retrieve source and target calendar models
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    // Get time zones for conversion
    ZoneId sourceTimeZone = sourceCalender.getTimeZone();
    ZoneId targetTimeZone = targetCalender.getTimeZone();

    // Define time range for the entire day
    LocalDateTime fromDateTime = fromDate.atStartOfDay();
    LocalDateTime endCalDate = fromDate.atTime(LocalTime.of(23, 59));

    // Find and deep copy events for the specified day
    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection.searchEventByTimeFrame(
            fromDateTime, endCalDate));

    // Calculate days offset for copying
    long daysOffset = java.time.temporal.ChronoUnit.DAYS.between(fromDate, tarCalDate);

    // Process and copy each event
    for (EventModel event : eventsToBeCopied) {
      // Adjust event times with days offset and convert time zones
      ZonedDateTime eventStartZoned =
              event.getStartDateTime().atZone(sourceTimeZone).plusDays(daysOffset);
      ZonedDateTime eventEndZoned =
              event.getEndDateTime().atZone(sourceTimeZone).plusDays(daysOffset);

      // Convert to target time zone
      eventStartZoned = eventStartZoned.withZoneSameInstant(targetTimeZone);
      eventEndZoned = eventEndZoned.withZoneSameInstant(targetTimeZone);

      // Update event times
      event.setStartDateTime(eventStartZoned.toLocalDateTime());
      event.setEndDateTime(eventEndZoned.toLocalDateTime());

      // Check for conflicts and add event
      addEventWithCollisionCheck(targetEventCollection, event);
    }
  }

  /**
   * Copies events between two specified dates from one calendar to another.
   * Similar to copyCalenderEntireDay, but with a more flexible date range.
   *
   * @param fromDate   Start date for event copying
   * @param toDate     End date for event copying
   * @param calName    Source calendar name
   * @param tarCalDate Target date to copy events to
   * @param tarCalName Target calendar name
   */
  public void copyCalenderBetweenDates(LocalDate fromDate, LocalDate toDate,
                                       String calName, LocalDate tarCalDate, String tarCalName) {
    // Similar implementation to copyCalenderEntireDay,
    // but with a broader date range and more flexible event selection
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    ZoneId sourceTimeZone = sourceCalender.getTimeZone();
    ZoneId targetTimeZone = targetCalender.getTimeZone();

    LocalDateTime fromDateTime = fromDate.atStartOfDay();
    LocalDateTime toDateTime = toDate.atTime(LocalTime.of(23, 59));

    // Find events within the specified date range
    List<EventModel> eventsToBeCopied =
            deepCopyList(sourceEventCollection.searchEventByTimeFrame(fromDateTime, toDateTime));

    // Calculate days offset between source and target dates
    long daysOffset = java.time.temporal.ChronoUnit.DAYS.between(fromDate, tarCalDate);

    for (EventModel event : eventsToBeCopied) {
      // Convert event times, adjusting for time zones and date offset
      ZonedDateTime eventStartZoned =
              event.getStartDateTime().atZone(sourceTimeZone).plusDays(daysOffset);
      ZonedDateTime eventEndZoned =
              event.getEndDateTime().atZone(sourceTimeZone).plusDays(daysOffset);

      eventStartZoned = eventStartZoned.withZoneSameInstant(targetTimeZone);
      eventEndZoned = eventEndZoned.withZoneSameInstant(targetTimeZone);

      // Update event times
      event.setStartDateTime(eventStartZoned.toLocalDateTime());
      event.setEndDateTime(eventEndZoned.toLocalDateTime());

      // Check for conflicts and add event
      addEventWithCollisionCheck(targetEventCollection, event);
    }
  }

  /**
   * Copies events with a specific name between two dates from one calendar to another.
   * Allows selective copying of events based on their name within a given time range.
   *
   * @param eventName  Name of events to copy
   * @param fromDate   Start date for event copying
   * @param toDate     End date for event copying
   * @param calName    Source calendar name
   * @param tarCalDate Target date to copy events to
   * @param tarCalName Target calendar name
   */
  public void copyEventsWithSameNameBetweenDates(String eventName, LocalDateTime fromDate,
                                                 LocalDateTime toDate, String calName,
                                                 LocalDateTime tarCalDate, String tarCalName) {
    // Retrieve source and target calendar models
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    // Find events with matching name in the specified time range
    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection
            .searchEventByTimeFrameAndName(eventName, fromDate, toDate));

    // Calculate days offset between source and target dates
    long daysOffset = java.time.Duration.between(fromDate, tarCalDate).toDays();

    for (EventModel event : eventsToBeCopied) {
      // Adjust event times with days offset
      LocalDateTime eventStart = event.getStartDateTime().plusDays(daysOffset);
      LocalDateTime eventEnd = event.getEndDateTime().plusDays(daysOffset);

      // Update event times
      event.setStartDateTime(eventStart);
      event.setEndDateTime(eventEnd);

      // Check for conflicts and add event
      addEventWithCollisionCheck(targetEventCollection, event);
    }
  }

  /**
   * Checks for event time conflicts and handles the event creation based on the conflict status.
   * If a conflict is detected, the event will not be created, and a message will be printed.
   * This is similar to the former checkForCollision method from EventCollectionModel.
   *
   * @param targetCollection the event collection to add the event to.
   * @param event the event to check for collisions and potentially add.
   * @return true if the event was not created due to a conflict, false otherwise.
   */
  private boolean addEventWithCollisionCheck(EventCollectionModel targetCollection,
                                             EventModel event) {
    // Ensure autoDecline is always true
    boolean autoDecline = true;

    boolean hasConflict = targetCollection.checkConflict(event.getStartDateTime(),
            event.getEndDateTime());

    if (autoDecline && hasConflict) {
      System.out.println(warningString + "Event \""
              + event.getEventName() + "\" was not created due to conflict");
      return true;
    }

    targetCollection.addEvent(event);
    System.out.println(successString + event.getEventName() + " was created");
    return false;
  }

  /**
   * Creates a deep copy of an event list to prevent reference-related issues.
   * Ensures that modifications to the copied list do not affect the original events.
   *
   * @param originalList The list of events to be deeply copied
   * @return A new list with new EventModel instances copied from the original
   */
  private List<EventModel> deepCopyList(List<EventModel> originalList) {
    List<EventModel> copyList = new ArrayList<>();
    for (EventModel event : originalList) {
      copyList.add(new EventModel(event));
    }
    return copyList;
  }

  /**
   * Constructs a formatted string listing all available calendar names.
   * Each calendar name is preceded by a dash and any double quotes in the names are removed.
   *
   * @return A string containing the list of available calendar names in a readable format.
   */
  public String printCalendars() {
    StringBuilder sb = new StringBuilder("\n");

    sb.append("Available Calendars: ").append("\n");

    for (String calName : calenderCollection.keySet()) {
      sb.append(" - ").append(calName.replace("\"", "")).append("\n");
    }
    return sb.toString();
  }
}