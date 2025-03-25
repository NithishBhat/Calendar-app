package controller;

import model.EventModel;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


/**
 * Testing class of event handler and Event controller and the command parser.
 * Test all possible commands and handles all event types and edge cases.
 */

public class EventHandlerTest {

  @Test
  public void normalEventCreation() {

    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T10:00 "
            + "description:\"test HELLO\" location:\"MA\" privacy:\"true\"");


    assertEquals(1, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel event = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

    assertEquals("hello world", event.getEventName());
    assertEquals("2025-10-10T10:00", event.getStartDateTime().toString());
    assertEquals("2025-10-10T10:00", event.getEndDateTime().toString());
    assertEquals("test HELLO", event.getDescription());
    assertEquals("MA", event.getLocation());
    assertEquals(true, event.getIsPrivate());
  }

  @Test
  public void copyCalEventCreation() {

    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T10:00 "
            + "description:\"test HELLO\" location:\"MA\" privacy:\"true\"");

    eventController.getEventHandler().createCalender("Hello", ZoneId.of("America/New_York"));
    eventController.filterCommand("copy event \"hello world\" on 2025-10-10T10:00 --target \"Hello\" to 2025-12-19T10:00");

    assertEquals(1, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    assertEquals(1, eventController.getEventHandler().getEventCollection("Hello").getEventList().size());

    EventModel event1 = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    EventModel event2 = eventController.getEventHandler().getEventCollection("Hello").getEventList().get(0);

    assertEquals("hello world", event1.getEventName());
    assertEquals("2025-10-10T10:00", event1.getStartDateTime().toString());
    assertEquals("2025-10-10T10:00", event1.getEndDateTime().toString());
    assertEquals("test HELLO", event1.getDescription());
    assertEquals("MA", event1.getLocation());
    assertEquals(true, event1.getIsPrivate());


    assertEquals("hello world", event2.getEventName());
    assertEquals("2025-12-19T10:00", event2.getStartDateTime().toString());
    assertEquals("2025-12-19T10:00", event2.getEndDateTime().toString());
    assertEquals("test HELLO", event2.getDescription());
    assertEquals("MA", event2.getLocation());
    assertEquals(true, event2.getIsPrivate());
  }

  @Test
  public void copyCalEventCreationChangeTimeZone() {

    EventController eventController = new EventController();
    eventController.filterCommand("create calendar --name \"test\" --timezone \"America/New_York\"");
    eventController.getEventHandler().processPrintCalendars();
    eventController.filterCommand("use calendar --name \"test\" ");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T10:00 "
            + "description:\"test HELLO\" location:\"MA\" privacy:\"true\"");

    eventController.filterCommand("create calendar --name \"test1\" --timezone \"America/Chicago\"");
    eventController.filterCommand("copy event \"hello world\" on 2025-10-10T10:00 --target \"test1\" to 2025-12-19T12:00");

    assertEquals(1, eventController.getEventHandler().getEventCollection("test").getEventList().size());
    assertEquals(1, eventController.getEventHandler().getEventCollection("test1").getEventList().size());

    EventModel event1 = eventController.getEventHandler().getEventCollection("test").getEventList().get(0);
    EventModel event2 = eventController.getEventHandler().getEventCollection("test1").getEventList().get(0);

    assertEquals("hello world", event1.getEventName());
    assertEquals("2025-10-10T10:00", event1.getStartDateTime().toString());
    assertEquals("2025-10-10T10:00", event1.getEndDateTime().toString());
    assertEquals("test HELLO", event1.getDescription());
    assertEquals("MA", event1.getLocation());
    assertEquals(true, event1.getIsPrivate());

