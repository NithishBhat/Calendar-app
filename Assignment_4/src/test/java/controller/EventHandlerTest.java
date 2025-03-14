package controller;

import model.EventModel;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Testing class of event handler and Event controller and the command parser.
 * Test all possible commands and handles all event types and edge cases.
 */

public class EventHandlerTest {

  @Test
  public void normalEventCreation() {

    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T10:00 " +
            "description:\"test HELLO\" location:\"MA\" privacy:\"true\"");

    assertEquals(1, eventController.getEventCollection().getEventList().size());
    EventModel event = eventController.getEventCollection().getEventList().get(0);

    assertEquals("hello world", event.getEventName());
    assertEquals("2025-10-10T10:00", event.getStartDateTime().toString());
    assertEquals("2025-10-10T10:00", event.getEndDateTime().toString());
    assertEquals("test HELLO", event.getDescription());
    assertEquals("MA", event.getLocation());
    assertEquals(true, event.getIsPrivate());
  }

  @Test
  public void eventCreationWithLocation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T10:00 location:\"MA\"");
    EventModel event = eventController.getEventCollection().getEventList().get(0);

    assertEquals("hello world", event.getEventName());
    assertEquals("2025-10-10T10:00", event.getStartDateTime().toString());
    assertEquals("2025-10-10T10:00", event.getEndDateTime().toString());
    assertEquals(null, event.getDescription());
    assertEquals("MA", event.getLocation());
    assertEquals(false, event.getIsPrivate());
  }

  @Test
  public void eventCreationWithDescription() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T10:00 description:\"test\"");
    EventModel event = eventController.getEventCollection().getEventList().get(0);

    assertEquals("hello world", event.getEventName());
    assertEquals("2025-10-10T10:00", event.getStartDateTime().toString());
    assertEquals("2025-10-10T10:00", event.getEndDateTime().toString());
    assertEquals("test", event.getDescription());
    assertEquals(null, event.getLocation());
    assertEquals(false, event.getIsPrivate());
  }

  @Test
  public void eventCreationWithPrivacy() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T10:00 ");
    EventModel event = eventController.getEventCollection().getEventList().get(0);

    assertEquals("hello world", event.getEventName());
    assertEquals("2025-10-10T10:00", event.getStartDateTime().toString());
    assertEquals("2025-10-10T10:00", event.getEndDateTime().toString());
    assertEquals(null, event.getDescription());
    assertEquals(null, event.getLocation());
    assertEquals(false, event.getIsPrivate());
  }

  @Test
  public void eventCreationNoOptionalParams() {
    EventController eventController = new EventController();
    eventController.filterCommand("CREATE event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T10:00");
    assertEquals(1, eventController.getEventCollection().getEventList().size());
    EventModel event = eventController.getEventCollection().getEventList().get(0);

    assertEquals("hello world", event.getEventName());
    assertEquals("2025-10-10T10:00", event.getStartDateTime().toString());
    assertEquals("2025-10-10T10:00", event.getEndDateTime().toString());
    assertEquals(null, event.getDescription());
    assertEquals(null, event.getLocation());
    assertEquals(false, event.getIsPrivate());
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T10:00 " +
            "privacy:\"true\" description:\"test\" location:\"MA\" ");
    eventController.filterCommand("print events on 2025-10-10");
  }


  @Test
  public void invalidSpellingErrorCommand() {

    EventController eventController = new EventController();
    eventController.filterCommand("createe event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T10:00");
    assertEquals(0, eventController.getEventCollection().getEventList().size());
  }

  @Test
  public void invalidNoCommand() {

    EventController eventController = new EventController();
    eventController.filterCommand(" ");
    assertEquals(0, eventController.getEventCollection().getEventList().size());

  }

  @Test
  public void ValidCaseMixCommand() {

    EventController eventController = new EventController();
    eventController.filterCommand(" CREATE event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T10:00");
    assertEquals(1, eventController.getEventCollection().getEventList().size());
    EventModel event = eventController.getEventCollection().getEventList().get(0);

    assertEquals("hello world", event.getEventName());
    assertEquals("2025-10-10T10:00", event.getStartDateTime().toString());
    assertEquals("2025-10-10T10:00", event.getEndDateTime().toString());
    assertEquals(null, event.getDescription());
    assertEquals(null, event.getLocation());
    assertEquals(false, event.getIsPrivate());
  }

  @Test
  public void recurringNCreation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW for 6 times  " +
            "description:\"test\" location:\"MA\" privacy:\"true\"");
    assertEquals(6, eventController.getEventCollection().getEventList().size());
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T10:00", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());

  }

  @Test
  public void processRecurringEnddateCreation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW " +
            "until 2025-12-10T10:00 description:\"test\" location:\"MA\" privacy:\"true\" ");
    assertEquals(27, eventController.getEventCollection().getEventList().size());
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T11:00", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
  }

  @Test
  public void RecurringEnddateCreationInclusiveCheck() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW " +
            "until 2025-12-10T10:00 description:\"test\" location:\"MA\" privacy:\"true\" ");
    assertEquals(27, eventController.getEventCollection().getEventList().size());
    EventModel eventOne = eventController.getEventCollection().getEventList().get(26);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-12-10T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-12-10T11:00", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
    //assertEquals(0, 1);
  }

  @Test
  public void RecurringEnddateCreationNoName() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"\" " +
            "from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW " +
            "until 2025-12-10T10:00 description:\"test\" location:\"MA\" privacy:\"true\" ");
    assertEquals(27, eventController.getEventCollection().getEventList().size());
    EventModel eventOne = eventController.getEventCollection().getEventList().get(26);

    assertEquals("", eventOne.getEventName());
    assertEquals("2025-12-10T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-12-10T11:00", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-12-10T10:00");


  }

  //create event --autoDecline <eventName> on <dateStringTtimeString>
  @Test
  public void processCreateAllDayEvent() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW until " +
            "2025-11-10T10:00  description:\"test\" location:\"MA\" privacy:\"true\" ");
    assertEquals(13, eventController.getEventCollection().getEventList().size());
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T11:00", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
  }

  @Test
  public void processCreateRecurringAllDayEventN() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" " +
            "on 2025-10-10 repeats MTW for 6 times description:\"test\" " +
            "location:\"MA\" privacy:\"true\"");
    assertEquals(6, eventController.getEventCollection().getEventList().size());
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T00:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T23:59", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());

  }

  @Test
  public void processCreateRecurringAllDayEventEnd() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" on 2025-10-10 " +
            "repeats MTW until 2025-12-10 description:\"test\" location:\"MA\" privacy:\"true\"");
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);
    assertEquals(27, eventController.getEventCollection().getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T00:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T23:59", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
  }

  @Test
  public void recurringEventLoopAround() {
    EventController eventController = new EventController();

    eventController.filterCommand("create event --autoDecline \"hello world\" " +
            "on 2025-10-10 repeats MTW until 2026-01-10 " +
            "description:\"test\" location:\"MA\" privacy:\"true\"");
    assertEquals(39, eventController.getEventCollection().getEventList().size());
    EventModel eventOne = eventController.getEventCollection().getEventList().get(38);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2026-01-07T00:00", eventOne.getStartDateTime().toString());
    assertEquals("2026-01-07T23:59", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
  }

  @Test
  public void editEventNameBeforeTime() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-12T10:00 to 2025-10-12T11:00");

    eventController.filterCommand("edit event eventName \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T14:00 with \"hazarrrrr\"");
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);
    assertEquals(3, eventController.getEventCollection().getEventList().size());

    assertEquals("hazarrrrr", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());
  }


  @Test
  public void editEventNameinTime() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-12T10:00 to 2025-10-12T11:00");

    eventController.filterCommand("edit event eventName \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-11T12:00 with \"hazarrrrr\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);
    EventModel eventTwo = eventController.getEventCollection().getEventList().get(1);
    assertEquals(3, eventController.getEventCollection().getEventList().size());

    assertEquals("hazarrrrr", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

    assertEquals("hazarrrrr", eventTwo.getEventName());
    assertEquals("2025-10-11T10:00", eventTwo.getStartDateTime().toString());
    assertEquals("2025-10-11T11:00", eventTwo.getEndDateTime().toString());
    assertEquals(null, eventTwo.getDescription());
    assertEquals(null, eventTwo.getLocation());
    assertEquals(false, eventTwo.getIsPrivate());

  }

  @Test
  public void editEventDateTime() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-12T10:00 to 2025-10-12T11:00");

    eventController.filterCommand("edit event startDateTime \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"2025-10-19T10:00\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    assertEquals(3, eventController.getEventCollection().getEventList().size());
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);
    EventModel eventTwo = eventController.getEventCollection().getEventList().get(1);
    EventModel eventThree = eventController.getEventCollection().getEventList().get(2);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-19T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());


    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-19T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-19T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());


  }

  @Test
  public void editEndDateTime() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-12T10:00 to 2025-10-12T11:00");

    eventController.filterCommand("edit event endDateTime \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"2025-10-19T10:00\"");
    assertEquals(3, eventController.getEventCollection().getEventList().size());
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);
    EventModel eventTwo = eventController.getEventCollection().getEventList().get(1);
    EventModel eventThree = eventController.getEventCollection().getEventList().get(2);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-19T10:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());


    assertEquals("hello world", eventTwo.getEventName());
    assertEquals("2025-10-11T10:00", eventTwo.getStartDateTime().toString());
    assertEquals("2025-10-19T10:00", eventTwo.getEndDateTime().toString());
    assertEquals(null, eventTwo.getDescription());
    assertEquals(null, eventTwo.getLocation());
    assertEquals(false, eventTwo.getIsPrivate());

    assertEquals("hello world", eventThree.getEventName());
    assertEquals("2025-10-12T10:00", eventThree.getStartDateTime().toString());
    assertEquals("2025-10-19T10:00", eventThree.getEndDateTime().toString());
    assertEquals(null, eventThree.getDescription());
    assertEquals(null, eventThree.getLocation());
    assertEquals(false, eventThree.getIsPrivate());

  }

  @Test
  public void editlocation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-12T10:00 to 2025-10-12T11:00");

    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);
    assertEquals(3, eventController.getEventCollection().getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

    eventController.filterCommand("edit event location \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"USAAAAAAAA\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");


    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals("USAAAAAAAA", eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

  }

  @Test
  public void editPrivacy() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-12T10:00 to 2025-10-12T11:00");

    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);
    assertEquals(3, eventController.getEventCollection().getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

    eventController.filterCommand("edit event privacyType \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"private\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());

  }

  @Test
  public void editDescription() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-12T10:00 to 2025-10-12T11:00");

    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);
    assertEquals(3, eventController.getEventCollection().getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());


    eventController.filterCommand("edit event description \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"hello friends\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals("hello friends", eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

  }

  @Test
  public void processEditEventsProperty() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-11T12:00 to 2025-10-11T13:00");
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);
    EventModel eventTwo = eventController.getEventCollection().getEventList().get(1);
    assertEquals(2, eventController.getEventCollection().getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

    assertEquals("hello world", eventTwo.getEventName());
    assertEquals("2025-10-11T12:00", eventTwo.getStartDateTime().toString());
    assertEquals("2025-10-11T13:00", eventTwo.getEndDateTime().toString());
    assertEquals(null, eventTwo.getDescription());
    assertEquals(null, eventTwo.getLocation());
    assertEquals(false, eventTwo.getIsPrivate());

    eventController.filterCommand("edit events eventName \"hello world\" " +
            "from 2025-10-10T10:00 with \"money printer goes BRRRRRRRRRRRRRRRRR\"");
    eventController.filterCommand("print events on 2025-10-10");

    assertEquals("money printer goes BRRRRRRRRRRRRRRRRR", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

    assertEquals("money printer goes BRRRRRRRRRRRRRRRRR", eventTwo.getEventName());
    assertEquals("2025-10-11T12:00", eventTwo.getStartDateTime().toString());
    assertEquals("2025-10-11T13:00", eventTwo.getEndDateTime().toString());
    assertEquals(null, eventTwo.getDescription());
    assertEquals(null, eventTwo.getLocation());
    assertEquals(false, eventTwo.getIsPrivate());

  }

  // edit events <property> <eventName> <NewPropertyValue>
  //Pattern editEventsByName = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\s(.*?)$");
  @Test
  public void processEditEventsByName() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T12:00 to 2025-10-10T13:00");
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);
    assertEquals(1, eventController.getEventCollection().getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());
    eventController.filterCommand("edit events location \"hello world\" \"MA Boston\"");
    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals("MA Boston", eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());
    eventController.filterCommand("print events on 2025-10-10");
  }


  // print events on <dateString>
  //Pattern printEventsOn = Pattern.compile("print\\sevents\\son\\s" + dateString + "$");
  @Test
  public void processPrintEventsOn() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-11T10:00 to 2025-11-10T11:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-12T10:00 to 2025-12-10T11:00");
    eventController.filterCommand("print events on 2025-10-10");
    EventModel eventOne = eventController.getEventCollection().getEventList().get(0);

    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 10, 10, 12, 00);
    LocalDateTime endDateTime = LocalDateTime
            .of(2025, 10, 10, 13, 00);

    List<EventModel> printList = new ArrayList<>();
    printList = eventController
            .getEventCollection()
            .searchEventByTimeFrame(startDateTime, endDateTime);

    assertEquals("\n*  eventName=hello world,  Start=2025-10-10T12:00, " +
                    " End=2025-10-10T13:00,  description=null,  " +
                    "isPrivate=false,  location=null,  autoDecline=false\n"
            , eventController.getEventCollection().printAssortments(printList));

    eventController.filterCommand("print events on 2025-10-10");
  }

  @Test
  public void processprintEventsBetween() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T11:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T11:00 to 2025-10-10T12:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-12T10:00 to 2025-10-12T11:00");
    eventController.filterCommand("print events from 2025-10-11T10:00 to 2025-11-10T10:00");
    EventModel eventOne = eventController.getEventCollection().getEventList().get(2);
    EventModel eventTwo = eventController.getEventCollection().getEventList().get(3);

    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 10, 11, 10, 00);
    LocalDateTime endDateTime = LocalDateTime
            .of(2025, 11, 10, 10, 00);

    List<EventModel> printList = new ArrayList<>();
    printList = eventController.getEventCollection().searchEventByTimeFrame(
        startDateTime, endDateTime);

    assertEquals(
            "\n" +
                    "*  eventName=hello world,  " +
                    "Start=2025-10-11T10:00,  End=2025-10-11T11:00,  " +
                    "description=null,  isPrivate=false,  location=null,  autoDecline=false\n" +
                    "*  eventName=hello world,  " +
                    "Start=2025-10-12T10:00,  End=2025-10-12T11:00,  " +
                    "description=null,  isPrivate=false,  location=null,  autoDecline=false\n"
            , eventController.getEventCollection().printAssortments(printList));
  }

  @Test
  public void ShowStatusAvailable() {
    EventController eventController = new EventController();
    eventController.filterCommand("show status on 2025-01-10T10:00");
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 01, 10, 10, 00);
    assertEquals("Available", eventController
            .getEventCollection().checkStatus(startDateTime, startDateTime));
  }

  @Test
  public void ShowStatusBusy() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T11:00");
    eventController.filterCommand("show status on 2025-10-10T10:30");
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 10, 10, 10, 00);
    assertEquals("Busy", eventController.getEventCollection()
            .checkStatus(startDateTime, startDateTime));
  }
}