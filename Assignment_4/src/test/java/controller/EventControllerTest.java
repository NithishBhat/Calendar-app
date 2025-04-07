package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the methods available in the event controller class.
 */
public class EventControllerTest {

  private final String testFilename = "test_commands.txt";
  private EventController eventController;
  private PrintStream originalSystemOut;
  private ByteArrayOutputStream testOut;

  @Before
  public void setUp() throws IOException {
    eventController = new EventController();

    originalSystemOut = System.out;
    testOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOut));
  }

  @After
  public void tearDown() {
    System.setOut(originalSystemOut);

    File file = new File(testFilename);
    if (file.exists()) {
      file.delete();
    }
  }

  @Test
  public void testHeadlessModeValidCommands() throws IOException {
    String commands = "create event \"test\" from 2025-10-10T07:00 to 2025-10-10T08:00\n" +
            "print events from 2025-10-10T07:00 to 2025-11-10T07:00\n" +
            "exit\n";

    writeToFile(testFilename, commands);
    eventController.headlessMode(new InputSource(testFilename));

    String output = testOut.toString();
    assertTrue(output.contains("\"test\"") && output.contains("successfully created"));
    assertTrue(output.contains("Bye"));
  }


  @Test
  public void testHeadlessModeFileNotExists() {
    File file = new File(testFilename);
    if (file.exists()) {
      file.delete();
    }

    eventController.headlessMode(new InputSource(testFilename));
    String output = testOut.toString();
    assertTrue(output.contains("File " + testFilename
            + " did not exist. A new file has been created"));
  }

  @Test
  public void testHeadlessModeInvalidCommand() throws IOException {
    String commands = "invalid command\nexit\n";
    writeToFile(testFilename, commands);

    eventController.headlessMode(new InputSource(testFilename));

    String output = testOut.toString();
    System.out.println(output);
    assertTrue(output.contains("Invalid command:"));
  }

  @Test
  public void testHeadlessModeNoExit() throws IOException {
    String commands = "create event \"Workout\" from 2025-10-10T07:00 to 2025-10-10T08:00\n";
    writeToFile(testFilename, commands);

    eventController.headlessMode(new InputSource(testFilename));

    String output = testOut.toString();
    assertTrue(output.contains("\u001B[31m[WARNING] \u001B[0mError: please type 'exit' " +
            "as the last command"));
  }

  @Test
  public void testHeadlessModeCalendar() throws IOException {
    String commands = "create calendar --name \"testCal\" --timezone \"America/New_York\" \n" +
            "use calendar --name \"testCal\" \n" +
            "create event \"Workout\" from 2025-10-10T07:00 to 2025-10-10T08:00\n" +
            "exit\n";
    writeToFile(testFilename, commands);

    eventController.headlessMode(new InputSource(testFilename));

    String output = testOut.toString();
    assertTrue(output.contains("testCal") && output.contains("America/New_York"));
    assertTrue(output.contains("\"Workout\"") && output.contains("successfully created"));
    assertTrue(output.contains("Bye"));
  }

  private void writeToFile(String filename, String content) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
      writer.write(content);
    }
  }

}