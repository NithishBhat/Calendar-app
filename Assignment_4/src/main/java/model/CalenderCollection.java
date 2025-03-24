package model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controller.EventHandler;


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

  public void removeCalenderByName(String name) {
    calenderCollection.remove(name);
  }

  public void RemoveAllCalenders() {
    calenderCollection.clear();
  }

  public void ChangeCalenderName(String oldName, String newName) {
    CalenderModel oldCalender = calenderCollection.get(oldName);
    calenderCollection.remove(oldName);
    calenderCollection.put(newName, oldCalender);
  }

  public void copyCalenderFromDate(LocalDateTime calDate, String calName, LocalDateTime tarCalDate, String tarCalName) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();
    //ZoneId sourceTimeZone = sourceCalender.getTimeZone();
    //ZoneId targetTimeZone = targetCalender.getTimeZone();

    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection.searchEventByEventStartDate(calDate));
    changeDateTime(eventsToBeCopied, tarCalDate);
    for (EventModel event : eventsToBeCopied) {
      targetEventCollection.checkForCollision(event, true);
    }
  }

  public void copyCalenderEntireDay(LocalDateTime calDate, String calName, LocalDateTime tarCalDate, String tarCalName) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();
    //ZoneId sourceTimeZone = sourceCalender.getTimeZone();
    //ZoneId targetTimeZone = targetCalender.getTimeZone();

    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection.searchEventByTimeFrame(
            calDate, calDate.toLocalDate().atTime(LocalTime.of(23, 59))));
    changeDate(eventsToBeCopied, tarCalDate);
    for (EventModel event : eventsToBeCopied) {
      targetEventCollection.checkForCollision(event, true);
    }
  }

  public void copyCalenderBetweenDates(LocalDateTime fromDate, LocalDateTime toDate, String calName, LocalDateTime tarCalDate, String tarCalName) {
    CalenderModel sourceCalender = calenderCollection.get(calName);
    CalenderModel targetCalender = calenderCollection.get(tarCalName);
    EventCollectionModel sourceEventCollection = sourceCalender.getEventCollection();
    EventCollectionModel targetEventCollection = targetCalender.getEventCollection();
    //ZoneId sourceTimeZone = sourceCalender.getTimeZone();
    //ZoneId targetTimeZone = targetCalender.getTimeZone();

    List<EventModel> eventsToBeCopied = deepCopyList(sourceEventCollection.searchEventByTimeFrame(
            fromDate, toDate));
    changeDate(eventsToBeCopied, tarCalDate);
    for (EventModel event : eventsToBeCopied) {
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


}
