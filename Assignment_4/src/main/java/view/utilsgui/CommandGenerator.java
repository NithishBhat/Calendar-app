package view.utilsgui;

import java.util.Map;

/**
 * This class is responsible for generating calendar command strings
 * based on form data collected in the UI.
 */
public class CommandGenerator {

  /**
   * Generate create calendar command
   */
  public String createCalendarCommand(Map<String, String> values) {
    String calendarName = values.get("Calendar Name:");
    String timezone = values.get("Time Zone (area/location):");

    return String.format("create calendar --name %s --timezone \"%s\"", calendarName, timezone);
  }

  /**
   * Generate edit calendar command
   */
  public String editCalendarCommand(Map<String, String> values) {
    String calendarName = values.get("Calendar Name:");
    String property = values.get("Property to Edit:");
    String newValue = values.get("New Value:");

    return String.format("edit calendar --name \"%s\" --property \"%s\" \"%s\"",
            calendarName, property, newValue);
  }

  /**
   * Generate create event command based on the UI form inputs.
   * Constructs commands in the proper format for creating events and recurring events.
   *
   * @param values Map of form field values from the UI
   * @return The properly formatted command string
   */
  public String createEventCommand(Map<String, String> values) {
    StringBuilder command = new StringBuilder("create event");

    // Get values from the form
    String eventName = values.get("Event Name");
    boolean autoDecline = Boolean.parseBoolean(values.get("AutoDecline"));
    boolean allDay = Boolean.parseBoolean(values.get("AllDay"));
    boolean recurring = Boolean.parseBoolean(values.get("Recurring"));

    // Add auto decline if needed
    if (autoDecline) {
      command.append(" --autoDecline");
    }

    // Add event name
    command.append(" \"").append(eventName).append("\"");

    // Determine if this is an all-day event or a timed event
    if (allDay) {
      String startDate = values.get("StartDate");
      command.append(" on ").append(startDate);
    } else {
      String startDate = values.get("StartDate");
      String startTime = values.get("StartTime");
      String endDate = values.get("EndDate");
      String endTime = values.get("EndTime");

      command.append(" from ")
              .append(startDate).append("T").append(startTime)
              .append(" to ")
              .append(endDate).append("T").append(endTime);
    }

    // Handle recurring events
    if (recurring) {
      // Build weekdays string (M, T, W, R, F, S, U)
      StringBuilder weekdays = new StringBuilder();

      // Check each day in order - these keys exactly match the FormField keys from the UI
      String[] daySymbols = {"M", "T", "W", "R", "F", "S", "U"};
      for (String symbol : daySymbols) {
        String key = "RepeatDay_" + symbol;
        if (Boolean.parseBoolean(values.get(key))) {
          weekdays.append(symbol);
        }
      }

      // Only add recurrence pattern if at least one day is selected
      if (weekdays.length() > 0) {
        command.append(" repeats ").append(weekdays);

        // Check if it's repeat N times or until date
        // For radio buttons, empty string means not selected, any value means selected
        String repeatNTimes = values.get("RepeatNTimesRB");
        String repeatUntil = values.get("RepeatUntilRB");

        // Add some debug output to check values
        System.out.println("RepeatNTimesRB value: [" + repeatNTimes + "]");
        System.out.println("RepeatUntilRB value: [" + repeatUntil + "]");
        System.out.println("Selected radio button: " +
                (Boolean.parseBoolean(repeatNTimes) ? "N Times" :
                        Boolean.parseBoolean(repeatUntil) ? "Until Date" : "None"));

        if (repeatNTimes != null && (repeatNTimes.equals("true") || repeatNTimes.equals("selected"))) {
          // For N times
          command.append(" for ").append(values.get("RepeatCount")).append(" times");
        } else if (repeatUntil != null && (repeatUntil.equals("true") || repeatUntil.equals("selected"))) {
          // Until date
          String untilDate = values.get("RepeatUntilDate");

          if (allDay) {
            // For all-day events, just use date
            command.append(" until ").append(untilDate);
          } else {
            // For timed events, include time from end time
            command.append(" until ").append(untilDate).append("T").append(values.get("EndTime"));
          }
        } else {
          // Default to repeat N times if neither option is explicitly selected
          command.append(" for ").append(values.get("RepeatCount")).append(" times");
        }
      }
    }

    // Add optional parameters if present
    String description = values.get("Description");
    if (description != null && !description.isEmpty()) {
      command.append(" description:\"").append(description).append("\"");
    }

    String location = values.get("Location");
    if (location != null && !location.isEmpty()) {
      command.append(" location:\"").append(location).append("\"");
    }

    // Add privacy setting
    String privacy = values.get("Privacy");
    if (privacy != null) {
      boolean isPrivate = privacy.equalsIgnoreCase("Private");
      command.append(" privacy:\"").append(isPrivate).append("\"");
    }

    return command.toString();
  }

  /**
   * Generate edit event command based on user selection
   */
  public String editEventCommand(Map<String, String> values) {
    String property = values.get("Property to Edit");
    String eventName = values.get("Event Name");
    String newValue = values.get("New Value");

    StringBuilder command = new StringBuilder();

    // Check which edit mode is selected
    if (Boolean.parseBoolean(values.get("ThisEventOnlyRB"))) {
      // Edit specific event (from-to)
      String fromDate = values.get("FromDate");
      String fromTime = values.get("FromTime");
      String toDate = values.get("ToDate");
      String toTime = values.get("ToTime");

      command.append("edit event ").append(property)
              .append(" \"").append(eventName).append("\"")
              .append(" from ").append(fromDate).append("T").append(fromTime)
              .append(" to ").append(toDate).append("T").append(toTime)
              .append(" with \"").append(newValue).append("\"");
    }
    else if (Boolean.parseBoolean(values.get("ThisEventLaterRB"))) {
      // Edit events from date
      String fromDate = values.get("FromDate");
      String fromTime = values.get("FromTime");

      command.append("edit events ").append(property)
              .append(" \"").append(eventName).append("\"")
              .append(" from ").append(fromDate).append("T").append(fromTime)
              .append(" with \"").append(newValue).append("\"");
    }
    else if (Boolean.parseBoolean(values.get("AllEventsRB"))) {
      // Edit all events with name
      command.append("edit events ").append(property)
              .append(" \"").append(eventName).append("\"")
              .append(" \"").append(newValue).append("\"");
    }

    return command.toString();
  }

  /**
   * Generate print events command
   */
  public String printEventsCommand(Map<String, String> values) {
    String onDate = values.get("Print Events on:");
    String fromDate = values.get("Interval From Date:");
    String fromTime = values.get("Interval From Time:");
    String toDate = values.get("Interval To Date:");
    String toTime = values.get("Interval To Time:");

    // If "on date" has a value, use that format
    if (onDate != null && !onDate.trim().isEmpty()) {
      return String.format("print events on %s", onDate);
    } else {
      // Otherwise use the interval format
      return String.format("print events from %sT%s to %sT%s",
              fromDate, fromTime, toDate, toTime);
    }
  }

  /**
   * Generate export calendar command
   */
  public String exportCalendarCommand(Map<String, String> values) {
    String filename = values.get("Filename:");
    return String.format("export cal %s", filename);
  }

  /**
   * Generate show status command
   */
  public String showStatusCommand(Map<String, String> values) {
    String date = values.get("Status Date:");
    String time = values.get("Status Time:");

    return String.format("show status on %sT%s", date, time);
  }

  public String importCalendarCommand(Map<String, String> values) {
    String filename = values.get("Filename:");
    return String.format("import cal %s", filename);
  }
}