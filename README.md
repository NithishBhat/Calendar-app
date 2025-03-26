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
create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString> [description:"Values and Values"] [location:"Values and Values"] [privacy:"(true OR false)"]
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
create event --autoDecline <eventName> on <dateStringTtimeString> [description:"Values and Values"] [location:"Values and Values"] [privacy:"(true OR false)"]
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
create event <eventName> on <dateString> repeats <weekdays> until <dateString> [description:"Values and Values"] [location:"Values and Values"] [privacy:"(true OR false)"]
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

## MODEs of Interaction
## HeadlessMode
## interactive Mode
