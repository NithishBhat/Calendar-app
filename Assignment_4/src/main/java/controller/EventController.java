package controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_YELLOW = "\u001B[33m";

  private static final String infoString = ANSI_YELLOW + "[INFO] " + ANSI_RESET;
  private static final String warningString = ANSI_RED + "[WARNING] " + ANSI_RESET;


  /**
   * Constructs an EventController instance.
   * Initializes the event collection, command parser, and event handler.
   */
  public EventController() {
    parser = new CommandParser();
    eventHandler = new EventHandler();
  }

  public EventHandler getEventHandler() {
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
      commandMap.put("copyEventsBetween", () -> eventHandler
              .processCopyCalenderBetweenDates(matcher));
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
   * The method takes a list of command strings,
   * and processes them until an "exit" command is encountered.
   *
   * @param inputCommands The list of commands from the input source.
   */
  public void headlessMode(InputCommand inputCommands) {
    try {
      List<String> commands = inputCommands.getListOfCommands();
      for (String command : commands) {
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
      System.out.println(warningString + "Error: please type 'exit' as the last command");
    } catch (IOException e) {
      System.out.println(warningString + "Error reading commands: " + e.getMessage());
    }
  }
}