    assertEquals("hello world", event2.getEventName());
    assertEquals("2025-12-19T12:00", event2.getStartDateTime().toString());
    assertEquals("2025-12-19T12:00", event2.getEndDateTime().toString());
    assertEquals("test HELLO", event2.getDescription());
    assertEquals("MA", event2.getLocation());
    assertEquals(true, event2.getIsPrivate());
  }

  @Test
  public void copyCalEventsOnSameDay() {
    EventController eventController = new EventController();

    eventController.filterCommand("create calendar --name \"test\" --timezone \"America/New_York\"");
    eventController.filterCommand("use calendar --name \"test\" ");

    eventController.filterCommand("create event \"test\" "
            + "from 2025-09-05T09:00 to 2025-09-05T10:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event \"test2\" "
            + "from 2025-09-05T11:00 to 2025-09-05T12:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event \"test3\" "
            + "from 2025-09-05T14:00 to 2025-09-05T15:30 "
            + "description:\"test\" location:\"test\" privacy:\"true\"");

    eventController.filterCommand("create calendar --name \"testCal\" --timezone \"America/Chicago\"");

    eventController.filterCommand("copy events on 2025-09-05 --target \"testCal\" to 2026-01-08");

    //check number of events in both calendars
    assertEquals(3, eventController.getEventHandler().getEventCollection("test").getEventList().size());
    assertEquals(3, eventController.getEventHandler().getEventCollection("testCal").getEventList().size());

    //get copied events
    EventModel originalEvent1 = eventController.getEventHandler().getEventCollection("test").getEventList().get(0);
    EventModel copiedEvent1 = eventController.getEventHandler().getEventCollection("testCal").getEventList().get(0);

    EventModel originalEvent2 = eventController.getEventHandler().getEventCollection("test").getEventList().get(1);
    EventModel copiedEvent2 = eventController.getEventHandler().getEventCollection("testCal").getEventList().get(1);

    EventModel originalEvent3 = eventController.getEventHandler().getEventCollection("test").getEventList().get(2);
    EventModel copiedEvent3 = eventController.getEventHandler().getEventCollection("testCal").getEventList().get(2);

    //check props
    assertEquals(originalEvent1.getEventName(), copiedEvent1.getEventName());
    assertEquals(originalEvent1.getDescription(), copiedEvent1.getDescription());
    assertEquals(originalEvent1.getLocation(), copiedEvent1.getLocation());
    assertEquals(originalEvent1.getIsPrivate(), copiedEvent1.getIsPrivate());

    // Ensure date offsets are correctly applied
    assertEquals("2026-01-08T08:00", copiedEvent1.getStartDateTime().toString());
    assertEquals("2026-01-08T09:30", copiedEvent1.getEndDateTime().toString());

    assertEquals("2026-01-08T10:00", copiedEvent2.getStartDateTime().toString());
    assertEquals("2026-01-08T11:30", copiedEvent2.getEndDateTime().toString());

    assertEquals("2026-01-08T13:00", copiedEvent3.getStartDateTime().toString());
    assertEquals("2026-01-08T14:30", copiedEvent3.getEndDateTime().toString());
  }

  @Test
  public void copycalEntireDay() {



    EventController eventController = new EventController();  eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T08:00 to 2025-10-10T09:00 "
            + "description:\"test HELLO\" location:\"MA\" privacy:\"true\"");

    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00 "
            + "description:\"test HELLO\" location:\"MA\" privacy:\"true\"");

    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T11:00 to 2025-10-10T12:00 "
            + "description:\"test HELLO\" location:\"MA\" privacy:\"true\"");

    eventController.getEventHandler().createCalender("Hello", ZoneId.of("America/New_York"));

    eventController.filterCommand("copy events on 2025-10-10 --target \"Hello\" to 2025-12-19");
    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    assertEquals(2, eventController.getEventHandler().getEventCollection("Hello").getEventList().size());
    EventModel event1 = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    EventModel event2 = eventController.getEventHandler().getEventCollection("Hello").getEventList().get(0);
    assertEquals("hello world", event1.getEventName());
    assertEquals("2025-10-10T08:00", event1.getStartDateTime().toString());
    assertEquals("2025-10-10T09:00", event1.getEndDateTime().toString());

    assertEquals("test HELLO", event1.getDescription());
    assertEquals("MA", event1.getLocation());
    assertEquals(true, event1.getIsPrivate());
    assertEquals("hello world", event2.getEventName());
    assertEquals("2025-12-19T03:00",
            event2.getStartDateTime().toString());
    assertEquals("2025-12-19T04:00",
            event2.getEndDateTime().toString());
    assertEquals("test HELLO",
            event2.getDescription());
    assertEquals("MA", event2.getLocation());
    assertEquals(true, event2.getIsPrivate());


    System.out.println(eventController.getEventHandler().getEventCollection("default").printAssortments(
            eventController.getEventHandler().getEventCollection("default").getEventList()));

    System.out.println(eventController.getEventHandler().getEventCollection("Hello").printAssortments(
            eventController.getEventHandler().getEventCollection("Hello").getEventList()));}

  @Test
  public void copyCalEventBetweenDates() {
    EventController eventController = new EventController();

    eventController.filterCommand("create calendar --name \"test\" --timezone \"America/New_York\"");


    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    try {
      eventController.filterCommand("use calendar --name \"test\" ");
      String output = outputStream.toString().trim(); // Capture output

      // Assert output message
      assertEquals("Changed to calendar: test", output);
    } finally {
      // Restore System.out
      System.setOut(originalOut);
    }

    eventController.filterCommand("create event \"test\" "
            + "from 2025-09-05T09:00 to 2025-09-05T10:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event \"test2\" "
            + "from 2025-09-07T11:00 to 2025-09-07T12:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event \"test3\" "
            + "from 2025-09-12T14:00 to 2025-09-12T15:30 "
            + "description:\"test\" location:\"test\" privacy:\"true\"");

    eventController.getEventHandler().createCalender("testCal", ZoneId.of("America/Los_Angeles"));

    eventController.filterCommand("copy events between 2025-09-05 and 2025-09-12 --target \"testCal\" to 2026-01-08");

    //check number of events in both calendars
    assertEquals(3, eventController.getEventHandler().getEventCollection("test").getEventList().size());
    assertEquals(3, eventController.getEventHandler().getEventCollection("testCal").getEventList().size());

    //get copied events
    EventModel originalEvent1 = eventController.getEventHandler().getEventCollection("test").getEventList().get(0);
    EventModel copiedEvent1 = eventController.getEventHandler().getEventCollection("testCal").getEventList().get(0);

    EventModel originalEvent2 = eventController.getEventHandler().getEventCollection("test").getEventList().get(1);
    EventModel copiedEvent2 = eventController.getEventHandler().getEventCollection("testCal").getEventList().get(1);

    EventModel originalEvent3 = eventController.getEventHandler().getEventCollection("test").getEventList().get(2);
    EventModel copiedEvent3 = eventController.getEventHandler().getEventCollection("testCal").getEventList().get(2);

    //check props
    assertEquals(originalEvent1.getEventName(), copiedEvent1.getEventName());
    assertEquals(originalEvent1.getDescription(), copiedEvent1.getDescription());
    assertEquals(originalEvent1.getLocation(), copiedEvent1.getLocation());
    assertEquals(originalEvent1.getIsPrivate(), copiedEvent1.getIsPrivate());

    // Ensure date offsets are correctly applied
    assertEquals("2026-01-08T06:00", copiedEvent1.getStartDateTime().toString());
    assertEquals("2026-01-08T07:30", copiedEvent1.getEndDateTime().toString());

    assertEquals("2026-01-10T08:00", copiedEvent2.getStartDateTime().toString());
    assertEquals("2026-01-10T09:30", copiedEvent2.getEndDateTime().toString());

    assertEquals("2026-01-15T11:00", copiedEvent3.getStartDateTime().toString());
    assertEquals("2026-01-15T12:30", copiedEvent3.getEndDateTime().toString());
  }

  @Test
  public void copyCalEventBetweenDatesWithSameName() {
    EventController eventController = new EventController();

    eventController.filterCommand("create event \"test\" "
            + "from 2025-09-05T09:00 to 2025-09-05T10:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event \"test\" "
            + "from 2025-09-07T11:00 to 2025-09-07T12:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event \"test2\" "
            + "from 2025-09-10T11:00 to 2025-09-10T12:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event \"test\" "
            + "from 2025-09-12T14:00 to 2025-09-12T15:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.getEventHandler().createCalender("testCal", ZoneId.of("America/New_York"));

    //Copy all events from September 5, 2025 - September 12, 2025, to start at January 8, 2026
    eventController.getEventHandler().processCopyCalendarBetweenDatesWithName(
            "test",
            LocalDateTime.parse("2025-09-05T00:00"),
            LocalDateTime.parse("2025-09-12T23:59"),
            "default",
            LocalDateTime.parse("2026-01-08T00:00"),
            "testCal"
    );

    //check number of events in both calendars
    assertEquals(4, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    assertEquals(3, eventController.getEventHandler().getEventCollection("testCal").getEventList().size());

    //get copied events
    EventModel originalEvent1 = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    EventModel copiedEvent1 = eventController.getEventHandler().getEventCollection("testCal").getEventList().get(0);

    EventModel originalEvent2 = eventController.getEventHandler().getEventCollection("default").getEventList().get(1);
    EventModel copiedEvent2 = eventController.getEventHandler().getEventCollection("testCal").getEventList().get(1);

    EventModel originalEvent3 = eventController.getEventHandler().getEventCollection("default").getEventList().get(2);
    EventModel copiedEvent3 = eventController.getEventHandler().getEventCollection("testCal").getEventList().get(2);

    //check props
    assertEquals(originalEvent1.getEventName(), copiedEvent1.getEventName());
    assertEquals(originalEvent1.getDescription(), copiedEvent1.getDescription());
    assertEquals(originalEvent1.getLocation(), copiedEvent1.getLocation());
    assertEquals(originalEvent1.getIsPrivate(), copiedEvent1.getIsPrivate());

    //date offsets are correctly applied
    assertEquals("2026-01-08T09:00", copiedEvent1.getStartDateTime().toString());
    assertEquals("2026-01-08T10:30", copiedEvent1.getEndDateTime().toString());

    assertEquals("2026-01-10T11:00", copiedEvent2.getStartDateTime().toString());
    assertEquals("2026-01-10T12:30", copiedEvent2.getEndDateTime().toString());

    assertEquals("2026-01-15T14:00", copiedEvent3.getStartDateTime().toString());
    assertEquals("2026-01-15T15:30", copiedEvent3.getEndDateTime().toString());
  }

  @Test
  public void testCreateCalender() {
    EventController eventController = new EventController();
    eventController.filterCommand("create calendar --name \"test\" " +
            "--timezone \"America/New_York\"");

    eventController.filterCommand("create calendar --name \"test2\" " +
            "--timezone \"America/Los_Angeles\"");

    eventController.filterCommand("create calendar --name \"test3\" " +
            "--timezone \"Europe/Paris\"");

    eventController.getEventHandler().processPrintCalendars();

    assertEquals(4, eventController.getEventHandler().getCalenderCollectionSize());
  }

  @Test
  public void testUseCalendar() {
    EventController eventController = new EventController();
    eventController.getEventHandler().createCalender("testCal1", ZoneId.of("America/New_York"));
    //eventController.filterCommand("create calendar --name \"testCal1\" " +
            //"--timezone \"America/New_York\"");
    //eventController.getEventHandler().useCalender("testCal1");
    eventController.filterCommand("use calendar --name \"testCal1\" ");
    eventController.filterCommand("create event \"test\" "
            + "from 2025-09-05T09:00 to 2025-09-05T10:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event --autoDecline \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW for 6 times  "
            + "description:\"test\" location:\"MA\" privacy:\"true\"");

    eventController.getEventHandler().createCalender("testCal2", ZoneId.of("America/New_York"));

    eventController.filterCommand("use calendar --name \"testCal2\" ");

    eventController.filterCommand("create event \"test\" "
            + "from 2025-09-05T09:00 to 2025-09-05T10:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event \"test\" "
            + "from 2025-09-07T11:00 to 2025-09-07T12:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event \"test2\" "
            + "from 2025-09-10T11:00 to 2025-09-10T12:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("create event \"test\" "
            + "from 2025-09-12T14:00 to 2025-09-12T15:30 "
            + "description:\"test\" location:\"test\" privacy:\"false\"");

    eventController.filterCommand("print events on 2025-09-05");

    assertEquals(7, eventController.getEventHandler().getEventCollection("testCal1").getEventList().size());
    assertEquals(4, eventController.getEventHandler().getEventCollection("testCal2").getEventList().size());

  }


  //edit calendar --name <calName> --property <property-name> <property-value>
  @Test
  public void testEditCalender() {
    EventController eventController = new EventController();
    eventController.filterCommand("create calendar --name \"test\" " +
            "--timezone \"America/New_York\"");

    eventController.filterCommand("create calendar --name \"test2\" " +
            "--timezone \"America/Los_Angeles\"");

    eventController.filterCommand("edit calendar --name \"test\" --property \"name\" \"CS5010\"");
    eventController.filterCommand("edit calendar --name \"test2\" --property \"time-zone\" \"America/New_York\" ");
    eventController.getEventHandler().processPrintCalendars();
  }

  @Test
  public void testEditCalenderInvalid() {
    EventController eventController = new EventController();
    eventController.filterCommand("create calendar --name \"test\" " +
            "--timezone \"America/New_York\"");

    eventController.filterCommand("create calendar --name \"test2\" " +
            "--timezone \"America/Los_Angeles\"");


      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        eventController.filterCommand("edit calendar --name \"test\" --property \"nasdasdadame\" \"CS5010\"");
      });
  }


  @Test
  public void eventCreationWithLocation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T10:00 location:\"MA\"");
    EventModel event = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

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
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T10:00 description:\"test\"");
    EventModel event = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

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
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T10:00 ");
    EventModel event = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

    assertEquals("hello world", event.getEventName());
    assertEquals("2025-10-10T10:00", event.getStartDateTime().toString());
    assertEquals("2025-10-10T10:00", event.getEndDateTime().toString());
    assertEquals(null, event.getDescription());
    assertEquals(null, event.getLocation());
    assertEquals(false, event.getIsPrivate());
  }

