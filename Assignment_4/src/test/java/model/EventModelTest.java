package model;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Unit tests for the EventModel class.
 * This test suite verifies the correct creation and initialization of EventModel objects.
 * Test cases include:
 * - Checking if an EventModel instance is correctly created with full details.
 * - Verifying default values when only mandatory parameters are provided.
 * - Ensuring getter methods return expected values.
 * Assertions validate:
 * - Event name, start time, and end time.
 * - Description, location, and privacy settings.
 * - Default values for optional fields like auto-decline.
 * The test suite ensures that EventModel correctly handles event creation
 * and maintains expected default behaviors.
 */
public class EventModelTest {

  @Test
  public void ModelCreationCheck() {
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime =
            LocalDateTime.of(2025, 3, 1, 15, 0);

    EventModel eventModel =
            new EventModel("Conference", startDateTime, endDateTime);

    eventModel.setDescription("Tech conference on AI");
    eventModel.setLocation("Conference Center");
    eventModel.setIsPrivate(true);

    assertEquals("Conference", eventModel.getEventName());
    assertEquals(startDateTime, eventModel.getStartDateTime());
    assertEquals(endDateTime, eventModel.getEndDateTime());
    assertEquals("Tech conference on AI", eventModel.getDescription());
    assertEquals("Conference Center", eventModel.getLocation());
    assertTrue(eventModel.getIsPrivate());
    assertFalse(eventModel.getAutoDecline());

    System.out.println(eventModel);
  }

  @Test
  public void TestBaseParams() {
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 3, 1, 10, 0);

    EventModel eventModel = new EventModel("Conference", startDateTime);

    assertEquals("Conference", eventModel.getEventName());
    assertEquals(startDateTime, eventModel.getStartDateTime());
    assertEquals(LocalDateTime.of(
            2025, 3, 1, 23, 59), eventModel.getEndDateTime());
    assertEquals(null, eventModel.getDescription());
    assertEquals(null, eventModel.getLocation());
    assertFalse(eventModel.getIsPrivate());
    assertFalse(eventModel.getAutoDecline());

    System.out.println(eventModel);
  }

  @Test
  public void TestExtraParams() {
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime =
            LocalDateTime.of(2025, 3, 1, 12, 0);

    EventModel eventModel = new EventModel("Conference", startDateTime, endDateTime,
            "description", false, true, "location");

    assertEquals("Conference", eventModel.getEventName());
    assertEquals(startDateTime, eventModel.getStartDateTime());
    assertEquals(LocalDateTime.of(
            2025, 3, 1, 12, 0), eventModel.getEndDateTime());
    assertEquals("description", eventModel.getDescription());
    assertEquals("location", eventModel.getLocation());
    assertFalse(eventModel.getIsPrivate());
    assertTrue(eventModel.getAutoDecline());

    System.out.println(eventModel);
  }

  @Test
  public void TestSameStartAndEndTime() {
    LocalDateTime dateTime = LocalDateTime
            .of(2025, 3, 1, 10, 0);
    EventModel eventModel = new EventModel("Meeting", dateTime, dateTime);

    assertEquals(dateTime, eventModel.getStartDateTime());
    assertEquals(dateTime, eventModel.getEndDateTime());
  }

  @Test
  public void TestNullEventName() {
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 3, 1, 10, 0);
    EventModel event = new EventModel(null, startDateTime);
    assertEquals(null, event.getEventName());

    event.setEventName("");
    assertEquals("", event.getEventName());
  }

  @Test
  public void TestNullEndDateTime() {
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 3, 1, 10, 0);
    EventModel event = new EventModel("hello", startDateTime);
    assertEquals(LocalDateTime
            .of(2025, 3, 1, 23, 59), event.getEndDateTime());

    assertEquals(false, event.getAutoDecline());
  }

  @Test
  public void checkAllDayevent() {
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 3, 1, 0, 0);
    EventModel event = new EventModel("hello", startDateTime);
    assertEquals(LocalDateTime
            .of(2025, 3, 1, 23, 59), event.getEndDateTime());

    assertEquals(true, event.isAllDayEvent());
  }

  @Test
  public void checkAllDayeventNegative() {
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 3, 1, 10, 0);
    EventModel event = new EventModel("hello", startDateTime);
    assertEquals(LocalDateTime
            .of(2025, 3, 1, 23, 59), event.getEndDateTime());

    assertEquals(false, event.isAllDayEvent());
  }

  @Test
  public void afterDateTime() {
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 3, 1, 10, 0);
    EventModel event = new EventModel("hello", startDateTime);
    LocalDateTime dateTime = LocalDateTime
            .of(2025, 3, 1, 11, 0);

    assertEquals(false, event.isAfterDateTime(dateTime));
  }

  @Test
  public void invalidAfterDateTime() {
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 3, 1, 10, 0);
    EventModel event = new EventModel("hello", startDateTime);
    LocalDateTime dateTime = LocalDateTime
            .of(2025, 3, 1, 9, 0);

    assertEquals(true, event.isAfterDateTime(dateTime));
  }

  @Test
  public void betweenDateTime() {
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 3, 1, 10, 0);

    EventModel event = new EventModel("hello", startDateTime);
    LocalDateTime dateTimeX = LocalDateTime
            .of(2025, 3, 1, 9, 0);
    LocalDateTime dateTimeY = LocalDateTime
            .of(2025, 4, 1, 9, 0);

    assertEquals(true, event.isEventBetweenDates(dateTimeX, dateTimeY));
  }

  @Test
  public void betweenDateTimeFalse() {
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 3, 1, 10, 0);

    EventModel event = new EventModel("hello", startDateTime);
    LocalDateTime dateTimeX = LocalDateTime
            .of(2025, 3, 1, 11, 0);
    LocalDateTime dateTimeY = LocalDateTime
            .of(2025, 3, 1, 18, 0);

    assertEquals(false, event.isEventBetweenDates(dateTimeX, dateTimeY));
  }

  @Test
  public void autoDeclineTest() {
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime
            .of(2025, 3, 1, 11, 0);

    EventModel event = new EventModel("hello", startDateTime);
    assertEquals(false, event.isAutoDecline());
    event.setAutoDecline(true);
    assertEquals(true, event.isAutoDecline());
  }


}
