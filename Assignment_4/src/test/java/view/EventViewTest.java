package view;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

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
    assertTrue(output.contains("Enter Command:"));
    assertTrue(output.contains("Event \"hello world\" was created"));
    assertTrue(output.contains("Goodbye!"));
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
    assertTrue(output.contains("Event \"hello world\" was created"));
    assertTrue(output.contains("Event \"test\" was created"));
    assertTrue(output.contains("Event \"test2\" was created"));
    assertTrue(output.contains("Event \"test3\" was created"));
    assertTrue(output.contains("Busy on 2025-10-10T10:30"));
    assertTrue(output.contains("Event \"test4\" was created"));

    assertTrue(output.contains("eventName=test"));
    assertTrue(output.contains("Start=2025-10-13T00:00"));
    assertTrue(output.contains("description=test"));

    assertTrue(output.contains("eventName=test"));
    assertTrue(output.contains("Start=2025-10-14T00:00"));
    assertTrue(output.contains("description=test"));

    assertTrue(output.contains("eventName=test"));
    assertTrue(output.contains("Start=2025-10-15T00:00"));
    assertTrue(output.contains("description=test"));

    assertTrue(output.contains("Event \"test5\" was not created due to conflict"));
    assertTrue(output.contains("Conflict detected, your " +
            "recurring event \"test7\" was not created"));
    assertTrue(output.contains("Event \"test9\" was not created due to conflict"));
    assertTrue(output.contains("CSV generated"));

    assertTrue(output.contains("Goodbye!"));

    //System.setOut(originalSystemOut);
    //System.out.println("Captured Output: " + output);
  }

  @Test
  public void testCreateOverlappingEvents() {
    String input =
            "create event \"Meeting\" from 2025-10-10T10:00 to 2025-10-10T11:00\n"
                    + "create event \"Overlap\" from 2025-10-10T10:30 to 2025-10-10T11:30\n"
                    + "exit\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();
    assertTrue(output.contains("Event \"Meeting\" was created"));
    assertTrue(output.contains("Event \"Overlap\" was created"));
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
    assertTrue(output.contains("Event \"Leap Day\" was created"));
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
    assertTrue(output.contains("Event \"Past Event\" was created"));
  }


  @Test
  public void testManyEventsCreation() {
    StringBuilder input = new StringBuilder();
    for (int i = 0; i < 105; i++) {
      input.append("create event \"Event")
              .append(i)
              .append("\" from 2025-10-10T10:00 to 2025-10-10T11:00\n");
    }
    input.append("exit\n");

    testIn = new ByteArrayInputStream(input.toString().getBytes());
    System.setIn(testIn);

    EventView eventView = new EventView();
    eventView.runInteractiveMode();

    String output = testOut.toString();

    assertTrue(output.contains("Event \"Event104\" was created"));
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
}

