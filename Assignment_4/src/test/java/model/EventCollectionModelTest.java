package model;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.EventController;
import controller.EventHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Unit tests for the EventCollectionModel class.
 * This test suite verifies various functionalities related to event management,
 * including adding, searching, and handling conflicts in events.
 * Test cases include:
 * - Accepting and rejecting conflicting events based on auto-decline settings.
 * - Searching for events by name, start date, and within a specific time range.
 * - Handling recurring events, including different recurrence rules and conflicts.
 * - Ensuring the correct number of events is generated for recurring event patterns.
 * Edge cases tested:
 * - Recurring events without a specified occurrence limit but with an end date.
 * - Recurring events with conflicts against existing scheduled events.
 * - Ensuring no infinite loops occur when generating recurring events.
 * Assertions verify correctness by checking event counts and expected properties.
 */
public class EventCollectionModelTest {

  @Test
  public void acceptConflictedEvent() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();

    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime =
            LocalDateTime.of(2025, 3, 1, 15, 0);
    EventModel eventModel = new EventModel("Conference", startDateTime, endDateTime);
    eventCollectionModel.addEvent(eventModel);

    LocalDateTime startDateTime_1 =
            LocalDateTime.of(2025, 3, 1, 10, 15);
    LocalDateTime endDateTime_1 =
            LocalDateTime.of(2025, 3, 1, 16, 0);
    EventModel event2 = new EventModel("Test", startDateTime_1, endDateTime_1);

    boolean added = eventCollectionModel.addEvent(event2);
    System.out.println(eventCollectionModel);

