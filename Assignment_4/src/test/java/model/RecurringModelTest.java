package model;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for recurring event model functionality.
 */
public class RecurringModelTest {

  @Test
  public void testRecurringEventModelInitialization() {
    String eventName = "Team Meeting";
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 3, 25, 10, 0);
    LocalDateTime endDateTime =
            LocalDateTime.of(2025, 3, 25, 11, 0);
    boolean autoDecline = true;
    List<Character> recurringDays =
            Arrays.asList('M', 'W', 'F'); // Recurs on Monday, Wednesday, Friday
    Integer occurrences = 10;
    LocalDateTime recurrenceEndDateTime =
            LocalDateTime.of(2025, 4, 25, 10, 0);

    RecurringEventModel event = new RecurringEventModel(
            eventName, startDateTime, endDateTime, autoDecline,
            recurringDays, occurrences, recurrenceEndDateTime
    );

    // Assertions to check if the values are correctly assigned
    assertEquals(eventName, event.getEventName());
    assertEquals(startDateTime, event.getStartDateTime());
    assertEquals(endDateTime, event.getEndDateTime());
    assertTrue(event.isAutoDecline());
    assertEquals(recurringDays, event.getRecurringDays());
    assertEquals(occurrences, event.getOccurrences());
    assertEquals(recurrenceEndDateTime, event.getRecurrenceEndDateTime());
  }

}
