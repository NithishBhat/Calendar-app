package Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Model.EventCollectionModel;
import Model.EventModel;
import Model.RecurringEventModel;
import View.EventView;
import java.time.DayOfWeek;
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
    String dateTimeString = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})";
    String dateString = "(\\d{4}-\\d{2}-\\d{2})";
    String weekDay="([MTWRFSU]{1,7})";// only weekday chars allowed, atleast 1 char is a must, order of chars doesn't matter

// Create event patterns
// create event --autodecline <eventName> from <dateStringTTimeString> to <dateStringTTimeString>
    Pattern createEventPattern = Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+from\\s+" + dateTimeString + "\\s+to\\s+" + dateTimeString + "\\s*$");

// create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
    Pattern createRecurringEventN = Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+from\\s+" + dateTimeString + "\\s+to\\s+" + dateTimeString + "\\s+repeats\\s+"+weekDay+"\\s+for\\s+(\\d+)\\s+times\\s*$");

// create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
    Pattern createRecurringEventEndDate = Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+from\\s+" + dateTimeString + "\\s+to\\s+" + dateTimeString + "\\s+repeats\\s+"+weekDay+"\\s+until\\s+" + dateTimeString + "\\s*$");

// create event --autoDecline <eventName> on <dateStringTtimeString>
    Pattern createWholeDayEvent = Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+on\\s+" + dateTimeString + "\\s*$");

// create event <eventName> on <dateString> repeats <weekdays> for <N> times
    Pattern createRecurringWholeDayEventN = Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+on\\s+" + dateString + "\\s+repeats\\s+"+weekDay+"\\s+for\\s+(\\d+)\\s+times\\s*$");

// create event <eventName> on <dateString> repeats <weekdays> until <dateString>
    Pattern createRecurringWholeDayEventEnd = Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+on\\s+" + dateString + "\\s+repeats\\s+"+weekDay+"\\s+until\\s+" + dateString + "\\s*$");

    // Edit event patterns
// edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
    Pattern editEvent = Pattern.compile("\\s*edit\\s+event\\s+(.*?)\\s+(.*?)\\s+from\\s+" + dateTimeString + "\\s+to\\s+" + dateTimeString + "\\s+with\\s+(.*?)\\s*$");

// edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
    Pattern editEvents = Pattern.compile("\\s*edit\\s+events\\s+(.*?)\\s+(.*?)\\s+from\\s+" + dateTimeString + "\\s+with\\s+(.*?)\\s*$");

// edit events <property> <eventName> <NewPropertyValue>
    Pattern editEventsByName = Pattern.compile("\\s*edit\\s+events\\s+(.*?)\\s+(.*?)\\s+(.*?)\\s*$");

// Print and export patterns
// print events on <dateString>
    Pattern printEventsOn = Pattern.compile("\\s*print\\s+events\\s+on\\s+" + dateString + "\\s*$");

// print events from <dateStringTtimeString> to <dateStringTtimeString>
    Pattern printEventsBetween = Pattern.compile("\\s*print\\s+events\\s+from\\s+" + dateTimeString + "\\s+to\\s+" + dateTimeString + "\\s*$");

// export cal fileName.csv
    Pattern export = Pattern.compile("\\s*export\\s+cal\\s+([a-zA-Z0-9]+\\.csv)\\s*");

