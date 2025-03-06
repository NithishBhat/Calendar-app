package View;
import java.util.Scanner;
import java.util.regex.*;
public class EventView {

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    String input="";


    while (!input.equals("exit")) {
      System.out.print("Type Command: ");
      input = scanner.nextLine();
    }

    scanner.close();
  }



}


