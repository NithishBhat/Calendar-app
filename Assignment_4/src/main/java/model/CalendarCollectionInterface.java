package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

/**
 * Interface defining operations for managing a collection of calendars.
 * Provides functionality for adding, removing, and modifying calendars,
 * as well as copying events between calendars with various constraints.
 */
public interface CalendarCollectionInterface {

  /**
   * Retrieves the entire collection of calendars.
   *
   * @return A HashMap containing all calendars, with calendar names as keys
   *         and CalenderModel instances as values.
   */
  HashMap<String, CalenderModel> getCalenderCollection();

  /**
   * Adds a new calendar to the collection.
   *
   * @param name     The name of the calendar to be added
   * @param timeZone The time zone associated with the calendar
   */
  void addCalender(String name, ZoneId timeZone);

  /**
   * Retrieves the event collection for a specific calendar.
   *
   * @param name The name of the calendar to retrieve events from
   * @return The EventCollectionModel containing events for the specified calendar
   * @throws IllegalArgumentException If the specified calendar does not exist
   */
  EventCollectionModel getEventCollection(String name);

  /**
   * Returns the total number of calendars in the collection.
   *
   * @return The number of calendars currently stored in the collection
   */
  int getCalenderCollectionSize();

  /**
   * Removes a calendar from the collection by its name.
   *
   * @param name The name of the calendar to be removed
   */
  void removeCalenderByName(String name);

  /**
   * Renames an existing calendar in the collection.
   *
   * @param oldName The current name of the calendar
   * @param newName The new name to be assigned to the calendar
   */
  void changeCalenderName(String oldName, String newName);

  /**
   * Finds the name of the calendar containing a specific event collection.
   *
   * @param eventCollection The EventCollectionModel to search for
   * @return The name of the calendar containing the event collection
   * @throws IllegalArgumentException If no calendar contains the event collection
   */
  String getCalendarName(EventCollectionModel eventCollection);

  /**
   * Updates the time zone for a specific calendar.
   *
   * @param calName  The name of the calendar to update
   * @param timeZone The new time zone to be assigned to the calendar
   */
  void changeCalendarTimeZone(String calName, ZoneId timeZone);

  /**
   * Copies a specific event from one calendar to another at a specified time.
   *
   * @param calName    Source calendar name
   * @param eventName  Name of the event to copy
   * @param calDate    Original start time of the event
   * @param tarCalName Target calendar name
   * @param tarCalDate Start time for the event in the target calendar
   * @throws IllegalArgumentException If the event cannot be found in the source calendar
   */
  void copyCalendarEventOn(String calName, String eventName,
                           LocalDateTime calDate, String tarCalName,
                           LocalDateTime tarCalDate);

  /**
   * Copies all events from an entire day in one calendar to another.
   *
   * @param fromDate   The source date to copy events from
   * @param calName    Source calendar name
   * @param tarCalDate Target date to copy events to
   * @param tarCalName Target calendar name
   */
  void copyCalenderEntireDay(LocalDate fromDate, String calName,
                             LocalDate tarCalDate, String tarCalName);

  /**
   * Copies events between two specified dates from one calendar to another.
   *
   * @param fromDate   Start date for event copying
   * @param toDate     End date for event copying
   * @param calName    Source calendar name
   * @param tarCalDate Target date to copy events to
   * @param tarCalName Target calendar name
   */
  void copyCalenderBetweenDates(LocalDate fromDate, LocalDate toDate,
                                String calName, LocalDate tarCalDate, String tarCalName);

  /**
   * Copies events with a specific name between two dates from one calendar to another.
   *
   * @param eventName  Name of events to copy
   * @param fromDate   Start date for event copying
   * @param toDate     End date for event copying
   * @param calName    Source calendar name
   * @param tarCalDate Target date to copy events to
   * @param tarCalName Target calendar name
   */
  void copyEventsWithSameNameBetweenDates(String eventName, LocalDateTime fromDate,
                                          LocalDateTime toDate, String calName,
                                          LocalDateTime tarCalDate, String tarCalName);

  /**
   * Constructs a formatted string listing all available calendar names.
   *
   * @return A string containing the list of available calendar names in a readable format.
   */
  String printCalendars();
}