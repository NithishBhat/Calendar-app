package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The EventController class is responsible for managing event-related commands.
 * It processes user commands, matches them against predefined patterns, and invokes
 * the corresponding event handler methods.
 */
public class EventController {

  private CommandParser parser;
  private EventHandler eventHandler;


  /**
   * Constructs an EventController instance.
   * Initializes the event collection, command parser, and event handler.
   */
  public EventController() {
    parser = new CommandParser();
    eventHandler = new EventHandler();
  }

  public EventHandler getEventHandler(){
    return eventHandler;
  }


  /**
   * Processes a given command string by matching it against predefined patterns.
   * If a matching pattern is found, the corresponding event handler method is executed.
   *
   * @param command The command string to be processed.
   */
  public void filterCommand(String command) {

    Matcher matcher = parser.parseCommand(command);

    if (matcher != null) {
      // Get the pattern that matched
      String pattern = matcher.pattern().toString();

      // Map the pattern string to the command
      Map<String, Runnable> commandMap = new HashMap<>();
      commandMap.put("createCalendar", () -> eventHandler.processCreateCalendar(matcher));
      commandMap.put("editCalendar", () -> eventHandler.processEditCalendar(matcher));
      commandMap.put("useCalendar", () -> eventHandler.processUseCalendar(matcher));
      commandMap.put("processCopyEventOn", () -> eventHandler.processCopyEventOn(matcher));
      commandMap.put("copyEventsOn", () -> eventHandler.processCopyEventsOn(matcher));
      commandMap.put("copyEventsBetween", () -> eventHandler.processCopyCalenderBetweenDates(matcher));
      commandMap.put("createEventPattern", () -> eventHandler.processCreateEvent(matcher));
      commandMap.put("createRecurringEventN", () -> eventHandler
              .processCreateRecurringEventN(matcher));
      commandMap.put("createRecurringEventEndDate", () -> eventHandler
              .processCreateRecurringEventEndDate(matcher));
      commandMap.put("createWholeDayEvent", () -> eventHandler.processCreateAllDayEvent(matcher));
      commandMap.put("createRecurringWholeDayEventN", () -> eventHandler
              .processCreateAllDayRecurring(matcher));
      commandMap.put("createRecurringWholeDayEventEnd", () -> eventHandler
              .processCreateRecurringWholeDayEventEnd(matcher));
      commandMap.put("editEvent", () -> eventHandler.processEditEvent(matcher));
      commandMap.put("editEvents", () -> eventHandler.processEditEvents(matcher));
      commandMap.put("editEventsByName", () -> eventHandler.processEditEventsByName(matcher));
      commandMap.put("printEventsOn", () -> eventHandler.processPrintEventsOn(matcher));
      commandMap.put("printEventsBetween", () -> eventHandler.processPrintEventsBetween(matcher));
      commandMap.put("export", () -> eventHandler.processExport(matcher));
      commandMap.put("show", () -> eventHandler.processShowStatus(matcher));

      // go through the map and call method if there is a match
      for (Map.Entry<String, Pattern> entry : parser.getPatterns().entrySet()) {
        if (entry.getValue().matcher(command).matches()) {
          commandMap.get(entry.getKey()).run(); // Execute command
          return;
        }
      }
    }
  }

  /**
   * Reads and processes commands from a file in a headless mode.
   * The method ensures the file exists, reads commands line by line,
   * and processes them until an "exit" command is encountered.
   *
   * @param filename The name of the file containing commands.
   */
  public void headlessMode(String filename) {
    File file = new File(filename);  // Force correct path

    // Ensure the file exists
    if (!file.exists()) {
      try {
        if (file.createNewFile()) {
          System.out.println("File " + filename
                  + " did not exist. A new file has been created at: \n"
                  + file.getAbsolutePath()
                  + "\nPlease input commands to this file.");
          return;
        }
      } catch (IOException e) {
        System.out.println("Error creating file: " + file.getAbsolutePath());
        return;
      }
    }

    // Use 'file' to explicitly read from 'src/res/'
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String command;
      while ((command = reader.readLine()) != null) {
        if (command.isEmpty()) {
          continue;
        }
        if (command.equals("exit")) {
          System.out.println("Bye");
          return;
        }
        try {
          filterCommand(command);
        } catch (Exception e) {
          System.out.println(e.getMessage());
          return;
        }
      }
      System.out.println("Error: please type 'exit' as last command");
    } catch (IOException e) {
      System.out.println("Unable to open file: " + file.getAbsolutePath());
    }
  }
}
