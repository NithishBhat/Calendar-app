package model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * EventCollectionModel represents a collection of events and provides methods to manage, search,
 * and manipulate events, including checking for conflicts and handling recurring events.
 */
public class EventCollectionModel {
  private List<EventModel> eventList;

  /**
   * Constructor to initialize the event list.
   */
  public EventCollectionModel() {
    eventList = new ArrayList<>();
  }

  /**
   * Adds an event to the collection.
   *
   * @param event The event to be added.
   * @return true if the event was added successfully, false otherwise.
   */
  public boolean addEvent(EventModel event) {
    eventList.add(event);
    return true;
  }

  /**
   * Removes an event from the collection.
   *
   * @param event The event to be removed.
   */
  public void removeEvent(EventModel event) {
    eventList.remove(event);
  }

  /**
   * Retrieves the list of all events in the collection.
   *
   * @return The list of events.
   */
  public List<EventModel> getEventList() {
    return eventList;
  }

  /**
   * Searches for events by their name in event List.
   *
   * @param eventName The name of the event to search for.
   * @return A list of events matching the given name.
   */
  public List<EventModel> searchEventByEventName(String eventName) {
    List<EventModel> filteredList = new ArrayList<>();
    for (EventModel event : eventList) {
      if (event.getEventName().equals(eventName)) {
        filteredList.add(event);
      }
    }
    return filteredList;
  }



  /**
   * returns List of all events within a specified Time Frame on a specific day.
   *
   * @param eventStartDateTime Upper bound of time frame.
   * @param eventEndDateTime   Lower bound of time frame.
   * @return List of Events.
   */
  public List<EventModel> searchEventByTimeFrame(LocalDateTime eventStartDateTime,
                                                 LocalDateTime eventEndDateTime) {
    List<EventModel> filteredList = new ArrayList<>();
    for (EventModel event : eventList) {
      if (!event.getStartDateTime().isBefore(eventStartDateTime)
              && !event.getEndDateTime().isAfter(eventEndDateTime)) {
        filteredList.add(event);
      }
    }
    return filteredList;
  }

  public List<EventModel> searchEventByTimeFrameAndName(String eventName,
                                                        LocalDateTime eventStartDateTime,
                                                        LocalDateTime eventEndDateTime) {
    List<EventModel> filteredList = new ArrayList<>();
    for (EventModel event : eventList) {
      if (!event.getStartDateTime().isBefore(eventStartDateTime)
              && !event.getEndDateTime().isAfter(eventEndDateTime)
              && event.getEventName().equals(eventName)) {
        filteredList.add(event);
      }
    }
    return filteredList;
  }

  public EventModel searchEventByStartTimeAndName(String eventName,
                                                        LocalDateTime eventStartDateTime) {
    List<EventModel> filteredList = new ArrayList<>();
    for (EventModel event : eventList) {
      if (event.getEventName().equals(eventName) && event.getStartDateTime().equals(eventStartDateTime)) {
        return event;
      }
    }
    return null;
  }

  /**
   * Checks if there is any conflict between the given start and end dates with existing events.
   *
   * @param eventStartDate The start date of the event.
   * @param eventEndDate   The end date of the event.
   * @return true if a conflict exists, false otherwise.
   */
  public boolean checkConflict(LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
    for (EventModel event : eventList) {
      //if event overlaps, there is a conflict
      if (!event.getEndDateTime().isBefore(eventStartDate) &&
              !event.getStartDateTime().isAfter(eventEndDate)) {
        return true;
      }

    }
    return false;
  }

  /**
   * Adds a recurring event to the collection by generating
   * all occurrences and checking for conflicts.
   *
   * @param event The recurring event to be added.
   */
  public void addRecurringEvent(RecurringEventModel event) {
    List<EventModel> recurringEvents = generateOccurrences(event);
    for (EventModel e : recurringEvents) {
      addEvent(e);
    }
  }

  /**
   * Generates occurrences for a recurring event based on the recurrence pattern and stops once
   * either the occurrence limit or the end date is reached.
   *
   * @param event The recurring event.
   * @return A list of events representing all occurrences.
   */
  public List<EventModel> generateOccurrences(RecurringEventModel event) {
    List<EventModel> occurringEvents = new ArrayList<>();
    LocalDateTime date = event.getStartDateTime();

    int count = 0;

    //iterate until occurrence (int), if there is a specfied end date then iterate until that date
    while ((event.getOccurrences() == null || count < event.getOccurrences()) &&
            (event.getRecurrenceEndDateTime() == null ||
                    !date.isAfter(event.getRecurrenceEndDateTime()))) {
      //check if one of the specified days of the week
      if (event.getOccuranceDayofWeek().contains(date.getDayOfWeek())) {
        occurringEvents.add(new EventModel(event.getEventName(), date,
                event.getEndDateTime().with(date.toLocalDate()), event.getDescription(),
                event.getIsPrivate(), event.getAutoDecline(), event.getLocation()));
        count++;
      }
      date = date.plusDays(1);
    }
    return occurringEvents;
  }

  /**
   * Prints a list of events in a user-friendly format.
   *
   * @param events The list of events to be printed.
   * @return A formatted string representing the events.
   */
  public String printAssortments(List<EventModel> events) {
    StringBuilder sb = new StringBuilder("\n");
    int count = 0;
    for (EventModel event : events) {
      sb.append(event.bulletString());
      if (++count < events.size()) {
        sb.append("\n"); //add comma before last child
      }
    }
    return sb.append("\n").toString();
  }

  /**
   * Checks the status of a given time frame (whether it is "Busy" or "Available").
   *
   * @param startDateTime The start date of the time frame.
   * @param endDateTime   The end date of the time frame.
   * @return A string indicating the availability ("Busy" or "Available").
   */
  public String checkStatus(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return checkConflict(startDateTime, startDateTime) ? "Busy" : "Available";
  }


  public boolean checkForCollision(EventModel event, boolean autoDecline) {
    boolean hasConflict = checkConflict(event.getStartDateTime(),event.getEndDateTime());
    autoDecline=true;
    if (autoDecline && hasConflict) {
      System.out.println("Event \"" + event.getEventName() + "\" was not created due to conflict");
      return true;
    }
    addEvent(event);
    System.out.println("Event \"" + event.getEventName() + "\" was created");
    return false;
  }


  /**
   * Checks for collision with existing events for recurring events and adds if allowed.
   *
   * @param recurringEvent The recurring event to check and potentially add
   * @param autoDecline    Whether to automatically decline events with conflicts
   * @return true if event was added, false if there was a conflict and autoDecline is true
   */
  public boolean checkForCollisionRecurringEvent(RecurringEventModel recurringEvent, boolean autoDecline) {
    autoDecline=true;
    if (!autoDecline) {
      addRecurringEvent(recurringEvent);
      System.out.println("Event \"" + recurringEvent.getEventName() + "\" was created");
      return false;
    }

    List<EventModel> recurringEventsList = generateOccurrences(recurringEvent);

    for (EventModel event : recurringEventsList) {
      if (checkConflict(event.getStartDateTime(), event.getEndDateTime())) {
        System.out.println("Conflict detected, your recurring event \"" + recurringEvent.getEventName() + "\" was not created");
        return true;
      }
    }

    addRecurringEvent(recurringEvent);
    System.out.println("Event \"" + recurringEvent.getEventName() + "\" was created");
    return false;
  }

}
