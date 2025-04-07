package view;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * This class tests various functionalities of the EventView interactive mode,
 * including event creation, invalid commands, overlapping events, recurring events,
 * exporting calendar data, and handling edge cases.
 */

public class EventViewTest {
  private final InputStream originalSystemIn = System.in;
  private final PrintStream originalSystemOut = System.out;
  private ByteArrayInputStream testIn;
  private ByteArrayOutputStream testOut;

  @Before
  public void setUp() {
    testOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOut));
  }

  @After
  public void restoreSystemIO() {
    System.setIn(originalSystemIn);
    System.setOut(originalSystemOut);
  }

  @Test
  public void testRunInteractiveModeInvalidCommand() {
    String input = "create event\nexit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();
    assertTrue(output.contains("Enter Command:"));
    assertTrue(output.contains("Invalid command: Missing 'from' or 'on' in event Command"));

  }

  @Test
  public void testRunInteractiveModeValidCommand() {
    String input = "create event \"hello world\" " +
            "from 2025-10-10T10:00 to 2025-10-10T11:00\nexit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();
    // Using trimmed strings and ignoring whitespace differences
    String expectedOutput = "\u001B[36m[INFO] \u001B[0mCalendar \"\"default\"\" created with timezone UTC\n" +
            "Enter Command: \u001B[32m[SUCCESS] \u001B[0mEvent \"hello world\" from 2025-10-10 10:00 to 2025-10-10 11:00 successfully created in calendar \"default\"\n" +
            "Enter Command: Goodbye!";

    // Option 1: Compare ignoring whitespace
    assertEquals(output.replaceAll("\\s+", " ").trim(),
            expectedOutput.replaceAll("\\s+", " ").trim());
  }

  @Test
  public void testRunInteractiveModeMultipleCommands() {
    String input =
            "create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00\n"
                    + "create event --autoDecline \"test\" " +
                    "on 2025-10-10 repeats MTW for 6 times" +
                    " description:\"test\" location:\"MA\" privacy:\"true\"\n"
                    + "create event --autoDecline \"test2\" from" +
                    " 2024-10-10T10:00 to 2024-10-10T11:00 repeats MTW until 2024-11-10T10:00\n"
                    + "create event --autoDecline \"test3\" from " +
                    "2026-10-10T10:00 to 2026-10-10T11:00 repeats MTW until 2025-12-10T10:00\n"
                    + "show status on 2025-10-10T10:30\n"
                    + "create event \"test4\" from 2025-10-10T12:00 to 2025-10-10T13:00\n"
                    + "print events from 2025-10-11T10:00 to 2025-11-10T10:00\n"
                    + "create  event --autoDecline \"test5\" " +
                    "from 2025-10-10T10:00 to 2025-10-10T11:00\n"
                    + "create  event \"test6\" from 2021-10-13T10:00 to 2021-10-13T11:00\n"
                    + "create event --autoDecline \"test7\" " +
                    "from 2021-10-10T10:00 to 2021-10-10T11:00 repeats MTW for 10 times\n"
                    + "create event  \"hello world\" " +
                    "from 2025-10-01T10:00 to " +
                    "2025-10-01T11:00 repeats MTW  until 2025-10-21T10:00\n"
                    + "create event \"test8\" on 2027-10-10T10:00\n"
                    + "create  event --autoDecline \"test9\"" +
                    " from 2027-10-10T10:00 to 2027-10-10T11:00\n"
                    + "export cal test.csv\n"
                    + "exit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();
    assertTrue(output.contains("Enter Command:"));
    assertTrue(output.contains("hello world\" from 2025-10-10 10:00 to 2025-10-10 11:00 successfully created"));
    assertTrue(output.contains("Created recurring event \"test\""));
    assertTrue(output.contains("Created recurring event \"test2\""));
    assertTrue(output.contains("Created recurring event \"test3\""));
    assertTrue(output.contains("Busy on 2025-10-10T10:30"));
    assertTrue(output.contains("test4\" from 2025-10-10 12:00 to 2025-10-10 13:00 successfully created"));

    assertTrue(output.contains("Event Name = test"));
    assertTrue(output.contains("Start = 2025-10-13T00:00"));
    assertTrue(output.contains("Description = test"));

    assertTrue(output.contains("Event Name = test"));
    assertTrue(output.contains("Start = 2025-10-14T00:00"));
    assertTrue(output.contains("Description = test"));

    assertTrue(output.contains("Event Name = test"));
    assertTrue(output.contains("Start = 2025-10-15T00:00"));
    assertTrue(output.contains("Description = test"));

    // Update these assertions to match your current error messages
    assertTrue(output.contains("Event \"test5\" was not created due to time conflict with an existing event"));
    assertTrue(output.contains("Recurring event \"test7\" was not created due to time conflict on 2021-10-13 at 10:00"));
    assertTrue(output.contains("Event \"test9\" was not created due to time conflict with an existing event"));

    assertTrue(output.contains("CSV generated"));
    assertTrue(output.contains("Goodbye!"));
  }

  @Test
  public void testCreateEventOnLeapDay() {
    String input = "create event \"Leap Day\" " +
            "from 2024-02-29T10:00 to 2024-02-29T11:00\nexit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();
    assertTrue(output.contains("Event \"Leap Day\" from 2024-02-29 10:00 to 2024-02-29 11:00" +
            " successfully created in calendar "));
  }


  @Test
  public void testCreateEventWithInvalidDateFormat() {
    String input = "create event \"Invalid Date\" " +
            "from 10-10-2025 10:00 to 10-10-2025 11:00\nexit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();

    assertTrue(output.contains("Invalid date/time format"));
  }

  @Test
  public void testCreateEventInPast() {
    String input = "create event \"Past Event\" " +
            "from 2020-10-10T10:00 to 2020-10-10T11:00\nexit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();
    assertTrue(output.contains("Event \"Past Event\" from 2020-10-10 10:00" +
            " to 2020-10-10 11:00 successfully created"));
  }

  @Test
  public void testRecurringEventUntilInvalidDate() {
    String input = "create event \"test\" from 2025-10-10T10:00 to " +
            "2025-10-10T11:00 repeats MTW until 2030-99-99T10:00\nexit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();

    assertTrue(output.contains("Invalid value for MonthOfYear (valid values 1 - 12): 99"));
  }

  @Test
  public void testExportWithoutEvents() {
    String input = "export cal empty.csv\nexit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();
    System.setOut(originalSystemOut);
    System.out.println("Captured Output: " + output);
    assertTrue(output.contains("No Events in Calendar."));
  }

  @Test
  public void testHour2400() {
    String input = "create event \"Meeting\" from 2025-10-10T10:00 to 2025-10-10T24:00\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();

    assertTrue(output.contains("Invalid value for HourOfDay (valid values 0 - 23): 24"));
    //System.setOut(originalSystemOut);
    //System.out.println("Captured Output: " + output);
  }

  @Test
  public void testRecurringEvent() {
    String input = "create event \"test\" from 2025-10-10T07:00 to 2025-10-10T08:00 " +
            "repeats MTW until 2025-11-10T07:00\n" +
            "print events from 2025-10-10T07:00 to 2025-11-10T07:00\n" +
            "exit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();

    System.setOut(originalSystemOut);
    System.out.println("Captured Output: " + output);

    assertTrue(output.contains("Created recurring event \"test\""));

    int eventCount = output.split("\\* Event Name = test").length - 1;
    assertEquals(12, eventCount);

  }

  @Test
  public void testRunInteractiveModeMissingEventName() {
    String input = "create event from 2025-10-10T10:00 to 2025-10-10T11:00\nexit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();
    assertTrue(output.contains("Invalid command: Missing event name in quotes"));
  }

  @Test
  public void testCreateConflictingRecurringEvent() {
    String input =
            "create event --autodecline \"test1\" from 2025-10-10T10:00 to 2025-10-10T11:00\n" +
                    "create event --autodecline \"test2\" " +
                    "from 2025-10-10T10:30 to 2025-10-10T11:30\n" +
                    "create event --autodecline \"test3\" from" +
                    " 2025-10-10T09:00 to 2025-10-10T11:00 " +
                    "repeats FS until 2025-12-01T10:00\n" +
                    "exit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();

    assertTrue(output.contains("Event \"test1\" from 2025-10-10 10:00 to" +
            " 2025-10-10 11:00 successfully created"));
    assertTrue(output.contains("Event \"test2\" was not created " +
            "due to time conflict with an existing event"));
    assertTrue(output.contains("Recurring event \"test3\" was not created due to time" +
            " conflict on 2025-10-10 at 09:00"));
  }

  @Test
  public void testRecurringEventEdgeCase() {
    String input = "create event \"Test Recurring\" from 2025-10-10T10:00 to 2025-10-10T11:00 " +
            "repeats XXX until 2025-12-10T10:00\nexit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();
    assertTrue(output.contains("Invalid weekday format after 'repeats'." +
            " Use M (Monday), T (Tuesday), W (Wednesday), etc."));
  }

  @Test
  public void testExportEmptyCalendar() {
    String input = "export cal empty.csv\nexit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();
    assertTrue(output.contains("No Events in Calendar"));
  }

  @Test
  public void testMainValidArgs() {
    String[] args = {"--mode", "interactive"};

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    String exitInput = "exit\n";
    InputStream originalSystemIn = System.in;
    System.setIn(new ByteArrayInputStream(exitInput.getBytes()));

    EventView.main(args);

    System.setIn(originalSystemIn);

    String output = out.toString();
    assertTrue(output.contains("Enter Command: "));

    assertTrue(output.contains("Goodbye!"));
  }

  @Test
  public void testMainHeadlessArgs() throws IOException {
    File tempFile = File.createTempFile("testCommands", ".txt");
    tempFile.deleteOnExit();

    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write("exit\n");
    }

    String[] args = {"--mode", "headless", tempFile.getAbsolutePath()};
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    EventView.main(args);

    String output = out.toString();
    assertTrue(output.contains("Bye"));
  }

  @Test
  public void testMainInvalidArgs() {
    String[] args = {"--mode", "invalid"};

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    EventView.main(args);

    String output = out.toString();
    assertTrue(output.contains("Usage: java EventView --mode [interactive|headless filename]"));
  }

  @Test
  public void testMainInvalidArgsLength() {
    String[] args = {"--mode"};

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    EventView.main(args);

    String output = out.toString();
    assertEquals("Usage: java EventView --mode [interactive|headless filename]\r\n", output);
  }
}