//  @Test
//  public void eventCreationNoOptionalParams() {
//    EventController eventController = new EventController();
//    eventController.filterCommand("CREATE event \"hello world\" "
//            + "from 2025-10-10T10:00 to 2025-10-10T10:00");
//    assertEquals(1, eventController.getEventHandler().getEventCollection("default").getEventList().size());
//    EventModel event = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
//
//    assertEquals("hello world", event.getEventName());
//    assertEquals("2025-10-10T10:00", event.getStartDateTime().toString());
//    assertEquals("2025-10-10T10:00", event.getEndDateTime().toString());
//    assertEquals(null, event.getDescription());
//    assertEquals(null, event.getLocation());
//    assertEquals(false, event.getIsPrivate());
//    eventController.filterCommand("create event \"hello world\" "
//            + "from 2025-10-10T10:00 to 2025-10-10T10:00 "
//            + "privacy:\"true\" description:\"test\" location:\"MA\" ");
//    eventController.filterCommand("print events on 2025-10-10");
//  }
//
//
//  @Test
//  public void invalidSpellingErrorCommand() {
//
//    EventController eventController = new EventController();
//    eventController.filterCommand("createe event \"hello world\" "
//            + "from 2025-10-10T10:00 to 2025-10-10T10:00");
//    assertEquals(0, eventController.getEventHandler().getEventCollection("default").getEventList().size());
//  }
//
//  @Test
//  public void invalidNoCommand() {
//
//    EventController eventController = new EventController();
//    eventController.filterCommand(" ");
//    assertEquals(0, eventController.getEventHandler().getEventCollection("default").getEventList().size());
//
//  }

  @Test
  public void ValidCaseMixCommand() {

    EventController eventController = new EventController();
    eventController.filterCommand(" CREATE event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T10:00");
    assertEquals(1, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel event = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

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
    eventController.filterCommand("create event --autoDecline \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW for 6 times  "
            + "description:\"test\" location:\"MA\" privacy:\"true\"");
    assertEquals(6, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T10:00", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
    assertEquals(true,eventOne.getAutoDecline());

  }

  @Test
  public void processRecurringEnddateCreation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW "
            + "until 2025-12-10T10:00 description:\"test\" location:\"MA\" privacy:\"true\" ");
    assertEquals(27, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T11:00", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
    assertEquals(true,eventOne.getAutoDecline());

  }

  @Test
  public void RecurringEnddateCreationInclusiveCheck() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW "
            + "until 2025-12-10T10:00 description:\"test\" location:\"MA\" privacy:\"true\" ");
    assertEquals(27, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(26);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-12-10T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-12-10T11:00", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
    assertEquals(true,eventOne.getAutoDecline());
    //assertEquals(0, 1);
  }

  @Test
  public void RecurringEnddateCreationNoName() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW "
            + "until 2025-12-10T10:00 description:\"test\" location:\"MA\" privacy:\"true\" ");
    assertEquals(27, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(26);

    assertEquals("", eventOne.getEventName());
    assertEquals("2025-12-10T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-12-10T11:00", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
    assertEquals(true,eventOne.getAutoDecline());
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-12-10T10:00");


  }
@Test
  public void processCreateAllDayEventRecurring() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW until "
            + "2025-11-10T10:00  description:\"test\" location:\"MA\" privacy:\"true\" ");
    assertEquals(13, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T10:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T11:00", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());

    assertEquals(true, eventOne.getIsPrivate());
  }
