package model;

import java.time.ZoneId;

/**
 * The CalenderModel class represents a calendar
 * that contains a collection of events and a time zone.
 * It implements the Calendar interface.
 */
public class CalenderModel implements Calendar {

  private EventCollectionModel eventCollection;
  private ZoneId timeZone;

  /**
   * Constructor for CalenderModel.
   * Initializes the event collection and sets the time zone.
   * If the provided time zone is null, it defaults to "America/New_York".
   *
   * @param eventCollection The collection of events associated with the calendar.
   * @param timeZone        The time zone to be set for the calendar, can be null
   *                        (defaults to "America/New_York").
   */
  public CalenderModel(EventCollectionModel eventCollection, ZoneId timeZone) {
    this.eventCollection = eventCollection;
    this.timeZone = timeZone == null ? ZoneId.of("America/New_York") : timeZone;
  }

  /**
   * Gets the event collection associated with this calendar.
   *
   * @return The event collection model.
   */
  public EventCollectionModel getEventCollection() {
    return eventCollection;
  }

  /**
   * Gets the time zone associated with this calendar.
   *
   * @return The time zone of the calendar.
   */
  public ZoneId getTimeZone() {
    return timeZone;
  }

  /**
   * Sets the time zone for this calendar.
   * If the provided time zone is null, an IllegalArgumentException is thrown.
   *
   * @param timeZone The new time zone to set for the calendar.
   * @throws IllegalArgumentException if the provided time zone is null.
   */
  public void setTimeZone(ZoneId timeZone) {
    if (timeZone == null) {
      throw new IllegalArgumentException("TimeZone provided is null");
    }
    this.timeZone = timeZone;
  }
}
