package Controller;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Model.EventCollectionModel;
import Model.EventModel;
import View.EventView;

public class EventController {


  private EventCollectionModel eventCollection;
  private EventView view;

  //create event --autodecline <eventName> from <dateStringTTimeString> to <dateStringTTimeString>
  //create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
  //create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
  //create event --autoDecline <eventName> on <dateStringTtimeString>
  //create event <eventName> on <dateString> repeats <weekdays> for <N> times
  //create event <eventName> on <dateString> repeats <weekdays> until <dateString>

  public EventController() {
    eventCollection = new EventCollectionModel();
    view = new EventView();
  }

  //this.eventName = eventName;
  //this.startDateTime = startDateTime;
  //this.endDateTime = endDateTime;
  //this.description = "No Description";
  //this.isPrivate = false;
  //this.autoDecline = autoDecline;
  //this.location = "No Location";

  public void createEventFromStartToEnd(boolean autoDecline, String eventName, String start, String end) {
    LocalDateTime startTime = LocalDateTime.parse(start.trim());
    LocalDateTime endTime = LocalDateTime.parse(end.trim());
    EventModel event = new EventModel(eventName, startTime, endTime, null, false, autoDecline, null);
  }

  public void filterCommand(String command) {

    ///  i am assuming that autodecline can be put anywhere

    //create commands
    // Define base patterns for reuse
    String dateTimeString = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}";
    String dateString = "\\d{4}-\\d{2}-\\d{2}";

// Create event patterns
// create event --autodecline <eventName> from <dateStringTTimeString> to <dateStringTTimeString>
    Pattern createEventPattern = Pattern.compile("create\\sevent(\\s--autoDecline\\s)?\\s(.*?)\\sfrom\\s(" + dateTimeString + ")\\sto\\s(" + dateTimeString + ")$");

// create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
    Pattern createRecurringEventN = Pattern.compile("create\\sevent\\s(--autoDecline)?\\s(.*?)\\sfrom\\s(" + dateTimeString + ")\\sto\\s(" + dateTimeString + ")\\srepeats\\s([a-zA-Z]+)\\sfor\\s(\\d+)\\stimes$");

// create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
    Pattern createRecurringEventEndDate = Pattern.compile("create\\sevent\\s(--autoDecline)?\\s(.*?)\\sfrom\\s(" + dateTimeString + ")\\sto\\s(" + dateTimeString + ")\\srepeats\\s([a-zA-Z]+)\\suntil\\s(" + dateTimeString + ")$");

// create event --autoDecline <eventName> on <dateStringTtimeString>
    Pattern createWholeDayEvent = Pattern.compile("create\\sevent\\s(--autoDecline)?\\s(.*?)\\son\\s(" + dateTimeString + ")$");

// create event <eventName> on <dateString> repeats <weekdays> for <N> times
    Pattern createRecurringWholeDayEventN = Pattern.compile("create\\sevent\\s(--autoDecline)?\\s(.*?)\\son\\s(" + dateString + ")\\srepeats\\s([a-zA-Z]+)\\sfor\\s(\\d+)\\stimes$");

// create event <eventName> on <dateString> repeats <weekdays> until <dateString>
    Pattern createRecurringWholeDayEventEnd = Pattern.compile("create\\sevent\\s(--autoDecline)?\\s(.*?)\\son\\s(" + dateString + ")\\srepeats\\s([a-zA-Z]+)\\suntil\\s(" + dateString + ")$");

    // Edit event patterns
// edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
    Pattern editEvent = Pattern.compile("edit\\sevent\\s(.*?)\\s(.*?)\\sfrom\\s(" + dateTimeString + ")\\sto\\s(" + dateTimeString + ")\\swith\\s(.*?)$");

// edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
    Pattern editAllEvents = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\sfrom\\s(" + dateTimeString + ")\\swith\\s(.*?)$");

// edit events <property> <eventName> <NewPropertyValue>
    Pattern editAllEventsByName = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\s(.*?)$");

// Print and export patterns
// print events on <dateString>
    Pattern printEventsOn = Pattern.compile("print\\sevents\\son\\s(" + dateString + ")$");

// print events from <dateStringTtimeString> to <dateStringTtimeString>
    Pattern printEventsBetween = Pattern.compile("print\\sevents\\sfrom\\s(" + dateTimeString + ")\\sto\\s(" + dateTimeString + ")$");

// export cal fileName.csv
    Pattern export = Pattern.compile("export\\scal\\s([a-zA-Z0-9]+\\.csv)");

// show status on <dateStringTtimeString>
    Pattern show = Pattern.compile("show\\sstatus\\son\\s(" + dateTimeString + ")$");

    Matcher matcher;

    if ((matcher = createEventPattern.matcher(command)).matches()) {
      processCreateEvent(matcher);
    } else if ((matcher = createRecurringEventN.matcher(command)).matches()) {
      processCreateRecurringEventN(matcher);
    }

    /**
     * } else if (createRecurringEventN.matcher(command).matches()) {
     *       processCreateRecurringEventN(command);
     *     } else if (createRecurringEventEndDate.matcher(command).matches()) {
     *       processCreateRecurringEventEndDate(command);
     *     } else if (createWholeDayEvent.matcher(command).matches()) {
     *       processCreateWholeDayEvent(command);
     *     } else if (createRecurringWholeDayEventN.matcher(command).matches()) {
     *       processCreateRecurringWholeDayEventN(command);
     *     } else if (createRecurringWholeDayEventEnd.matcher(command).matches()) {
     *       processCreateRecurringWholeDayEventEnd(command);
     *     } else if (editEvent.matcher(command).matches()) {
     *       processEditEvent(command);
     *     } else if (editAllEvents.matcher(command).matches()) {
     *       processEditAllEvents(command);
     *     } else if (editAllEventsByName.matcher(command).matches()) {
     *       processEditAllEventsByName(command);
     *     } else if (printEventsOn.matcher(command).matches()) {
     *       processPrintEventsOn(command);
     *     } else if (printEventsBetween.matcher(command).matches()) {
     *       processPrintEventsBetween(command);
     *     } else if (export.matcher(command).matches()) {
     *       processExport(command);
     *     } else if (show.matcher(command).matches()) {
     *       processShowStatus(command);
     */
  else
  {
    System.out.println("Invalid command format");
  }
}

  //create event --autodecline <eventName> from <dateStringTTimeString> to <dateStringTTimeString>
  //create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
  //create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
  //create event --autoDecline <eventName> on <dateStringTtimeString>
  //create event <eventName> on <dateString> repeats <weekdays> for <N> times
  //create event <eventName> on <dateString> repeats <weekdays> until <dateString>

private void processCreateEvent(Matcher matcher) {
  Boolean autoDecline;

  String autoDeclineString = matcher.group(1);
  autoDecline = autoDeclineString == null ? false : true;
  String eventName = matcher.group(2);
  String startDateTime = matcher.group(3);
  String endDateTime = matcher.group(4);
  System.out.println("Creating event: " + eventName + " " + startDateTime + " " + endDateTime + "  autoDecline:" + autoDecline);

  LocalDateTime startDate = LocalDateTime.parse(startDateTime);
  LocalDateTime endDate = LocalDateTime.parse(endDateTime);
 //createEventFromStartToEnd();
}

  private void processCreateRecurringEventN(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);
    String endDateTime = matcher.group(4);
    String weekDay = matcher.group(5);
    String numberOfTimes = matcher.group(6);
    System.out.println("Creating event: " + eventName + " " + startDateTime + " " + endDateTime + "  autoDecline:" + autoDecline);

  }


}
