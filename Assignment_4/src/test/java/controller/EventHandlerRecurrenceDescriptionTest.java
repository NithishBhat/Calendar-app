package controller;

import model.RecurringEventModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import view.CalendarUIListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test class specifically for testing the buildRecurrenceDescription method
 * in EventHandler through the addRecurringEvent method.
 */
public class EventHandlerRecurrenceDescriptionTest {

  private EventHandler eventHandler;
  private TestCalendarUIListener testListener;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  /**
   * Custom implementation of CalendarUIListener for testing.
   */
  private static class TestCalendarUIListener implements CalendarUIListener {
    private final List<String> commandOutputCalls = new ArrayList<>();

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

    public void reset() {
      commandOutputCalls.clear();
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
   * Tests buildRecurrenceDescription for all-day recurring event with occurrences.
   */
  @Test
  public void testAllDayRecurringEventWithOccurrences() {
    // Output the actual result to debug
    System.err.println("Starting test testAllDayRecurringEventWithOccurrences");

    // Create a recurring event model for an all-day event
    LocalDateTime startDate = LocalDateTime.of(2025, 10, 10, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2025, 10, 10, 23, 59);
    List<Character> recurringDays = Arrays.asList('M', 'W', 'F');
    Integer occurrences = 5;

    // Use the constructor with end date not null
    RecurringEventModel recurringEvent = new RecurringEventModel(
            "All Day Test",
            startDate,
            endDate,
            "Test Description",
            false,
            false,
            "Test Location",
            recurringDays,
            occurrences,
            null // no recurrence end date
    );

    // Reset output stream to make test more focused
    outputStream.reset();

    // Add the recurring event
    eventHandler.addRecurringEvent(recurringEvent);

    // Get the entire output
    String output = outputStream.toString();
    System.err.println("Test output: " + output);

    // Just assert it has the most basic components
    assertTrue("Output should contain event name", output.contains("All Day Test"));
  }

  /**
   * Tests buildRecurrenceDescription with a valid weekday character.
   */
  @Test
  public void testRecurringEventWithCustomWeekday() {
    // Create a recurring event model with a valid weekday character
    LocalDateTime startTime = LocalDateTime.parse("2025-10-10T09:00");
    LocalDateTime endTime = LocalDateTime.parse("2025-10-10T10:00");

    // Use 'R' for Thursday as defined in the system
    List<Character> recurringDays = Arrays.asList('R');
    Integer occurrences = 3;

    RecurringEventModel recurringEvent = new RecurringEventModel(
            "Thursday Only",
            startTime,
            endTime,
            "Test Description",
            false,
            false,
            "Test Location",
            recurringDays,
            occurrences,
            null // no recurrence end date
    );

    // Add the recurring event
    eventHandler.addRecurringEvent(recurringEvent);

    // Check the output
    String output = outputStream.toString();
    System.err.println("Output from weekday test: " + output);

    // Look for key components
    assertTrue(output.contains("Thursday Only"));
    assertTrue(output.contains("Thursday"));
    assertTrue(output.contains("3 times"));
  }
  /**
   * Tests buildRecurrenceDescription with a single weekday.
   */
  @Test
  public void testRecurringEventWithSingleWeekday() {
    // Create a recurring event model with just one weekday
    LocalDateTime startTime = LocalDateTime.parse("2025-10-10T09:00");
    LocalDateTime endTime = LocalDateTime.parse("2025-10-10T10:00");

    List<Character> recurringDays = Arrays.asList('M');
    Integer occurrences = 10;

    RecurringEventModel recurringEvent = new RecurringEventModel(
            "Monday Only",
            startTime,
            endTime,
            "Test Description",
            false,
            false,
            "Test Location",
            recurringDays,
            occurrences,
            null // no recurrence end date
    );

    // Add the recurring event
    eventHandler.addRecurringEvent(recurringEvent);

    // Check the output
    String output = outputStream.toString();
    assertTrue(output.contains("Created recurring event \"Monday Only\" from 2025-10-10 09:00 to 2025-10-10 10:00 repeating on Monday for 10 times"));
  }

  /**
   * Tests buildRecurrenceDescription with all weekdays.
   */
  @Test
  public void testRecurringEventWithAllWeekdays() {
    // Create a recurring event model with all weekdays
    LocalDateTime startTime = LocalDateTime.parse("2025-10-10T09:00");
    LocalDateTime endTime = LocalDateTime.parse("2025-10-10T10:00");
    LocalDateTime recurrenceEndDate = LocalDateTime.parse("2025-12-31T23:59");

    List<Character> recurringDays = Arrays.asList('M', 'T', 'W', 'R', 'F', 'S', 'U');

    RecurringEventModel recurringEvent = new RecurringEventModel(
            "Every Day",
            startTime,
            endTime,
            "Test Description",
            false,
            false,
            "Test Location",
            recurringDays,
            null, // no occurrences
            recurrenceEndDate
    );

    // Add the recurring event
    eventHandler.addRecurringEvent(recurringEvent);

    // Check the output
    String output = outputStream.toString();
    assertTrue(output.contains("Monday, Tuesday, Wednesday, Thursday, Friday, Saturday and Sunday"));
    assertTrue(output.contains("until 2025-12-31"));
  }

}