// show status on <dateStringTtimeString>
    Pattern show = Pattern.compile("\\s*show\\s+status\\s+on\\s+" + dateTimeString + "\\s*$");

    Matcher matcher;

    if ((matcher = createEventPattern.matcher(command)).matches()) {
      processCreateEvent(matcher);
    } else if ((matcher = createRecurringEventN.matcher(command)).matches()) {
      processCreateRecurringEventN(matcher);
    } else if ((matcher = createRecurringEventEndDate.matcher(command)).matches()) {
      processCreateRecurringEventEndDate(matcher);
    }else if ((matcher = createWholeDayEvent.matcher(command)).matches()) {
      processCreateAllDayEvent(matcher);
    }else if ((matcher = createRecurringWholeDayEventN.matcher(command)).matches()) {
      processCreateAllDayRecurring(matcher);
    }else if ((matcher=createRecurringWholeDayEventEnd.matcher(command)).matches()) {
      processCreateRecurringWholeDayEventEnd(matcher);
    } else if ((matcher=editEvent.matcher(command)).matches()) {
     processEditEvent(matcher);
    } else if ((matcher=editEvents.matcher(command)).matches()) {
      processEditEvents(matcher);
    }else if ((matcher=editEventsByName.matcher(command)).matches()) {
      processEditEventsByName(matcher);
    }else if ((matcher=printEventsOn.matcher(command)).matches()) {
         processPrintEventsOn(matcher);
          } else if ((matcher=printEventsBetween.matcher(command)).matches()) {
            processPrintEventsBetween(matcher);
         } else if ((matcher=export.matcher(command)).matches()) {
           processExport(matcher);
         } else if ((matcher=show.matcher(command)).matches()) {
      processShowStatus(matcher);
    }
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
  //

  // create event --autodecline <eventName> from <dateStringTTimeString> to <dateStringTTimeString>
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

  EventModel event = new EventModel(eventName, startDate, endDate,null,false,autoDecline,null);
  eventCollection.addEvent(event);
}

  //create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
  private void processCreateRecurringEventN(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);
    String endDateTime = matcher.group(4);
    String weekDay = matcher.group(5);
    Integer numOfTimes = Integer.valueOf(matcher.group(6));

    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    LocalDateTime endDate = LocalDateTime.parse(endDateTime);
    List<Character> recurringWeekDay= StringToDayOfWeek(weekDay);
    RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDate, endDate,null,
            false,autoDecline,null,recurringWeekDay,numOfTimes,null);

    eventCollection.addRecurringEvent(recurringEvent);
    System.out.println("recurring n times created");
  }

  // create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
