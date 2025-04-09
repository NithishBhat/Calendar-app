package view;

import controller.EventController;
import controller.InputSource;
import view.utils.CommandGenerator;

import java.util.Scanner;

/**
 * The EventView class that handles the user interface for the event management system.
 * Implements the View component in the MVC architecture.
 */
public class EventView {

  private EventController eventController;

  /**
   * Constructor that initializes a new EventController.
   */
  public EventView() {
    this.eventController = new EventController();
  }

  /**
   * Runs the application in interactive mode, accepting user commands continuously
   * until the user enters "exit".
   */
  public void runInteractiveMode() {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.print("Enter Comma" +
              "" +
              "nd: ");
      String command = scanner.nextLine();
      if (command.equals("exit")) {
        System.out.println("Goodbye!");
        break;
      }
      try {
        eventController.filterCommand(command);
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
        break;
      }
    }
    scanner.close();
  }

  /**
   * Runs the application in headless mode, processing commands from a specified file.
   *
   * @param filename the path to the file containing commands to process
   */
  public void runHeadlessMode(String filename) {
    eventController.headlessMode(new InputSource(filename));
  }

  public void runGUIMode() {
    CommandGenerator commandGenerator = new CommandGenerator();
    CalendarUI ui = new CalendarUI(eventController, commandGenerator);
    eventController.getEventHandler().addUIListener(ui);
    ui.start();
  }

  private static void printUsage() {
    System.out.println("Usage:");
    System.out.println("  java -jar Program.jar");
    System.out.println("  java -jar Program.jar --mode interactive");
    System.out.println("  java -jar Program.jar --mode headless <filename>");
  }

  /**
   * Main method for running the application. Processes command-line arguments to determine the
   * operating mode.
   *
   * @param args command-line arguments in the format: --mode [interactive|headless filename]
   */
  public static void main(String[] args) {
    EventView eventView = new EventView();

    if (args.length == 0) {
      // No args → Launch GUI by default
      eventView.runGUIMode();
    } else if (args.length >= 2 && args[0].equalsIgnoreCase("--mode")) {
      if (args[1].equalsIgnoreCase("interactive")) {
        eventView.runInteractiveMode();
      } else if (args[1].equalsIgnoreCase("headless") && args.length == 3) {
        eventView.runHeadlessMode(args[2]);
      } else {
        printUsage();
      }
    } else {
      printUsage();
    }
  }
}


