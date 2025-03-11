package Model;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;


public class EventCollectionModel {
  private List<EventModel> eventList;

  public EventCollectionModel() {
    eventList = new ArrayList<>();
  }

  public boolean addEvent(EventModel event) {
    //if (event.isAutoDecline() && checkConflict(event.getStartDateTime(), event.getEndDateTime())) {
    //  return false; //conflict, cannot add event
    //}
    // Nithish handles autodecline in the controller
    eventList.add(event);
    return true;
  }

  public void removeEvent(EventModel event) {
    eventList.remove(event);
  }

  public List<EventModel> getEventList() {
    return eventList;
  }

  /**
   * returns all events with the same event name
   * @param eventName
   * @return filteredList
   */
  public List<EventModel> searchEventByEventName(String eventName) {
    List<EventModel> filteredList = new ArrayList<>();
    for (EventModel event : eventList) {
      if(event.getEventName().equals(eventName)){
        filteredList.add(event);
      }
    }
    return filteredList;
  }

  /**
   * returns DateTime of all events with the same StartDate
   * @param eventDate
   * @return filteredList
   */
  public List<EventModel> searchEventByEventStartDate(LocalDateTime eventDate) {
    List<EventModel> filteredList = new ArrayList<>();
    for (EventModel event : eventList) {
      if(event.getStartDateTime().equals(eventDate))
      {
        filteredList.add(event);
      }
    }
    return filteredList;
  }

  /**
   * returns DateTime of all events with collisions with the parameter StartDate and endDate
   * probably goes into controller or does it??
   * @param eventStartDate
   * @param eventEndDate
   * @return filteredList
   */
  public List<EventModel> collisionSearch(LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
    List<EventModel> filteredList = new ArrayList<>();
    for (EventModel event : eventList) {
      LocalDateTime eventEnd = event.getEndDateTime() == null ? event.getStartDateTime() : event.getEndDateTime();

      if (!eventEnd.isBefore(eventStartDate) && !event.getStartDateTime().isAfter(eventEndDate)) {
        filteredList.add(event);
      }
    }
    return filteredList;
  }

  /**
   * returns List of all events within a specified Time Frame on a specific day
   * @param eventStartDateTime Upper bound of time frame
   * @param eventEndDateTime Lower bound of time frame
   * @return List of Events
   */
  public List<EventModel> searchEventByTimeFrame(LocalDateTime eventStartDateTime, LocalDateTime eventEndDateTime) {
    List<EventModel> filteredList = new ArrayList<>();
    for (EventModel event : eventList) {
      if (!event.getStartDateTime().isBefore(eventStartDateTime) && !event.getEndDateTime().isAfter(eventEndDateTime)) {
        filteredList.add(event);
      }
    }
    return filteredList;
  }

  public boolean checkConflict(LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
    for (EventModel event : eventList) {
      //if event overlaps, there is a conflict
      if (!event.getEndDateTime().isBefore(eventStartDate) && !event.getStartDateTime().isAfter(eventEndDate)) {
        return true;
      }

    }
    return false;
  }


  public void addRecurringEvent(RecurringEventModel event) {
    List<EventModel> recurringEvents = generateOccurrences(event);

    //conflict check
    for(EventModel e : recurringEvents){
      if(checkConflict(e.getStartDateTime(), e.getEndDateTime())) {
        //if(e.getAutoDecline()){
          //return;}
        // Nithish removed the conflict check . code needs revaluating
          //recurring event conflicts with exisiting event
          //should return error in the future
        }

      }
    for (EventModel e : recurringEvents) {
      addEvent(e);
    }
  }

  public List<EventModel> generateOccurrences(RecurringEventModel event) {
    List<EventModel> occurringEvents = new ArrayList<>();
    LocalDateTime date = event.getStartDateTime();

    int count = 0;

    //iterate until occurrence (int), if there is a specfied end date then iterate until that date
    while((event.getOccurrences() == null || count < event.getOccurrences())
    && (event.getRecurrenceEndDateTime() == null || !date.isAfter(event.getRecurrenceEndDateTime()))) {
      //check if one of the specified days of the week
      if (event.getOccuranceDayofWeek().contains(date.getDayOfWeek())) {
        occurringEvents.add(new EventModel(
                event.getEventName(),
                date,
                event.getEndDateTime().with(date.toLocalDate()),
                event.getAutoDecline()
        ));
        count++;
      }
      date = date.plusDays(1);
    }
    return occurringEvents;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("EventCollectionModel{\n");
    int count = 0;
    for (EventModel event : eventList) {
      sb.append(event.toString());
      if (++count < eventList.size()) {
        sb.append(","); //add comma before last child
      }
      sb.append("\n");
    }
    return sb.append("}\n").toString();
  }

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

}
