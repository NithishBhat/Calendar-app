package model;

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

  public HashMap<String, CalenderModel> getCalenderCollection(String calenderName) {
    return calenderCollection;
  }

  public void addCalender(String name, ZoneId timeZone) {
    calenderCollection.put(name, new CalenderModel(new EventCollectionModel(), timeZone));
  }

  public EventCollectionModel getEventCollection(String name) {
    return calenderCollection.get(name).getEventCollection();
  }

  public int getCalenderCollectionSize() {
    return calenderCollection.size();
  }

  public void removeCalenderByName(String name) {
    calenderCollection.remove(name);
  }

  public void RemoveAllCalenders() {
    calenderCollection.clear();
  }

  public void changeCalenderName(String oldName, String newName) {
    calenderCollection.put(newName, calenderCollection.remove(oldName));
  }

  public String getCalendarName(EventCollectionModel eventCollection) {
    for (HashMap.Entry<String, CalenderModel> entry : calenderCollection.entrySet()) {
      if (entry.getValue().getEventCollection() == eventCollection) {
        return entry.getKey();
      }
    }
    return null;
  }

  public void changeCalendarTimeZone(String calName, ZoneId timeZone) {
    calenderCollection.get(calName).setTimeZone(timeZone);
  }

  public void copyCalenderFromDate(LocalDateTime calDate,
                                   String calName,
                                   LocalDateTime tarCalDate,
                                   String tarCalName) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection
            .searchEventByEventStartDate(calDate));

    changeDateTime(eventsToBeCopied,tarCalDate);

    for (EventModel event : eventsToBeCopied ) {
      targetEventCollection.checkForCollision(event, true);
    }
  }

  public void copyCalendarEventOn(String calName, String eventName,
                                  LocalDateTime calDate, String tarCalName,
                                  LocalDateTime tarCalDate) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    EventModel eventToCopy = sourceEventCollection
            .searchEventByStartTimeAndName(eventName, calDate);

    if (eventToCopy == null) {
      System.out.println("Error: Event " + eventName
              + " not found on " + calDate
              + " in calendar " + tarCalName + ".");
      return;
    }

    EventModel copiedEvent = new EventModel(eventToCopy);
    copiedEvent.setStartDateTime(tarCalDate);
    targetEventCollection.checkForCollision(copiedEvent, true);
  }

  public void copyCalenderEntireDay(LocalDate fromDate, String calName, LocalDate tarCalDate, String tarCalName) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    LocalDateTime fromDateTime = fromDate.atStartOfDay();
    LocalDateTime endCalDate = fromDate.atTime(LocalTime.of(23, 59));

    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection.searchEventByTimeFrame(
            fromDateTime,endCalDate));

    //changeDateTime(eventsToBeCopied,tarCalDateTime);

    long daysOffset = java.time.temporal.ChronoUnit.DAYS.between(fromDate, tarCalDate);

    for (EventModel event : eventsToBeCopied ) {
      LocalDateTime eventStart = event.getStartDateTime().plusDays(daysOffset);
      LocalDateTime eventEnd = event.getEndDateTime().plusDays(daysOffset);

      event.setStartDateTime(eventStart);
      event.setEndDateTime(eventEnd);

      targetEventCollection.checkForCollision(event, true);
    }
  }

  public void copyCalenderBetweenDates(LocalDate fromDate, LocalDate toDate, String calName, LocalDate tarCalDate, String tarCalName) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();

    LocalDateTime fromDateTime = fromDate.atStartOfDay();
    LocalDateTime toDateTime = toDate.atTime(LocalTime.of(23, 59));

    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection.searchEventByTimeFrame(fromDateTime, toDateTime));

    //Calculate the days offset between the source and target calendar dates
    long daysOffset = java.time.temporal.ChronoUnit.DAYS.between(fromDate, tarCalDate);

    for (EventModel event : eventsToBeCopied) {
      //convert to target calendar time zone

      LocalDateTime eventStart = event.getStartDateTime().plusDays(daysOffset);
      LocalDateTime eventEnd = event.getEndDateTime().plusDays(daysOffset);

      //set new times
      event.setStartDateTime(eventStart);
      event.setEndDateTime(eventEnd);

      targetEventCollection.checkForCollision(event, true);
    }
  }

  public void copyCalenderBetweenDates(LocalDateTime fromDate,LocalDateTime toDate, String calName, LocalDateTime tarCalDate, String tarCalName) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();
    //ZoneId sourceTimeZone = sourceCalender.getTimeZone();
    //ZoneId targetTimeZone = targetCalender.getTimeZone();

    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection.searchEventByTimeFrame(
            fromDate,toDate));
    changeDateTime(eventsToBeCopied,tarCalDate);
    for (EventModel event : eventsToBeCopied ) {
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

  private List<EventModel> convertTimeZoneList(List<EventModel> events, ZoneId sourceZoneId, ZoneId destinationZoneId) {
    List<EventModel> eventsList = new ArrayList<>();
    eventsList = deepCopyList(events);
    for (EventModel event : eventsList) {
      event.setStartDateTime(convertTimeZone(event.getStartDateTime(), sourceZoneId, destinationZoneId));
      event.setEndDateTime(convertTimeZone(event.getEndDateTime(), sourceZoneId, destinationZoneId));
      eventsList.add(event);
    }
    return eventsList;
  }

  private void changeDateTime(List<EventModel> events, LocalDateTime newDateTime) {
    for (EventModel event : events) {
      event.setStartDateTime(newDateTime);
    }
  }

  public void changeDate(List<EventModel> events, LocalDateTime newDateTime) {
    for (EventModel event : events) {
      LocalDateTime oldStartDateTime = event.getStartDateTime();
      LocalDateTime oldEndDateTime = event.getStartDateTime();

      LocalTime oldStartTime = oldStartDateTime.toLocalTime(); // Extract the original time
      LocalTime oldEndTime = oldEndDateTime.toLocalTime();
      LocalDateTime updatedStartDateTime = LocalDateTime.of(newDateTime.toLocalDate(), oldStartTime);
      LocalDateTime updatedEndDateTime = LocalDateTime.of(newDateTime.toLocalDate(), oldStartTime);// Combine new date with old time

      event.setStartDateTime(updatedStartDateTime);
      event.setEndDateTime(updatedEndDateTime);
    }
  }


  private LocalDateTime convertTimeZone(LocalDateTime dateTime, ZoneId sourceZoneId, ZoneId destinationZoneId) {
    ZonedDateTime sourceZonedDateTime = dateTime.atZone(sourceZoneId);
    ZonedDateTime destinationZonedDateTime = sourceZonedDateTime.withZoneSameInstant(destinationZoneId);
    return destinationZonedDateTime.toLocalDateTime();
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
