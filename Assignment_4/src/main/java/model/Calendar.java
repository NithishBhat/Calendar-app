package model;

import java.time.ZoneId;

/**
 * Represents a calendar that manages events and time zones.
 */
public interface Calendar {

  /**
   * Retrieves the collection of events associated with this calendar.
   *
   * @return the event collection model.
   */
  EventCollectionModel getEventCollection();

  /**
   * Gets the time zone of the calendar.
   *
   * @return the current time zone.
   */
  ZoneId getTimeZone();

  /**
   * Sets the time zone of the calendar.
   *
   * @param timeZone the new time zone to set.
   */
  void setTimeZone(ZoneId timeZone);
}
