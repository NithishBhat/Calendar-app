package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CalenderCollection {

  private HashMap<String, CalenderModel> calenderCollection;

  public CalenderCollection() {
    calenderCollection = new HashMap<>();
  }

  public HashMap<String, CalenderModel> getCalenderCollection() {
    return calenderCollection;
  }

  public void addCalender(String name, ZoneId timeZone) {
    calenderCollection.put(name.replace("\"", ""), new CalenderModel(new EventCollectionModel(), timeZone));
  }


  public EventCollectionModel getEventCollection(String name) {
    if (!calenderCollection.containsKey(name)) {
      throw new IllegalArgumentException("Calendar does not exist: " + name);
    }
    return calenderCollection.get(name).getEventCollection();
  }

  public int getCalenderCollectionSize() {
    return calenderCollection.size();
  }

  public void removeCalenderByName(String name) {
    calenderCollection.remove(name);
  }

  public void changeCalenderName(String oldName, String newName) {
    calenderCollection.put(newName.replace("\"", ""), calenderCollection.remove(oldName));
  }

  public String getCalendarName(EventCollectionModel eventCollection) {
    for (HashMap.Entry<String, CalenderModel> entry : calenderCollection.entrySet()) {
      if (entry.getValue().getEventCollection() == eventCollection) {
        return entry.getKey();
      }
    }
    throw new IllegalArgumentException("event collection does not belong to a calender collection");
  }

  public void changeCalendarTimeZone(String calName, ZoneId timeZone) {
    calenderCollection.get(calName).setTimeZone(timeZone);
  }

  public void copyCalendarEventOn(String calName, String eventName,
                                  LocalDateTime calDate, String tarCalName,
                                  LocalDateTime tarCalDate) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    EventModel eventToCopy = sourceEventCollection.searchEventByStartTimeAndName(eventName, calDate);

    if (eventToCopy == null) {
      throw new IllegalArgumentException("\"Error: Event \" + eventName\n" +
              "              + \" not found on \" + calDate\n" +
              "              + \" in calendar \" + tarCalName + \"");
    }

    Duration duration = Duration.between(eventToCopy.getStartDateTime(), eventToCopy.getEndDateTime());

    EventModel copiedEvent = new EventModel(eventToCopy);
    copiedEvent.setStartDateTime(tarCalDate);

    LocalDateTime newEndDateTime = tarCalDate.plus(duration);
    copiedEvent.setEndDateTime(newEndDateTime);
    targetEventCollection.checkForCollision(copiedEvent, true);
  }

  public void copyCalenderEntireDay(LocalDate fromDate, String calName, LocalDate tarCalDate, String tarCalName) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    ZoneId sourceTimeZone = sourceCalender.getTimeZone();
    ZoneId targetTimeZone = targetCalender.getTimeZone();

    LocalDateTime fromDateTime = fromDate.atStartOfDay();
    LocalDateTime endCalDate = fromDate.atTime(LocalTime.of(23, 59));

    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection.searchEventByTimeFrame(
            fromDateTime,endCalDate));

    long daysOffset = java.time.temporal.ChronoUnit.DAYS.between(fromDate, tarCalDate);

    for (EventModel event : eventsToBeCopied ) {
      ZonedDateTime eventStartZoned = event.getStartDateTime().atZone(sourceTimeZone).plusDays(daysOffset);
      ZonedDateTime eventEndZoned = event.getEndDateTime().atZone(sourceTimeZone).plusDays(daysOffset);

      //Convert to the target time zone
      eventStartZoned = eventStartZoned.withZoneSameInstant(targetTimeZone);
      eventEndZoned = eventEndZoned.withZoneSameInstant(targetTimeZone);

      event.setStartDateTime(eventStartZoned.toLocalDateTime());
      event.setEndDateTime(eventEndZoned.toLocalDateTime());

      targetEventCollection.checkForCollision(event, true);
    }
  }

  public void copyCalenderBetweenDates(LocalDate fromDate, LocalDate toDate, String calName, LocalDate tarCalDate, String tarCalName) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    ZoneId sourceTimeZone = sourceCalender.getTimeZone();
    ZoneId targetTimeZone = targetCalender.getTimeZone();

    LocalDateTime fromDateTime = fromDate.atStartOfDay();
    LocalDateTime toDateTime = toDate.atTime(LocalTime.of(23, 59));

    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection.searchEventByTimeFrame(fromDateTime, toDateTime));

    //Calculate the days offset between the source and target calendar dates
    long daysOffset = java.time.temporal.ChronoUnit.DAYS.between(fromDate, tarCalDate);

    for (EventModel event : eventsToBeCopied) {
      //convert to target calendar time zone

      ZonedDateTime eventStartZoned = event.getStartDateTime().atZone(sourceTimeZone).plusDays(daysOffset);
      ZonedDateTime eventEndZoned = event.getEndDateTime().atZone(sourceTimeZone).plusDays(daysOffset);

      eventStartZoned = eventStartZoned.withZoneSameInstant(targetTimeZone);
      eventEndZoned = eventEndZoned.withZoneSameInstant(targetTimeZone);

      event.setStartDateTime(eventStartZoned.toLocalDateTime());
      event.setEndDateTime(eventEndZoned.toLocalDateTime());

      targetEventCollection.checkForCollision(event, true);
    }
  }


  public void copyEventsWithSameNameBetweenDates(String eventName, LocalDateTime fromDate,
                                                 LocalDateTime toDate, String calName,
                                                 LocalDateTime tarCalDate, String tarCalName) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection
            .searchEventByTimeFrameAndName(eventName, fromDate, toDate));

    //Calculate the days offset between the source and target calendar dates
    long daysOffset = java.time.Duration.between(fromDate, tarCalDate).toDays();

    for (EventModel event : eventsToBeCopied) {
      //Must implement time zone conversion.

      LocalDateTime eventStart = event.getStartDateTime().plusDays(daysOffset);
      LocalDateTime eventEnd = event.getEndDateTime().plusDays(daysOffset);

      //set new times
      event.setStartDateTime(eventStart);
      event.setEndDateTime(eventEnd);

      targetEventCollection.checkForCollision(event, true);
    }

  }

  private List<EventModel> deepCopyList(List<EventModel> original) {//deep copy to prevent reference
    List<EventModel> copy = new ArrayList<>();
    for (EventModel event : original) {
      copy.add(new EventModel(event));
    }
    return copy;
  }

  public String printCalendars() {
    StringBuilder sb = new StringBuilder("\n");

    sb.append("Available Calendars: ").append("\n");

    for (String calName : calenderCollection.keySet()) {
      sb.append(" - ").append(calName.replace("\"", "")).append("\n");
    }

    return sb.toString();
  }
}
