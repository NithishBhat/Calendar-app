package controller;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTest {

  private final CommandParser parser = new CommandParser();

  @Test
  public void testGetDiagnosticMessage_EmptyCommand() {
    assertEquals("Unknown command ''. Available commands are: create, edit, print, export, show", parser.getDiagnosticMessage(" "));
  }

  @Test
  public void testGetDiagnosticMessage_UnknownCommand() {
    assertTrue(parser.getDiagnosticMessage("unknown").contains("Unknown command"));
  }

  @Test
  public void testDiagnoseCreateCommand_MissingEventKeyword() {
    assertEquals("Missing 'event' keyword in create command", parser.getDiagnosticMessage("create something"));
  }

  @Test
  public void testDiagnoseCreateCommand_MissingToKeyword() {
    assertEquals("Missing 'to' keyword in time range Command", parser.getDiagnosticMessage("create event \"Meeting\" from 2025-03-31T10:00"));
  }

  @Test
  public void testDiagnoseCreateCommand_MissingFromOrOn() {
    assertEquals("Missing 'from' or 'on' in event Command", parser.getDiagnosticMessage("create event \"Meeting\""));
  }

  @Test
  public void testDiagnoseEditCommand_MissingEventKeyword() {
    assertEquals("Missing 'event' or 'events' keyword in edit command", parser.getDiagnosticMessage("edit something"));
  }

  @Test
  public void testDiagnosePrintCommand_MissingEventsKeyword() {
    assertEquals("Missing 'events' keyword in print command", parser.getDiagnosticMessage("print something"));
  }

  @Test
  public void testDiagnosePrintCommand_InvalidDateFormat() {
    assertEquals("Invalid date format after 'on'. Use YYYY-MM-DD", parser.getDiagnosticMessage("print events on 20250331"));
  }

  @Test
  public void testDiagnoseExportCommand_MissingCalKeyword() {
    assertEquals("Missing 'cal' keyword in export command", parser.getDiagnosticMessage("export something.csv"));
  }

  @Test
  public void testDiagnoseExportCommand_InvalidFileExtension() {
    assertEquals("Missing CSV filename or invalid file extension (must end with .csv)", parser.getDiagnosticMessage("export cal calendar.txt"));
  }

  @Test
  public void testDiagnoseShowCommand_MissingStatusKeyword() {
    assertEquals("Missing 'status' keyword in show command", parser.getDiagnosticMessage("show something"));
  }

  @Test
  public void testDiagnoseShowCommand_InvalidDateTimeFormat() {
    assertEquals("Invalid date/time format after 'on'. Use YYYY-MM-DDThh:mm", parser.getDiagnosticMessage("show status on 20250331"));
  }

  @Test
  public void testCreateEvent_ValidFullEvent() {
    String command = "create event \"Team Meeting\" from 2025-03-31T10:00 to 2025-03-31T11:00 description:\"Weekly sync\" location:\"Zoom\" privacy:\"false\"";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Valid full event command should be parsed");
  }

  @Test
  public void testCreateEvent_WholeDay() {
    String command = "create event \"Holiday\" on 2025-03-31T00:00";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Whole day event should be parsed");
  }

  @Test
  public void testCreateEvent_RecurringWithCount() {
    String command = "create event \"Weekly Meeting\" from 2025-03-31T14:00 to 2025-03-31T15:00 repeats MW for 10 times";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Recurring event with count should be parsed");
  }

  @Test
  public void testCreateEvent_RecurringUntilDate() {
    String command = "create event \"Monthly Review\" from 2025-03-31T14:00 to 2025-03-31T15:00 repeats F until 2025-12-31T00:00";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Recurring event with end date should be parsed");
  }

  @Test
  public void testCreateEvent_AutoDecline() {
    String command = "create event --autoDecline \"Mandatory Training\" from 2025-03-31T09:00 to 2025-03-31T17:00";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Event with auto-decline should be parsed");
  }

  // Tests for edit commands
  @Test
  public void testEditEvent_SingleEvent() {
    String command = "edit event eventName \"Old Meeting\" from 2025-03-31T10:00 to 2025-03-31T11:00 with \"New Meeting\"";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Edit single event should be parsed");
  }

  @Test
  public void testEditEvents_ByLocation() {
    String command = "edit events location \"Team Meeting\" from 2025-03-31T10:00 with \"Conference Room\"";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Edit events by location should be parsed");
  }

  @Test
  public void testEditCalendar_Name() {
    String command = "edit calendar --name \"OldCalendar\" --property \"name\" \"NewCalendar\"";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Edit calendar name should be parsed");
  }

  @Test
  public void testEditCalendar_Timezone() {
    String command = "edit calendar --name \"WorkCalendar\" --property \"time-zone\" \"America/New_York\"";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Edit calendar timezone should be parsed");
  }

  // Print command tests
  @Test
  public void testPrintEvents_OnSpecificDate() {
    String command = "print events on 2025-03-31";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Print events on specific date should be parsed");
  }

  @Test
  public void testPrintEvents_BetweenDateTimes() {
    String command = "print events from 2025-03-31T09:00 to 2025-03-31T17:00";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Print events between date times should be parsed");
  }


  // Show status tests
  @Test
  public void testShowStatus_AtSpecificDateTime() {
    String command = "show status on 2025-03-31T14:00";
    Matcher matcher = parser.parseCommand(command);
    assertNotNull(matcher, "Show status at specific date time should be parsed");
  }

  // Diagnostic message tests for invalid scenarios
  @Test
  public void testDiagnoseCreateCommand_InvalidWeekday() {
    String invalidCommand = "create event \"Weekly Meeting\" from 2025-03-31T14:00 to 2025-03-31T15:00 repeats XY for 10 times";
    assertTrue(parser.getDiagnosticMessage(invalidCommand).contains("Invalid weekday format"));
  }

  @Test
  public void testDiagnoseEditCommand_InvalidProperty() {
    String invalidCommand = "edit event invalidProperty \"Meeting\" from 2025-03-31T10:00 to 2025-03-31T11:00 with \"New Value\"";
    assertTrue(parser.getDiagnosticMessage(invalidCommand).contains("Invalid property"));
  }

  @Test
  public void testDiagnoseCreateCommand_MissingQuotes() {
    String invalidCommand = "create event Meeting from 2025-03-31T10:00 to 2025-03-31T11:00";
    assertTrue(parser.getDiagnosticMessage(invalidCommand).contains("Missing event name in quotes"));
  }


  @Test
  public void testParseCommand_InvalidCommand() {
    String invalidCommand = "random gibberish that makes no sense";
    Matcher matcher = parser.parseCommand(invalidCommand);
    assertNull(matcher, "Invalid command should return null matcher");
  }



  @Test
  public void testDiagnoseEditEvent_MissingFromDateTime() {
    String command = "edit event \"Meeting\" to 2025-03-31T15:00 with \"New Meeting\"";
    assertEquals("Missing 'from' keyword in edit event command",
            parser.getDiagnosticMessage(command));
  }

  @Test
  public void testDiagnoseEditEvent_MissingToDateTime() {
    String command = "edit event \"Meeting\" from 2025-03-31T14:00 with \"New Meeting\"";
    assertEquals("Missing 'to' keyword in edit event command",
            parser.getDiagnosticMessage(command));
  }

  @Test
  public void testDiagnoseEditEvent_MissingWithKeyword() {
    String command = "edit event \"Meeting\" from 2025-03-31T14:00 to 2025-03-31T15:00 \"New Meeting\"";
    assertEquals("Missing 'with' keyword for new property value in edit event command",
            parser.getDiagnosticMessage(command));
  }

  @Test
  public void testDiagnoseEditEvents_MissingWithKeywordAfterDateTime() {
    String command = "edit events \"Meeting\" from 2025-03-31T14:00 \"Conference Room\"";
    assertEquals("Missing 'with' keyword after 'from <dateTime>' in edit events command",
            parser.getDiagnosticMessage(command));
  }





  @Test
  public void testDiagnoseEditCommand_EdgeCases() {
    String[] invalidCommands = {
            "edit",
            "edit something",
            "edit event",
            "edit events",
            "edit calendar"
    };

    for (String command : invalidCommands) {
      assertNotNull(parser.getDiagnosticMessage(command),
              "Should provide diagnostic message for incomplete command: " + command);
    }
  }

  @Test
  public void testDiagnoseEditCommand_InvalidCommandStructure() {
    String[] invalidStructures = {
            "edit event \"Meeting\" random words",
            "edit events \"Meeting\" random words",
            "edit calendar random words"
    };

    for (String command : invalidStructures) {
      assertNotNull(parser.getDiagnosticMessage(command),
              "Should provide diagnostic message for invalid command structure: " + command);
    }
  }
}
