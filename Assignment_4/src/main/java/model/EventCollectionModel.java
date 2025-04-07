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
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";

  private static final String infoString = ANSI_YELLOW + "[INFO] " + ANSI_RESET;
  private static final String warningString = ANSI_RED + "[WARNING] " + ANSI_RESET;
  private static final String successString = ANSI_GREEN + "[SUCCESS] " + ANSI_RESET;

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
   * Adds a recurring event to the collection by generating all of its occurrences
   * and adding them to the event list.
   *
   * @param event The recurring event to be added.
   * @return true if the recurring event was added successfully, false otherwise.
   */
  public boolean addRecurringEvent(RecurringEventModel event) {
    List<EventModel> occurrences = generateOccurrences(event);
    for (EventModel occurrence : occurrences) {
      addEvent(occurrence);
    }
    return true;
  }

  /**
   * Checks for collision between a given event and existing events.
   * If auto-decline is enabled and a collision is detected, the event won't be added.
   * Otherwise, the event will be added regardless of collision.
   *
   * @param event The event to check for collision.
   * @param autoDecline Whether to auto-decline events with collisions.
   * @return true if the event was added, false if it was declined due to collision.
   */
  public boolean checkForCollision(EventModel event, boolean autoDecline) {
    boolean hasCollision = checkConflict(event.getStartDateTime(), event.getEndDateTime());

    if (hasCollision && event.isAutoDecline()) {
      return false;
    }

    eventList.add(event);
    return true;
  }

  /**
   * Checks for collision between a recurring event's occurrences and existing events.
   * If auto-decline is enabled and any occurrence has a collision, the recurring event won't be added.
   * Otherwise, all occurrences will be added regardless of collisions.
   *
   * @param event The recurring event to check for collision.
   * @param autoDecline Whether to auto-decline events with collisions.
   * @return true if the recurring event was added, false if it was declined due to collision.
   */
  public boolean checkForCollisionRecurringEvent(RecurringEventModel event, boolean autoDecline) {
    List<EventModel> occurrences = generateOccurrences(event);

    if (autoDecline) {
      for (EventModel occurrence : occurrences) {
        if (checkConflict(occurrence.getStartDateTime(), occurrence.getEndDateTime())) {
          return false;
        }
      }
    }

    // If we get here, either autoDecline is false or no collisions were found
    for (EventModel occurrence : occurrences) {
      eventList.add(occurrence);
    }

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

  /**
   * Searches for events by their name and start/end time frame.
   * Filters events that fall within the specified time frame and match the event name.
   *
   * @param eventName the name of the event to search for.
   * @param eventStartDateTime the start time of the time frame to filter events by.
   * @param eventEndDateTime the end time of the time frame to filter events by.
   * @return a list of events that match the specified name and fall within the given time frame.
   */
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

  /**
   * Searches for an event by its name and start time.
   * Returns the first event that matches both the event name and start time.
   *
   * @param eventName the name of the event to search for.
   * @param eventStartDateTime the start time of the event to search for.
   * @return the event that matches the specified name and start time, or null if no match is found.
   */
  public EventModel searchEventByStartTimeAndName(String eventName,
                                                  LocalDateTime eventStartDateTime) {
    for (EventModel event : eventList) {
      if (event.getEventName().equals(eventName)
              && event.getStartDateTime().equals(eventStartDateTime)) {
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


}