// NEED TO HANDLE CASES WHERE RECURRING DAYS CANT HAPPEN DUE TO ANOMALIES LIKE 28 day months
  private void processCreateRecurringEventEndDate(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);
    String endDateTime = matcher.group(4);
    String weekDay = matcher.group(5);
    String recurrernceEndDate = matcher.group(6);

    List<Character> recurringWeekDay= StringToDayOfWeek(weekDay);
    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    LocalDateTime endDate = LocalDateTime.parse(endDateTime);
    LocalDateTime recurrernceEndDateTime =  LocalDateTime.parse(recurrernceEndDate);

    RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDate, endDate,null,
            false,autoDecline,null,recurringWeekDay,null,recurrernceEndDateTime);

    eventCollection.addRecurringEvent(recurringEvent);

    System.out.println("recurring End date created");
  }

  //create event --autoDecline <eventName> on <dateStringTtimeString>
  private void processCreateAllDayEvent(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);



    LocalDateTime startDate = LocalDateTime.parse(startDateTime);
    EventModel event = new EventModel(eventName, startDate, null,null,false,autoDecline,null);
    eventCollection.addEvent(event);
    System.out.println("create event all day");
  }

  //create event <eventName> on <dateString> repeats <weekdays> for <N> times
  private void processCreateAllDayRecurring(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDateTime = matcher.group(3);

    String weekDay = matcher.group(4);
    Integer numOfTimes = Integer.valueOf(matcher.group(5));

    LocalDateTime startDate = LocalDate.parse(startDateTime).atStartOfDay();
    List<Character> recurringWeekDay= StringToDayOfWeek(weekDay);


    RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDate, null,null,
            false,autoDecline,null,recurringWeekDay,numOfTimes,null);
    eventCollection.addRecurringEvent(recurringEvent);
    System.out.println("create event recurring all day event");
  }

  //create event <eventName> on <dateString> repeats <weekdays> until <dateString>
  private void processCreateRecurringWholeDayEventEnd(Matcher matcher) {
    Boolean autoDecline;

    String autoDeclineString = matcher.group(1);
    autoDecline = autoDeclineString == null ? false : true;
    String eventName = matcher.group(2);
    String startDate = matcher.group(3);

    String weekDay = matcher.group(4);
    String recurrernceEndDate = matcher.group(5);

    List<Character> recurringWeekDay= StringToDayOfWeek(weekDay);
    LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
    LocalDateTime recurrernceEndDateTime =  LocalDate.parse(recurrernceEndDate).atStartOfDay();


    RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDateTime, null,null,
            false,autoDecline,null,recurringWeekDay,null,recurrernceEndDateTime);
    eventCollection.addRecurringEvent(recurringEvent);
    System.out.println("create event recurring all day event till end date<String datetime>");
  }

  //edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
  //Pattern editEvent = Pattern.compile("edit\\sevent\\s(.*?)\\s(.*?)\\sfrom\\s" + dateTimeString + "\\sto\\s" + dateTimeString + "\\swith\\s(.*?)$");
  private void processEditEvent(Matcher matcher) {
    String property = matcher.group(1);
    String eventName = matcher.group(2);
    String startDate = matcher.group(3);
    String endDate = matcher.group(4);
    String newProperty = matcher.group(5);

    LocalDateTime startDateTime= LocalDateTime.parse(startDate);
    LocalDateTime endDateTime = LocalDateTime.parse(endDate);
    System.out.println("edit event " + eventName + " startDate=" + startDate + " endDate=" + endDate);
    //get events with same event name
    //from that get events between start and end
    //replace property with new property

  }

  // edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
  //Pattern editAllEvents = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\sfrom\\s" + dateTimeString + "\\swith\\s(.*?)$");
  private void processEditEvents(Matcher matcher) {
    String property = matcher.group(1);
    String eventName = matcher.group(2);
    String startDate = matcher.group(3);
    String newProperty = matcher.group(4);

    LocalDateTime startDateTime= LocalDateTime.parse(startDate);
    System.out.println("edit event multiple events");
  }


  // edit events <property> <eventName> <NewPropertyValue>
  //Pattern editEventsByName = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\s(.*?)$");
  private void processEditEventsByName(Matcher matcher) {
    String property = matcher.group(1);
    String eventName = matcher.group(2);
    String newProperty = matcher.group(3);

    System.out.println("edit event multiple events by NAME");
  }

  // print events on <dateString>
  //Pattern printEventsOn = Pattern.compile("print\\sevents\\son\\s" + dateString + "$");
  //Prints a bulleted list of all events on that day along with their start and end time and location
  private void processPrintEventsOn(Matcher matcher) {
    String startDate = matcher.group(1);
    LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
    LocalDateTime endDateTime =  startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));
    System.out.println("print eventSZZZZ on " + startDate);

    List<EventModel> printList= new ArrayList<>();
    printList=eventCollection.searchEventByTimeFrame(startDateTime,endDateTime);
    System.out.println(eventCollection.printAssortments(printList));

  }



  // print events from <dateStringTtimeString> to <dateStringTtimeString>
  //Pattern printEventsBetween = Pattern.compile("print\\sevents\\sfrom\\s" + dateTimeString + "\\sto\\s" + dateTimeString + "$");
  private void processPrintEventsBetween(Matcher matcher) {
    String startDate = matcher.group(1);
    String endDate = matcher.group(2);
    LocalDateTime startDateTime = LocalDateTime.parse(startDate);
    LocalDateTime endDateTime = LocalDateTime.parse(endDate);
    System.out.println("print events from " + startDateTime+"to "+endDateTime);

    List<EventModel> printList= new ArrayList<>();
    printList=eventCollection.searchEventByTimeFrame(startDateTime,endDateTime);
    System.out.println(eventCollection.printAssortments(printList));
  }


  // export cal fileName.csv
  //Pattern export = Pattern.compile("export\\scal\\s([a-zA-Z0-9]+\\.csv)");
  private void processExport(Matcher matcher) {
    String fileName = matcher.group(1);
    System.out.println("export to " + fileName);
  }


  // show status on <dateStringTtimeString>
  //Pattern show = Pattern.compile("show\\sstatus\\son\\s" + dateTimeString + "$");
  //Prints busy status if the user has events scheduled on a given day and time, otherwise, available.
  private void processShowStatus(Matcher matcher) {
    String startDate = matcher.group(1);
    LocalDateTime startDateTime = LocalDateTime.parse(startDate);
    System.out.println("show status from " + startDate+" BUSY OR NOT?????????????????");
  }



  /**
   * command string extract for weekdays are broken into chars and put in a list.
   * we assume string is gonna me like "MT"
   * @param weekDay
   * @return dayOfWeek
   */
  private List<Character> StringToDayOfWeek(String weekDay) {
    List<Character> dayOfWeek= new ArrayList<>();
   for(int i=0;i<weekDay.length();i++){
      dayOfWeek.add(weekDay.charAt(i));
    }
   return dayOfWeek;
  }


}
