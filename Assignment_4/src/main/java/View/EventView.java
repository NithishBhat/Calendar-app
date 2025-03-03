package View;
import java.util.Scanner;
import java.util.regex.*;
public class EventView {

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    String input="";


    while (!input.equals("exit")) {
      System.out.print("Type Command: ");
      filterCommand(input);
      input = scanner.nextLine();
    }

    scanner.close();
  }


  private static void filterCommand(String command) {
    if (command.startsWith("create event")) {
      processCreateEvent(command);
    } else {
      System.out.println("Unknown command: " + command);
    }
  }

  private static void processCreateEvent(String command) {
    Boolean autoDecline;
    String regex = "create event --autoDecline (\\S+) from (\\S+) to (\\S+)";

    if (command.contains("autoDecline")) {
      autoDecline = true;
    }

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(command);

    String eventName = matcher.group(1);
    String startDateTime = matcher.group(2);
    String endDateTime = matcher.group(3);
    System.out.println("Creating event: " + eventName + " " + startDateTime + " " + endDateTime);

  }

  private static void processrecurringEvent(String command) {
    Boolean autoDecline;
    String regex = "create event --autoDecline (\\S+) from (\\S+) to (\\S+) repeats (\\S+) for (\\S+) times";

    if (command.contains("autoDecline")) {
      autoDecline = true;
    }

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(command);

    String eventName = matcher.group(1);
    String startDateTime = matcher.group(2);
    String endDateTime = matcher.group(3);
    String repeats = matcher.group(4);
    String times = matcher.group(5);
    System.out.println("Creating event: " + eventName + " " + startDateTime + " " + endDateTime + " " + repeats + " " + times );

  }

  private static void processCreateEventWithStartDateOnly(String command) {
    Boolean autoDecline;
    String regex = "create event --autoDecline (\\S+) on (\\S+)";

    if (command.contains("autoDecline")) {
      autoDecline = true;
    }

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(command);

    String eventName = matcher.group(1);
    String startDateTime = matcher.group(2);

    System.out.println("Creating event: " + eventName + " " + startDateTime );

  }



}


