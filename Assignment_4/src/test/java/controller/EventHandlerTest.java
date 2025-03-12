package controller;

import Controller.EventController;
import Model.EventCollectionModel;
import Model.EventModel;
import View.EventView;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
public class EventHandlerTest {

    @Test
    public void normalEventCreation() {

        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T10:00 description:\"test HELLO\" location:\"MA\" privacy:\"true\"");

        assertEquals(1, eventController.getEventCollection().getEventList().size());
        EventModel event = eventController.getEventCollection().getEventList().get(0);

        assertEquals("hello world",event.getEventName());
        assertEquals("2025-10-10T10:00",event.getStartDateTime().toString());
        assertEquals("2025-10-10T10:00",event.getEndDateTime().toString());
        assertEquals("test HELLO",event.getDescription());
        assertEquals("MA",event.getLocation());
        assertEquals(true,event.getIsPrivate());
    }

    @Test
    public void eventCreationWithLocation() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T10:00 location:\"MA\"");
        EventModel event = eventController.getEventCollection().getEventList().get(0);

        assertEquals("hello world",event.getEventName());
        assertEquals("2025-10-10T10:00",event.getStartDateTime().toString());
        assertEquals("2025-10-10T10:00",event.getEndDateTime().toString());
        assertEquals(null,event.getDescription());
        assertEquals("MA",event.getLocation());
        assertEquals(false,event.getIsPrivate());
    }
    @Test
    public void eventCreationWithDescription() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T10:00 description:\"test\"");
        EventModel event = eventController.getEventCollection().getEventList().get(0);

        assertEquals("hello world",event.getEventName());
        assertEquals("2025-10-10T10:00",event.getStartDateTime().toString());
        assertEquals("2025-10-10T10:00",event.getEndDateTime().toString());
        assertEquals("test",event.getDescription());
        assertEquals(null,event.getLocation());
        assertEquals(false,event.getIsPrivate());
    }
    @Test
    public void eventCreationWithPrivacy() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T10:00 ");
        EventModel event = eventController.getEventCollection().getEventList().get(0);

        assertEquals("hello world",event.getEventName());
        assertEquals("2025-10-10T10:00",event.getStartDateTime().toString());
        assertEquals("2025-10-10T10:00",event.getEndDateTime().toString());
        assertEquals(null,event.getDescription());
        assertEquals(null,event.getLocation());
        assertEquals(false,event.getIsPrivate());
    }

    @Test
    public void eventCreationNoOptionalParams() {
        EventController eventController = new EventController();
        eventController.filterCommand("CREATE event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T10:00");
        assertEquals(1, eventController.getEventCollection().getEventList().size());
        EventModel event = eventController.getEventCollection().getEventList().get(0);

        assertEquals("hello world",event.getEventName());
        assertEquals("2025-10-10T10:00",event.getStartDateTime().toString());
        assertEquals("2025-10-10T10:00",event.getEndDateTime().toString());
        assertEquals(null,event.getDescription());
        assertEquals(null,event.getLocation());
        assertEquals(false,event.getIsPrivate());
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T10:00 privacy:\"true\" description:\"test\" location:\"MA\" ");
        eventController.filterCommand("print events on 2025-10-10");
    }



    @Test
    public void invalidSpellingErrorCommand() {

        EventController eventController = new EventController();
        eventController.filterCommand("createe event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T10:00");
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
        eventController.filterCommand(" CREATE event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T10:00");
        assertEquals(1, eventController.getEventCollection().getEventList().size());
        EventModel event = eventController.getEventCollection().getEventList().get(0);

        assertEquals("hello world",event.getEventName());
        assertEquals("2025-10-10T10:00",event.getStartDateTime().toString());
        assertEquals("2025-10-10T10:00",event.getEndDateTime().toString());
        assertEquals(null,event.getDescription());
        assertEquals(null,event.getLocation());
        assertEquals(false,event.getIsPrivate());
    }


    //create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
    @Test
    public void recurringNCreation() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event --autoDecline \"hello world\" from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW for 6 times  description:\"test\" location:\"MA\" privacy:\"true\"");
        assertEquals(6, eventController.getEventCollection().getEventList().size());
        EventModel eventOne = eventController.getEventCollection().getEventList().get(0);

        assertEquals("hello world",eventOne.getEventName());
        assertEquals("2025-10-13T10:00",eventOne.getStartDateTime().toString());
        assertEquals("2025-10-13T10:00",eventOne.getEndDateTime().toString());
        assertEquals("test",eventOne.getDescription());
        assertEquals("MA",eventOne.getLocation());
        assertEquals(true,eventOne.getIsPrivate());

    }

    //create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
    @Test
    public void processRecurringEnddateCreation() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event --autoDecline \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW until 2025-12-10T10:00 description:\"test\" location:\"MA\" privacy:\"true\" ");
        assertEquals(27, eventController.getEventCollection().getEventList().size());
        EventModel eventOne = eventController.getEventCollection().getEventList().get(0);

        assertEquals("hello world",eventOne.getEventName());
        assertEquals("2025-10-13T10:00",eventOne.getStartDateTime().toString());
        assertEquals("2025-10-13T11:00",eventOne.getEndDateTime().toString());
        assertEquals("test",eventOne.getDescription());
        assertEquals("MA",eventOne.getLocation());
        assertEquals(true,eventOne.getIsPrivate());
    }

    //create event --autoDecline <eventName> on <dateStringTtimeString>
    @Test
    public void processCreateAllDayEvent() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event --autoDecline \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW until 2025-11-10T10:00  description:\"test\" location:\"MA\" privacy:\"true\" ");
        assertEquals(1, eventController.getEventCollection().getEventList().size());
        EventModel eventOne = eventController.getEventCollection().getEventList().get(0);

        assertEquals("hello world",eventOne.getEventName());
        assertEquals("2025-10-10T10:00",eventOne.getStartDateTime().toString());
        assertEquals("2025-10-10T11:00",eventOne.getEndDateTime().toString());
        assertEquals("test",eventOne.getDescription());
        assertEquals("MA",eventOne.getLocation());
        assertEquals(true,eventOne.getIsPrivate());
    }

    //create event <eventName> on <dateString> repeats <weekdays> for <N> times
    @Test
    public void processCreateRecurringAllDayEventN() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event --autoDecline \"hello world\" on 2025-10-10 repeats MTW for 6 times description:\"test\" location:\"MA\" privacy:\"true\"");
        eventController.filterCommand("print events on 2025-10-10");
        // Pattern createRecurringWholeDayEventN = Pattern.compile("create\\sevent(\\s--autoDecline)?\\s(.*?)\\son\\s"
        // + dateString + "\\srepeats\\s"+weekDay+"\\sfor\\s(\\d+)\\stimes$");
    }

    //create event <eventName> on <dateString> repeats <weekdays> until <dateString>

    @Test
    public void processCreateRecurringAllDayEventEnd() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event --autoDecline \"hello world\" on 2025-10-10 repeats MTW until 2025-12-10 description:\"test\" location:\"MA\" privacy:\"true\"");
        eventController.filterCommand("print events on 2025-10-10");

    }
    //NEEED TEST FOR LOOPING AROUND LIKE 10->11->12->1->2 woking .... all good here
    @Test
    public void recurringEventLoopAround() {
        EventController eventController = new EventController();

        eventController.filterCommand("create event --autoDecline \"hello world\" on 2025-10-10 repeats MTW until 2026-01-10 description:\"test\" location:\"MA\" privacy:\"true\"");
        eventController.filterCommand("print events on 2025-10-10");
    }

    //edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
    //Pattern editEvent = Pattern.compile("edit\\sevent\\s(.*?)\\s(.*?)\\sfrom\\s" + dateTimeString + "\\sto\\s" + dateTimeString + "\\swith\\s(.*?)$");

    @Test
    public void editEventNameBeforeTime() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T12:00 to 2025-10-10T13:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-11T10:00 to 2025-10-11T11:00 ");
        eventController.filterCommand("create event \"hello world\" from 2025-10-12T10:00 to 2025-10-12T11:00");

        eventController.filterCommand("edit event eventName \"hello world\" from 2025-10-10T10:00 to 2025-10-10T14:00 with \"hazarrrrr\"");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");
    }

    @Test
    public void editEventNameinTime() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T12:00 to 2025-10-10T13:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-11T10:00 to 2025-10-11T11:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-12T10:00 to 2025-10-12T11:00");

        eventController.filterCommand("edit event eventName \"hello world\" from 2025-10-10T10:00 to 2025-10-11T12:00 with \"hazarrrrr\"");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");
    }
    @Test
    public void editEventDateTime() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T12:00 to 2025-10-10T13:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-11T10:00 to 2025-10-11T11:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-12T10:00 to 2025-10-12T11:00");

        eventController.filterCommand("edit event startDateTime \"hello world\" from 2025-10-10T10:00 to 2025-10-13T10:00 with \"2025-10-19T10:00\"");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");


    }

    @Test
    public void editEndDateTime() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T12:00 to 2025-10-10T13:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-11T10:00 to 2025-10-11T11:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-12T10:00 to 2025-10-12T11:00");

        eventController.filterCommand("edit event endDateTime \"hello world\" from 2025-10-10T10:00 to 2025-10-13T10:00 with \"2025-10-19T10:00\"");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    }

    @Test
    public void editlocation() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T12:00 to 2025-10-10T13:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-11T10:00 to 2025-10-11T11:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-12T10:00 to 2025-10-12T11:00");

        eventController.filterCommand("edit event location \"hello world\" from 2025-10-10T10:00 to 2025-10-13T10:00 with \"USAAAAAAAA\"");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    }

    @Test
    public void editPrivacy() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T12:00 to 2025-10-10T13:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-11T10:00 to 2025-10-11T11:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-12T10:00 to 2025-10-12T11:00");

        eventController.filterCommand("edit event privacyType \"hello world\" from 2025-10-10T10:00 to 2025-10-13T10:00 with \"private\"");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    }

    @Test
    public void editDescription() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T12:00 to 2025-10-10T13:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-11T10:00 to 2025-10-11T11:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-12T10:00 to 2025-10-12T11:00");

        eventController.filterCommand("edit event description \"hello world\" from 2025-10-10T10:00 to 2025-10-13T10:00 with \"hello friends\"");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    }



    // edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
    //Pattern editAllEvents = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\sfrom\\s" + dateTimeString + "\\swith\\s(.*?)$");
    @Test
    public void processEditEventsProperty() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T12:00 to 2025-10-10T13:00");
        eventController.filterCommand("edit events eventName \"hello world\" from 2025-10-10T10:00 with \"money printer goes BRRRRRRRRRRRRRRRRR\"");
        eventController.filterCommand("print events on 2025-10-10");
    }

    // edit events <property> <eventName> <NewPropertyValue>
    //Pattern editEventsByName = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\s(.*?)$");
    @Test
    public void processEditEventsByName() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T12:00 to 2025-10-10T13:00");
        eventController.filterCommand("edit events location \"hello world\" \"MA Boston\"");
        eventController.filterCommand("print events on 2025-10-10");
    }


    // print events on <dateString>
    //Pattern printEventsOn = Pattern.compile("print\\sevents\\son\\s" + dateString + "$");
    @Test
    public void processPrintEventsOn() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T12:00 to 2025-10-10T13:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-11T10:00 to 2025-11-10T11:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-12T10:00 to 2025-12-10T11:00");
        eventController.filterCommand("print events on 2025-10-10");
    }
    // print events from <dateStringTtimeString> to <dateStringTtimeString>
    //Pattern printEventsBetween = Pattern.compile("print\\sevents\\sfrom\\s" + dateTimeString + "\\sto\\s" + dateTimeString + "$");
    @Test
    public void processprintEventsBetween() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T11:00 to 2025-10-10T12:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-11T10:00 to 2025-10-11T11:00");
        eventController.filterCommand("create event \"hello world\" from 2025-10-12T10:00 to 2025-10-12T11:00");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");

    }
    // export cal fileName.csv
    //Pattern export = Pattern.compile("export\\scal\\s([a-zA-Z0-9]+\\.csv)");
    @Test
    public void processExport() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00");
        eventController.filterCommand("export cal test.csv");
    }
    // show status on <dateStringTtimeString>
    //Pattern show = Pattern.compile("show\\sstatus\\son\\s" + dateTimeString + "$");
    //Prints busy status if the user has events scheduled on a given day and time, otherwise, available.
    @Test
    public void ShowStatusAvailable() {
        EventController eventController = new EventController();
        eventController.filterCommand("show status on 2025-01-10T10:00");
    }
    @Test
    public void ShowStatusBusy() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00");
        eventController.filterCommand("show status on 2025-10-10T10:30");
    }

    @Test
    public void EventCollision() {
        EventController eventController = new EventController();
        eventController.filterCommand("create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00");
        eventController.filterCommand("create  event --autoDecline \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00");
    }
    @Test
    public void recurringEventCollision() {
        EventController eventController = new EventController();
        eventController.filterCommand("create  event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00");
        eventController.filterCommand("create event --autoDecline \"hello world1\" from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW for 10 times");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-12-10T10:00");

    }

    @Test
    public void collisionWithoutAutoDecline() {
        EventController eventController = new EventController();
        eventController.filterCommand("create  event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00");
        eventController.filterCommand("create event  \"hello world\" from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW for 2 times  ");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-12-10T10:00");
    }

    //create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
    // create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
    @Test
    public void collisionrecurringWithEnd() {
        EventController eventController = new EventController();
        eventController.filterCommand("create  event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00");
        eventController.filterCommand("create event  \"hello world\" from 2025-10-01T10:00 to 2025-10-01T11:00 repeats MTW  until 2025-10-21T10:00 ");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-12-10T10:00");
    }

    @Test
    public void collisionalldayevents() {
        EventController eventController = new EventController();
        EventCollectionModel eventCollectionModel = new EventCollectionModel();
        eventController.filterCommand("create event \"hello world\" on 2025-10-10T10:00");
        eventController.filterCommand("create  event --autoDecline \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00");
        eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-10-12T10:00");

    }
}