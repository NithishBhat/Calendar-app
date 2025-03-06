package Model;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * the class provides structure to store events.
 * type is either private or public
 */
public class EventModel {

  private String eventName;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String description;
  private boolean isPrivate;
  private boolean autoDecline;
  private String location;

  //create event --autodecline <eventName> from <dateStringTTimeString> to <dateStringTTimeString>
  //create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
  //create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
  //create event --autoDecline <eventName> on <dateStringTtimeString>
  //create event <eventName> on <dateString> repeats <weekdays> for <N> times
  //create event <eventName> on <dateString> repeats <weekdays> until <dateString>

  /// accounting for it its made... also better design .. can you adapt to this?
  public EventModel(String eventName,LocalDateTime startDateTime, LocalDateTime endDateTime,
                    String description, boolean isPrivate, boolean autoDecline, String location) {
    this.eventName = eventName;
    this.startDateTime = startDateTime;
    //if enddatetime is null then samedate with last possible time of the day making it a all day event
    this.endDateTime = endDateTime != null ? endDateTime : startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));
    this.description = description;
    this.isPrivate = isPrivate;
    this.autoDecline = autoDecline;
    this.location = location;
  }

  public EventModel(String eventName,LocalDateTime startDateTime, LocalDateTime endDateTime, boolean autoDecline) {
    this.eventName = eventName;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.description = "No Description";
    this.isPrivate = false;
    this.autoDecline = autoDecline;
    this.location = "No Location";
  }

  //Constructor for no auto decline
  public EventModel(String eventName,LocalDateTime startDateTime, LocalDateTime endDateTime) {
    this(eventName, startDateTime, endDateTime, false);
  }

  //Constructor for all day events
  public EventModel(String eventName,LocalDateTime startDateTime) {
    this(eventName, startDateTime,null ,false);
  }

  public String getEventName() {
    return eventName;
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    if (endDateTime == null) {
      return startDateTime.with(LocalTime.of(23,59));
    } else {
      return endDateTime;
    }
  }

  public String getDescription() {
    return description;
  }

  public String getLocation() {
    return location;
  }

  public boolean getAutoDecline() {
    return autoDecline;
  }

  public Boolean getIsPrivate() {
    return isPrivate;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public void setStartDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  public void setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setIsPrivate(Boolean type) {
    this.isPrivate = type;
  }

  public boolean isAutoDecline() {
    return autoDecline;
  }

  public void setAutoDecline(boolean autoDecline) {
    this.autoDecline = autoDecline;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public boolean isAllDayEvent() {
    if (endDateTime == null) {
      return true;
    }
    //check if day is the same
    //check if start time is at 00:00
    //check if end time is at 23:59
    return startDateTime.toLocalDate().equals(endDateTime.toLocalDate()) &&
            startDateTime.toLocalTime().equals(LocalTime.MIN) &&
            endDateTime.toLocalTime().equals(LocalTime.of(23,59));
  }

  @Override
  public String toString() {
    return "[\n" + "  eventName=" + eventName + ",\n" +
            "  Start=" + startDateTime + ",\n" +
            "  End=" + endDateTime + ",\n" +
            "  description=" + description + ",\n" +
            "  isPrivate=" + isPrivate + ",\n" +
            "  autoDecline=" + autoDecline +
            "\n]";
  }
}
