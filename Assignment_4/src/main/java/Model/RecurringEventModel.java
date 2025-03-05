package Model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

/**
 * weekdays refers to day of the week the recurring event occurs
 * recurringEnddateTime refers the end date for the recurring . its a bit counfusing with an actual endate already specified
 * but its in the command section of assignment 4 question so.
 * The end of the recurring event can be indecated by recurringEndDateTime or numOfReoccurences
 */
public class RecurringEventModel extends EventModel {
  private List<DayOfWeek> recurringDays;
  private int occurrences;
  private LocalDateTime recurrenceEndDateTime;

  public RecurringEventModel(String eventName,
                             LocalDateTime startDateTime,
                             LocalDateTime endDateTime,
                             boolean autoDecline,
                             List<DayOfWeek> recurringDays,
                             int occurrences,
                             LocalDateTime recurrenceEndDateTime) {
    super(eventName, startDateTime, endDateTime, autoDecline);
    this.recurringDays = recurringDays;
    this.occurrences = occurrences;
    this.recurrenceEndDateTime = recurrenceEndDateTime;
  }

  public List<DayOfWeek> getRecurringDays() {
    return recurringDays;
  }

  public int getOccurrences() {
    return occurrences;
  }

  public LocalDateTime getRecurrenceEndDateTime() {
    return recurrenceEndDateTime;
  }

  @Override
  public String toString() {
    return super.toString() + ",\n" +
            "  Recurring days=" + recurringDays + ",\n" +
            "  Occurrences=" + (occurrences == -1 ? "Until " + recurrenceEndDateTime : occurrences)
            + "\n]";
  }
}
