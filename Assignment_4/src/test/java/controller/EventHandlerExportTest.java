package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import view.componentsgui.CalendarUIListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Test class specifically for testing the export functionality in EventHandler.
 */
public class EventHandlerExportTest {

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

    // Clean up any test files
    File testFile = new File("test_export.csv");
    if (testFile.exists()) {
      testFile.delete();
    }
  }

  /**
   * Tests the export functionality with a non-existent file.
   */
  @Test
  public void testExportToNewFile() {
    // Create a test event
    eventHandler.addEvent(new model.EventModel(
            "Export Test Event",
            LocalDateTime.of(2025, 10, 10, 10, 0),
            LocalDateTime.of(2025, 10, 10, 11, 0),
            "Test Description",
            false,
            false,
            "Test Location"
    ));

    // Reset the listener
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for export command
    String fileName = "test_export.csv";
    String command = "export cal " + fileName;
    Pattern pattern = Pattern.compile("export\\scal\\s(.*?)$");
    Matcher matcher = pattern.matcher(command);

    // Delete the file if it exists to ensure we're testing creation
    File testFile = new File(fileName);
    if (testFile.exists()) {
      testFile.delete();
    }

    // Process the export command
    if (matcher.find()) {
      eventHandler.processExport(matcher);

      // Check output contains the export message
      String output = outputStream.toString();
      assertTrue("Should show exporting message", output.contains("Exporting to " + fileName));

      // Check the file creation message
      assertTrue("Should show file creation message", output.contains("File \"" + fileName + "\" did not exist"));
      assertTrue("Should notify listener about file creation",
              testListener.wasCommandOutputCalledWith("File \"" + fileName + "\" did not exist"));

      // Check success message
      assertTrue("Should show success message", output.contains("Successfully exported 1 events"));
      assertTrue("Should notify listener about export success",
              testListener.wasCommandOutputCalledWith("Successfully exported 1 events"));

      // Verify file exists
      File exportedFile = new File(fileName);
      assertTrue("File should exist after export", exportedFile.exists());
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests the export functionality with an existing file.
   */
  @Test
  public void testExportToExistingFile() {
    // Create a test event
    eventHandler.addEvent(new model.EventModel(
            "Export Test Event",
            LocalDateTime.of(2025, 10, 10, 10, 0),
            LocalDateTime.of(2025, 10, 10, 11, 0),
            "Test Description",
            false,
            false,
            "Test Location"
    ));

    // Reset the listener
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for export command
    String fileName = "test_export.csv";
    String command = "export cal " + fileName;
    Pattern pattern = Pattern.compile("export\\scal\\s(.*?)$");
    Matcher matcher = pattern.matcher(command);

    // Create the file first to ensure it exists
    try {
      File testFile = new File(fileName);
      testFile.createNewFile();
      assertTrue("File should exist before export", testFile.exists());
    } catch (Exception e) {
      fail("Failed to create test file: " + e.getMessage());
    }

    // Process the export command
    if (matcher.find()) {
      eventHandler.processExport(matcher);

      // Check output contains the export message
      String output = outputStream.toString();
      assertTrue("Should show exporting message", output.contains("Exporting to " + fileName));

      // Should NOT have file creation message
      assertFalse("Should not show file creation message", output.contains("File \"" + fileName + "\" did not exist"));

      // Check success message
      assertTrue("Should show success message", output.contains("Successfully exported 1 events"));
      assertTrue("Should notify listener about export success",
              testListener.wasCommandOutputCalledWith("Successfully exported 1 events"));
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests export with file creation error.
   */
  @Test
  public void testExportWithFileCreationError() {
    // This test simulates an error when creating a file
    // We can't easily force a real file creation error in a unit test,
    // so we'll use a special filename that causes generateCSV to throw an exception

    // Create a test event
    eventHandler.addEvent(new model.EventModel(
            "Export Test Event",
            LocalDateTime.of(2025, 10, 10, 10, 0),
            LocalDateTime.of(2025, 10, 10, 11, 0),
            "Test Description",
            false,
            false,
            "Test Location"
    ));

    // Reset the listener
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for export command
    // Use an invalid file path to cause an error
    String fileName = "/invalid/path/that/does/not/exist/test.csv";
    String command = "export cal " + fileName;
    Pattern pattern = Pattern.compile("export\\scal\\s(.*?)$");
    Matcher matcher = pattern.matcher(command);

    // Process the export command
    if (matcher.find()) {
      eventHandler.processExport(matcher);

      // Check output contains the export message
      String output = outputStream.toString();
      assertTrue("Should show exporting message", output.contains("Exporting to " + fileName));

      // Check error message
      assertTrue("Should show error message", output.contains("Error exporting calendar:") ||
              output.contains("Error creating file:"));

      // One of these should be true depending on whether the error occurs during
      // file creation or during CSV generation
      boolean creationError = testListener.wasCommandOutputCalledWith("Error creating file:");
      boolean exportError = testListener.wasCommandOutputCalledWith("Error exporting calendar:");

      assertTrue("Should notify listener about error", creationError || exportError);
    } else {
      fail("Command pattern didn't match");
    }
  }

  /**
   * Tests export with general exception.
   */
  @Test
  public void testExportWithGeneralException() {
    // Reset the listener
    testListener.reset();
    outputStream.reset();

    // Create a pattern and matcher for export command
    String fileName = "test_export.csv";
    String command = "export cal " + fileName;
    Pattern pattern = Pattern.compile("export\\scal\\s(.*?)$");
    Matcher matcher = pattern.matcher(command);

    // Make eventCollection null to force exception
    try {
      java.lang.reflect.Field field = EventHandler.class.getDeclaredField("eventCollection");
      field.setAccessible(true);
      Object originalCollection = field.get(eventHandler);
      field.set(eventHandler, null);

      // Process the export command
      if (matcher.find()) {
        eventHandler.processExport(matcher);

        // Check output contains the export message
        String output = outputStream.toString();
        assertTrue("Should show exporting message", output.contains("Exporting to " + fileName));

        // Check error message
        assertTrue("Should show error message", output.contains("Error exporting calendar:"));
        assertTrue("Should notify listener about error",
                testListener.wasCommandOutputCalledWith("Error exporting calendar:"));
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