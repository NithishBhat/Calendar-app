package Controller;

import Model.EventCollectionModel;
import Model.EventModel;
import Model.ExportToCSV;
import Model.RecurringEventModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class EventHandler {

    private EventCollectionModel eventCollection;

    public EventHandler(EventCollectionModel eventCollection) {
        this.eventCollection = eventCollection;
    }

    public void createEventFromStartToEnd(boolean autoDecline, String eventName, String start, String end) {
        LocalDateTime startTime = LocalDateTime.parse(start.trim());
        LocalDateTime endTime = LocalDateTime.parse(end.trim());
        EventModel event = new EventModel(eventName, startTime, endTime, null, false, autoDecline, null);

        eventCollection.addEvent(event);
    }

    void processCreateEvent(Matcher matcher) {
        Boolean autoDecline;

        String autoDeclineString = matcher.group(1);
        autoDecline = autoDeclineString == null ? false : true;
        String eventName = matcher.group(2);
        String startDateTime = matcher.group(3);
        String endDateTime = matcher.group(4);

        String description = matcher.group(5);
        String location = matcher.group(6);
        boolean privacy = matcher.group(7) != null ? Boolean.parseBoolean(matcher.group(7)) : false;


        LocalDateTime startDate = LocalDateTime.parse(startDateTime);
        LocalDateTime endDate = LocalDateTime.parse(endDateTime);
        if(isEndBeforeStart(startDate, endDate)) {
            System.out.println("End DateTime: " + endDate+" is before Start DateTime: " + startDate);
            System.out.println("Hence Event was not created");
            return;
        }

        EventModel event = new EventModel(eventName, startDate, endDate,description,privacy,autoDecline,location);
        checkForCollision(event,autoDecline,startDate,endDate);
    }

    //create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
    void processCreateRecurringEventN(Matcher matcher) {
        Boolean autoDecline;

        String autoDeclineString = matcher.group(1);
        autoDecline = autoDeclineString == null ? false : true;
        String eventName = matcher.group(2);
        String startDateTime = matcher.group(3);
        String endDateTime = matcher.group(4);
        String weekDay = matcher.group(5);
        Integer numOfTimes = Integer.valueOf(matcher.group(6));

        String description = matcher.group(7);
        String location = matcher.group(8);
        Boolean privacy = matcher.group(9) != null ? Boolean.parseBoolean(matcher.group(9)) : false;

        LocalDateTime startDate = LocalDateTime.parse(startDateTime);
        LocalDateTime endDate = LocalDateTime.parse(endDateTime);
        if(isEndBeforeStart(startDate, endDate)) {
            System.out.println("End Date: " + endDate+" is before Start Date: " + startDate);
            System.out.println("Event was not created");
            return;
        }
        List<Character> recurringWeekDay= StringToDayOfWeek(weekDay);
        RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDate, endDate,description,
                privacy,autoDecline,location,recurringWeekDay,numOfTimes,null);

        checkForCollisionRecurringEvent(recurringEvent,autoDecline);
    }

    // create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>
