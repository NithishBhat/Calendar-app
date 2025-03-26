package model;

import java.time.LocalDateTime;

/**
 * Represents an event with details such as name, start and end times, description, and more.
 */
public interface Event {

  /**
   * Retrieves the name of the event.
   *
   * @return the name of the event.
   */
  String getEventName();

  /**
   * Sets the name of the event.
   *
   * @param eventName the name of the event to set.
   */
  void setEventName(String eventName);

  /**
   * Retrieves the start date and time of the event.
   *
   * @return the start date and time of the event.
   */
  LocalDateTime getStartDateTime();

  /**
   * Sets the start date and time of the event.
   *
   * @param startDateTime the start date and time to set.
   */
  void setStartDateTime(LocalDateTime startDateTime);

  /**
   * Retrieves the end date and time of the event.
   *
   * @return the end date and time of the event.
   */
  LocalDateTime getEndDateTime();

  /**
   * Sets the end date and time of the event.
   *
   * @param endDateTime the end date and time to set.
   */
  void setEndDateTime(LocalDateTime endDateTime);

  /**
   * Retrieves the description of the event.
   *
   * @return the description of the event.
   */
  String getDescription();

  /**
   * Sets the description of the event.
   *
   * @param description the description to set.
   */
  void setDescription(String description);

  /**
   * Retrieves whether the event is private.
   *
   * @return true if the event is private, false otherwise.
   */
  Boolean getIsPrivate();

  /**
   * Sets whether the event is private.
   *
   * @param isPrivate the privacy status to set.
   */
  void setIsPrivate(Boolean isPrivate);

  /**
   * Retrieves whether the event has auto-decline enabled.
   *
   * @return true if auto-decline is enabled, false otherwise.
   */
  Boolean getAutoDecline();

  /**
   * Sets whether the event has auto-decline enabled.
   *
   * @param autoDecline the auto-decline status to set.
   */
  void setAutoDecline(Boolean autoDecline);

  /**
   * Retrieves the location of the event.
   *
   * @return the location of the event.
   */
  String getLocation();

  /**
   * Sets the location of the event.
   *
   * @param location the location to set.
   */
  void setLocation(String location);

  /**
   * Checks if the event is an all-day event.
   *
   * @return true if the event lasts all day, false otherwise.
   */
  boolean isAllDayEvent();

  /**
   * Determines if the event occurs between the given start and end date/time.
   *
   * @param start the start date/time to check.
   * @param end the end date/time to check.
   * @return true if the event occurs between the given start and end date/time, false otherwise.
   */
  boolean isEventBetweenDates(LocalDateTime start, LocalDateTime end);

  /**
   * Checks if the event occurs after the given date/time.
   *
   * @param dateTime the date/time to check against.
   * @return true if the event is after the given date/time, false otherwise.
   */
  boolean isAfterDateTime(LocalDateTime dateTime);

  /**
   * Returns a formatted string representing the event in bullet form.
   *
   * @return the formatted string representation of the event.
   */
  String bulletString();
}
