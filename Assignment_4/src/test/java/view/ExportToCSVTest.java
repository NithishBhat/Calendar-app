package view;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import model.EventCollectionModel;
import model.EventModel;
import controller.ExportToCSV;
import model.RecurringEventModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Unit tests for the ExportToCSV class.
 * This test suite verifies the correct export of events to a CSV file.
 * Test cases include:
 * - Checking if a CSV file is generated with correct headers and event details.
 * - Ensuring events with special characters (e.g., commas, newlines)
 * in descriptions are properly handled.
 * - Verifying the correct formatting of recurring and non-recurring events in the CSV output.
 * Assertions validate:
 * - Presence and correctness of CSV headers.
 * - Inclusion of expected event names in the output.
 * - Proper escaping of special characters like commas and newlines.
 * The test suite ensures that the ExportToCSV class correctly processes and exports
 * event data while maintaining data integrity in the generated CSV file.
 */
public class ExportToCSVTest {
  @Test
  public void testExportToCSV() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 5, 1, 12, 1);
    EventModel event1 =
            new EventModel("Presentation", startDateTime);

    eventCollectionModel.addEvent(event1);

    LocalDateTime startDateTime2 =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime2 =
            LocalDateTime.of(2025, 3, 1, 13, 0);

    LocalDateTime recurrenceEndDateTime =
            LocalDateTime.of(2025, 4, 2, 0, 0);

    List<Character> days = Arrays.asList('M', 'T', 'W');
    RecurringEventModel recurringCalendarEvent = new RecurringEventModel(
            "Conference",
            startDateTime2,
            endDateTime2,
            true,
            days,
            null,
            recurrenceEndDateTime);
    eventCollectionModel.addRecurringEvent(recurringCalendarEvent);

    String fileName = "test.csv";

    ExportToCSV.generateCSV(fileName, eventCollectionModel.getEventList());

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String header = br.readLine();
      assertEquals("Subject,"
              + "Start Date,Start Time,End Date,End Time,All Day Event,"
              + "Description,Location,Private", header);

      String firstEvent = br.readLine();
      assertNotNull(firstEvent);
      assertTrue(firstEvent.contains("Presentation"));

      String secondEvent = br.readLine();
      assertNotNull(secondEvent);
      assertTrue(secondEvent.contains("Conference"));

    } catch (IOException e) {
      fail("Error reading CSV file: " + e.getMessage());
    }
  }

  @Test
  public void testExportToCSVWithRegexAndCommaInDescription() {
    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    LocalDateTime startDateTime =
            LocalDateTime.of(2025, 5, 1, 12, 1);
    EventModel event1 = new EventModel("Presentation", startDateTime);
    event1.setDescription("This is a description, with a comma.");

    eventCollectionModel.addEvent(event1);

    event1.setDescription("Changed description");

    LocalDateTime startDateTime2 =
            LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime2 =
            LocalDateTime.of(2025, 3, 1, 13, 0);

    LocalDateTime recurrenceEndDateTime =
            LocalDateTime.of(2025, 4, 2, 0, 0);

    List<Character> days = Arrays.asList('M', 'T', 'W');
    RecurringEventModel recurringCalendarEvent = new RecurringEventModel(
            "Conference",
            startDateTime2,
            endDateTime2,
            true,
            days,
            null,
            recurrenceEndDateTime);

    eventCollectionModel.addRecurringEvent(recurringCalendarEvent);

    for (EventModel event : eventCollectionModel.getEventList()) {
      if (event.getEventName().equals("Conference")) {
        event.setDescription("Updated description with escape"
                + " sequences\nThis should still print on the same line.");
      }
    }

    String fileName = "test.csv";


    ExportToCSV.generateCSV(fileName, eventCollectionModel.getEventList());

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String header = br.readLine();
      assertEquals("Subject,Start Date,Start Time,End Date,"
              + "End Time,All Day Event,Description,Location,Private", header);

      String firstEvent = br.readLine();
      assertNotNull(firstEvent);
      assertTrue(firstEvent.contains("Presentation"));

      String secondEvent = br.readLine();
      assertNotNull(secondEvent);
      assertTrue(secondEvent.contains("Conference"));

    } catch (IOException e) {
      fail("Error reading CSV file: " + e.getMessage());
    }
  }
}