// NEED TO HANDLE CASES WHERE RECURRING DAYS CANT HAPPEN DUE TO ANOMALIES LIKE 28 day months
    void processCreateRecurringEventEndDate(Matcher matcher) {
        Boolean autoDecline;

        String autoDeclineString = matcher.group(1);
        autoDecline = autoDeclineString == null ? false : true;
        String eventName = matcher.group(2);
        String startDateTime = matcher.group(3);
        String endDateTime = matcher.group(4);
        String weekDay = matcher.group(5);
        String recurrernceEndDate = matcher.group(6);

        String description = matcher.group(7);
        String location = matcher.group(8);
        Boolean privacy = matcher.group(9) != null ? Boolean.parseBoolean(matcher.group(9)) : false;

        List<Character> recurringWeekDay= StringToDayOfWeek(weekDay);
        LocalDateTime startDate = LocalDateTime.parse(startDateTime);
        LocalDateTime endDate = LocalDateTime.parse(endDateTime);


        if(isEndBeforeStart(startDate, endDate)) {
            System.out.println("End Date: " + endDate+" is before Start Date: " + startDate);
            System.out.println("Event was not created");
            return;
        }

        LocalDateTime recurrernceEndDateTime =  LocalDateTime.parse(recurrernceEndDate);

        RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDate, endDate,description,
                privacy,autoDecline,location,recurringWeekDay,null,recurrernceEndDateTime);

        checkForCollisionRecurringEvent(recurringEvent,autoDecline);
    }

    //create event --autoDecline <eventName> on <dateStringTtimeString>
    void processCreateAllDayEvent(Matcher matcher) {
        Boolean autoDecline;

        String autoDeclineString = matcher.group(1);
        autoDecline = autoDeclineString == null ? false : true;
        String eventName = matcher.group(2);
        String startDateTime = matcher.group(3);

        String description = matcher.group(4);
        String location = matcher.group(5);
        Boolean privacy = matcher.group(6) != null ? Boolean.parseBoolean(matcher.group(6)) : false;

        LocalDateTime startDate = LocalDateTime.parse(startDateTime);
        EventModel event = new EventModel(eventName, startDate, null,description,privacy,autoDecline,location);
        checkForCollision(event,autoDecline,event.getEndDateTime(),event.getEndDateTime());
    }

    //create event <eventName> on <dateString> repeats <weekdays> for <N> times
    void processCreateAllDayRecurring(Matcher matcher) {
        Boolean autoDecline;

        String autoDeclineString = matcher.group(1);
        autoDecline = autoDeclineString == null ? false : true;
        String eventName = matcher.group(2);
        String startDateTime = matcher.group(3);

        String weekDay = matcher.group(4);
        Integer numOfTimes = Integer.valueOf(matcher.group(5));

        String description = matcher.group(6);
        String location = matcher.group(7);
        Boolean privacy = matcher.group(8) != null ? Boolean.parseBoolean(matcher.group(8)) : false;

        LocalDateTime startDate = LocalDate.parse(startDateTime).atStartOfDay();
        List<Character> recurringWeekDay= StringToDayOfWeek(weekDay);


        RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDate, null,description,
                privacy,autoDecline,location,recurringWeekDay,numOfTimes,null);
        checkForCollisionRecurringEvent(recurringEvent,autoDecline);
    }

    //create event <eventName> on <dateString> repeats <weekdays> until <dateString>
    void processCreateRecurringWholeDayEventEnd(Matcher matcher) {
        Boolean autoDecline;

        String autoDeclineString = matcher.group(1);
        autoDecline = autoDeclineString == null ? false : true;
        String eventName = matcher.group(2);
        String startDate = matcher.group(3);

        String weekDay = matcher.group(4);
        String recurrernceEndDate = matcher.group(5);

        String description = matcher.group(6);
        String location = matcher.group(7);
        Boolean privacy = matcher.group(8) != null ? Boolean.parseBoolean(matcher.group(8)) : false;

        List<Character> recurringWeekDay= StringToDayOfWeek(weekDay);
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime recurrernceEndDateTime =  LocalDate.parse(recurrernceEndDate).atStartOfDay();


        RecurringEventModel recurringEvent = new RecurringEventModel(eventName, startDateTime, null,description,
                privacy,autoDecline,location,recurringWeekDay,null,recurrernceEndDateTime);
        checkForCollisionRecurringEvent(recurringEvent,autoDecline);
    }

    //edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
    //Pattern editEvent = Pattern.compile("edit\\sevent\\s(.*?)\\s(.*?)\\sfrom\\s" + dateTimeString + "\\sto\\s" + dateTimeString + "\\swith\\s(.*?)$");
    void processEditEvent(Matcher matcher) {
        String property = matcher.group(1);
        String eventName = matcher.group(2);
        String startDate = matcher.group(3);
        String endDate = matcher.group(4);
        String newProperty = matcher.group(5);

        LocalDateTime startDateTime= LocalDateTime.parse(startDate);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate);
        if(isEndBeforeStart(startDateTime, endDateTime)) {
            System.out.println("End Date: " + endDate + " is before Start Date: " + startDate);
            System.out.println("Event was not created");
            return;
        }
        editEvents(property,newProperty,eventName,startDateTime,endDateTime);
    }

    // edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
    //Pattern editAllEvents = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\sfrom\\s" + dateTimeString + "\\swith\\s(.*?)$");
    void processEditEvents(Matcher matcher) {
        String property = matcher.group(1);
        String eventName = matcher.group(2);
        String startDate = matcher.group(3);
        String newProperty = matcher.group(4);

        LocalDateTime startDateTime= LocalDateTime.parse(startDate);
        editEvents(property,newProperty,eventName,startDateTime,null);
    }


    // edit events <property> <eventName> <NewPropertyValue>
    //Pattern editEventsByName = Pattern.compile("edit\\sevents\\s(.*?)\\s(.*?)\\s(.*?)$");
    void processEditEventsByName(Matcher matcher) {
        String property = matcher.group(1);
        String eventName = matcher.group(2);
        String newProperty = matcher.group(3);

        editEvents(property,newProperty,eventName,null,null);
    }

    // print events on <dateString>
    //Pattern printEventsOn = Pattern.compile("print\\sevents\\son\\s" + dateString + "$");
    //Prints a bulleted list of all events on that day along with their start and end time and location
    void processPrintEventsOn(Matcher matcher) {
        String startDate = matcher.group(1);
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime =  startDateTime.toLocalDate().atTime(LocalTime.of(23, 59));

        List<EventModel> printList= new ArrayList<>();
        printList=eventCollection.searchEventByTimeFrame(startDateTime,endDateTime);
        System.out.println(eventCollection.printAssortments(printList));

    }



    // print events from <dateStringTtimeString> to <dateStringTtimeString>
    //Pattern printEventsBetween = Pattern.compile("print\\sevents\\sfrom\\s" + dateTimeString + "\\sto\\s" + dateTimeString + "$");
    void processPrintEventsBetween(Matcher matcher) {
        String startDate = matcher.group(1);
        String endDate = matcher.group(2);
        LocalDateTime startDateTime = LocalDateTime.parse(startDate);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate);

        if(isEndBeforeStart(startDateTime, endDateTime)) {
            System.out.println("End Date: " + endDate + " is before Start Date: " + startDate);
            System.out.println("Event was not created");
            return;
        }

        List<EventModel> printList= new ArrayList<>();
        printList=eventCollection.searchEventByTimeFrame(startDateTime,endDateTime);
        System.out.println(eventCollection.printAssortments(printList));
    }


    // export cal fileName.csv
    //Pattern export = Pattern.compile("export\\scal\\s([a-zA-Z0-9]+\\.csv)");
    void processExport(Matcher matcher) {
        String fileName = matcher.group(1);
        System.out.println("exporting to " + fileName + "...");
        List<EventModel> eventList = eventCollection.getEventList();
        try {
            ExportToCSV.generateCSV(fileName,eventList);
        } catch (Exception e) {
            System.out.println("Could not complete Export: " + e.getMessage());
        }
    }


    // show status on <dateStringTtimeString>
    //Pattern show = Pattern.compile("show\\sstatus\\son\\s" + dateTimeString + "$");
    //Prints busy status if the user has events scheduled on a given day and time, otherwise, available.
    String processShowStatus(Matcher matcher) {
        String startDate = matcher.group(1);
        LocalDateTime startDateTime = LocalDateTime.parse(startDate);
        return (true==eventCollection.checkConflict(startDateTime,startDateTime)?"Busy":"Available");
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


    private void editEvents(String property,String newProperty,String eventName,LocalDateTime StartDateTime,LocalDateTime EndDateTime) {
        if (property.contains("startDateTime")) {
            setStartDateTime(LocalDateTime.parse(newProperty),eventName,StartDateTime,EndDateTime);
        }
        else if (property.contains("endDateTime")) {
            setEndDateTime(LocalDateTime.parse(newProperty),eventName,StartDateTime,EndDateTime);
        }
        else if (property.contains("location")) {
            setLocation(newProperty,eventName,StartDateTime,EndDateTime);
        }
        else if (property.contains("description")) {
            setDescription(eventName,newProperty,StartDateTime,EndDateTime);
        }
        else if (newProperty.contains("private") && property.contains("privacyType")) {
            setIsPrivate(eventName,true,StartDateTime,EndDateTime);
        }
        else if (property.contains("public") && property.contains("privacyType")) {
            setIsPrivate(newProperty,false,StartDateTime,EndDateTime);
        }
        else if (property.contains("eventName")) {
            setEventName(eventName,newProperty,StartDateTime,EndDateTime);
        }

    }


    private Void setStartDateTime(LocalDateTime newProperty,String eventName,LocalDateTime startDateTime,LocalDateTime endDateTime) {
        for (EventModel event : eventCollection.getEventList()) {
            // if start and enddate is specified then we need to check if the event given is between the two dates
            if (event.getEventName().equals(eventName) &&  checkEventBetweenDates(event,startDateTime,endDateTime)) {
                event.setStartDateTime(newProperty);
            }
        }
        return null;
    }
    private Void setEndDateTime(LocalDateTime newProperty,String eventName,LocalDateTime startDateTime,LocalDateTime endDateTime) {
        for (EventModel event : eventCollection.getEventList()) {
            if (event.getEventName().equals(eventName) &&  checkEventBetweenDates(event,startDateTime,endDateTime)) {
                event.setEndDateTime(newProperty);
            }
        }
        return null;
    }

    private Void setLocation(String location,String eventName,LocalDateTime startDateTime,LocalDateTime endDateTime) {
        for (EventModel event : eventCollection.getEventList()) {
            if (event.getEventName().equals(eventName)&&  checkEventBetweenDates(event,startDateTime,endDateTime)) {
                event.setLocation(location);
            }
        }
        return null;
    }

    private Void setEventName(String eventName,String newEventName,LocalDateTime startDateTime,LocalDateTime endDateTime) {
        for (EventModel event : eventCollection.getEventList()) {
            if (event.getEventName().equals(eventName) && checkEventBetweenDates(event,startDateTime,endDateTime)) {
                event.setEventName(newEventName);
            }
        }
        return null;
    }

    private Void setIsPrivate(String eventName,boolean isPrivate,LocalDateTime startDateTime,LocalDateTime endDateTime) {
        for (EventModel event : eventCollection.getEventList()) {
            if (event.getEventName().equals(eventName) && checkEventBetweenDates(event,startDateTime,endDateTime)) {
                event.setIsPrivate(isPrivate);
            }
        }
        return null;
    }


    private Void setDescription(String eventName,String description,LocalDateTime startDateTime,LocalDateTime endDateTime) {
        for (EventModel event : eventCollection.getEventList()) {
            if (event.getEventName().equals(eventName) && checkEventBetweenDates(event,startDateTime,endDateTime)) {
                event.setDescription(description);
            }
        }
        return null;
    }

    private boolean checkEventBetweenDates(EventModel event,LocalDateTime startDateTime, LocalDateTime endDateTime) {

        if(startDateTime==null && endDateTime==null){
            return true;
        }
        else if(startDateTime!=null && endDateTime==null){
            return event.isAfterDateTime(startDateTime);
        }
        else{
            return event.isEventBetweenDates(startDateTime,endDateTime);
        }
    }

    private void checkForCollision(EventModel event,boolean autoDecline,LocalDateTime startDateTime,LocalDateTime endDateTime) {
        if(autoDecline==true)
        {
            if (!eventCollection.checkConflict(startDateTime, endDateTime)) {
                eventCollection.addEvent(event);
                System.out.println("Event \""+event.getEventName()+"\" was created");
            } else {
                System.out.println("Event \""+event.getEventName()+"\" was not created due to conflict");
            }
        }
        else{
            eventCollection.addEvent(event);
            System.out.println("Event \""+event.getEventName()+"\" was created");
        }
    }

    private void checkForCollisionRecurringEvent(RecurringEventModel recurringEvent,boolean autoDecline) {
        boolean conflictDetected = false;
        if(autoDecline==true)
        {
            List<EventModel> recurringEventsList=eventCollection.generateOccurrences(recurringEvent);
            for (EventModel event : recurringEventsList) {
                if (eventCollection.checkConflict(event.getStartDateTime(),event.getEndDateTime())) {
                    System.out.println("Conflict detected, your recurring event \""+recurringEvent.getEventName()+"\" was not created");
                    conflictDetected = true;
                    break;
                }
            }
            if (!conflictDetected) {
                eventCollection.addRecurringEvent(recurringEvent);
                System.out.println("Event \""+recurringEvent.getEventName()+"\" was created");
            }
        }
        else{
            eventCollection.addRecurringEvent(recurringEvent);
            System.out.println("Event \""+recurringEvent.getEventName()+"\" was created");
        }
    }

    public boolean isEndBeforeStart(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (endDateTime.isBefore(startDateTime)) {
            return true;
        }
        return false;
    }
}
