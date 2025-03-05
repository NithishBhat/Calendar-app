import Model.RecurringEventModel;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.EventCollectionModel;
import Model.EventModel;
import Model.ExportToCSV;

import static org.junit.Assert.*;

public class EventCollectionModelTest {

  @Test
  public void acceptConflictedEvent() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();

    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 1, 15, 0);
    EventModel eventModel = new EventModel("Conference", startDateTime, endDateTime);
    eventCollectionModel.addEvent(eventModel);

    LocalDateTime startDateTime_1 = LocalDateTime.of(2025, 3, 1, 10, 15);
    LocalDateTime endDateTime_1 = LocalDateTime.of(2025, 3, 1, 16, 0);
    EventModel event2 = new EventModel("Test", startDateTime_1, endDateTime_1);

    boolean added = eventCollectionModel.addEvent(event2);
    System.out.println(eventCollectionModel);

    assertTrue(added);
  }

  @Test
  public void rejectConflictedEvent() {
    EventCollectionModel calendar = new EventCollectionModel();

    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 1, 15, 0);
    EventModel event1 = new EventModel("Conference", startDateTime, endDateTime);
    calendar.addEvent(event1);

    LocalDateTime startDateTime_1 = LocalDateTime.of(2025, 3, 1, 9, 15);
    LocalDateTime endDateTime_1 = LocalDateTime.of(2025, 3, 1, 16, 0);
    EventModel event2 = new EventModel("Conference2", startDateTime_1);
    event2.setAutoDecline(true);

    boolean added = calendar.addEvent(event2);
    assertFalse(added);

    System.out.println(calendar);
  }

  @Test
  public void searchEvent() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 1, 15, 0);
    EventModel event1 = new EventModel("Conference", startDateTime, endDateTime);

    eventCollectionModel.addEvent(event1);

    LocalDateTime startDateTime_1 = LocalDateTime.of(2025, 4, 1, 10, 15);
    LocalDateTime endDateTime_1 = LocalDateTime.of(2025, 4, 1, 16, 0);
    EventModel event2 = new EventModel("Conference", startDateTime_1, endDateTime_1);

    eventCollectionModel.addEvent(event2);

    LocalDateTime startDateTime_2 = LocalDateTime.of(2025, 5, 1, 10, 15);
    LocalDateTime endDateTime_2 = LocalDateTime.of(2025, 5, 1, 16, 0);
    EventModel event3 = new EventModel("Presentation", startDateTime_2, endDateTime_2);

    eventCollectionModel.addEvent(event3);

    List<EventModel> events  = new ArrayList<>(eventCollectionModel.searchEventByEventName("Conference"));
    assertEquals(events.size(), 2);
  }

  @Test
  public void searchEventByStartDateTime() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 1, 15, 0);
    EventModel event1 = new EventModel("Conference", startDateTime, endDateTime);

    eventCollectionModel.addEvent(event1);

    LocalDateTime startDateTime_2 = LocalDateTime.of(2025, 5, 1, 10, 15);
    LocalDateTime endDateTime_2 = LocalDateTime.of(2025, 5, 1, 16, 0);
    EventModel event3 = new EventModel("Presentation", startDateTime_2, endDateTime_2);

    eventCollectionModel.addEvent(event3);

    LocalDateTime startDateTime_search = LocalDateTime.of(2025, 5, 1, 10, 15);

    List<EventModel> events  = new ArrayList<>(eventCollectionModel
            .searchEventByEventStartDate(startDateTime_search));
    assertEquals(events.size(), 1);
  }

  @Test
  public void searchEventByRange() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime = LocalDateTime.of(2025, 4, 1, 12, 1);
    EventModel event1 = new EventModel("Conference", startDateTime);

    eventCollectionModel.addEvent(event1);

    LocalDateTime startDateTime_1 = LocalDateTime.of(2025, 3, 1, 10, 15);
    LocalDateTime endDateTime_1 = LocalDateTime.of(2025, 3, 1, 16, 0);
    EventModel event2 = new EventModel("Conference2", startDateTime_1, endDateTime_1);

    eventCollectionModel.addEvent(event2);

    LocalDateTime startDateTime_2 = LocalDateTime.of(2025, 4, 1, 10, 15);
    LocalDateTime endDateTime_2 = LocalDateTime.of(2025, 4, 1, 18, 0);
    EventModel event3 = new EventModel("Presentation", startDateTime_2, endDateTime_2);

    eventCollectionModel.addEvent(event3);

    LocalDateTime startDateTime_search = LocalDateTime.of(2025, 4, 1, 1, 0);
    LocalDateTime endDateTime_search = LocalDateTime.of(2025, 4, 1, 23, 59);

    List<EventModel> events  = new ArrayList<>(eventCollectionModel
            .searchEventByTimeFrame(startDateTime_search, endDateTime_search));

    assertEquals(events.size(), 2);
    assertEquals("Conference", events.get(0).getEventName());
    assertEquals("Presentation", events.get(1).getEventName());
  }

  @Test
  public void testRecurringEvents() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 1, 15, 0);

    List<DayOfWeek> days = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY);
    RecurringEventModel recurringCalendarEvent = new RecurringEventModel(
            "Conference",
            startDateTime,
            endDateTime,
            true,
            days,
            5,
            null);
    eventCollectionModel.addRecurringEvent(recurringCalendarEvent);

    List<EventModel> events  = new ArrayList<>(eventCollectionModel.getEventList());

    assertEquals(events.size(), 5);
    //System.out.println(eventCollectionModel);
  }

  @Test
  public void testRecurringEventsWithoutOccurrences() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 1, 11, 0);

    LocalDateTime RecurrenceEndDateTime = LocalDateTime.of(2025, 4, 2, 0, 0);

    List<DayOfWeek> days = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY);
    RecurringEventModel recurringCalendarEvent = new RecurringEventModel(
            "Conference",
            startDateTime,
            endDateTime,
            true,
            days,
            -1,
            RecurrenceEndDateTime);
    eventCollectionModel.addRecurringEvent(recurringCalendarEvent);

    List<EventModel> events  = new ArrayList<>(eventCollectionModel.getEventList());

    assertEquals(events.size(), 14);
  }

  @Test
  public void testRecurringEventsWithConflicts() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime = LocalDateTime.of(2025, 4, 1, 12, 1);
    EventModel event1 = new EventModel("Presentation", startDateTime);

    eventCollectionModel.addEvent(event1);

    LocalDateTime startDateTime2 = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime2 = LocalDateTime.of(2025, 3, 1, 13, 0);

    LocalDateTime RecurrenceEndDateTime = LocalDateTime.of(2025, 4, 2, 0, 0);

    List<DayOfWeek> days = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY);
    RecurringEventModel recurringCalendarEvent = new RecurringEventModel(
            "Conference",
            startDateTime2,
            endDateTime2,
            true,
            days,
            -1,
            RecurrenceEndDateTime);
    eventCollectionModel.addRecurringEvent(recurringCalendarEvent);

    List<EventModel> events  = new ArrayList<>(eventCollectionModel.getEventList());
    assertEquals(events.size(), 1);
  }
}
