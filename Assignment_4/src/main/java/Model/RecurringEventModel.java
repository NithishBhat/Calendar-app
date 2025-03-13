package Model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * weekdays refers to day of the week the recurring event occurs
 * recurringEnddateTime refers the end date for the recurring . its a bit counfusing with an actual endate already specified
 * but its in the command section of assignment 4 question so.
 * The end of the recurring event can be indecated by recurringEndDateTime or numOfReoccurences
 */
public class RecurringEventModel extends EventModel {
  private List<Character> recurringDays;
  private Integer occurrences;
  private LocalDateTime recurrenceEndDateTime;
  private static final Map<Character, String> dayMap = Map.of(
          'M', "MONDAY",
          'T', "TUESDAY",
          'W', "WEDNESDAY",
          'H', "THURSDAY",
          'F', "FRIDAY",
          'S', "SATURDAY",
          'U', "SUNDAY");

  // had to add in the extra parameters as they should have a connection to Event model else data cant pass.
  public RecurringEventModel(String eventName,LocalDateTime startDateTime, LocalDateTime endDateTime,
                             String description, boolean isPrivate, boolean autoDecline, String location,
                             List<Character> recurringDays,
                             Integer occurrences,
                             LocalDateTime recurrenceEndDateTime) {
    super(eventName, startDateTime, endDateTime,description,isPrivate, autoDecline,location);
    this.recurringDays = recurringDays;
    this.occurrences = occurrences;
    this.recurrenceEndDateTime = recurrenceEndDateTime;
  }

  //(String eventName,LocalDateTime startDateTime, LocalDateTime endDateTime,
  //                    String description, boolean isPrivate, boolean autoDecline, String location)
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



  public List<Character> getRecurringDays() {
    return recurringDays;
  }

  public Integer getOccurrences() {
    return occurrences;
  }

  public List<DayOfWeek> getOccuranceDayofWeek() {
    List<DayOfWeek> days = new ArrayList<>();
    for (Character day : recurringDays) {
      days.add(DayOfWeek.valueOf(dayMap.get(day)));
    }
    return days;
  }


  public LocalDateTime getRecurrenceEndDateTime() {
    return recurrenceEndDateTime;
  }

}
