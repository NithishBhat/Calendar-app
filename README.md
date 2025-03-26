# Calendar CLI Commands Documentation

This document outlines the commands used for creating, editing, printing, exporting, and checking the status of events on the calendar. Each command follows a strict structure, including required and optional parameters. Ensure you adhere to the specified format for the commands to work correctly.

---

### Table of Contents
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

- [Calender Commands](#create-event-commands)
  - [Create calendar](#create-event-commands)
  - [Use calendar](#create-event-commands)
  - [Copy Specific Event](#create-event-commands)
  - [Copy All Events On A Day](#create-event-commands)
  - [Copy All Events In Interval](#create-event-commands)
  - [Edit Calendar](#create-event-commands)

---

## Create Event Commands

### Single Event

Creates a single event in the calendar. Note \<dateString> is a string of the form "YYYY-MM-DD" and \<timeString> is a string of the form "hh:mm".

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
  - `--autoDecline` (optional): Rejects creation if there is a conflict.
  - `<eventName>`: Must be enclosed in double quotes.
  - `<dateStringTtimeString>`: Format `YYYY-MM-DDThh:mm` (e.g., `2025-10-10T10:00`).
  - `[description:"..."], [location:"..."], [privacy:"true" or "false"]` (optional): Must be in the order shown.

**Examples:**

```bash
create event --autoDecline "hello world" from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW for 6 times description:"test" location:"MA" privacy:"true"
create event "hello world" from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW for 6 times
```

---

### Recurring All Day Event Until a Specific Date

Creates a recurring event until a specific date (inclusive).

**Command Syntax:**

```bash
create event --autoDecline "<eventName>" from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString> [description:"Values and Values"] [location:"Values and Values"] [privacy:"(true OR false)"]
```

- **Parameters:**
  - `<weekdays>`: Sequence of characters representing days (M, T, W, R, F, S, U).
  - `<N>`: Number of occurrences.
  - `--autoDecline` (optional): Rejects creation if there is a conflict.
  - `<eventName>`: Must be enclosed in double quotes.
  - `<dateStringTtimeString>`: Format `YYYY-MM-DDThh:mm` (e.g., `2025-10-10T10:00`).
  - `[description:"..."], [location:"..."], [privacy:"true" or "false"]` (optional): Must be in the order shown.

**Examples:**

```bash
create event --autoDecline "hello world" from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW until  2025-10-10T10:00 description:"test" location:"MA" privacy:"true"
create event "hello world" from 2025-10-10T10:00 to 2025-12-10T10:00 repeats MTW until 2025-10-10T10:00
```

---
### All Day Single Event
Creates a single all day event

**Command Syntax:**

```bash
create event --autoDecline "<eventName>" on <dateStringTtimeString> [description:"Values and Values"] [location:"Values and Values"] [privacy:"(true OR false)"]
```

- **Parameters:**
  - `--autoDecline` (optional): Rejects creation if there is a conflict.
  - `<eventName>`: Must be enclosed in double quotes.
  - `<dateStringTtimeString>`: Format `YYYY-MM-DDThh:mm` (e.g., `2025-10-10T10:00`).
  - `[description:"..."], [location:"..."], [privacy:"true" or "false"]` (optional): Must be in the order shown.

**Examples:**

```bash
create event --autoDecline "hello world" on 2025-12-10T10:00  description:"test" location:"MA" privacy:"true"
create event --autoDecline "hello world" on 2025-12-10T10:00
```

---
### Recurring All Day Event by Number of Occurrences

Creates a recurring all day event until a specific date (inclusive).

**Command Syntax:**

```bash
create event "<eventName>" on <dateString> repeats <weekdays> until <dateString> [description:"Values and Values"] [location:"Values and Values"] [privacy:"(true OR false)"]
```

- **Parameters:**
  - `--autoDecline` (optional): Rejects creation if there is a conflict.
  -  `<weekdays>`: Sequence of characters representing days (M, T, W, R, F, S, U).
  - `<eventName>`: Must be enclosed in double quotes.
  - `<dateStringTtimeString>`: Format `YYYY-MM-DDThh:mm` (e.g., `2025-10-10T10:00`).
  - `[description:"..."], [location:"..."], [privacy:"true" or "false"]` (optional): Must be in the order shown.

**Examples:**

```bash
create event --autoDecline "hello world" on 2025-12-10T10:00  repeats MTW until  2025-10-10T10:00  description:"test" location:"MA" privacy:"true"
create event --autoDecline "hello world" on 2025-12-10T10:00 repeats MTW until  2025-10-10T10:00
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

### Edit Multiple Events by Date/Time

Changes the property (e.g., name) of all events starting at a specific date/time and have the same event name.

**Command Syntax:**

```bash
edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
```

**Example:**

```bash
edit event eventName "hello world" from 2025-10-10T12:00 with "New Event Name"
```

---

### Edit Multiple Events by Name

Change the property (e.g., name) of all events with the same event name.

**Command Syntax:**

```bash
edit events <property> <eventName> <NewPropertyValue>
```

**Example:**

```bash
edit events eventName "hello world"  "New Event Name"
```
---

## Print Event Commands

### Print Events on Date

Prints a bulleted list of all events on that day along with their start and end time and location (if any).

**Command Syntax:**

```bash
print events on <dateString>
```

**Example:**

```bash
print events on 2025-10-10
```

---

### Print Events in a Given Interval

Prints a bulleted list of all events on that day along with their start and end time and location (if any).

**Command Syntax:**

```bash
print events from <dateStringTtimeString> to <dateStringTtimeString>
```

**Example:**

```bash
print events from 2025-10-10T12:00 to 2025-10-10T13:00 
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
  - `--autoDecline` (optional): Rejects creation if there is a conflict.
  -  `<weekdays>`: Sequence of characters representing days (M, T, W, R, F, S, U).
  - `<eventName>`: Must be enclosed in double quotes.
  - `<dateStringTtimeString>`: Format `YYYY-MM-DDThh:mm` (e.g., `2025-10-10T10:00`).
  - `[description:"..."], [location:"..."], [privacy:"true" or "false"]` (optional): Must be in the order shown.

---


### Create Calendar

This command will create a new calendar with a unique name and timezone as specified by the user. The expected timezone format is the IANA Time Zone Database format Links to an external site. . In this format the timezone is specified as "area/location". Few examples include "America/New_York", "Europe/Paris", "Asia/Kolkata", "Australia/Sydney", "Africa/Cairo", etc. The command is invalid if the user provides a non-unique calendar name or an unsupported timezone.

**Command Syntax:**

```bash
create calendar --name <calName> --timezone area/location
```

**Example:**

```bash
create calendar --name "default" --timezone "Africa/Cairo"
```

### Use Calendar

A user can create/edit/print/export events in the context of a calendar. They can use this command to set the calendar context. Note this means that the commands in the previous iteration only make sense when a calendar is in use (i.e. some calendar must be in use for them to work). Otherwise, they are invalid.

**Command Syntax:**

```bash
use calendar --name <name-of-calendar>
```

**Example:**

```bash
use calendar --name "Hello"
```

### Copy Specific Event

A user can create/edit/print/export events in the context of a calendar. They can use this command to set the calendar context. Note this means that the commands in the previous iteration only make sense when a calendar is in use (i.e. some calendar must be in use for them to work). Otherwise, they are invalid.

**Command Syntax:**

```bash
copy event <eventName> on <dateStringTtimeString> --target <calendarName> to <dateStringTtimeString>
```

**Example:**

```bash
copy event "Hello" on 2025-10-10T13:00  --target "Hello" to 2025-10-10T13:00 
```

### Copy All Events On A Day

The command is used to copy a specific event with the given name and start date/time from the current calendar to the target calendar to start at the specified date/time. The "to" date/time is assumed to be specified in the timezone of the target calendar. The copied event must be in the same timezone as the target calendar.

**Command Syntax:**

```bash
copy events on <dateString> --target <calendarName> to <dateString>
```

**Example:**

```
copy events on 2025-10-10 --target "hello" to 2025-10-10
```
### Copy All Events In Interval

The command has the same behavior as the other copy commands, except it copies all events scheduled in the specified date interval. The date string in the target calendar corresponds to the start of the interval. The endpoint dates of the interval are inclusive.

**Command Syntax:**

```bash
copy events between <dateString> and <dateString> --target <calendarName> to <dateString>
```

**Example:**

```bash
copy events between 2025-10-10 and 2025-11-10 --target "Hello" to 2025-10-10
```

### Edit Calendar

This command is used to change/modify an existing property ( name or timezone ) of the calendar. The command is invalid if the property being changed is absent or the value is invalid in the context of the property.

**Command Syntax:**

```bash
edit calendar --name <name-of-calendar> --property <property-name> <new-property-value>
```

**Example:**

```bash
edit calendar --name "test" --property "name" "CS5010"
edit calendar --name "test2" --property "time-zone" "America/New_York" 
```
---

## Notes on Formatting

- **Mandatory Spaces:** There must be a space between each parameter as indicated in the command structure.
- **Date and Time Formats:**
  - `<dateString>` must follow `YYYY-MM-DD`.
  - `<dateStringTtimeString>` must follow `YYYY-MM-DDThh:mm`.
  - `<calendarname>`: Must be enclosed in double quotes.
  - `<dateStringTtimeString>`: Format `YYYY-MM-DDThh:mm` (e.g., `2025-10-10T10:00`).
  - '<property>' and '<new-property-value>' must be in quotes.




## Modes of Interaction
In order to run the application the user must specify whether to use the interactive CLI 
or the headless application. Before running any modes the user must compile the files from the 
/java directory. You must be in the /java directory when running commands.
**Command Syntax:**
```bash
javac -d . */*.java
```
## HeadlessMode
Allows users to input commands from a .txt file. Users must specify the name of the file and 
follow it with the .txt filetype. Users must end the list of commands with "exit" 
in order to finish the session.

**Command Syntax:**

```bash
java view.EventView.java --mode headless <filename>.txt 
```
## interactive Mode
Allows users to enter an interactive command line interface where commands are input line by line
and users get instant feedback from their actions.

**Command Syntax:**

```bash
java view.EventView.java --mode interactive 
```

## Distribution
Nithish did the 
  - Event handler
  - Recurring events model, events model
  - Model tests
  - 

Phillip did 
  - Command parser
  - event controller
  - export to csv
  - event collection model
  - event view
  - view test