@Test
  public void processCreateAllDayEventRecurringInvalid() {
    EventController eventController = new EventController();

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      eventController.filterCommand("create event --autoDecline \"hello world\" "
              + "from 2025-10-10T10:00 to 2025-09-10T11:00 repeats MTW until "
              + "2025-11-10T10:00  description:\"test\" location:\"MA\" privacy:\"true\" ");
    });

    // Optionally, check the exception message
    assertEquals("End time cannot be before start time, Event was not created", exception.getMessage());

  }

  @Test
  public void RecurringEnddateCreationNoNameInvalid() {
    EventController eventController = new EventController();


    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      eventController.filterCommand("create event --autoDecline \"\" "
              + "from 2025-10-10T10:00 to 2025-10-09T11:00 repeats MTW "
              + "until 2025-12-10T10:00 description:\"test\" location:\"MA\" privacy:\"true\" ");
    });

    // Optionally, check the exception message
    assertEquals("End time cannot be before start time, Event was not created", exception.getMessage());

  }

  //create event --autoDecline <eventName> on <dateStringTtimeString>
  @Test
  public void processCreateAllDayEventSingle() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" "
            + " on 2025-10-10T11:00  description:\"test\" location:\"MA\" privacy:\"true\" ");
    assertEquals(1, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T11:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T23:59", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true,eventOne.getAutoDecline());
    assertEquals(true, eventOne.getIsPrivate());

  }

  @Test
  public void processCreateAllDayEventNoAutoDecline() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event  \"hello world\" "
            + " on 2025-10-10T11:00  description:\"test\" location:\"MA\" privacy:\"true\" ");
    assertEquals(1, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T11:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T23:59", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(false,eventOne.getAutoDecline());
    assertEquals(true, eventOne.getIsPrivate());

  }

  @Test
  public void processCreateRecurringAllDayEventN() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" "
            + "on 2025-10-10 repeats MTW for 6 times description:\"test\" "
            + "location:\"MA\" privacy:\"true\"");
    assertEquals(6, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T00:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T23:59", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
    assertEquals(true,eventOne.getAutoDecline());

  }

  @Test
  public void processCreateRecurringAllDayEventEnd() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline \"hello world\" on 2025-10-10 "
            + "repeats MTW until 2025-12-10 description:\"test\" location:\"MA\" privacy:\"true\"");
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    assertEquals(27, eventController.getEventHandler().getEventCollection("default").getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T00:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T23:59", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
    assertEquals(true,eventOne.getAutoDecline());
  }

  @Test
  public void processCreateRecurringAllDayEventEndNoAutoDecline() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" on 2025-10-10 "
            + "repeats MTW until 2025-12-10 description:\"test\" location:\"MA\" privacy:\"true\"");
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    assertEquals(27, eventController.getEventHandler().getEventCollection("default").getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-13T00:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-13T23:59", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
    assertEquals(false,eventOne.getAutoDecline());
  }

  @Test
  public void recurringEventLoopAround() {
    EventController eventController = new EventController();

    eventController.filterCommand("create event --autoDecline \"hello world\" "
            + "on 2025-10-10 repeats MTW until 2026-01-10 "
            + "description:\"test\" location:\"MA\" privacy:\"true\"");
    assertEquals(39, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(38);

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2026-01-07T00:00", eventOne.getStartDateTime().toString());
    assertEquals("2026-01-07T23:59", eventOne.getEndDateTime().toString());
    assertEquals("test", eventOne.getDescription());
    assertEquals("MA", eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());
    assertEquals(true,eventOne.getAutoDecline());

  }

  @Test
  public void editEventNameBeforeTime() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");

    eventController.filterCommand("edit event eventName \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T14:00 with \"hazarrrrr\"");
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());

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
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");

    eventController.filterCommand("edit event eventName \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-11T12:00 with \"hazarrrrr\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    EventModel eventTwo = eventController.getEventHandler().getEventCollection("default").getEventList().get(1);
    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());

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
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");

    eventController.filterCommand("edit event startDateTime \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"2025-10-19T10:00\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    EventModel eventTwo = eventController.getEventHandler().getEventCollection("default").getEventList().get(1);
    EventModel eventThree = eventController.getEventHandler().getEventCollection("default").getEventList().get(2);

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
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");

    eventController.filterCommand("edit event endDateTime \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"2025-10-19T10:00\"");
    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    EventModel eventTwo = eventController.getEventHandler().getEventCollection("default").getEventList().get(1);
    EventModel eventThree = eventController.getEventHandler().getEventCollection("default").getEventList().get(2);

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
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");

    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

    eventController.filterCommand("edit event location \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"USAAAAAAAA\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");


    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals("USAAAAAAAA", eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

  }


  @Test
  public void editlocationInvalidDate() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");

    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());




    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      eventController.filterCommand("edit event location \"hello world\" "
              + "from 2025-10-10T10:00 to 2025-10-09T10:00 with \"USAAAAAAAA\"");
    });
    assertEquals("End time cannot be before start time, Event was not created", exception.getMessage());

  }

  @Test
  public void editPrivacy() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");

    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

    eventController.filterCommand("edit event privacyType \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"private\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());

  }

  @Test
  public void editPrivacyPublic() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");

    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

    eventController.filterCommand("edit event privacyType \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"public\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

  }

  @Test
  public void editPrivacyPublicChange() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00 privacy:\"true\"");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00 privacy:\"true\"" );
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00 privacy:\"true\"");

    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(true, eventOne.getIsPrivate());

    eventController.filterCommand("edit event privacyType \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"public\"");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());

  }

  @Test
  public void editDescription() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");

    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    assertEquals(3, eventController.getEventHandler().getEventCollection("default").getEventList().size());

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("2025-10-10T12:00", eventOne.getStartDateTime().toString());
    assertEquals("2025-10-10T13:00", eventOne.getEndDateTime().toString());
    assertEquals(null, eventOne.getDescription());
    assertEquals(null, eventOne.getLocation());
    assertEquals(false, eventOne.getIsPrivate());


    eventController.filterCommand("edit event description \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-13T10:00 with \"hello friends\"");
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
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T12:00 to 2025-10-11T13:00");
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    EventModel eventTwo = eventController.getEventHandler().getEventCollection("default").getEventList().get(1);
    assertEquals(2, eventController.getEventHandler().getEventCollection("default").getEventList().size());

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

    eventController.filterCommand("edit events eventName \"hello world\" "
            + "from 2025-10-10T10:00 with \"money printer goes BRRRRRRRRRRRRRRRRR\"");
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
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    assertEquals(1, eventController.getEventHandler().getEventCollection("default").getEventList().size());

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
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-11-10T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-12-10T11:00");
    eventController.filterCommand("print events on 2025-10-10");
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);

    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 10, 10, 12, 00);
    LocalDateTime endDateTime = LocalDateTime
            .of(2025, 10, 10, 13, 00);

    List<EventModel> printList = new ArrayList<>();
    printList = eventController
            .getEventHandler().getEventCollection("default")
            .searchEventByTimeFrame(startDateTime, endDateTime);

    assertEquals("\n*  eventName=hello world,  Start=2025-10-10T12:00, "
                    + " End=2025-10-10T13:00,  description=null,  "
                    + "isPrivate=false,  location=null,  autoDecline=false\n",
            eventController.getEventHandler().getEventCollection("default").printAssortments(printList));

    eventController.filterCommand("print events on 2025-10-10");
  }

  @Test
  public void processprintEventsBetween() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T11:00 to 2025-10-10T12:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");
    eventController.filterCommand("print events from 2025-10-11T10:00 to 2025-11-10T10:00");
    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(2);
    EventModel eventTwo = eventController.getEventHandler().getEventCollection("default").getEventList().get(3);

    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 10, 11, 10, 00);
    LocalDateTime endDateTime = LocalDateTime
            .of(2025, 11, 10, 10, 00);

    List<EventModel> printList = new ArrayList<>();
    printList = eventController.getEventHandler().getEventCollection("default").searchEventByTimeFrame(
        startDateTime, endDateTime);

    assertEquals(
            "\n"
                    + "*  eventName=hello world,  "
                    + "Start=2025-10-11T10:00,  End=2025-10-11T11:00,  "
                    + "description=null,  isPrivate=false,  location=null,  autoDecline=false\n"
                    + "*  eventName=hello world,  "
                    + "Start=2025-10-12T10:00,  End=2025-10-12T11:00,  "
                    + "description=null,  isPrivate=false,  location=null,  autoDecline=false\n",
            eventController.getEventHandler().getEventCollection("default").printAssortments(printList));
  }

  @Test
  public void processprintEventsBetweenInvalid() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T11:00 to 2025-10-10T12:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-12T10:00 to 2025-10-12T11:00");


    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      eventController.filterCommand("print events from 2025-10-11T10:00 to 2025-10-10T10:00");
    });

    // Optionally, check the exception message
    assertEquals("End time cannot be before start time, Event was not created", exception.getMessage());
  }


  @Test
  public void ShowStatusAvailable() {
    EventController eventController = new EventController();
    eventController.filterCommand("show status on 2025-01-10T10:00");
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 01, 10, 10, 00);
    assertEquals("Available", eventController
            .getEventHandler().getEventCollection("default").checkStatus(startDateTime, startDateTime));
  }

  @Test
  public void ShowStatusBusy() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00");
    eventController.filterCommand("show status on 2025-10-10T10:30");
    LocalDateTime startDateTime = LocalDateTime
            .of(2025, 10, 10, 10, 00);
    assertEquals("Busy", eventController.getEventHandler().getEventCollection("default")
            .checkStatus(startDateTime, startDateTime));
  }

  @Test
  public void removeCalender() {

    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T10:00 "
            + "description:\"test HELLO\" location:\"MA\" privacy:\"true\"");

    eventController.getEventHandler().createCalender("Hello", ZoneId.of("America/New_York"));
    eventController.filterCommand("copy event \"hello world\" on 2025-10-10T10:00 --target \"Hello\" to 2025-12-19T10:00");

    assertEquals(2,eventController.getEventHandler().getCalenderCollection().getCalenderCollectionSize());
    eventController.getEventHandler().getCalenderCollection().removeCalenderByName("Hello");
    assertEquals(1,eventController.getEventHandler().getCalenderCollection().getCalenderCollectionSize());
  }

  //edit calendar --name <calName> --property <property-name> <property-value>
  @Test
  public void printCalendersTest() {
    String print =
            "\nAvailable Calendars: \n" +
                    " - default\n" +
                    " - test2\n" +
                    " - CS5010\n";
    EventController eventController = new EventController();
    eventController.filterCommand("create calendar --name \"test\" " +
            "--timezone \"America/New_York\"");

    eventController.filterCommand("create calendar --name \"test2\" " +
            "--timezone \"America/Los_Angeles\"");

    eventController.filterCommand("edit calendar --name \"test\" --property \"name\" \"CS5010\"");
    eventController.filterCommand("edit calendar --name \"test2\" --property \"time-zone\" \"America/New_York\" ");
    assertEquals(print,eventController.getEventHandler().getCalenderCollection().printCalendars());

  }
  @Test
  public void EditCalenderTimezone() {
    String print =
            "\nAvailable Calendars: \n" +
                    " - default\n" +
                    " - test2\n" +
                    " - CS5010\n";
    EventController eventController = new EventController();
    eventController.filterCommand("create calendar --name \"test\" " +
            "--timezone \"America/New_York\"");

    eventController.filterCommand("create calendar --name \"test2\" " +
            "--timezone \"America/Los_Angeles\"");

    eventController.filterCommand("edit calendar --name \"test\" --property \"name\" \"CS5010\"");
    eventController.filterCommand("edit calendar --name \"test2\" --property \"time-zone\" \"America/New_York\" ");
    assertEquals(print,eventController.getEventHandler().getCalenderCollection().printCalendars());
    assertEquals(ZoneId.of("America/New_York"),
            eventController.getEventHandler().getCalenderCollection().getCalenderCollection()
                    .get("test2").getTimeZone());

  }
  @Test
