package model;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a generic event with common properties such as event name,
 * start and end date-time, description, privacy settings, and location.
 * This class serves as a base model for different types of events.
 * It also provides getter and setter methods to access and modify event details.
 */
public class EventModel {

  private String eventName;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String description;
  private boolean isPrivate;
  private boolean autoDecline;
  private String location;


  /**
   * Creates an event with the specified details.
   * If endDateTime is null, it defaults to the last possible time
   * of the start date (23:59), making it an all-day event.
   *
   * @param eventName     The name of the event.
   * @param startDateTime The start date and time of the event.
   * @param endDateTime   The end date and time of the event (nullable, defaults to 23:59 if null).
   * @param description   A brief description of the event.
   * @param isPrivate     Indicates if the event is private.
   * @param autoDecline   Specifies if the event should be auto-declined.
   * @param location      The location of the event.
   */
  public EventModel(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime,
                    String description, boolean isPrivate, boolean autoDecline, String location) {
    this.eventName = eventName;
    this.startDateTime = startDateTime;
    //if enddatetime is null then samedate with last possible
    // time of the day making it a all day event.
    this.endDateTime = endDateTime != null ?
            endDateTime : startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));
    this.description = description;
    this.isPrivate = isPrivate;
    this.autoDecline = autoDecline;
    this.location = location;
  }

  /**
   * Constructs an EventModel with the specified event name, start and end date-time,
   * and auto-decline setting. This constructor initializes an event with default values
   * for description, privacy, and location.
   *
   * @param eventName     The name of the event.
   * @param startDateTime The starting date and time of the event.
   * @param endDateTime   The ending date and time of the event. If null, defaults to
   *                      the same date with the last possible time of the day (23:59).
   * @param autoDecline   Whether the event should be automatically declined.
   */
  public EventModel(String eventName, LocalDateTime startDateTime,
                    LocalDateTime endDateTime, boolean autoDecline) {
    this.eventName = eventName;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime != null ?
            endDateTime : startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));
    this.description = null;
    this.isPrivate = false;
    this.autoDecline = autoDecline;
    this.location = null;
  }

  /**
   * Constructor that copies an event to another.
   *
   * @param event Event model object.
   */
  public EventModel(EventModel event) {
    this.eventName = event.getEventName();
    this.startDateTime = event.getStartDateTime();
    this.endDateTime = event.getEndDateTime();
    this.description =event.getDescription();
    this.isPrivate = event.getIsPrivate();
    this.autoDecline = event.getAutoDecline();
    this.location =event.getLocation();
  }

  /**
   * Constructs an EventModel with the specified event name, start and end date-time.
   * This constructor defaults the auto-decline setting to false.
   *
   * @param eventName     The name of the event.
   * @param startDateTime The starting date and time of the event.
   * @param endDateTime   The ending date and time of the event. If null, defaults to
   *                      the same date with the last possible time of the day (23:59).
   */
  public EventModel(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    this(eventName, startDateTime, endDateTime, false);
  }


  /**
   * Constructs an EventModel with the specified event name and start date-time.
   * This constructor assumes the event is an all-day event by default,
   * setting the end time to 23:59 of the same day.
   * The auto-decline setting is set to  false.
   *
   * @param eventName     The name of the event.
   * @param startDateTime The starting date and time of the event.
   */
  public EventModel(String eventName, LocalDateTime startDateTime) {
    this(eventName, startDateTime, null, false);
  }


  /**
   * Gets the name of the event.
   *
   * @return the name of the event
   */
  public String getEventName() {
    return eventName;
  }

  /**
   * Gets the start date and time of the event.
   *
   * @return the start date and time of the event
   */

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  /**
   * Gets the end date and time of the event.
   * If the end date and time is null, it defaults to 11:59 PM of the start date.
   *
   * @return the end date and time of the event
   */
  public LocalDateTime getEndDateTime() {
    if (endDateTime == null) {
      return startDateTime.with(LocalTime.of(23, 59));
    } else {
      return endDateTime;
    }
  }

  /**
   * Gets the description of the event.
   *
   * @return the description of the event
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the location of the event.
   *
   * @return the location of the event
   */
  public String getLocation() {
    return location;
  }

  /**
   * Gets whether the event has the auto decline feature enabled.
   *
   * @return true if auto decline is enabled, false otherwise
   */
  public boolean getAutoDecline() {
    return autoDecline;
  }

  /**
   * Gets whether the event is private.
   *
   * @return true if the event is private, false otherwise
   */
  public Boolean getIsPrivate() {
    return isPrivate;
  }

  /**
   * Sets the name of the event.
   *
   * @param eventName the name to set for the event
   */
  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  /**
   * Sets the start date and time of the event.
   *
   * @param startDateTime the start date and time to set for the event
   */
  public void setStartDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }


  /**
   * Sets the end date and time of the event.
   *
   * @param endDateTime the end date and time to set for the event
   */
  public void setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }

  /**
   * Sets the description of the event.
   *
   * @param description the description to set for the event
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Sets whether the event is private.
   *
   * @param type the privacy status to set for the event
   */
  public void setIsPrivate(Boolean type) {
    this.isPrivate = type;
  }

  /**
   * Gets whether the event has the auto decline feature enabled.
   *
   * @return true if auto decline is enabled, false otherwise
   */
  public boolean isAutoDecline() {
    return autoDecline;
  }

  /**
   * Sets whether the event has the auto decline feature enabled.
   *
   * @param autoDecline the value to set for auto decline
   */
  public void setAutoDecline(boolean autoDecline) {
    this.autoDecline = autoDecline;
  }

  /**
   * Sets the location of the event.
   *
   * @param location the location to set for the event
   */
  public void setLocation(String location) {
    this.location = location;
  }


  /**
   * Checks if the event is an all-day event.
   * An event is considered all-day if it starts at 00:00 and ends at 23:59 on the same day.
   *
   * @return true if the event lasts all day, false otherwise.
   */
  public boolean isAllDayEvent() {
    if (endDateTime == null) {
      return true;
    }
    //check if day is the same
    //check if start time is at 00:00
    //check if end time is at 23:59
    return startDateTime.toLocalDate().equals(endDateTime.toLocalDate()) &&
            startDateTime.toLocalTime().equals(LocalTime.MIN) &&
            endDateTime.toLocalTime().equals(LocalTime.of(23, 59));
  }

  /**
   * Checks if the event occurs completely within the specified date range.
   *
   * @param  eventStartDateTime  The start of the date range.
   * @param  eventEndDateTime    The end of the date range.
   * @return  true if the event starts on or after eventStartDateTime and
   *          ends on or before eventEndDateTime, false otherwise.
   */
  public boolean isEventBetweenDates(LocalDateTime eventStartDateTime,
                                     LocalDateTime eventEndDateTime) {
    return !this.getStartDateTime().isBefore(eventStartDateTime)
            && !this.getEndDateTime().isAfter(eventEndDateTime);
  }

  /**
   * Checks if the given DateTime is after or before the time of the StartDateTime of the
   * current event. If it is after then it returns true else false.
   *
   * @param  eventStartDateTime  The DateTime to compare against the event's start time.
   * @return  Boolean indicating whether the given time is after the start time.
   */
  public boolean isAfterDateTime(LocalDateTime eventStartDateTime) {
    return this.getStartDateTime().isAfter(eventStartDateTime);
  }

  /**
   * returns a bulletted single line of string that can be used to print the list of events.
   *
   * @return String
   */
  public String bulletString() {
    return "*" + "  eventName=" + eventName + "," +
            "  Start=" + startDateTime + "," +
            "  End=" + endDateTime + "," +
            "  description=" + description + "," +
            "  isPrivate=" + isPrivate + "," +
            "  location=" + location + "," +
            "  autoDecline=" + autoDecline;
  }
}
