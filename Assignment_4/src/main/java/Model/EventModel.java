package Model;

import java.time.LocalDateTime;

/**
 * the class provides structure to store events.
 * type is either private or public
 */
public class EventModel {

  private String eventName;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String description;
  private Boolean isPrivate;

  public EventModel(String eventName,LocalDateTime startDateTime, LocalDateTime endDateTime,String description,Boolean isPrivate) {
    this.eventName = eventName;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.description = description;
    this.isPrivate = isPrivate;
  }

  public String getEventName() {
    return eventName;
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  public String getDescription() {
    return description;
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

}
