import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import Model.EventModel;

import static org.junit.Assert.assertEquals;



public class EventModelTest {

  @Test
  public void ModelCreationCheck() {
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 1, 15, 0);

    EventModel eventModel = new EventModel("Conference", startDateTime, endDateTime, "Tech conference on AI", true);

    assertEquals("Conference", eventModel.getEventName());
    assertEquals(startDateTime, eventModel.getStartDateTime());
    assertEquals(endDateTime, eventModel.getEndDateTime());
    assertEquals("Tech conference on AI", eventModel.getDescription());
  }

  // need to check what to do for wrong date wrong time


  @Test
  public void badMonthCreationCheck() {
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 10000, 1, 15, 0);

    EventModel eventModel = new EventModel("Conference", startDateTime, endDateTime, "Tech conference on AI", true);

    assertEquals("Conference", eventModel.getEventName());
    assertEquals(startDateTime, eventModel.getStartDateTime());
    assertEquals(endDateTime, eventModel.getEndDateTime());
    assertEquals("Tech conference on AI", eventModel.getDescription());
  }
}
