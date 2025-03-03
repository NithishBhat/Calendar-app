package Model;

import java.time.LocalDateTime;

/**
 * weekdays refers to day of the week the recurring event occurs
 * recurringEnddateTime refers the end date for the recurring . its a bit counfusing with an actual endate already specified
 * but its in the command section of assignment 4 question so.
 * The end of the recurring event can be indecated by recurringEndDateTime or numOfReoccurences
 */
public class RecurringEventModel {
  private LocalDateTime StartDateTime;
  private LocalDateTime EndDateTime;
  private Character weekday;
  private LocalDateTime recurringEndDateTime;
  private Integer numOfReoccurences;


  public LocalDateTime getStartDateTime() {
    return StartDateTime;
  }
  public void setStartDateTime(LocalDateTime startDateTime) {
    StartDateTime = startDateTime;
  }
  public LocalDateTime getEndDateTime() {
    return EndDateTime;
  }
  public void setEndDateTime(LocalDateTime endDateTime) {
    EndDateTime = endDateTime;
  }
  public Character getWeekday() {
    return weekday;
  }
  public void setWeekday(Character weekday) {
    this.weekday = weekday;
  }
  public LocalDateTime getRecurringEndDateTime() {
    return recurringEndDateTime;
  }
  public void setRecurringEndDateTime(LocalDateTime recurringEndDateTime) {
    this.recurringEndDateTime = recurringEndDateTime;
  }
  public Integer getNumOfRecurences() {
    return this.numOfReoccurences;
  }
  public void setNumOfRecurences(Integer numOfRecurences) {
    this.numOfReoccurences = numOfRecurences;
  }

}
