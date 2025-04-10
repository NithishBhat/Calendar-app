package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.EventModel;

/**
 * A class that implements the ImportInterface to import events from a CSV file.
 * This implementation reads event data from a CSV file and converts it into a list
 * of EventModel objects.
 */
public class ImportFromCSV implements ImportInterface {

  /**
   * Imports events from a CSV file and converts them to EventModel objects.
   *
   * @param file The CSV file to import events from. The file should have a header row
   *             and each subsequent row should contain event data in the following format:
   *             eventName,startDate,startTime,endDate,endTime,allDay,description,location,isPrivate
   * @return A List of EventModel objects containing the imported events
   * @throws IOException If there is an error reading the file or if the file is empty
   */
  @Override
  public List<EventModel> importEvents(File file) throws IOException {
    List<EventModel> events = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String headerLine = br.readLine();
      if (headerLine == null) {
        throw new IOException("Empty CSV file");
      }
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = line.split(",", -1);
        if (data.length < 9) {
          System.out.println("Skipping line due to insufficient fields: " + line);
          continue;
        }

        // Extract data from CSV fields
        String eventName = data[0].trim();
        String startDate = data[1].trim();
        String startTime = data[2].trim();
        String endDate = data[3].trim();
        String endTime = data[4].trim();
        boolean allDay = data[5].trim().equalsIgnoreCase("true");
        String description = data[6].trim();
        String location = data[7].trim();
        boolean isPrivate = data[8].trim().equalsIgnoreCase("true");

        // Parse dates and times
        LocalDateTime start;
        LocalDateTime end;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (allDay) {
          start = LocalDateTime.parse(startDate + " 00:00", formatter);
          end = LocalDateTime.parse(endDate + " 23:59", formatter);
        } else {
          start = LocalDateTime.parse(startDate + " " + startTime, formatter);
          end = LocalDateTime.parse(endDate + " " + endTime, formatter);
        }

        // Create and add the event model
        events.add(new EventModel(eventName, start, end, description, isPrivate, true, location));
      }
    }
    return events;
  }
}