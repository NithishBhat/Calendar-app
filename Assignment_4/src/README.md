# Calendar CLI Commands Documentation

This document outlines the commands used for creating, editing, printing, exporting, and checking the status of events on the calendar. Each command follows a strict structure, including required and optional parameters. Ensure you adhere to the specified format for the commands to work correctly.

---

## Table of Contents

- [Create Event Commands](#create-event-commands)
  - [Single Event](#single-event)
  - [Recurring Event by Number of Occurrences](#recurring-event-for-n-times)
  - [Recurring Event Until a Specific Date](#recurring-event-until-date)
  - [All Day Single Event](#all-day-single-event)
  - [Recurring All Day Event by Number of Occurrences](#recurring-all-day-event-for-n-times)
  - [Recurring All Day Event Until a Specific Date](#recurring-all-day-event-until-date)
- [Edit Event Commands](#edit-event-commands)
  - [Edit a Specific Event](#edit-a-specific-event)
  - [Edit Multiple Events by Date/Time](#edit-multiple-events-by-date-time)
  - [Edit Multiple Events by Name](#edit-multiple-events-by-name)
- [Print Event Commands](#print-event-commands)
  - [Print Events on a Specific Date](#print-events-on-date)
  - [Print Events in a Given Interval](#print-events-in-interval)
- [Export Command](#export-command)
- [Status Command](#status-command)
- [Notes on Formatting](#notes-on-formatting)
- [Modes of Interaction](#modes-of-interaction)
- [Changelog](#changelog)
- [Features](#features)
- [Distribution of Work](#distribution-of-work)

---

## Create Event Commands

### Single Event

Creates a single event in the calendar.

**Command Syntax:**

```bash
create event [--autoDecline] "<eventName>" from <dateStringTtimeString> to <dateStringTtimeString> [description:"Values and Values"] [location:"Values and Values"] [privacy:"(true OR false)"]
```

- **Parameters:**
  - `--autoDecline` (optional): Rejects creation if there is a conflict.
  - `<eventName>`: Must be enclosed in double quotes.
  - `<dateStringTtimeString>`: Format `YYYY-MM-DDThh:mm` (e.g., `2025-10-10T10:00`).
  - `[description:"..."], [location:"..."], [privacy:"true" or "false"]` (optional): Must be in the order shown.

**Examples:**

```bash
create event "hello world" from 2025-10-10T10:00 to 2025-10-10T10:00 description:"test HELLO" location:"MA" privacy:"true"
create event --autoDecline "hello world" from 2025-10-10T10:00 to 2025-10-10T10:00 description:"test HELLO" location:"MA" privacy:"true"
```

---

### Recurring Event for N Times

Creates a recurring event that repeats a specified number of times on specific weekdays.

**Command Syntax:**

```bash
create event [--autoDecline] "<eventName>" from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times [description:"Values and Values"] [location:"Values and Values"] [privacy:"(true OR false)"]
```

- **Parameters:**
  - `<weekdays>`: Sequence of characters representing days (M, T, W, R, F, S, U).
  - `<N>`: Number of occurrences.

**Examples:**

```bash
create event --autoDecline "hello world" from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW for 6 times description:"test" location:"MA" privacy:"true"
create event "hello world" from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW for 6 times
```

---

## Edit Event Commands

### Edit a Specific Event

Change a property of a single event identified by its name and specific start and end date/time.

**Command Syntax:**

```bash
edit event <property> "<eventName>" from <dateStringTtimeString> to <dateStringTtimeString> with "<NewPropertyValue>"
```

**Example:**

```bash
edit event "hello world" from 2025-10-10T12:00 to 2025-10-10T13:00 with "New Event Name"
```

---

## Print Event Commands

### Print Events on Date

Prints a bulleted list of all events on a specific date along with their start and end times and location (if available).

**Command Syntax:**

```bash
print events on <dateString>
```

**Example:**

```bash
print events on 2025-10-10
```

---

## Export Command

Exports the calendar as a CSV file that can be imported into the Google Calendar app. The command will also print the absolute path of the generated CSV file.

**Command Syntax:**

```bash
export cal <fileName.csv>
```

**Example:**

```bash
export cal HELLO.csv
```

---

## Status Command

Prints the busy status if there are events scheduled at a given date and time, otherwise indicates availability.

**Command Syntax:**

```bash
show status on <dateStringTtimeString>
```

**Example:**

```bash
show status on 2025-01-10T10:00
```

---

## Notes on Formatting

- **Mandatory Spaces:** There must be a space between each parameter as indicated in the command structure.
- **Date and Time Formats:**
  - `<dateString>` must follow `YYYY-MM-DD`.
  - `<dateStringTtimeString>` must follow `YYYY-MM-DDThh:mm`.
- **Optional Parameters:** The parameters `--autoDecline`, `description:"..."`, `location:"..."`, and `privacy:"(true OR false)"` are optional. If used, they must be in the exact order specified.
- **Weekdays Format:** For recurring events, `<weekdays>` is a sequence of characters where:
  - `M` = Monday, `T` = Tuesday, `W` = Wednesday, `R` = Thursday, `F` = Friday, `S` = Saturday, `U` = Sunday.
- **Privacy:** Can only be either `true` or `false`.

---

## Modes of Interaction
In order to run the application the user must specify whether to use the interactive CLI 
or the headless application. Before running any modes the user must compile the files from the 
/java directory.

**Command Syntax:**

If using bash:
```bash
javac -d . */*.java
```
or if using shell commands:
```bash
javac -d . (Get-ChildItem -Path . -Filter *.java -Recurse).FullName
```
## Running the .jar file
When running the .jar file you must be in the /res directory.

Headless Mode:
```bash
java -jar Assignment_4.jar --mode headless <file name>
```
**NOTE**:
Files must be adjacent to the .jar when using headless mode. If a file is not present one 
will be created where the user can input commands.

Interactive Mode:
```bash
java -jar Assignment_4.jar --mode interactive
```

## Headless Mode
Allows users to input commands from a .txt file. Users must specify the name of the file and 
follow it with the .txt filetype. Users must end the list of commands with "exit" 
in order to finish the session.

**Command Syntax:**

```bash
java view.EventView.java --mode headless <filename>.txt 
```
or
```bash
java view.EventView --mode headless <filename>.txt 
```
## Interactive Mode
Allows users to enter an interactive command line interface where commands are input line by line
and users get instant feedback from their actions.

**Command Syntax:**

```bash
java view.EventView.java --mode interactive 
```
or
```bash
java view.EventView --mode interactive 
```

## Changelog

Here is a list of changes we made to previous code:
- EventCollectionModel:
  - Added two new methods to handle search queries for calendar functions.
    ```bash 
      public List<EventModel> searchEventByTimeFrameAndName(String eventName, LocalDateTime eventStartDateTime, LocalDateTime eventEndDateTime)
    ```
    Allows for events to be queried by name, start datetime, and end datetime. 
    Returns a list of events with the same name from a certain start dateTime until a certain 
    end dateTime. 
    ```bash
      public EventModel searchEventByStartTimeAndName(String eventName,LocalDateTime eventStartDateTime)
    ```
    Returns the first event with a certain name and start dateTime. These methods are necessary for
    getting recurring events. 
  - Moved CheckConflict method to EventCollectionModel class since checking for conflicts 
    is a part of event management in a calendar. EventModel handles the logic for a single event,
    this migration better accommodates Separation of Concerns and Encapsulation.

- Command Parser:
  - Update Regex Patterns to accommodate new Calendar Commands, necessary for parsing and executing new calendar commands.
    ```bash
      patterns.put("useCalendar", Pattern.compile(
            "\\s*use\\s+"
              + "calendar"
              + "\\s+--name\\s+\"(.*?)\"\\s*", Pattern.CASE_INSENSITIVE));

      patterns.put("createCalendar", Pattern.compile(
              "\\s*create\\s+"
              + "calendar"
              + "\\s+--name\\s+(\\S+)"
              + "\\s+--timezone\\s+\"([^\"]+)\""
              + "\\s*$", Pattern.CASE_INSENSITIVE));
  
      patterns.put("editCalendar", Pattern.compile(
              "\\s*edit\\s+calendar"
              + "\\s+--name\\s+\"(.*?)\""
              + "\\s+--property\\s+([a-zA-Z0-9_-]+)"
              + "\\s+\"(.*?)\"\\s*", Pattern.CASE_INSENSITIVE));
  
      patterns.put("processCopyEventOn", Pattern.compile(
              "\\s*copy\\s+"
              + "event\\s+\"(.*?)\"\\s"
              + "+on\\s+"+ dateTimeString
              + "\\s+--target\\s+\"(.*?)\"\\s"
              + "+to\\s+"
              + dateTimeString +"\\s*", Pattern.CASE_INSENSITIVE));
  
      patterns.put("copyEventsOn", Pattern.compile(
              "\\s*copy\\s+events\\s+on\\s+" + dateString +
              "\\s+--target\\s+\"([^\"]+)\"\\s+to\\s+" + dateString +
              "\\s*", Pattern.CASE_INSENSITIVE));
  
      patterns.put("copyEventsBetween", Pattern.compile(
              "\\s*copy\\s+events\\s+between\\s+" + dateString +
              "\\s+and\\s+" + dateString +
              "\\s+--target\\s+\"([^\"]+)\"\\s+to\\s+" + dateString +
              "\\s*", Pattern.CASE_INSENSITIVE));
    ```
    These 6 new patterns accommodate the 6 new commands implemented with the calendar functionality.
    Without this update to the Regex patterns then whenever the user would try to use a command relating
    to the functionality of the calendar the program would return an error because the command string would be unknown.
  - Update error handling to accommodate new commands. If a user inputs the wrong syntax for a command it will show where the error is.
    This improves user-friendliness and error handling.
    ```bash
      if (input.matches("\\s*edit\\s+calendar\\s+.*")) {
        if (!input.contains("--name")) {
          return "Missing '--name' argument to specify the calendar name in edit calendar command";
        }

        if (!input.contains("--property")) {
          return "Missing '--property' argument to specify the property to edit in edit calendar command";
        }

        if (!input.contains("\"")) {
          return "Missing value for property in edit calendar command";
        }

        if (!(input.contains("--property \"name\"") || input.contains("--property \"time-zone\""))) {
          return "Invalid property for calendar. Valid properties are: name, time-zone";
        }

        if (!input.matches(".*\"([^\"]+)\".*")) {
          return "Missing value for the property in edit calendar command";
        }
    }
    ```
  
- Event Controller:
  - Update commandMap with new calendar commands.
    ```bash
      commandMap.put("createCalendar", () -> eventHandler.processCreateCalendar(matcher));
      commandMap.put("editCalendar", () -> eventHandler.processEditCalendar(matcher));
      commandMap.put("useCalendar", () -> eventHandler.processUseCalendar(matcher));
      commandMap.put("processCopyEventOn", () -> eventHandler.processCopyEventOn(matcher));
      commandMap.put("copyEventsOn", () -> eventHandler.processCopyEventsOn(matcher));
      commandMap.put("copyEventsBetween", () -> eventHandler.processCopyCalenderBetweenDates(matcher));
    ```
    Added these 6 commands to the commandMap hashmap, so that after a command is parsed the correct
    method in the eventHandler is called.

- Event Handler: 
  - Update with new calendar commands.
    ```bash
      public void useCalender(String calName)
      public void processCopyEventOn
      public void processCopyCalenderBetweenDates(Matcher matcher)
      public void processCopyEventsOn(Matcher matcher) 
      public void processCopyCalendarBetweenDatesWithName(String eventName, LocalDateTime fromDate,
                                                        LocalDateTime toDate,
                                                        String sourceCalendarName,
                                                        LocalDateTime targetStartDate,
                                                        String targetCalendarName)
      public void processEditCalendar(Matcher matcher) 
      public void processUseCalendar(Matcher matcher) 
      void processCreateCalendar(Matcher matcher)
      void createCalendar(string CalName, zoneId)
      public EventCollectionModel getEventCollection()
      void processPrintCalendars()
    ```
    These methods were added to the eventHandler to allow for the creating, managing, and editing of 
    calendars as well as the copying of individual events. These methods are necessary for the eventHandler to
    delegate the correct responsibilities to the Model package. 
  
  - EventHandler now constructs a default calendar so that if a user tried to create an event without using a
    specific calendar it would be added to the default calendar. The default calendar prevents 
  errors when users create events without specifying a calendar, improving usability and user-friendliness.
    ```bash
      public EventHandler() {
        calenderCollection= new CalenderCollection();
        createCalender("\"default\"",ZoneId.of("UTC"));
        eventCollection=calenderCollection.getEventCollection("default");
      }
    ```
  - Methods now throw new IllegalArgumentException when an error occurs. Previously the program will 
    System.out.println() the error message. This is necessary for improved and robust
  error handling, the program now cannot ignore the error.

## Features
All Features work as intended in the readme.

## Distribution of Work
Phillip did:

Nithish did:
    
