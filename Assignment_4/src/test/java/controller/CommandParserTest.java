package controller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the command parser class tests the command parser class methods.
 */

public class CommandParserTest {

  CommandParser parser = new CommandParser();

  @Test
  public void testGetDiagnosticMessage_EmptyCommand() {
    assertEquals("Unknown command ''. Available commands are: create, edit," +
            " print, export, show", parser.getDiagnosticMessage(" "));
  }

  @Test
  public void testGetDiagnosticMessage_UnknownCommand() {
    assertTrue(parser.getDiagnosticMessage("unknown").contains("Unknown command"));
  }

  @Test
  public void testDiagnoseCreateCommand_MissingEventKeyword() {
    assertEquals("Missing 'event' keyword in create command",
            parser.getDiagnosticMessage("create something"));
  }

  @Test
  public void testDiagnoseCreateCommand_MissingToKeyword() {
    assertEquals("Missing 'to' keyword in time range Command",
            parser.getDiagnosticMessage("create event \"Meeting\" from 2025-03-31T10:00"));
  }

  @Test
  public void testDiagnoseCreateCommand_MissingFromOrOn() {
    assertEquals("Missing 'from' or 'on' in event Command",
            parser.getDiagnosticMessage("create event \"Meeting\""));
  }

  @Test
  public void testDiagnoseEditCommand_MissingEventKeyword() {
    assertEquals("Missing 'event' or 'events' keyword in edit command",
            parser.getDiagnosticMessage("edit something"));
  }

  @Test
  public void testDiagnosePrintCommand_MissingEventsKeyword() {
    assertEquals("Missing 'events' keyword in print command",
            parser.getDiagnosticMessage("print something"));
  }

  @Test
  public void testDiagnosePrintCommand_InvalidDateFormat() {
    assertEquals("Invalid date format after 'on'. Use YYYY-MM-DD",
            parser.getDiagnosticMessage("print events on 20250331"));
  }

  @Test
  public void testDiagnoseExportCommand_MissingCalKeyword() {
    assertEquals("Missing 'cal' keyword in export command",
            parser.getDiagnosticMessage("export something.csv"));
  }

  @Test
  public void testDiagnoseExportCommand_InvalidFileExtension() {
    assertEquals("Missing CSV filename or invalid file extension (must end with .csv)",
            parser.getDiagnosticMessage("export cal calendar.txt"));
  }

  @Test
  public void testDiagnoseShowCommand_MissingStatusKeyword() {
    assertEquals("Missing 'status' keyword in show command",
            parser.getDiagnosticMessage("show something"));
  }

  @Test
  public void testDiagnoseShowCommand_InvalidDateTimeFormat() {
    assertEquals("Invalid date/time format after 'on'. Use YYYY-MM-DDThh:mm",
            parser.getDiagnosticMessage("show status on 20250331"));
  }


  @Test
  public void testDiagnoseCreateCommand_InvalidWeekday() {
    String invalidCommand = "create event \"Weekly Meeting\" " +
            "from 2025-03-31T14:00 to 2025-03-31T15:00 repeats XY for 10 times";
    assertTrue(parser.getDiagnosticMessage(invalidCommand).contains("Invalid weekday format"));
  }

  @Test
  public void testDiagnoseEditCommand_InvalidProperty() {
    String invalidCommand = "edit event invalidProperty \"Meeting\" " +
            "from 2025-03-31T10:00 to 2025-03-31T11:00 with \"New Value\"";
    assertTrue(parser.getDiagnosticMessage(invalidCommand).contains("Invalid property"));
  }

  @Test
  public void testDiagnoseCreateCommand_MissingQuotes() {
    String invalidCommand = "create event Meeting from 2025-03-31T10:00 to 2025-03-31T11:00";
    assertTrue(parser.getDiagnosticMessage(invalidCommand)
            .contains("Missing event name in quotes"));
  }

  @Test
  public void Invalidcreate() {
    String command = "create event \"Weekly Meeting\" from 2025-03-31T14:00 to" +
            " 2025-03-31T15:00 re XY for 10 times";
    assertEquals("Invalid create command format. Examples:\n" +
                    "  create event \"Meeting\" from 2023-03-15T14:00 to 2023-03-15T15:00\n" +
                    "  create event \"Birthday\" on 2023-03-15\n" +
                    "  create event \"Weekly Meeting\" from 2023-03-15T14:00 to 2023-03-15T15:00" +
                    " repeats MW for 10 times",
            parser.getDiagnosticMessage(command));
  }


