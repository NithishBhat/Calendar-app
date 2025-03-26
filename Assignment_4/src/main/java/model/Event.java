package model;


import java.time.LocalDateTime;

public interface Event {

  String getEventName();
  void setEventName(String eventName);

  LocalDateTime getStartDateTime();
  void setStartDateTime(LocalDateTime startDateTime);

  LocalDateTime getEndDateTime();
  void setEndDateTime(LocalDateTime endDateTime);

  String getDescription();
  void setDescription(String description);

  Boolean getIsPrivate();
  void setIsPrivate(boolean isPrivate);

  Boolean getAutoDecline();
  void setAutoDecline(boolean autoDecline);

  String getLocation();
  void setLocation(String location);

  boolean isAllDayEvent();
  boolean isEventBetweenDates(LocalDateTime start, LocalDateTime end);
  boolean isAfterDateTime(LocalDateTime dateTime);

  String bulletString();
}
