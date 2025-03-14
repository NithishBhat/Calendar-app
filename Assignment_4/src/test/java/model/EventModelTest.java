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
}
