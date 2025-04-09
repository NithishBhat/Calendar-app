package controller;

import model.EventModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import view.CalendarUIListener;

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
 * Test class specifically for testing the processEditEvents and processEditEventsByName methods
 * in EventHandler.
 */
public class EventHandlerEditEventsTest {

  private EventHandler eventHandler;
  private TestCalendarUIListener testListener;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  /**
   * Custom implementation of CalendarUIListener for testing.
   */
  private static class TestCalendarUIListener implements CalendarUIListener {
    private final List<String> commandOutputCalls = new ArrayList<>();
    private int eventsChangedCount = 0;

    @Override
    public void onCalendarChanged(String calendarName) {
      // Not needed for this test
    }

    @Override
    public void onEventsChanged(model.CalenderCollection calenderCollection) {
      eventsChangedCount++;
    }

    @Override
    public void onCalendarListChanged() {
      // Not needed for this test
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

    public int getEventsChangedCount() {
      return eventsChangedCount;
    }

    public void reset() {
      commandOutputCalls.clear();
      eventsChangedCount = 0;
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
   * Tests processEditEvents with a single event.
   * Tests lines 622-635.
   */
  @Test
  public void testProcessEditEventsSingleEvent() {
    // Create a test event
    EventModel event = new EventModel(
            "EditEventsTest",
            LocalDateTime.of(2025, 10, 10, 10, 0),
            LocalDateTime.of(2025, 10, 10, 11, 0),
            "Original Description",
            false,
            false,
            "Original Location"
    );
    eventHandler.addEvent(event);

    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for edit events command
    String command = "edit events description \"EditEventsTest\" from 2025-10-01T00:00 with \"Updated Description\"";
    Pattern pattern = Pattern.compile("edit\\sevents\\s(.*?)\\s\"(.*?)\"\\sfrom\\s(.*?)\\swith\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command
    if (matcher.find()) {
      eventHandler.processEditEvents(matcher);

      // Verify notifications were sent
      assertEquals(1, testListener.getEventsChangedCount());

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Successfully edited description to \"Updated Description\" for 1 occurrence of event \"EditEventsTest\""));

      // Check listener notification
      assertTrue(testListener.wasCommandOutputCalledWith(
              "Successfully edited description to \"Updated Description\" for 1 occurrence of event \"EditEventsTest\""));

      // Verify the event was actually updated
      assertEquals("Updated Description", event.getDescription());
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests processEditEvents with multiple events.
   * Tests the plural message formatting in lines 627-631.
   */
  @Test
  public void testProcessEditEventsMultipleEvents() {
    // Create multiple test events with the same name
    EventModel event1 = new EventModel(
            "MultiEditTest",
            LocalDateTime.of(2025, 10, 10, 10, 0),
            LocalDateTime.of(2025, 10, 10, 11, 0),
            "Original Description",
            false,
            false,
            "Original Location"
    );
    EventModel event2 = new EventModel(
            "MultiEditTest",
            LocalDateTime.of(2025, 10, 15, 10, 0),
            LocalDateTime.of(2025, 10, 15, 11, 0),
            "Original Description",
            false,
            false,
            "Original Location"
    );
    eventHandler.addEvent(event1);
    eventHandler.addEvent(event2);

    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for edit events command
    String command = "edit events location \"MultiEditTest\" from 2025-10-01T00:00 with \"New Location\"";
    Pattern pattern = Pattern.compile("edit\\sevents\\s(.*?)\\s\"(.*?)\"\\sfrom\\s(.*?)\\swith\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command
    if (matcher.find()) {
      eventHandler.processEditEvents(matcher);

      // Verify notifications were sent
      assertEquals(1, testListener.getEventsChangedCount());

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Successfully edited location to \"New Location\" for 2 occurrences of event \"MultiEditTest\""));

      // Check listener notification
      assertTrue(testListener.wasCommandOutputCalledWith(
              "Successfully edited location to \"New Location\" for 2 occurrences of event \"MultiEditTest\""));

      // Verify both events were actually updated
      assertEquals("New Location", event1.getLocation());
      assertEquals("New Location", event2.getLocation());
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests processEditEvents with no matching events.
   * Tests the error handling in lines 615-620.
   */
  @Test
  public void testProcessEditEventsNoEvents() {
    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for edit events command with non-existent event
    String command = "edit events description \"NonExistentEvent\" from 2025-10-01T00:00 with \"Updated Description\"";
    Pattern pattern = Pattern.compile("edit\\sevents\\s(.*?)\\s\"(.*?)\"\\sfrom\\s(.*?)\\swith\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command
    if (matcher.find()) {
      eventHandler.processEditEvents(matcher);

      // Verify no events changed notification was sent
      assertEquals(0, testListener.getEventsChangedCount());

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("No events named \"NonExistentEvent\" found from 2025-10-01T00:00 onwards"));

      // Check listener notification
      assertTrue(testListener.wasCommandOutputCalledWith(
              "No events named \"NonExistentEvent\" found from 2025-10-01T00:00 onwards"));
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests processEditEvents with an exception.
   * Tests the exception handling in lines 636-640.
   */
  @Test
  public void testProcessEditEventsException() {
    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher that will cause an exception
    String command = "edit events description \"TestEvent\" from INVALID_DATE with \"Updated Description\"";
    Pattern pattern = Pattern.compile("edit\\sevents\\s(.*?)\\s\"(.*?)\"\\sfrom\\s(.*?)\\swith\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command
    if (matcher.find()) {
      eventHandler.processEditEvents(matcher);

      // Verify no events changed notification was sent
      assertEquals(0, testListener.getEventsChangedCount());

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Error editing events:"));

      // Check listener notification
      assertTrue(testListener.wasCommandOutputCalledWith("Error editing events:"));
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests processEditEventsByName with a single event.
   * Tests lines 664-676.
   */
  @Test
  public void testProcessEditEventsByNameSingleEvent() {
    // Create a test event
    EventModel event = new EventModel(
            "EditByNameTest",
            LocalDateTime.of(2025, 10, 10, 10, 0),
            LocalDateTime.of(2025, 10, 10, 11, 0),
            "Original Description",
            false,
            false,
            "Original Location"
    );
    eventHandler.addEvent(event);

    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for edit events by name command
    String command = "edit events description \"EditByNameTest\" \"Updated Description\"";
    Pattern pattern = Pattern.compile("edit\\sevents\\s(.*?)\\s\"(.*?)\"\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command
    if (matcher.find()) {
      eventHandler.processEditEventsByName(matcher);

      // Verify notifications were sent
      assertEquals(1, testListener.getEventsChangedCount());

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Successfully edited description to \"Updated Description\" for 1 occurrence of event \"EditByNameTest\""));

      // Check listener notification
      assertTrue(testListener.wasCommandOutputCalledWith(
              "Successfully edited description to \"Updated Description\" for 1 occurrence of event \"EditByNameTest\""));

      // Verify the event was actually updated
      assertEquals("Updated Description", event.getDescription());
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests processEditEventsByName with multiple events.
   * Tests the plural message formatting in lines 669-673.
   */
  @Test
  public void testProcessEditEventsByNameMultipleEvents() {
    // Create multiple test events with the same name
    EventModel event1 = new EventModel(
            "MultiNameEditTest",
            LocalDateTime.of(2025, 10, 10, 10, 0),
            LocalDateTime.of(2025, 10, 10, 11, 0),
            "Original Description",
            false,
            false,
            "Original Location"
    );
    EventModel event2 = new EventModel(
            "MultiNameEditTest",
            LocalDateTime.of(2025, 10, 15, 10, 0),
            LocalDateTime.of(2025, 10, 15, 11, 0),
            "Original Description",
            false,
            false,
            "Original Location"
    );
    eventHandler.addEvent(event1);
    eventHandler.addEvent(event2);

    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for edit events by name command
    String command = "edit events location \"MultiNameEditTest\" \"New Location\"";
    Pattern pattern = Pattern.compile("edit\\sevents\\s(.*?)\\s\"(.*?)\"\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command
    if (matcher.find()) {
      eventHandler.processEditEventsByName(matcher);

      // Verify notifications were sent
      assertEquals(1, testListener.getEventsChangedCount());

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("Successfully edited location to \"New Location\" for 2 occurrences of event \"MultiNameEditTest\""));

      // Check listener notification
      assertTrue(testListener.wasCommandOutputCalledWith(
              "Successfully edited location to \"New Location\" for 2 occurrences of event \"MultiNameEditTest\""));

      // Verify both events were actually updated
      assertEquals("New Location", event1.getLocation());
      assertEquals("New Location", event2.getLocation());
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests processEditEventsByName with no matching events.
   * Tests the error handling in lines 657-662.
   */
  @Test
  public void testProcessEditEventsByNameNoEvents() {
    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for edit events by name command with non-existent event
    String command = "edit events description \"NonExistentEvent\" \"Updated Description\"";
    Pattern pattern = Pattern.compile("edit\\sevents\\s(.*?)\\s\"(.*?)\"\\s\"(.*?)\"$");
    Matcher matcher = pattern.matcher(command);

    // Process the command
    if (matcher.find()) {
      eventHandler.processEditEventsByName(matcher);

      // Verify no events changed notification was sent
      assertEquals(0, testListener.getEventsChangedCount());

      // Check console output
      String output = outputStream.toString();
      assertTrue(output.contains("No events named \"NonExistentEvent\" found"));

      // Check listener notification
      assertTrue(testListener.wasCommandOutputCalledWith(
              "No events named \"NonExistentEvent\" found"));
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests processEditEventsByName with an exception.
   * Tests the exception handling in lines 677-681.
   */
  @Test
  public void testProcessEditEventsByNameException() {
    // Reset the listener and output
    testListener.reset();
    outputStream.reset();

    // Make eventCollection null to force exception
    try {
      java.lang.reflect.Field field = EventHandler.class.getDeclaredField("eventCollection");
      field.setAccessible(true);
      Object originalCollection = field.get(eventHandler);
      field.set(eventHandler, null);

      // Create a pattern and matcher
      String command = "edit events description \"TestEvent\" \"Updated Description\"";
      Pattern pattern = Pattern.compile("edit\\sevents\\s(.*?)\\s\"(.*?)\"\\s\"(.*?)\"$");
      Matcher matcher = pattern.matcher(command);

      // Process the command
      if (matcher.find()) {
        eventHandler.processEditEventsByName(matcher);

        // Verify no events changed notification was sent
        assertEquals(0, testListener.getEventsChangedCount());

        // Check console output
        String output = outputStream.toString();
        assertTrue(output.contains("Error editing events:"));

        // Check listener notification
        assertTrue(testListener.wasCommandOutputCalledWith("Error editing events:"));
      } else {
        fail("Command pattern didn't match");
      }

      // Restore the original eventCollection
      field.set(eventHandler, originalCollection);
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }
}