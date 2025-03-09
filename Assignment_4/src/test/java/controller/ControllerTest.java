package controller;

import org.junit.Before;
import org.junit.Test;

import Controller.EventController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ControllerTest {

  @Before
  public void setUp(){

  }
  @Test
  public void processNormalCreation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline hello world from 2025-10-10T10:00 to 2025-10-10T10:00");
  }

  @Test
  public void processEventCreation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event hello world from 2025-10-10T10:00 to 2025-10-10T10:00");
  }

  @Test
  public void processrecurringNCreation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline hello world from 2025-10-10T10:00 to 2025-10-10T10:00 repeats MTW for 6 times");
    //Pattern createRecurringEventN = Pattern.compile("create\\sevent(\\s--autoDecline)?\\s(.*?)\\sfrom\\s(" + dateTimeString + ")\\sto\\s(" + dateTimeString + ")\\srepeats\\s"+weekDay+"\\sfor\\s(\\d+)\\stimes$");
  }

  //create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
  @Test
  public void processrecurringEnddateCreation() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline hello world from 2025-10-10T10:00 to 2025-10-10T11:00 repeats MTW until 2025-11-10T10:00");
    //    Pattern createRecurringEventEndDate = Pattern.compile("create\\sevent\\s(--autoDecline)?\\s(.*?)\\sfrom\\s" + dateTimeString + "\\sto\\s" + dateTimeString + "\\srepeats\\s"+weekDay+"\\suntil\\s" + dateTimeString + "$");

  }

  //create event --autoDecline <eventName> on <dateStringTtimeString>
  @Test
  public void processCreateAllDayEvent() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event hello world on 2025-10-10T10:00");
    //Pattern createWholeDayEvent = Pattern.compile("create\\sevent(\\s--autoDecline)?\\s(.*?)\\son\\s" + dateTimeString + "$");
  }

  //create event <eventName> on <dateString> repeats <weekdays> for <N> times
  @Test
  public void processCreateRecurringAllDayEventN() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline hello world on 2025-10-10 repeats MTW for 6 times");
    // Pattern createRecurringWholeDayEventN = Pattern.compile("create\\sevent(\\s--autoDecline)?\\s(.*?)\\son\\s"
    // + dateString + "\\srepeats\\s"+weekDay+"\\sfor\\s(\\d+)\\stimes$");
  }

  //create event <eventName> on <dateString> repeats <weekdays> until <dateString>

  @Test
  public void processCreateRecurringAllDayEventEnd() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline hello world on 2025-10-10 repeats MTW until 2025-12-10");

  }
  //NEEED TEST FOR LOOPING AROUND LIKE 10->11->12->1->2 woking .... all good here
  @Test
  public void recurringEventLoopAround() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event --autoDecline hello world on 2025-10-10 repeats MTW until 2026-01-10");
  }

  //edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
  //Pattern editEvent = Pattern.compile("edit\\sevent\\s(.*?)\\s(.*?)\\sfrom\\s" + dateTimeString + "\\sto\\s" + dateTimeString + "\\swith\\s(.*?)$");

  @Test
  public void processEditEventProperty() {
    EventController eventController = new EventController();
    eventController.filterCommand("edit event <property> Hello word from 2025-10-10T10:00 to 2025-10-10T10:00 with <NewPropertyValue>");

  }

  // edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
  //Pattern editAllEvents = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\sfrom\\s" + dateTimeString + "\\swith\\s(.*?)$");
  @Test
  public void processEditEventsProperty() {
    EventController eventController = new EventController();
    eventController.filterCommand("edit events <property> Hello word from 2025-10-10T10:00 with <NewPropertyValue>");
  }

  // edit events <property> <eventName> <NewPropertyValue>
  //Pattern editEventsByName = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\s(.*?)$");
  @Test
  public void processEditEventsByName() {
    EventController eventController = new EventController();
    eventController.filterCommand("edit events <property> Hello word <NewPropertyValue>");
  }


  // print events on <dateString>
  //Pattern printEventsOn = Pattern.compile("print\\sevents\\son\\s" + dateString + "$");
  @Test
  public void processPrintEventsOn() {


    EventController eventController = new EventController();
    eventController.filterCommand("create event hello world from 2025-10-10T12:00 to 2025-10-10T13:00");
    eventController.filterCommand("create event hello world from 2025-10-11T10:00 to 2025-11-10T11:00");
    eventController.filterCommand("create event hello world from 2025-10-12T10:00 to 2025-12-10T11:00");
    eventController.filterCommand("print events on 2025-10-10");
  }
  // print events from <dateStringTtimeString> to <dateStringTtimeString>
  //Pattern printEventsBetween = Pattern.compile("print\\sevents\\sfrom\\s" + dateTimeString + "\\sto\\s" + dateTimeString + "$");
  @Test
  public void processprintEventsBetween() {
    EventController eventController = new EventController();
    eventController.filterCommand("create event hello world from 2025-10-10T10:00 to 2025-10-10T11:00");
    eventController.filterCommand("create event hello world from 2025-10-10T11:00 to 2025-10-10T12:00");
    eventController.filterCommand("create event hello world from 2025-10-11T10:00 to 2025-10-11T11:00");
    eventController.filterCommand("create event hello world from 2025-10-12T10:00 to 2025-10-12T11:00");
    eventController.filterCommand("print events from 2025-10-10T10:00 to 2025-11-10T10:00");
  }
  // export cal fileName.csv
  //Pattern export = Pattern.compile("export\\scal\\s([a-zA-Z0-9]+\\.csv)");
  @Test
  public void processExport() {
    EventController eventController = new EventController();
    eventController.filterCommand("export cal FWERTYU.csv");
  }
  // show status on <dateStringTtimeString>
  //Pattern show = Pattern.compile("show\\sstatus\\son\\s" + dateTimeString + "$");
  //Prints busy status if the user has events scheduled on a given day and time, otherwise, available.
  @Test
  public void processShow() {
    EventController eventController = new EventController();
    eventController.filterCommand("show status on 2025-01-10T10:00");
  }
}
