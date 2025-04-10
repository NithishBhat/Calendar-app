package controller;

import model.CalenderCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import view.componentsGui.CalendarUIListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Test class specifically for testing EventHandler UI listener functionality.
 */
public class EventHandlerUITest {

  private EventHandler eventHandler;
  private TestCalendarUIListener testListener;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  /**
   * Custom implementation of CalendarUIListener for testing.
   */
  private static class TestCalendarUIListener implements CalendarUIListener {
    private final List<String> calendarChangedCalls = new ArrayList<>();
    private final List<CalenderCollection> eventsChangedCalls = new ArrayList<>();
    private int calendarListChangedCount = 0;
    private final List<String> commandOutputCalls = new ArrayList<>();

    @Override
    public void onCalendarChanged(String calendarName) {
      calendarChangedCalls.add(calendarName);
    }

    @Override
    public void onEventsChanged(CalenderCollection calenderCollection) {
      eventsChangedCalls.add(calenderCollection);
    }

    @Override
    public void onCalendarListChanged() {
      calendarListChangedCount++;
    }

    @Override
    public void onCommandOutput(String message) {
      commandOutputCalls.add(message);
    }

    // Methods to inspect call history
    public boolean wasCalendarChangedCalledWith(String calendarName) {
      return calendarChangedCalls.contains(calendarName);
    }

    public int getCalendarChangedCallCount() {
      return calendarChangedCalls.size();
    }

    public int getEventsChangedCallCount() {
      return eventsChangedCalls.size();
    }

    public int getCalendarListChangedCount() {
      return calendarListChangedCount;
    }

    public boolean wasCommandOutputCalledWith(String substring) {
      for (String msg : commandOutputCalls) {
        if (msg.contains(substring)) {
          return true;
        }
      }
      return false;
    }

    public int getCommandOutputCallCount() {
      return commandOutputCalls.size();
    }

    public void reset() {
      calendarChangedCalls.clear();
      eventsChangedCalls.clear();
      calendarListChangedCount = 0;
      commandOutputCalls.clear();
    }
  }

  @Before
  public void setUp() {
    eventHandler = new EventHandler();
    testListener = new TestCalendarUIListener();

    // Set up output capture
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
  }

  @After
  public void tearDown() {
    // Restore the original output stream
    System.setOut(originalOut);
  }

  /**
   * Tests adding a UI listener.
   */
  @Test
  public void testAddUIListener() {
    // Add listener
    eventHandler.addUIListener(testListener);

    // Create a calendar to trigger notifications
    eventHandler.createCalender("TestCal", ZoneId.of("UTC"));

    // Verify the listener was called
    assertEquals(1, testListener.getCalendarListChangedCount());
    assertTrue(testListener.wasCommandOutputCalledWith("TestCal"));
  }

  /**
   * Tests removing a UI listener.
   */
  @Test
  public void testRemoveUIListener() {
    // Add listener
    eventHandler.addUIListener(testListener);

    // Create a calendar to verify listener is working
    eventHandler.createCalender("TestCal1", ZoneId.of("UTC"));

    // Verify the listener was called
    assertEquals(1, testListener.getCalendarListChangedCount());

    // Reset tracker data
    testListener.reset();

    // Remove listener
    eventHandler.removeUIListener(testListener);

    // Create another calendar - listener should not be called now
    eventHandler.createCalender("TestCal2", ZoneId.of("UTC"));

    // Verify the listener was not called after removal
    assertEquals(0, testListener.getCalendarListChangedCount());
  }

  /**
   * Tests notification when calendar changes.
   */
  @Test
  public void testNotifyCalendarChanged() {
    eventHandler.addUIListener(testListener);

    // This method calls notifyCalendarChanged
    eventHandler.useCalender("default");

    // Verify notification was sent
    assertTrue(testListener.wasCalendarChangedCalledWith("default"));
  }

  /**
   * Tests notification when events change.
   */
  @Test
  public void testNotifyEventsChanged() {
    eventHandler.addUIListener(testListener);

    // Create an event controller and get its event handler
    EventController eventController = new EventController();
    EventHandler controllerHandler = eventController.getEventHandler();

    // Add the same listener to the controller's handler
    controllerHandler.addUIListener(testListener);

    // Create an event which should trigger notifyEventsChanged
    eventController.filterCommand("create event \"Test Event\" from 2025-10-10T10:00 to 2025-10-10T11:00");

    // Verify notification was sent
    assertTrue(testListener.getEventsChangedCallCount() > 0);
  }