    assertTrue(added);
  }

  @Test
  public void rejectConflictedEvent() {
    EventCollectionModel calendar = new EventCollectionModel();

    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime =
            LocalDateTime.of(2025, 3, 1, 15, 0);
    EventModel event1 = new EventModel("Conference", startDateTime, endDateTime);
    calendar.addEvent(event1);

    LocalDateTime startDateTime_1 =
            LocalDateTime.of(2025, 3, 1, 9, 15);
    LocalDateTime endDateTime_1 =
            LocalDateTime.of(2025, 3, 1, 16, 0);
    EventModel event2 = new EventModel("Conference2", startDateTime_1);
    event2.setAutoDecline(true);

    boolean added = calendar.addEvent(event2);
    assertTrue(added);

    System.out.println(calendar);
  }

  @Test
  public void searchEvent() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime =
            LocalDateTime.of(2025, 3, 1, 15, 0);
    EventModel event1 = new EventModel("Conference", startDateTime, endDateTime);

    eventCollectionModel.addEvent(event1);

    LocalDateTime startDateTime_1 =
            LocalDateTime.of(2025, 4, 1, 10, 15);
    LocalDateTime endDateTime_1 =
            LocalDateTime.of(2025, 4, 1, 16, 0);
    EventModel event2 = new EventModel("Conference", startDateTime_1, endDateTime_1);

    eventCollectionModel.addEvent(event2);

    LocalDateTime startDateTime_2 =
            LocalDateTime.of(2025, 5, 1, 10, 15);
    LocalDateTime endDateTime_2 =
            LocalDateTime.of(2025, 5, 1, 16, 0);
    EventModel event3 = new EventModel("Presentation", startDateTime_2, endDateTime_2);

    eventCollectionModel.addEvent(event3);

    List<EventModel> events =
            new ArrayList<>(eventCollectionModel.searchEventByEventName("Conference"));
    assertEquals(events.size(), 2);
  }


  @Test
  public void searchEventByRange() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 4, 1, 12, 1);
    EventModel event1 = new EventModel("Conference", startDateTime);

    eventCollectionModel.addEvent(event1);

    LocalDateTime startDateTime_1 =
            LocalDateTime.of(2025, 3, 1, 10, 15);
    LocalDateTime endDateTime_1 =
            LocalDateTime.of(2025, 3, 1, 16, 0);
    EventModel event2 = new EventModel("Conference2", startDateTime_1, endDateTime_1);

    eventCollectionModel.addEvent(event2);

    LocalDateTime startDateTime_2 =
            LocalDateTime.of(2025, 4, 1, 10, 15);
    LocalDateTime endDateTime_2 =
            LocalDateTime.of(2025, 4, 1, 18, 0);
    EventModel event3 = new EventModel("Presentation", startDateTime_2, endDateTime_2);

    eventCollectionModel.addEvent(event3);

    LocalDateTime startDateTime_search =
            LocalDateTime.of(2025, 4, 1, 1, 0);
    LocalDateTime endDateTime_search =
            LocalDateTime.of(2025, 4, 1, 23, 59);

    List<EventModel> events = new ArrayList<>(eventCollectionModel
            .searchEventByTimeFrame(startDateTime_search, endDateTime_search));

    assertEquals(events.size(), 2);
    assertEquals("Conference", events.get(0).getEventName());
    assertEquals("Presentation", events.get(1).getEventName());
  }

  @Test
  public void testRecurringEvents() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime =
            LocalDateTime.of(2025, 3, 1, 15, 0);

    List<Character> days = Arrays.asList('M', 'T', 'W');// i changed it from date time to char list
    RecurringEventModel recurringCalendarEvent = new RecurringEventModel(
            "Conference",
            startDateTime,
            endDateTime,
            true,
            days,
            5,
            null);
    eventCollectionModel.addRecurringEvent(recurringCalendarEvent);

    List<EventModel> events = new ArrayList<>(eventCollectionModel.getEventList());

    assertEquals(events.size(), 5);
    //System.out.println(eventCollectionModel);
  }

  @Test
  public void testRecurringEventsWithoutOccurrences() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime =
            LocalDateTime.of(2025, 3, 1, 11, 0);

    LocalDateTime recurrenceEndDateTime =
            LocalDateTime.of(2025, 4, 2, 0, 0);

    List<Character> days = Arrays.asList('M', 'T', 'W');// i changed it from date time to char list
    RecurringEventModel recurringCalendarEvent = new RecurringEventModel(
            "Conference",
            startDateTime,
            endDateTime,
            true,
            days,
            null,
            recurrenceEndDateTime);
    eventCollectionModel.addRecurringEvent(recurringCalendarEvent);

    List<EventModel> events = new ArrayList<>(eventCollectionModel.getEventList());

    assertEquals(events.size(), 14);
  }

  @Test
  public void testRecurringEventsWithConflicts() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 4, 1, 12, 1);
    EventModel event1 = new EventModel("Presentation", startDateTime);

    eventCollectionModel.addEvent(event1);

    LocalDateTime startDateTime2 =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime2 =
            LocalDateTime.of(2025, 3, 1, 13, 0);

    LocalDateTime recurrenceEndDateTime =
            LocalDateTime.of(2025, 4, 2, 0, 0);

    List<Character> days = Arrays.asList('M', 'T', 'W');// i changed it from date time to char list
    RecurringEventModel recurringCalendarEvent = new RecurringEventModel(
            "Conference",
            startDateTime2,
            endDateTime2,
            true,
            days,
            null,
            recurrenceEndDateTime);
    eventCollectionModel.addRecurringEvent(recurringCalendarEvent);

    List<EventModel> events = new ArrayList<>(eventCollectionModel.getEventList());
    assertEquals(events.size(), 15);
    eventCollectionModel.removeEvent(events.get(0));
    assertEquals(eventCollectionModel.getEventList().size(), 14);
  }

  @Test
  public void checkConflictingEvents() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime =
            LocalDateTime.of(2025, 3, 1, 15, 0);

    LocalDateTime startDateTimeX =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTimeY =
            LocalDateTime.of(2025, 3, 1, 15, 0);
    EventModel eventX = new EventModel("Conference", startDateTime, endDateTime);
    EventModel eventY = new EventModel("Conference", startDateTimeX, endDateTimeY);

    eventCollectionModel.addEvent(eventX);
    assertEquals(true,eventCollectionModel.checkForCollision(eventY,true));
  }


  @Test
  public void testRecurringEventModelInitialization() {
    // Sample valid inputs
    String eventName = "Daily Standup";
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 25, 9, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 25, 9, 30);
    boolean autoDecline = false;
    List<Character> recurringDays = Arrays.asList('M', 'T', 'W', 'T', 'F'); // Weekdays
    Integer occurrences = 5;
    LocalDateTime recurrenceEndDateTime = LocalDateTime.of(2025, 4, 1, 9, 0);

    RecurringEventModel event = new RecurringEventModel(
            eventName, startDateTime, endDateTime, autoDecline, recurringDays, occurrences, recurrenceEndDateTime
    );

    // Assertions
    assertEquals(eventName, event.getEventName());
    assertEquals(startDateTime, event.getStartDateTime());
    assertEquals(endDateTime, event.getEndDateTime());
    assertFalse(event.isAutoDecline());
    assertEquals(recurringDays, event.getRecurringDays());
    assertEquals(occurrences, event.getOccurrences());
    assertEquals(recurrenceEndDateTime, event.getRecurrenceEndDateTime());
  }




  @Test
  public void testRecurringEventModelCollision() {
    String eventName = "Meeting";
    EventController eventController = new EventController();
    EventHandler eventHandler = eventController.getEventHandler();

    // Fixing the incorrect endDateTime (should be after startDateTime)
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 29, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 29, 11, 0); // Fixed: End is after start

    boolean autoDecline = true;
    List<Character> recurringDays = Arrays.asList('M', 'W');
    Integer occurrences = 5;
    LocalDateTime recurrenceEndDateTime = LocalDateTime.of(2025, 4, 5, 10, 0);
    String dateTimeString = "2025-03-31T10:00";
    LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString);
    RecurringEventModel recurringEventModel = new RecurringEventModel(
            eventName, startDateTime, endDateTime, autoDecline, recurringDays, occurrences, recurrenceEndDateTime
    );

    RecurringEventModel recurringEventModelX = new RecurringEventModel(
            "X", startDateTime.plusHours(1), endDateTime.plusHours(1), autoDecline, recurringDays, occurrences, recurrenceEndDateTime
    );

    assertEquals(false,eventHandler.getEventCollection().checkForCollisionRecurringEvent(recurringEventModel, true));
    assertEquals(true,eventHandler.getEventCollection().checkForCollisionRecurringEvent(recurringEventModelX, true));

    assertEquals(2, eventHandler.getEventCollection().getEventList().size());
    assertEquals(recurringEventModel.getEventName(), eventHandler.getEventCollection().getEventList().get(0).getEventName());
    assertEquals(localDateTime, eventHandler.getEventCollection().getEventList().get(0).getStartDateTime());
  }

  @Test
  public void testSearchEventByTimeFrame_WithinRange() {
    EventController eventController = new EventController();
    EventHandler eventHandler = eventController.getEventHandler();
    eventHandler.getEventCollection().checkForCollision(new EventModel("Meeting",
            LocalDateTime.of(2025, 3, 25, 10, 0),
            LocalDateTime.of(2025, 3, 25, 11, 0),
            false),false);

    eventHandler.getEventCollection().checkForCollision(new EventModel("Lunch",
            LocalDateTime.of(2025, 3, 25, 12, 30),
            LocalDateTime.of(2025, 3, 25, 13, 30),
            false),false);

    LocalDateTime searchStart = LocalDateTime.of(2025, 3, 25, 10, 0);
    LocalDateTime searchEnd = LocalDateTime.of(2025, 3, 25, 13, 30);

    List<EventModel> result = eventHandler.getEventCollection().searchEventByTimeFrame(searchStart, searchEnd);

    assertEquals(2, result.size());
    assertEquals("Meeting", result.get(0).getEventName());
    assertEquals("Lunch", result.get(1).getEventName());
  }


  @Test
  public void autoDeclineEventCollectionModel() {
    EventController eventController = new EventController();
    EventHandler eventHandler = eventController.getEventHandler();
    eventHandler.getEventCollection().checkForCollision(new EventModel("Meeting",
            LocalDateTime.of(2025, 3, 25, 10, 0),
            LocalDateTime.of(2025, 3, 25, 11, 0),
            false),false);
    eventHandler.getEventCollection().checkForCollision(new EventModel("Meeting",
            LocalDateTime.of(2025, 3, 25, 10, 0),
            LocalDateTime.of(2025, 3, 25, 11, 0),
            false),true);
    assertEquals(1,eventHandler.getEventCollection().getEventList().size());

  }

  @Test
  public void nullSearchEventByTimeFrame() {
    EventController eventController = new EventController();
    EventHandler eventHandler = eventController.getEventHandler();
    assertEquals(null,eventHandler.getEventCollection().searchEventByStartTimeAndName("Meeting", LocalDateTime.of(2025, 3, 25, 10, 0)));
  }

}
