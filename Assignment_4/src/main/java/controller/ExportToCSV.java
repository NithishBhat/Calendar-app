package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import model.EventModel;


/**
 * ExportToCSV is responsible for generating a CSV file from a list of EventModel objects.
 * It formats the data in a way that can be easily opened and read in spreadsheet programs.
 */
public class ExportToCSV {

  /**
   * Generates a CSV file from the list of events.
   *
   * @param filepath The path where the CSV file will be saved.
   * @param events   The list of EventModel objects to be exported to CSV.
   */
  public static void generateCSV(String filepath, List<EventModel> events) {
    if (!events.isEmpty()) {
      try {
        File file = new File(filepath);
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(String.join(",", "Subject", "Start Date", "Start Time",
                "End Date", "End Time", "All Day Event", "Description", "Location", "Private"));
        bw.newLine();

        for (EventModel event : events) {
          LocalDate startDate = event.getStartDateTime().toLocalDate();
          LocalDate endDate = event.getEndDateTime().toLocalDate();

          LocalTime startTime = event.getStartDateTime().toLocalTime();
          LocalTime endTime = event.getEndDateTime().toLocalTime();

          String allDayEvent = String.valueOf(event.isAllDayEvent());

          String isPrivate = String.valueOf(event.getIsPrivate());

          String description = removeRegex(event.getDescription() ==
                  null ? "No Description" : event.getDescription());
          String location = removeRegex(event.getLocation() ==
                  null ? "No Location" : event.getLocation());


          //check for regex???
          //check for commas, replace with another delimiter?
          bw.write(event.getEventName());
          bw.write(", ");
          bw.write(startDate.toString());
          bw.write(", ");
          bw.write(startTime.toString());
          bw.write(", ");
          bw.write(endDate.toString());
          bw.write(", ");
          bw.write(endTime.toString());
          bw.write(", ");
          bw.write(allDayEvent);
          bw.write(", ");
          bw.write(description);
          bw.write(", ");
          bw.write(location);
          bw.write(", ");
          bw.write(isPrivate);
          bw.newLine();
        }
        System.out.println("CSV generated");
        bw.close();
      } catch (IOException e) {
        throw new RuntimeException("Error Writing to CSV File: " + e.getMessage());
      }
    } else {
      System.out.println("No Events in Calendar.");
    }
  }

  /**
   * Cleans the string by removing unwanted characters like
   * quotes, newlines, tabs, and carriage returns.
   * It also ensures that quotes within the string are escaped properly for CSV formatting.
   *
   * @param stringValue The string to be cleaned and formatted.
   * @return The cleaned and properly formatted string.
   */
  public static String removeRegex(String stringValue) {
    if (Objects.equals(stringValue, "null")) {
      return "";
    }

    stringValue = stringValue
            .replaceAll("\"", "\"\"")
            .replaceAll("\n", " ")
            .replaceAll("\t", " ")
            .replaceAll("\r", " ");
    return "\"" + stringValue + "\"";
  }
}
