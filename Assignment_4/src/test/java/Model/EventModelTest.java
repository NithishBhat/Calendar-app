package Model;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;


public class EventModelTest {

  @Test
  public void ModelCreationCheck() {
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 1, 15, 0);

    EventModel eventModel = new EventModel("Conference", startDateTime, endDateTime);

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
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);

    EventModel eventModel = new EventModel("Conference", startDateTime);

    assertEquals("Conference", eventModel.getEventName());
    assertEquals(startDateTime, eventModel.getStartDateTime());
    assertEquals(LocalDateTime.of(2025, 3, 1, 23, 59), eventModel.getEndDateTime());
    assertEquals(null, eventModel.getDescription());
    assertEquals(null, eventModel.getLocation());
    assertFalse(eventModel.getIsPrivate());
    assertFalse(eventModel.getAutoDecline());

    System.out.println(eventModel);
  }
}
