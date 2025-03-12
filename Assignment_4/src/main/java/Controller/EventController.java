package Controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Model.EventCollectionModel;
import View.EventView;
public class EventController {

  private EventCollectionModel eventCollection;
  private CommandParser parser;
  private EventHandler eventHandler;

  public EventController() {
    eventCollection = new EventCollectionModel();
    parser = new CommandParser();
    eventHandler = new EventHandler(eventCollection);
  }

  public void filterCommand(String command) {

    Matcher matcher = parser.parseCommand(command);

    if (matcher != null) {
      // Get the pattern that matched
      String pattern = matcher.pattern().toString();

      // Map the pattern string to the command
      Map<String, Runnable> commandMap = new HashMap<>();
      commandMap.put("createEventPattern", () -> eventHandler.processCreateEvent(matcher));
      commandMap.put("createRecurringEventN", () -> eventHandler.processCreateRecurringEventN(matcher));
      commandMap.put("createRecurringEventEndDate", () -> eventHandler.processCreateRecurringEventEndDate(matcher));
      commandMap.put("createWholeDayEvent", () -> eventHandler.processCreateAllDayEvent(matcher));
      commandMap.put("createRecurringWholeDayEventN", () -> eventHandler.processCreateAllDayRecurring(matcher));
      commandMap.put("createRecurringWholeDayEventEnd", () -> eventHandler.processCreateRecurringWholeDayEventEnd(matcher));
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

      // If no pattern was matched
      System.out.println("No matching command for the pattern: " + pattern);
    } else {
      System.out.println("Invalid command format" + command);
    }
  }

  public void headlessMode(String filename) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String command;
      while ((command = reader.readLine()) != null) {
        if (command.isEmpty()){
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
    } catch (IOException e){
      System.out.println("Unable to open file " + filename);
    }
  }
}