public void TestGetCalenderName() {
    EventController eventController = new EventController();
  assertEquals("default", eventController.getEventHandler().getCalenderCollection().
          getCalendarName(eventController.getEventHandler().getEventCollection("default")));
  }

  @Test
  public void TestGetCalenderNameInvalid() {
      EventController eventController = new EventController();
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        eventController.getEventHandler().getCalenderCollection().getCalendarName(null);
      });
      assertEquals("event collection does not belong to a calender collection", exception.getMessage());
  }

  @Test
  public void GetCalenderNameDoesNotExist() {
    EventController eventController = new EventController();
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      eventController.getEventHandler().getCalenderCollection().getCalendarName(
              eventController.getEventHandler().getEventCollection("defaultttt"));
    });
    assertEquals("Calendar does not exist: defaultttt", exception.getMessage());
  }


  @Test
  public void checkEventBetweenDates() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T11:00 to 2025-10-10T12:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");

    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    EventModel eventTwo = eventController.getEventHandler().getEventCollection("default").getEventList().get(1);
    EventModel eventThree = eventController.getEventHandler().getEventCollection("default").getEventList().get(2);
//edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
    eventController.filterCommand("edit events eventName \"hello world\" from 2025-10-10T09:00 with  \"New Event Name\"\n");

    assertEquals("New Event Name", eventOne.getEventName());
    assertEquals("New Event Name", eventTwo.getEventName());
    assertEquals("New Event Name", eventThree.getEventName());
  }

  @Test
  public void checkNotEventBetweenDates() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T11:00 to 2025-10-10T12:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");

    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    EventModel eventTwo = eventController.getEventHandler().getEventCollection("default").getEventList().get(1);
    EventModel eventThree = eventController.getEventHandler().getEventCollection("default").getEventList().get(2);
//edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
    eventController.filterCommand("edit events eventName \"hello world\" from 2025-10-10T11:00 with  \"New Event Name\"\n");

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("hello world", eventTwo.getEventName());
    assertEquals("New Event Name", eventThree.getEventName());
  }


  @Test
  public void checkNotEventBothBetweenDates() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T10:00 to 2025-10-10T11:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-10T11:00 to 2025-10-10T12:00");
    eventController.filterCommand("create event \"hello world\" "
            + "from 2025-10-11T10:00 to 2025-10-11T11:00");

    EventModel eventOne = eventController.getEventHandler().getEventCollection("default").getEventList().get(0);
    EventModel eventTwo = eventController.getEventHandler().getEventCollection("default").getEventList().get(1);
    EventModel eventThree = eventController.getEventHandler().getEventCollection("default").getEventList().get(2);
//dit event <property> "<eventName>" from <dateStringTtimeString> to <dateStringTtimeString> with "<NewPropertyValue>"
    eventController.filterCommand("edit event eventName \"hello world\" from 2025-10-10T11:00 to  2025-10-10T12:00 with  \"New Event Name\"\n");

    assertEquals("hello world", eventOne.getEventName());
    assertEquals("New Event Name", eventTwo.getEventName());
    assertEquals("hello world", eventThree.getEventName());
  }



}