  @Test
  public void create() {
    String command = "create event \"Weekly Meeting\" from 2025-03-31T14:00 to" +
            " 2025-03-31T15:00 repeats XY  10 times";
    assertEquals("Missing 'for' or 'until' in recurring event Command",
            parser.getDiagnosticMessage(command));
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
    String command = "edit event \"Meeting\" from" +
            " 2025-03-31T14:00 to 2025-03-31T15:00 \"New Meeting\"";
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
  public void testGetDiagnosticMessage_MissingEventsKeyword() {
    assertEquals("Missing 'events' keyword in print command",
            parser.getDiagnosticMessage("print something"));
  }

  @Test
  public void testGetDiagnosticMessage_InvalidDateFormatAfterOn() {
    assertEquals("Invalid date format after 'on'. Use YYYY-MM-DD",
            parser.getDiagnosticMessage("print events on 20250331"));
  }

  @Test
  public void testGetDiagnosticMessage_MissingToKeyword() {
    assertEquals("Missing 'to' keyword in time range specification",
            parser.getDiagnosticMessage("print events from 2025-03-31T10:00"));
  }


  @Test
  public void testGetDiagnosticMessage_MissingOnOrFromTo() {
    assertEquals("Missing 'on' or 'from...to' in print command",
            parser.getDiagnosticMessage("print events"));
  }


  @Test
  public void wrongshow() {
    String command = "show abc";
    assertEquals("Missing 'status' keyword in show command",
            parser.getDiagnosticMessage(command));

  }

  @Test
  public void showMissingOn() {
    String command = "show status  2025-03-31T10:00";
    assertEquals("Missing 'on' keyword in show status command",
            parser.getDiagnosticMessage(command));

  }

  @Test
  public void showInvalid() {
    String command = "show status on 2025-03-31T10:00asdhjkadsjkls";
    assertEquals("Invalid show command format. Example:\n" +
                    "  show status on 2023-03-15T14:00",
            parser.getDiagnosticMessage(command));

  }

  @Test
  public void testMissingEventsKeyword() {
    String input = "print from 2025-03-31T10:00 to 2025-03-31T12:00";
    String result = parser.getDiagnosticMessage(input);
    assertEquals("Missing 'events' keyword in print command", result);
  }

  @Test
  public void testInvalidDateFormatAfterOn() {
    String input = "print events on 03-31-2025";
    String result = parser.getDiagnosticMessage(input);
    assertEquals("Invalid date format after 'on'. Use YYYY-MM-DD", result);
  }

  @Test
  public void testMissingToKeywordInRange() {
    String input = "print events from 2025-03-31T10:00";
    String result = parser.getDiagnosticMessage(input);
    assertEquals("Missing 'to' keyword in time range specification", result);
  }


  @Test
  public void testMissingOnOrFromTo() {
    String input = "print events";
    String result = parser.getDiagnosticMessage(input);
    assertEquals("Missing 'on' or 'from...to' in print command", result);
  }

  @Test
  public void printInvalidDate() {
    String input = "print events from 03234234223 to 21341423432";
    String result = parser.getDiagnosticMessage(input);
    assertEquals("Invalid date/time format. Use YYYY-MM-DDThh:mm", result);
  }

  @Test
  public void printInvalid() {
    String input = "print events on 2025-00-00 asjasdjkads";
    String result = parser.getDiagnosticMessage(input);
    assertEquals("Invalid print command format. Examples:\n" +
            "  print events on 2023-03-15\n" +
            "  print events from 2023-03-15T09:00 to 2023-03-15T17:00", result);
  }


  @Test
  public void wrongexport() {
    String command = "export abc";
    assertEquals("Missing 'cal' keyword in export command",
            parser.getDiagnosticMessage(command));
  }

  @Test
  public void invalidexport() {
    String command = "export hello cal abc.csv";
    assertEquals("Invalid export command format. Example:\n" +
                    "  export cal calendar.csv",
            parser.getDiagnosticMessage(command).trim());
  }


}
