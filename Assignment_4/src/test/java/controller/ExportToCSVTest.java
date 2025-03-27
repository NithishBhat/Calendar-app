package controller;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.EventModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * class test the functionality and the methods of the export csv class.
 */
public class ExportToCSVTest {
  private List<EventModel> events;

  @Before
  public void setUp() {
    events = new ArrayList<>();
    events.add(new EventModel("test",
            LocalDateTime.of(2025, 10, 13, 7, 0),
            LocalDateTime.of(2025, 10, 13, 8, 0),
            "testDesc", true, false, "MA"));

    //eventName, startDateTime, endDateTime, autoDecline
    events.add(new EventModel("test2",
            LocalDateTime.of(2025, 10, 14, 9, 0),
            LocalDateTime.of(2025, 10, 14, 10, 0),
            false));

    //eventName, startDateTime, endDateTime
    events.add(new EventModel("test3",
            LocalDateTime.of(2025, 11, 15, 10, 0),
            LocalDateTime.of(2025, 11, 15, 12, 0)));
  }

  @Test
  public void checkLines() {
    String filePath = "test_output.csv";

    ExportToCSV.generateCSV(filePath, events);

    File file = new File(filePath);
    assertTrue(file.exists());

    //add one for header
    int expectedNumberOfLines = 2;
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
      int actualNumberOfLines = 0;
      while (reader.readLine() != null) {
        actualNumberOfLines++;
        assertTrue(reader.readLine().contains(", "));
      }
      assertEquals(expectedNumberOfLines, actualNumberOfLines);
    } catch (IOException e) {
      fail("Error reading the generated CSV file: " + e.getMessage());
    }

