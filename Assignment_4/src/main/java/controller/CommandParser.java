package controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The CommandParser class is responsible for parsing and validating various
 * event-related commands.It uses regular expressions to match commands
 * and provides diagnostic messages for incorrect inputs.
 */
public class CommandParser {

  private Map<String, Pattern> patterns = new HashMap<>();

  /**
   * Constructs a CommandParser and initializes the command patterns for event creation, editing,
   * printing, exporting, and showing status commands.
   */
  public CommandParser() {
    String dateTimeString = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})";
    String dateString = "(\\d{4}-\\d{2}-\\d{2})";
    String weekDay = "([MTWRFSU]{1,7})";
    String optionalParamsPattern = "\\s*(?:description:\\s*\"(.*?)\")?\\s*" +
            "(?:location:\\s*\"(.*?)\")?\\s*(?:privacy:\\s*\"(true|false)\")?";

    //Calendar commands
    //use calendar --name <name-of-calendar>
    patterns.put("useCalendar", Pattern.compile(
            "\\s*use\\s+"
                    + "calendar"
                    + "\\s+--name\\s+\"(.*?)\"\\s*", Pattern.CASE_INSENSITIVE));

    patterns.put("createCalendar", Pattern.compile(
            "\\s*create\\s+"
            + "calendar"
            + "\\s+--name\\s+(\\S+)"
            + "\\s+--timezone\\s+\"(.*?)\""
            + "\\s*$", Pattern.CASE_INSENSITIVE));

    //edit calendar --name <calName> --property <property-name> <property-value>
    patterns.put("editCalendar", Pattern.compile(
            "\\s*edit\\s+"
            + "calendar"
            + "\\s+--name\\s+\"(.*?)\""
            + "\\s+--property\\s+\"(.*?)\""
            + "\\s+\"(.*?)\"\\s*$", Pattern.CASE_INSENSITIVE));

    //copy event <eventName> on <dateTimeString> --target <calName> to <dateTimeString>
    patterns.put("processCopyEventOn", Pattern.compile(
            "\\s*copy\\s+"
            + "event\\s+\"(.*?)\"\\s"
            + "+on\\s+"+ dateTimeString
            + "\\s+--target\\s+\"(.*?)\"\\s"
            + "+to\\s+"
            + dateTimeString +"\\s*$", Pattern.CASE_INSENSITIVE));

    //copy events on <dateString> --target <calName> to <dateString>
    patterns.put("copyEventsOn", Pattern.compile(
            "\\s*copy\\s+events\\s+on\\s+" + dateString +
            "\\s+--target\\s+\"([^\"]+)\"\\s+to\\s+" + dateString +
            "\\s*$", Pattern.CASE_INSENSITIVE));

    //copy events between <dateString> and <dateString> --target <calName> to <dateString>
    patterns.put("copyEventsBetween", Pattern.compile(
            "\\s*copy\\s+events\\s+between\\s+" + dateString +
            "\\s+and\\s+" + dateString +
            "\\s+--target\\s+\"([^\"]+)\"\\s+to\\s+" + dateString +
            "\\s*$", Pattern.CASE_INSENSITIVE));

    //Create Events pattern
    patterns.put("createEventPattern", Pattern.compile("\\s*create\\s+"
            + "event(\\s+--autoDecline)?\\s+\"(.*?)\"\\s+from\\s+"
            + dateTimeString
            + "\\s+to\\s+"
            + dateTimeString
            + optionalParamsPattern
            + "\\s*$", Pattern.CASE_INSENSITIVE));
    patterns.put("createRecurringEventN", Pattern.compile("\\s*create\\s+"
            + "event(\\s+--autoDecline)?\\s+\"(.*?)\"\\s+from\\s+"
            + dateTimeString
            + "\\s+to\\s+"
            + dateTimeString
            + "\\s+repeats\\s+"
            + weekDay
            + "\\s+for\\s+(\\d+)\\s+times\\s*"
            + optionalParamsPattern
            + "\\s*$", Pattern.CASE_INSENSITIVE));
    patterns.put("createRecurringEventEndDate", Pattern.compile("\\s*create\\s+"
            + "event(\\s+--autoDecline)?\\s+\"(.*?)\"\\s+from\\s+"
            + dateTimeString
            + "\\s+to\\s+"
            + dateTimeString
            + "\\s+repeats\\s+"
            + weekDay
            + "\\s+until\\s+"
            + dateTimeString
            + "\\s*"
            + optionalParamsPattern
            + "\\s*$", Pattern.CASE_INSENSITIVE));
    patterns.put("createWholeDayEvent", Pattern.compile("\\s*create\\s+"
            + "event(\\s+--autoDecline)?\\s+\"(.*?)\"\\s+on\\s+"
            + dateTimeString
            + "\\s*"
            + optionalParamsPattern
            + "\\s*$", Pattern.CASE_INSENSITIVE));
    patterns.put("createRecurringWholeDayEventN", Pattern.compile("\\s*create\\s+"
            + "event(\\s+--autoDecline)?\\s+\"(.*?)\"\\s+on\\s+"
            + dateString +
            "\\s+repeats\\s+"
            + weekDay
            + "\\s+for\\s+(\\d+)\\s+times\\s*"
            + optionalParamsPattern
            + "\\s*$", Pattern.CASE_INSENSITIVE));
    patterns.put("createRecurringWholeDayEventEnd", Pattern.compile("\\s*create\\s+"
            + "event(\\s+--autoDecline)?\\s+\"(.*?)\"\\s+on\\s+"
            + dateString
            + "\\s+repeats\\s+"
            + weekDay
            + "\\s+until\\s+"
            + dateString
            + "\\s*"
            + optionalParamsPattern
            + "\\s*$", Pattern.CASE_INSENSITIVE));

    // Edit event patterns
    patterns.put("editEvent", Pattern.compile("\\s*edit\\s+"
            + "event\\s+(\\S+)\\s+\"(.*?)\"\\s+from\\s+"
            + dateTimeString
            + "\\s+to\\s+"
            + dateTimeString
            + "\\s+with\\s+\"(.*?)\"\\s*$", Pattern.CASE_INSENSITIVE));
    patterns.put("editEvents", Pattern.compile("\\s*edit\\s+"
            + "events\\s+(\\S+)\\s+\"(.*?)\"\\s+from\\s+"
            + dateTimeString
            + "\\s+with\\s+\"(.*?)\"\\s*$", Pattern.CASE_INSENSITIVE));
    patterns.put("editEventsByName", Pattern.compile("\\s*edit\\s+"
            + "events\\s+(\\S+)\\s+\"(.+?)\"\\s+\"(.*?)\"\\s*$", Pattern.CASE_INSENSITIVE));

    // Print and export patterns
    patterns.put("printEventsOn", Pattern.compile("\\s*print\\s+events\\s+on\\s+"
            + dateString
            + "\\s*$", Pattern.CASE_INSENSITIVE));
    patterns.put("printEventsBetween", Pattern.compile("\\s*print\\s+events\\s+from\\s+"
            + dateTimeString
            + "\\s+to\\s+"
            + dateTimeString
            + "\\s*$", Pattern.CASE_INSENSITIVE));
    patterns.put("export", Pattern.compile("\\s*export\\s+cal\\s+"
            + "([a-zA-Z0-9]+\\.csv)\\s*", Pattern.CASE_INSENSITIVE));

    // show status on <dateStringTtimeString>
    patterns.put("show", Pattern.compile("\\s*show\\s+status\\s+on\\s+"
            + dateTimeString
            + "\\s*$", Pattern.CASE_INSENSITIVE));
  }

  /**
   * Parses the input command and matches it to one of the predefined patterns.
   *
   * @param input The input string representing the command.
   * @return A Matcher object containing the matched result if any, or null if no match is found.
   */
  public Matcher parseCommand(String input) {
    for (Map.Entry<String, Pattern> entry : patterns.entrySet()) {
      Matcher matcher = entry.getValue().matcher(input);
      if (matcher.matches()) {
        return matcher;
      }
    }

    String diagnosticMessage = getDiagnosticMessage(input);
    throw new IllegalArgumentException("Invalid command: " + diagnosticMessage);
  }

  /**
   * Returns a map of all the predefined patterns used for command parsing.
   *
   * @return A map of string keys and pattern values.
   */
  public Map<String, Pattern> getPatterns() {
    return patterns;
  }


  /**
   * Generates a diagnostic message based on the invalid input command.
   *
   * @param input The invalid input command.
   * @return A string diagnostic message describing the error.
   */
  public String getDiagnosticMessage(String input) {
    String[] words = input.trim().split("\\s+");

    String firstWord = words[0].toLowerCase();
    switch (firstWord) {
      case "create":
        return diagnoseCreateCommand(input);
      case "edit":
        return diagnoseEditCommand(input);
      case "print":
        return diagnosePrintCommand(input);
      case "export":
        return diagnoseExportCommand(input);
      case "show":
        return diagnoseShowCommand(input);
      default:
        return "Unknown command '" + firstWord + "'. Available commands are:"
                + " create, edit, print, export, show";
    }
  }

  /**
   * Diagnoses the errors in the 'create' command format.
   *
   * @param input The 'create' command input string.
   * @return A string diagnostic message describing the error.
   */
  private String diagnoseCreateCommand(String input) {
    if (!input.contains("event")) {
      return "Missing 'event' keyword in create command";
    }
    if (input.contains("from") && !input.contains("to")) {
      return "Missing 'to' keyword in time range Command";
    }
    if (!input.contains("from") && !input.contains("on")) {
      return "Missing 'from' or 'on' in event Command";
    }
    if (!input.contains("\"")) {
      return "Missing event name in quotes";
    }
    if (input.contains("repeats")) {
      if (!input.contains("for") && !input.contains("until")) {
        return "Missing 'for' or 'until' in recurring event Command";
      }
      if (!input.matches(".*repeats\\s+[MTWRFSU]+\\s+.*")) {
        return "Invalid weekday format after 'repeats'. Use M (Monday), "
                + "T (Tuesday), W (Wednesday), etc.";
      }
    }


    if (input.contains("from") || input.contains("to") || input.contains("on")) {
      if (!input.matches(".*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.*")
              && !input.matches(".*\\d{4}-\\d{2}-\\d{2}.*")) {
        return "Invalid date/time format. Use YYYY-MM-DDThh:mm"
                + " for date and time or YYYY-MM-DD for date only";
      }
    }

    return "Invalid create command format. Examples:\n" + "" +
            "  create event \"Meeting\" from 2023-03-15T14:00 to 2023-03-15T15:00\n"
            + "  create event \"Birthday\" on 2023-03-15\n" + "  create event \"Weekly Meeting\""
            + " from 2023-03-15T14:00 to 2023-03-15T15:00 repeats MW for 10 times";
  }

  /**
   * Diagnoses the errors in the 'edit' command format.
   *
   * @param input The 'edit' command input string.
   * @return A string diagnostic message describing the error.
   */
  private String diagnoseEditCommand(String input) {
    if (!input.contains("event") && !input.contains("events")) {
      return ("Missing 'event' or 'events' keyword in edit command");
    }

    else if (input.matches("\\s*edit\\s+event\\s+.*")) {
      if (!input.contains("from")) {
        return ("Missing 'from' keyword in edit event command");
      }

      if (input.contains("from") && !input.contains("to")) {
        return "Missing 'to' keyword in edit event command";
      }

      if (!input.contains("with")) {
        return "Missing 'with' keyword for new property value in edit event command";
      }

      if (!input.matches(".*\".*\".*")) {
        return ("Missing event name in quotes");
      }

    }

    else if (input.matches("\\s*edit\\s+events\\s+.*")) {
      if (!input.matches(".*\".*\".*")) {
        return ("Missing event name in quotes");
      }

      if (input.contains("from") && !input.contains("with")) {
        return ("Missing 'with' keyword after 'from <dateTime>' in edit events command");
      }
    }



    String[] validProperties = {"startDateTime", "endDateTime", "location",
        "description", "privacyType", "eventName"};
    boolean hasValidProperty = false;
    for (String prop : validProperties) {
      if (input.contains(prop)) {
        hasValidProperty = true;
        break;
      }
    }
    if (!hasValidProperty && input.split("\\s+").length > 2) {
      return "Invalid property. Valid properties are: startDateTime, endDateTime,"
              + " location, description, privacyType, eventName";
    }

    return "Invalid edit command format. Examples:\n" + ""
            + "  edit event eventName \"Meeeeeeeting 1\" from 2023-03-15T14:00 to "
            + "2023-03-15T15:00 with \"New Meeting\"\n" + "  edit events location \"Meeting 2\" "
            + "from 2023-03-15T14:00 with \"Conference Room\"\n" + "  edit events description"
            + " \"Meeting\" \"Weekly status update\""
            + "  edit calendar --name \"test\" --property \"name\" \"CS5010\"\n"
            + "  edit calendar --name \"test2\" --property \"time-zone\" \"America/New_York\"";
  }

  /**
   * Diagnoses the errors in the 'print' command format.
   *
   * @param input The 'print' command input string.
   * @return A string diagnostic message describing the error.
   */
  private String diagnosePrintCommand(String input) {
    if (!input.contains("events")) {
      return "Missing 'events' keyword in print command";
    }

    if (input.contains("on")) {
      if (!input.matches(".*\\d{4}-\\d{2}-\\d{2}.*")) {
        return "Invalid date format after 'on'. Use YYYY-MM-DD";
      }
    } else if (input.contains("from")) {
      if (!input.contains("to")) {
        return "Missing 'to' keyword in time range specification";
      }
      if (!input.matches(".*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.*")) {
        return "Invalid date/time format. Use YYYY-MM-DDThh:mm";
      }
    } else {
      return "Missing 'on' or 'from...to' in print command";
    }

    return "Invalid print command format. Examples:\n" + "  print events on 2023-03-15\n"
            + "  print events from 2023-03-15T09:00 to 2023-03-15T17:00";
  }

  /**
   * Diagnoses errors in the 'export' command format.
   *
   * @param input The 'export' command input string.
   * @return A diagnostic message describing the error if the command is invalid.
   */

  private String diagnoseExportCommand(String input) {
    if (!input.contains("cal")) {
      return "Missing 'cal' keyword in export command";
    }

    if (!input.matches(".*\\.csv\\s*$")) {
      return "Missing CSV filename or invalid file extension (must end with .csv)";
    }

    return "Invalid export command format. Example:\n" + "  export cal calendar.csv";
  }

  /**
   * Diagnoses errors in the 'show' command format.
   *
   * @param input The 'show' command input string.
   * @return A diagnostic message describing the error if the command is invalid.
   */
  private String diagnoseShowCommand(String input) {
    if (!input.contains("status")) {
      return "Missing 'status' keyword in show command";
    }

    if (!input.contains("on")) {
      return "Missing 'on' keyword in show status command";
    }

    if (!input.matches(".*\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.*")) {
      return "Invalid date/time format after 'on'. Use YYYY-MM-DDThh:mm";
    }

    return "Invalid show command format. Example:\n" + "  show status on 2023-03-15T14:00";
  }
}
