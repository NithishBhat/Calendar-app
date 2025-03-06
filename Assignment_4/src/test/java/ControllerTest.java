import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import Controller.EventController;
import Model.EventModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ControllerTest {

  @Before
  public void setUp(){

  }
  @Test
  public void processNormalCreation() {

    //this.eventName = eventName;
    //this.startDateTime = startDateTime;
    //this.endDateTime = endDateTime;
    //this.description = "No Description";
    //this.isPrivate = false;
    //this.autoDecline = autoDecline;
    //this.location = "No Location";

    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline hello world from 2025-10-10T10:00 to 2025-10-10T10:00");
  }
  //create event --autodecline <eventName> from <dateStringTTimeString> to <dateStringTTimeString>
  //create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
  //create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
  //create event --autoDecline <eventName> on <dateStringTtimeString>
  //create event <eventName> on <dateString> repeats <weekdays> for <N> times
  //create event <eventName> on <dateString> repeats <weekdays> until <dateString>

  @Test
  public void processEventCreation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event hello world from 2025-10-10T10:00 to 2025-10-10T10:00");
  }

  @Test
  public void processrecurringNCreation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline hello world from 2025-10-10T10:00 to 2025-10-10T10:00 repeats mon,tue");
  }
}
