package Model;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class EventCollectionModel {
  private List<EventModel> eventList;

  public EventCollectionModel() {
    eventList = new ArrayList<>();
  }

  public void addEvent(EventModel event) {
    eventList.add(event);
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
      if(event.getEventName() == eventName){
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
      if(event.getStartDateTime() == eventDate)
      {
        filteredList.add(event);
      }
    }
    return filteredList;
  }

  /**
   * returns DateTime of all events with collisions with the parameter StartDate and endDate
   * probably goes into controller or does it??
   * @param eventStartDate,eventEndDate
   * @return filteredList
   */

  public List<EventModel> collisionSearch(LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
    List<EventModel> filteredList = new ArrayList<>();
    for (EventModel event : eventList) {
      if (!event.getEndDateTime().isBefore(eventStartDate) && !event.getStartDateTime().isAfter(eventEndDate)) {
        filteredList.add(event);
      }
    }
    return filteredList;
  }

}