  /**
   * Tests notification when calendar list changes.
   */
  @Test
  public void testNotifyCalendarListChanged() {
    eventHandler.addUIListener(testListener);

    // Create a calendar to trigger notifyCalendarListChanged
    eventHandler.createCalender("NewCal", ZoneId.of("UTC"));

    // Verify notification was sent
    assertEquals(1, testListener.getCalendarListChangedCount());
  }

  /**
   * Tests notification with user messages.
   */
  @Test
  public void testNotifyUserMessage() {
    eventHandler.addUIListener(testListener);

    // This call creates a calendar which sends a user message
    eventHandler.createCalender("MessageTest", ZoneId.of("UTC"));

    // Verify that a message was sent to the listener
    assertTrue(testListener.wasCommandOutputCalledWith("MessageTest"));
  }

  /**
   * Tests the useCalender method and its notifications.
   */
  @Test
  public void testUseCalender() {
    eventHandler.addUIListener(testListener);

    // Create a calendar first
    eventHandler.createCalender("UseTest", ZoneId.of("UTC"));

    // Reset tracker data
    testListener.reset();

    // Use the calendar
    eventHandler.useCalender("UseTest");

    // Verify notifications were sent
    assertTrue(testListener.wasCalendarChangedCalledWith("UseTest"));
    assertTrue(testListener.getEventsChangedCallCount() > 0);
  }

  /**
   * Tests the process of copying an event with listener notifications.
   */
  @Test
  public void testProcessCopyEventOn() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create two calendars
    eventHandler.createCalender("SourceCal", ZoneId.of("UTC"));
    eventHandler.createCalender("TargetCal", ZoneId.of("UTC"));

    // Use the source calendar
    eventHandler.useCalender("SourceCal");

    // Create an event using controller
    EventController eventController = new EventController();
    // Get the event handler from the controller and add event to it
    EventHandler controllerHandler = eventController.getEventHandler();

    // Create a test event directly
    eventHandler.addEvent(new model.EventModel("CopyTest",
            LocalDateTime.parse("2025-10-10T10:00"),
            LocalDateTime.parse("2025-10-10T11:00"),
            "Test Description", false, false, "Test Location"));

    // Reset tracker data
    testListener.reset();

