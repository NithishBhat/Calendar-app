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

public class ImportFromCSV implements ImportInterface{

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

        String eventName = data[0].trim();
        String startDate = data[1].trim();
        String startTime = data[2].trim();
        String endDate = data[3].trim();
        String endTime = data[4].trim();
        boolean allDay = data[5].trim().equalsIgnoreCase("true");
        String description = data[6].trim();
        String location = data[7].trim();
        boolean isPrivate = data[8].trim().equalsIgnoreCase("true");

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

        events.add(new EventModel(eventName, start, end, description, isPrivate, true, location));
      }
    }
    return events;
  }
}
