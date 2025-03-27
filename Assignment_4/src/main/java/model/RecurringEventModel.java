package model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RecurringEventModel represents an event that occurs multiple times according to a defined
 * schedule.
 * It extends the EventModel class and adds additional properties to handle recurring events.
 * The recurrence can be defined using a list of days the event repeats, the number of occurrences,
 * or an end date for the recurring event.
 */
public class RecurringEventModel extends EventModel {
  private List<Character> recurringDays;
  private Integer occurrences;
  private LocalDateTime recurrenceEndDateTime;
  private static final Map<Character, String> dayMap = Map.of(
          'M', "MONDAY",
          'T', "TUESDAY",
          'W', "WEDNESDAY",
          'R', "THURSDAY",
          'F', "FRIDAY",
          'S', "SATURDAY",
          'U', "SUNDAY");

  /**
   * Constructor to initialize a recurring event with all necessary parameters.
   * This constructor includes details like event name, start time, end time, description, and more.
   *
   * @param eventName             The name of the event
   * @param startDateTime         The start time of the event
   * @param endDateTime           The end time of the event
   * @param description           A description of the event
   * @param isPrivate             Whether the event is private or not
   * @param autoDecline           Whether automatic declines are enabled for the event
   * @param location              The location of the event
   * @param recurringDays         A list of characters representing the days the event repeats
   * @param occurrences           The number of occurrences for the event
   * @param recurrenceEndDateTime The end date/time of the recurring event
   */
  public RecurringEventModel(String eventName,
                             LocalDateTime startDateTime,
                             LocalDateTime endDateTime,
                             String description,
                             boolean isPrivate,
                             boolean autoDecline,
                             String location,
                             List<Character> recurringDays,
                             Integer occurrences,
                             LocalDateTime recurrenceEndDateTime) {
    super(eventName, startDateTime, endDateTime, description, isPrivate, autoDecline, location);
    this.recurringDays = recurringDays;
    this.occurrences = occurrences;
    this.recurrenceEndDateTime = recurrenceEndDateTime;
  }

  /**
   * Constructor to initialize a recurring event with essential details
   * and the recurrence parameters.
   * The description and location are omitted in this version.
   *
   * @param eventName             The name of the event
   * @param startDateTime         The start time of the event
   * @param endDateTime           The end time of the event
   * @param autoDecline           Whether automatic declines are enabled for the event
   * @param recurringDays         A list of characters representing the days the event repeats
   * @param occurrences           The number of occurrences for the event
   * @param recurrenceEndDateTime The end date/time of the recurring event
   */
  public RecurringEventModel(String eventName,
                             LocalDateTime startDateTime,
                             LocalDateTime endDateTime,
                             boolean autoDecline,
                             List<Character> recurringDays,
                             Integer occurrences,
                             LocalDateTime recurrenceEndDateTime) {
    super(eventName, startDateTime, endDateTime, autoDecline);
    this.recurringDays = recurringDays;
    this.occurrences = occurrences;
    this.recurrenceEndDateTime = recurrenceEndDateTime;
  }

  /**
   * Getter method to retrieve the list of recurring days for the event.
   *
   * @return List of characters representing the days the event recurs.
   */
  public List<Character> getRecurringDays() {
    return recurringDays;
  }

  /**
   * Getter method to retrieve the number of occurrences for the event.
   *
   * @return Integer representing the number of occurrences.
   */
  public Integer getOccurrences() {
    return occurrences;
  }

  /**
   * Converts the list of recurring days (characters) to a list of DayOfWeek enums.
   * This method is useful for further processing or display of the recurring days.
   *
   * @return List of DayOfWeek enums corresponding to the recurring days.
   */
  public List<DayOfWeek> getOccuranceDayofWeek() {
    List<DayOfWeek> days = new ArrayList<>();
    for (Character day : recurringDays) {
      days.add(DayOfWeek.valueOf(dayMap.get(day)));
    }
    return days;
  }


  /**
   * Getter method to retrieve the recurrence end date/time for the event.
   *
   * @return LocalDateTime representing when the recurring event should end.
   */
  public LocalDateTime getRecurrenceEndDateTime() {
    return recurrenceEndDateTime;
  }

}