    file.delete();
  }

  @Test
  public void checkForCommasInCSV() {
    String filePath = "test_output.csv";

    EventModel event1 = new EventModel("Event 1", LocalDate.of(2025, 3, 1).atTime(9, 0),
            LocalDate.of(2025, 3, 1).atTime(10, 0), null, true, true, "test");
    EventModel event2 = new EventModel("Event 2", LocalDate.of(2025, 3, 2).atTime(11, 0),
            LocalDate.of(2025, 3, 2).atTime(12, 0), null, true, true, "test");

    List<EventModel> events = Arrays.asList(event1, event2);

    ExportToCSV.generateCSV(filePath, events);

    File file = new File(filePath);
    assertTrue(file.exists());

    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("Subject")) {
          continue;
        }

        int commaCount = line.split(",").length - 1;

        assertEquals(8, commaCount);

        assertFalse("Line ends with a comma", line.endsWith(","));
      }
    } catch (IOException e) {
      fail("Error reading the generated CSV file: " + e.getMessage());
    }

    file.delete();
  }

  @Test
  public void testGenerateCSVFile() {
    String filePath = "test_output.csv";

    ExportToCSV.generateCSV(filePath, events);

    File file = new File(filePath);
    assertTrue(file.exists());

    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
      String line = reader.readLine();
      assertNotNull(line);

      assertEquals("Subject,Start Date,Start Time,End Date," +
              "End Time,All Day Event,Description,Location,Private", line);

      line = reader.readLine();
      assertNotNull(line);
      assertTrue(line.contains("test"));
      assertTrue(line.contains("2025-10-13"));
      assertTrue(line.contains("07:00"));
      assertTrue(line.contains("08:00"));
      assertTrue(line.contains("false"));
      assertTrue(line.contains("testDesc"));
      assertTrue(line.contains("MA"));
      assertTrue(line.contains("false"));

      line = reader.readLine();
      assertNotNull(line);
      assertTrue(line.contains("test2"));
      assertTrue(line.contains("2025-10-14"));
      assertTrue(line.contains("09:00"));
      assertTrue(line.contains("10:00"));
      assertTrue(line.contains("false"));
      assertTrue(line.contains("\"No Description\""));
      assertTrue(line.contains("\"No Location\""));
      assertTrue(line.contains("false"));

      line = reader.readLine();
      assertNotNull(line);
      assertTrue(line.contains("test3"));
      assertTrue(line.contains("2025-11-15"));
      assertTrue(line.contains("10:00"));
      assertTrue(line.contains("12:00"));
      assertTrue(line.contains("false"));
      assertTrue(line.contains("\"No Description\""));
      assertTrue(line.contains("\"No Location\""));
      assertTrue(line.contains("false"));
    } catch (IOException e) {
      fail("Error reading the generated CSV file: " + e.getMessage());
    }
    file.delete();
  }



  @Test
  public void testRemoveRegex() {
    String input = "Hello, \"World\"!\nThis is a test\twith\rregex.";
    String expected = "\"Hello, \"\"World\"\"! This is a test with regex.\"";
    String result = ExportToCSV.removeRegex(input);

    assertEquals(expected, result);
  }

  @Test
  public void testRemoveRegexWithNullString() {
    String result = ExportToCSV.removeRegex("null");
    assertEquals("", result);
  }

  @Test
  public void testGenerateCSVWithNullDescriptionAndLocation() throws IOException {
    EventModel event2 = new EventModel("Event with null fields",
            LocalDateTime.of(2025, 10, 11, 9, 0),
            LocalDateTime.of(
                    2025, 10, 11, 10, 0), null,
            false, true, null);
    events.add(event2);

    String filepath = "test_null_fields.csv";
    ExportToCSV.generateCSV(filepath, events);

    File file = new File(filepath);
    assertTrue(file.exists());

    String content = new String(Files.readAllBytes(Paths.get(filepath)));
    assertTrue(content.contains("Event with null fields"));
    assertTrue(content.contains("No Description"));
    assertTrue(content.contains("No Location"));

    file.delete();
  }

  @Test
  public void testGenerateCSVWithIOException() {
    String invalidPath = "/invalid/path/to/file.csv";
    Exception exception = assertThrows(RuntimeException.class, () -> {
      ExportToCSV.generateCSV(invalidPath, events);
    });
    assertTrue(exception.getMessage().contains("Error Writing to CSV File"));
  }

  @Test
  public void testGenerateCSVWithLargeNumberOfEvents() {
    List<EventModel> events = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      events.add(new EventModel("Event " + i,
              LocalDateTime.of(2025, 10, 13, 7, 0),
              LocalDateTime.of(2025, 10, 13, 8, 0),
              "Description " + i, true, false,
              "Location " + i));
    }

    String filePath = "test_large_output.csv";
    ExportToCSV.generateCSV(filePath, events);

    File file = new File(filePath);
    assertTrue(file.exists());

    int expectedNumberOfLines = events.size() + 1;
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
      int actualNumberOfLines = 0;
      while (reader.readLine() != null) {
        actualNumberOfLines++;
      }
      assertEquals(expectedNumberOfLines, actualNumberOfLines);
    } catch (IOException e) {
      fail("Error reading the generated CSV file: " + e.getMessage());
    }

    file.delete();
  }

  @Test
  public void testEventModelWithNullValues() {
    List<EventModel> nullValueEvents = new ArrayList<>();
    nullValueEvents.add(new EventModel("Event with Null Description",
            LocalDateTime.of(2025, 10, 13, 7, 0),
            LocalDateTime.of(2025, 10, 13, 8, 0),
            null, true, false, null));

    String filePath = "test_null_values_output.csv";
    ExportToCSV.generateCSV(filePath, nullValueEvents);

    File file = new File(filePath);
    assertTrue(file.exists());

    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
      String line = reader.readLine();
      assertNotNull(line);

      line = reader.readLine();
      assertTrue(line.contains("Event with Null Description"));
      assertTrue(line.contains("\"No Description\""));
      assertTrue(line.contains("\"No Location\""));
    } catch (IOException e) {
      fail("Error reading the generated CSV file: " + e.getMessage());
    }
    file.delete();
  }
}