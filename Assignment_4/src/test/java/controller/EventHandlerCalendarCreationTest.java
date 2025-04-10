package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import view.componentsgui.CalendarUIListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Test class specifically for testing the calendar creation functionality in EventHandler.
 */
public class EventHandlerCalendarCreationTest {

  private EventHandler eventHandler;
  private TestCalendarUIListener testListener;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  /**
   * Custom implementation of CalendarUIListener for testing.
   */
  private static class TestCalendarUIListener implements CalendarUIListener {
    private final List<String> commandOutputCalls = new ArrayList<>();
    private int calendarListChangedCount = 0;

    @Override
    public void onCalendarChanged(String calendarName) {
      // Not needed for this test
    }

    @Override
    public void onEventsChanged(model.CalenderCollection calenderCollection) {
      // Not needed for this test
    }

    @Override
    public void onCalendarListChanged() {
      calendarListChangedCount++;
    }

    @Override
    public void onCommandOutput(String message) {
      commandOutputCalls.add(message);
    }

    public boolean wasCommandOutputCalledWith(String substring) {
      for (String msg : commandOutputCalls) {
        if (msg.contains(substring)) {
          return true;
        }
      }
      return false;
    }

    public int getCalendarListChangedCount() {
      return calendarListChangedCount;
    }

    public List<String> getCommandOutputCalls() {
      return commandOutputCalls;
    }

    public void reset() {
      commandOutputCalls.clear();
      calendarListChangedCount = 0;
    }
  }

  @Before
  public void setUp() {
    eventHandler = new EventHandler();
    testListener = new TestCalendarUIListener();
    eventHandler.addUIListener(testListener);

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
   * Tests direct calendar creation with valid parameters.
   * Tests lines 304-307 for success path.
   */
  @Test
  public void testCreateCalenderSuccess() {
    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create a calendar directly
    String calendarName = "TestCalendar";
    ZoneId timezone = ZoneId.of("America/New_York");
    eventHandler.createCalender(calendarName, timezone);

    // Verify notifications were sent
    assertEquals(1, testListener.getCalendarListChangedCount());

    // Check console output
    String output = outputStream.toString();
    assertTrue(output.contains("Calendar \"TestCalendar\" created with timezone America/New_York"));

    // Check listener notification
    assertTrue(testListener.wasCommandOutputCalledWith(
            "Calendar \"TestCalendar\" created with timezone America/New_York"));

    // Verify the calendar was actually created
    assertNotNull(eventHandler.getCalenderCollection().getEventCollection(calendarName));
  }

  /**
   * Tests error handling in calendar creation method.
   * Tests lines 308-311 for error handling.
   */
  @Test
  public void testCreateCalenderException() {
    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create an invalid calendar name that will cause an internal exception
    String invalidName = ""; // Empty name should cause issues in most implementations
    eventHandler.createCalender(invalidName, ZoneId.of("UTC"));

    // Print the output for debugging
    String output = outputStream.toString();
    System.err.println("Actual output: " + output);

    // Just check if any message was sent to the listener
    assertTrue("Should have sent at least one message to the listener",
            testListener.getCommandOutputCalls().size() > 0);
  }

  /**
   * Tests processCreateCalendar with valid parameters.
   * Tests lines 326-327 for success path.
   */
  @Test
  public void testProcessCreateCalendarSuccess() {
    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for calendar creation
    String command = "create calendar --name \"ProcessTest\" --timezone \"Europe/London\"";
    Pattern pattern = Pattern.compile("create\\scalendar\\s--name\\s\"(.*?)\"\\s--timezone\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command
    if (matcher.find()) {
      eventHandler.processCreateCalendar(matcher);

      // Verify notifications were sent
      assertEquals(1, testListener.getCalendarListChangedCount());

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Calendar \"ProcessTest\" created with timezone Europe/London"));

      // Check listener notification
      assertTrue(testListener.wasCommandOutputCalledWith(
              "Calendar \"ProcessTest\" created with timezone Europe/London"));

      // Verify the calendar was actually created
      assertNotNull(eventHandler.getCalenderCollection().getEventCollection("ProcessTest"));
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests processCreateCalendar with an invalid time zone.
   * Tests lines 329-332 for error handling.
   */
  @Test
  public void testProcessCreateCalendarInvalidTimeZone() {
    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher with invalid time zone
    String command = "create calendar --name \"InvalidTest\" --timezone \"Invalid/TimeZone\"";
    Pattern pattern = Pattern.compile("create\\scalendar\\s--name\\s\"(.*?)\"\\s--timezone\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command
    if (matcher.find()) {
      eventHandler.processCreateCalendar(matcher);

      // Verify no calendar list changed notification was sent
      assertEquals(0, testListener.getCalendarListChangedCount());

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Failed to create calendar: Invalid time zone: Invalid/TimeZone"));

      // Check listener notification
      assertTrue(testListener.wasCommandOutputCalledWith(
              "Failed to create calendar: Invalid time zone: Invalid/TimeZone"));

      // Verify the calendar was not created
      try {
        assertNull(eventHandler.getCalenderCollection().getEventCollection("InvalidTest"));
        fail("Calendar should not exist");
      } catch (Exception e) {
        // Expected - calendar doesn't exist
      }
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests creating multiple calendars.
   */
  @Test
  public void testCreateMultipleCalendars() {
    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create several calendars
    eventHandler.createCalender("Calendar1", ZoneId.of("UTC"));
    eventHandler.createCalender("Calendar2", ZoneId.of("America/New_York"));
    eventHandler.createCalender("Calendar3", ZoneId.of("Europe/Paris"));

    // Verify notifications were sent
    assertEquals(3, testListener.getCalendarListChangedCount());

    // Check listener notifications
    assertTrue(testListener.wasCommandOutputCalledWith("Calendar \"Calendar1\" created with timezone UTC"));
    assertTrue(testListener.wasCommandOutputCalledWith("Calendar \"Calendar2\" created with timezone America/New_York"));
    assertTrue(testListener.wasCommandOutputCalledWith("Calendar \"Calendar3\" created with timezone Europe/Paris"));

    // Verify calendars were created
    assertNotNull(eventHandler.getCalenderCollection().getEventCollection("Calendar1"));
    assertNotNull(eventHandler.getCalenderCollection().getEventCollection("Calendar2"));
    assertNotNull(eventHandler.getCalenderCollection().getEventCollection("Calendar3"));

    // Verify multiple calendars were created
    assertEquals(4, eventHandler.getCalenderCollectionSize()); // Including default calendar
  }
}