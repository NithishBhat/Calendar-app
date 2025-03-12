package View;
import Controller.EventController;

import java.util.Scanner;
import java.util.regex.*;
public class EventView {

  private EventController eventController;

  public EventView() {
    this.eventController = new EventController();
  }

  public void runInteractiveMode() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter Command: ");

    while (true) {
      String command = scanner.nextLine();
      if (command.equals("exit")) {
        System.out.println("Goodbye!");
        break;
      }
      eventController.filterCommand(command);
    }
    scanner.close();
  }

  public void runHeadlessMode(String filename) {
    eventController.headlessMode(filename);
  }

  public static void main(String[] args) {
    if (args.length < 2 || !args[0].equalsIgnoreCase("--mode")){
      System.out.println("Usage: java EventView --mode [interactive|headless filename]");
      return;
    }
    EventView eventView = new EventView();

    if (args[1].equalsIgnoreCase("interactive")) {
      eventView.runInteractiveMode();
    } else if (args[1].equalsIgnoreCase("headless") && args.length == 3) {
      eventView.runHeadlessMode(args[2]);
    } else {
      System.out.println("Usage: java EventView --mode [interactive|headless filename]");
    }
  }
}