    // Create a pattern and matcher to invoke processCopyEventOn
    String command = "copy event \"CopyTest\" on 2025-10-10T10:00 --target \"TargetCal\" to 2025-12-19T10:00";
    Pattern pattern = Pattern.compile("copy\\sevent\\s\"(.*?)\"\\son\\s(.*?)\\s--target\\s\"(.*?)\"\\sto\\s(.*?)$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processCopyEventOn(matcher);

      // Verify the notification was sent
      assertTrue(testListener.getEventsChangedCallCount() > 0);

      // Check output contains success message
      String output = outputStream.toString();
      assertTrue(output.contains("CopyTest copied to calendar"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests copying events between dates with listener notifications.
   */
  @Test
  public void testProcessCopyCalenderBetweenDates() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create two calendars
    eventHandler.createCalender("SourceCal", ZoneId.of("UTC"));
    eventHandler.createCalender("TargetCal", ZoneId.of("UTC"));

    // Use the source calendar
    eventHandler.useCalender("SourceCal");

    // Create events directly
    eventHandler.addEvent(new model.EventModel("BetweenTest1",
            LocalDateTime.parse("2025-10-05T10:00"),
            LocalDateTime.parse("2025-10-05T11:00"),
            "Test Description", false, false, "Test Location"));

    eventHandler.addEvent(new model.EventModel("BetweenTest2",
            LocalDateTime.parse("2025-10-10T10:00"),
            LocalDateTime.parse("2025-10-10T11:00"),
            "Test Description", false, false, "Test Location"));

    // Reset tracker data
    testListener.reset();

    // Create a pattern and matcher to invoke processCopyCalenderBetweenDates
    String command = "copy events between 2025-10-01 and 2025-10-15 --target \"TargetCal\" to 2025-12-01";
    Pattern pattern = Pattern.compile("copy\\sevents\\sbetween\\s(.*?)\\sand\\s(.*?)\\s--target\\s\"(.*?)\"\\sto\\s(.*?)$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processCopyCalenderBetweenDates(matcher);

      // Verify the notification was sent
      assertTrue(testListener.getEventsChangedCallCount() > 0);

      // Check output contains success message
      String output = outputStream.toString();
      assertTrue(output.contains("Events from 2025-10-01 to 2025-10-15 copied to calendar TargetCal"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests copying events on a specific day with listener notifications.
   */
  @Test
  public void testProcessCopyEventsOn() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create two calendars
    eventHandler.createCalender("SourceCal", ZoneId.of("UTC"));
    eventHandler.createCalender("TargetCal", ZoneId.of("UTC"));

    // Use the source calendar
    eventHandler.useCalender("SourceCal");

    // Create events directly
    eventHandler.addEvent(new model.EventModel("DayTest1",
            LocalDateTime.parse("2025-10-05T10:00"),
            LocalDateTime.parse("2025-10-05T11:00"),
            "Test Description", false, false, "Test Location"));

    eventHandler.addEvent(new model.EventModel("DayTest2",
            LocalDateTime.parse("2025-10-05T14:00"),
            LocalDateTime.parse("2025-10-05T15:00"),
            "Test Description", false, false, "Test Location"));

    // Reset tracker data
    testListener.reset();

    // Create a pattern and matcher to invoke processCopyEventsOn
    String command = "copy events on 2025-10-05 --target \"TargetCal\" to 2025-12-05";
    Pattern pattern = Pattern.compile("copy\\sevents\\son\\s(.*?)\\s--target\\s\"(.*?)\"\\sto\\s(.*?)$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processCopyEventsOn(matcher);

      // Verify the notification was sent
      assertTrue(testListener.getEventsChangedCallCount() > 0);

      // Check output contains success message
      String output = outputStream.toString();
      assertTrue(output.contains("Events on 2025-10-05 copied to calendar"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests copying events with same name with listener notifications.
   */
  @Test
  public void testProcessCopyCalendarBetweenDatesWithName() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create two calendars
    eventHandler.createCalender("SourceCal", ZoneId.of("UTC"));
    eventHandler.createCalender("TargetCal", ZoneId.of("UTC"));

    // Use the source calendar
    eventHandler.useCalender("SourceCal");

    // Create events directly
    eventHandler.addEvent(new model.EventModel("SameNameTest",
            LocalDateTime.parse("2025-10-05T10:00"),
            LocalDateTime.parse("2025-10-05T11:00"),
            "Test Description", false, false, "Test Location"));

    eventHandler.addEvent(new model.EventModel("SameNameTest",
            LocalDateTime.parse("2025-10-10T10:00"),
            LocalDateTime.parse("2025-10-10T11:00"),
            "Test Description", false, false, "Test Location"));

    eventHandler.addEvent(new model.EventModel("DifferentName",
            LocalDateTime.parse("2025-10-15T10:00"),
            LocalDateTime.parse("2025-10-15T11:00"),
            "Test Description", false, false, "Test Location"));

    // Reset tracker data
    testListener.reset();

    // Call the method directly
    LocalDateTime fromDate = LocalDateTime.parse("2025-10-01T00:00");
    LocalDateTime toDate = LocalDateTime.parse("2025-10-20T23:59");
    LocalDateTime targetStartDate = LocalDateTime.parse("2025-12-01T00:00");
    eventHandler.processCopyCalendarBetweenDatesWithName("SameNameTest", fromDate, toDate,
            "SourceCal", targetStartDate, "TargetCal");

    // Verify the notification was sent
    assertTrue(testListener.getEventsChangedCallCount() > 0);

    // Check output contains success message
    String output = outputStream.toString();
    assertTrue(output.contains("Events SameNameTest copied to calendar"));
  }

  /**
   * Tests editing a calendar with listener notifications.
   */
  @Test
  public void testProcessEditCalendar() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create a calendar
    eventHandler.createCalender("EditTest", ZoneId.of("UTC"));

    // Reset tracker data
    testListener.reset();

    // Create a pattern and matcher to invoke processEditCalendar
    String command = "edit calendar --name \"EditTest\" --property \"name\" \"NewName\"";
    Pattern pattern = Pattern.compile("edit\\scalendar\\s--name\\s\"(.*?)\"\\s--property\\s\"(.*?)\"\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processEditCalendar(matcher);

      // Verify notifications were sent
      assertTrue(testListener.wasCalendarChangedCalledWith("NewName"));
      assertEquals(1, testListener.getCalendarListChangedCount());

      // Check output contains success message
      String output = outputStream.toString();
      assertTrue(output.contains("EditTest property: name changed to: NewName"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests editing a calendar's timezone with listener notifications.
   */
  @Test
  public void testProcessEditCalendarTimezone() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create a calendar
    eventHandler.createCalender("TzTest", ZoneId.of("UTC"));

    // Reset tracker data
    testListener.reset();

    // Create a pattern and matcher to invoke processEditCalendar
    String command = "edit calendar --name \"TzTest\" --property \"timezone\" \"America/New_York\"";
    Pattern pattern = Pattern.compile("edit\\scalendar\\s--name\\s\"(.*?)\"\\s--property\\s\"(.*?)\"\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processEditCalendar(matcher);

      // Verify notifications were sent
      assertTrue(testListener.wasCalendarChangedCalledWith("TzTest"));

      // Check output contains success message
      String output = outputStream.toString();
      assertTrue(output.contains("TzTest property: timezone changed to: America/New_York"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests the processUseCalendar method with listener notifications.
   */
  @Test
  public void testProcessUseCalendar() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create a calendar
    eventHandler.createCalender("UseTestCal", ZoneId.of("UTC"));

    // Reset tracker data
    testListener.reset();

    // Create a pattern and matcher to invoke processUseCalendar
    String command = "use calendar --name \"UseTestCal\"";
    Pattern pattern = Pattern.compile("use\\scalendar\\s--name\\s\"(.*?)\".*?$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processUseCalendar(matcher);

      // Verify that appropriate methods were called
      assertTrue(testListener.wasCalendarChangedCalledWith("UseTestCal"));
      assertTrue(testListener.getEventsChangedCallCount() > 0);

      // Check output contains success message
      String output = outputStream.toString();
      assertTrue(output.contains("Changed to calendar: UseTestCal"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests error handling in use calendar with invalid name.
   */
  @Test
  public void testProcessUseCalendarError() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create a pattern and matcher to invoke processUseCalendar with non-existent calendar
    String command = "use calendar --name \"NonExistentCal\"";
    Pattern pattern = Pattern.compile("use\\scalendar\\s--name\\s\"(.*?)\".*?$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processUseCalendar(matcher);

      // Verify error message was sent to listener
      assertTrue(testListener.wasCommandOutputCalledWith("Error changing calendar"));

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Error changing events"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests the processPrintEventsOn method with listener notifications.
   */
  @Test
  public void testProcessPrintEventsOn() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create an event directly
    eventHandler.addEvent(new model.EventModel("PrintTest",
            LocalDateTime.parse("2025-10-10T10:00"),
            LocalDateTime.parse("2025-10-10T11:00"),
            "Test Description", false, false, "Test Location"));

    // Reset tracker data
    testListener.reset();

    // Create a pattern and matcher to invoke processPrintEventsOn
    String command = "print events on 2025-10-10";
    Pattern pattern = Pattern.compile("print\\sevents\\son\\s(.*?)$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processPrintEventsOn(matcher);

      // Verify the listener was called with the right messages
      assertTrue(testListener.wasCommandOutputCalledWith("Found 1 event on 2025-10-10"));

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Found 1 event on 2025-10-10"));
      assertTrue(output.contains("PrintTest"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests the processPrintEventsOn method with no events found.
   */
  @Test
  public void testProcessPrintEventsOnNoEvents() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Reset tracker data
    testListener.reset();

    // Create a pattern and matcher to invoke processPrintEventsOn
    String command = "print events on 2025-11-11"; // A date with no events
    Pattern pattern = Pattern.compile("print\\sevents\\son\\s(.*?)$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processPrintEventsOn(matcher);

      // Verify the listener was called with the right messages
      assertTrue(testListener.wasCommandOutputCalledWith("Found 0 events on 2025-11-11"));

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Found 0 events on 2025-11-11"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests error handling in edit event methods.
   */
  @Test
  public void testEditEventsNotFound() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create a pattern and matcher to invoke processEditEvents for a non-existent event
    String command = "edit events eventName \"NonExistentEvent\" from 2025-10-10T10:00 with \"NewName\"";
    Pattern pattern = Pattern.compile("edit\\sevents\\s(.*?)\\s\"(.*?)\"\\sfrom\\s(.*?)\\swith\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processEditEvents(matcher);

      // Verify error message was sent to listener
      assertTrue(testListener.wasCommandOutputCalledWith("No events named \"NonExistentEvent\" found"));

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("No events named \"NonExistentEvent\" found"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests the processEditEvent method with invalid time parameters.
   * This tests the error handling in processEditEvent method.
   */
  @Test
  public void testProcessEditEventInvalidTime() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Create an event directly
    eventHandler.addEvent(new model.EventModel("InvalidTimeTest",
            LocalDateTime.parse("2025-10-10T10:00"),
            LocalDateTime.parse("2025-10-10T11:00"),
            "Test Description", false, false, "Test Location"));

    // Reset tracker data
    testListener.reset();

    // Create a pattern and matcher with end time before start time
    String command = "edit event eventName \"InvalidTimeTest\" from 2025-10-11T10:00 to 2025-10-09T10:00 with \"NewName\"";
    Pattern pattern = Pattern.compile("edit\\sevent\\s(.*?)\\s\"(.*?)\"\\sfrom\\s(.*?)\\sto\\s(.*?)\\swith\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processEditEvent(matcher);

      // Verify error message was sent to listener
      assertTrue(testListener.wasCommandOutputCalledWith("End time cannot be before start time, event was not edited"));

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("End time cannot be before start time, event was not edited"));

      // Verify the event name was not changed
      assertEquals("InvalidTimeTest", eventHandler.getEventCollection().getEventList().get(0).getEventName());
    } else {
      fail("Pattern did not match the command");
    }
  }



  /**
   * Tests the processEditEvent method with an event that doesn't exist.
   * This tests the "no events found" error handling.
   */
  @Test
  public void testProcessEditEventNotFound() {
    // Setup
    eventHandler.addUIListener(testListener);

    // Reset tracker data
    testListener.reset();

    // Create a pattern and matcher for a non-existent event
    String command = "edit event eventName \"NonExistentEvent\" from 2025-10-10T10:00 to 2025-10-11T10:00 with \"NewName\"";
    Pattern pattern = Pattern.compile("edit\\sevent\\s(.*?)\\s\"(.*?)\"\\sfrom\\s(.*?)\\sto\\s(.*?)\\swith\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processEditEvent(matcher);

      // Verify error message was sent to listener
      assertTrue(testListener.wasCommandOutputCalledWith("No events named \"NonExistentEvent\" found"));

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("No events named \"NonExistentEvent\" found"));
    } else {
      fail("Pattern did not match the command");
    }
  }

  /**
   * Tests the processEditEvent method with an exception.
   * This tests the general exception handling in the method.
   */
  @Test
  public void testProcessEditEventException() {
    // Setup - use a spy to throw an exception
    // Since we can't use Mockito, we'll have to rely on causing a real exception

    eventHandler.addUIListener(testListener);

    // Create a pattern and matcher that will cause a parsing exception
    String command = "edit event eventName \"ErrorTest\" from INVALID_DATE to 2025-10-11T10:00 with \"NewName\"";
    Pattern pattern = Pattern.compile("edit\\sevent\\s(.*?)\\s\"(.*?)\"\\sfrom\\s(.*?)\\sto\\s(.*?)\\swith\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command if the pattern matches
    if (matcher.find()) {
      eventHandler.processEditEvent(matcher);

      // Verify error message was sent to listener
      assertTrue(testListener.wasCommandOutputCalledWith("Error editing event:"));

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Error editing event:"));
    } else {
      fail("Pattern did not match the command");
    }
  }
}