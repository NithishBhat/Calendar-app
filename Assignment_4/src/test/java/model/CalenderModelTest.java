package model;

import org.junit.Test;

import java.time.ZoneId;

import controller.EventController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


/**
 * class to test calender model and its functionality.
 */
public class CalenderModelTest {

  @Test
  public void setTimeZone() {
    EventController eventController = new EventController();

    assertEquals(ZoneId.of("UTC"),
            eventController.getEventHandler().getCalenderCollection().getCalenderCollection()
                    .get("default").getTimeZone());
    eventController.getEventHandler().getCalenderCollection().getCalenderCollection().get("default")
            .setTimeZone(ZoneId.of("America/New_York"));
    assertEquals(ZoneId.of("America/New_York"),
            eventController.getEventHandler().getCalenderCollection().getCalenderCollection()
                    .get("default").getTimeZone());
  }

  @Test
  public void setTimeZoneNull() {
    EventController eventController = new EventController();

    // Assert that the default calendar has UTC as its time zone
    assertEquals(ZoneId.of("UTC"),
            eventController.getEventHandler().getCalenderCollection().getCalenderCollection()
                    .get("default").getTimeZone());

    // Assert that setting the time zone to null throws the expected exception
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      eventController.getEventHandler().getCalenderCollection().getCalenderCollection()
              .get("default").setTimeZone(null);
    });
    assertEquals("TimeZone provided is null", thrown.getMessage());
  }

  @Test public void calenderTimezone() {
    EventController eventController = new EventController();

    assertEquals(ZoneId.of("UTC"),eventController.getEventHandler().getCalenderCollection()
            .getCalenderCollection().get("default").getTimeZone());
  }


}
