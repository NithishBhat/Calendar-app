package Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    private Map<String, Pattern> patterns = new HashMap<>();

    public CommandParser() {
        String dateTimeString = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})";
        String dateString = "(\\d{4}-\\d{2}-\\d{2})";
        String weekDay="([MTWRFSU]{1,7})";

        // Create event patterns
        patterns.put("createEventPattern",
                Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+from\\s+"
                        + dateTimeString + "\\s+to\\s+"
                        + dateTimeString + "\\s*$"));
        patterns.put("createRecurringEventN",
                Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+from\\s+"
                        + dateTimeString + "\\s+to\\s+"
                        + dateTimeString + "\\s+repeats\\s+"
                        + weekDay + "\\s+for\\s+(\\d+)\\s+times\\s*$"));
        patterns.put("createRecurringEventEndDate",
                Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+from\\s+"
                        + dateTimeString + "\\s+to\\s+"
                        + dateTimeString + "\\s+repeats\\s+"
                        + weekDay+"\\s+until\\s+"
                        + dateTimeString + "\\s*$"));
        patterns.put("createWholeDayEvent",
                Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+on\\s+"
                        + dateTimeString + "\\s*$"));
        patterns.put("createRecurringWholeDayEventN",
                Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+on\\s+"
                        + dateString + "\\s+repeats\\s+"
                        + weekDay + "\\s+for\\s+(\\d+)\\s+times\\s*$"));
        patterns.put("createRecurringWholeDayEventEnd",
                Pattern.compile("\\s*create\\s+event(\\s+--autoDecline)?\\s+(.*?)\\s+on\\s+"
                        + dateString + "\\s+repeats\\s+"
                        + weekDay + "\\s+until\\s+"
                        + dateString + "\\s*$"));

        // Edit event patterns
        patterns.put("editEvent",
                Pattern.compile("\\s*edit\\s+event\\s+(.*?)\\s+(.*?)\\s+from\\s+"
                        + dateTimeString + "\\s+to\\s+"
                        + dateTimeString + "\\s+with\\s+(.*?)\\s*$"));
        patterns.put("editEvents",
                Pattern.compile("\\s*edit\\s+events\\s+(.*?)\\s+(.*?)\\s+from\\s+"
                        + dateTimeString + "\\s+with\\s+(.*?)\\s*$"));
        patterns.put("editEventsByName",
                Pattern.compile("\\s*edit\\s+events\\s+(.*?)\\s+(.*?)\\s+(.*?)\\s*$"));

        // Print and export patterns
        patterns.put("printEventsOn",
                Pattern.compile("\\s*print\\s+events\\s+on\\s+"
                        + dateString + "\\s*$"));
        patterns.put("printEventsBetween",
                Pattern.compile("\\s*print\\s+events\\s+from\\s+"
                        + dateTimeString + "\\s+to\\s+"
                        + dateTimeString + "\\s*$"));
        patterns.put("export",
                Pattern.compile("\\s*export\\s+cal\\s+([a-zA-Z0-9]+\\.csv)\\s*"));

        // show status on <dateStringTtimeString>
        patterns.put("show",
                Pattern.compile("\\s*show\\s+status\\s+on\\s+"
                        + dateTimeString + "\\s*$"));
    }

    public Matcher parseCommand(String input) {
        for (Map.Entry<String, Pattern> entry : patterns.entrySet()) {
            Matcher matcher = entry.getValue().matcher(input);
            if (matcher.matches()) {
                return matcher;
            }
        }

        // If no pattern matched, return null
        System.out.println("No matching pattern found for command: " + input);
        return null;
    }

    public Map<String, Pattern> getPatterns() {
        return patterns;
    }
}
