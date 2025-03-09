package Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private CommandParser parser;

  public EventController() {
    eventCollection = new EventCollectionModel();
    view = new EventView();
    parser = new CommandParser();
  }

  public void createEventFromStartToEnd(boolean autoDecline, String eventName, String start, String end) {
    LocalDateTime startTime = LocalDateTime.parse(start.trim());
    LocalDateTime endTime = LocalDateTime.parse(end.trim());
    EventModel event = new EventModel(eventName, startTime, endTime, null, false, autoDecline, null);

    eventCollection.addEvent(event);
  }

  public void filterCommand(String command) {

    Matcher matcher = parser.parseCommand(command);

    if (matcher != null) {
      // Get the pattern that matched
      String pattern = matcher.pattern().toString();

      // Map the pattern string to the command
      Map<String, Runnable> commandMap = new HashMap<>();
      commandMap.put("createEventPattern", () -> processCreateEvent(matcher));
      commandMap.put("createRecurringEventN", () -> processCreateRecurringEventN(matcher));
      commandMap.put("createRecurringEventEndDate", () -> processCreateRecurringEventEndDate(matcher));
      commandMap.put("createWholeDayEvent", () -> processCreateAllDayEvent(matcher));
      commandMap.put("createRecurringWholeDayEventN", () -> processCreateAllDayRecurring(matcher));
      commandMap.put("createRecurringWholeDayEventEnd", () -> processCreateRecurringWholeDayEventEnd(matcher));
      commandMap.put("editEvent", () -> processEditEvent(matcher));
      commandMap.put("editEvents", () -> processEditEvents(matcher));
      commandMap.put("editEventsByName", () -> processEditEventsByName(matcher));
      commandMap.put("printEventsOn", () -> processPrintEventsOn(matcher));
      commandMap.put("printEventsBetween", () -> processPrintEventsBetween(matcher));
      commandMap.put("export", () -> processExport(matcher));
      commandMap.put("show", () -> processShowStatus(matcher));

      // go through the map and call method if there is a match
      for (Map.Entry<String, Pattern> entry : parser.getPatterns().entrySet()) {
        if (entry.getValue().matcher(command).matches()) {
          commandMap.get(entry.getKey()).run(); // Execute command
          return;
        }
      }

      // If no pattern was matched
      System.out.println("No matching command for the pattern: " + pattern);
    } else {
      System.out.println("Invalid command format" + command);
    }
  }

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
    System.out.println("created event all day");
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
    System.out.println("created event recurring all day event");
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
    System.out.println("created event recurring all day event till end date<String datetime>");
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
