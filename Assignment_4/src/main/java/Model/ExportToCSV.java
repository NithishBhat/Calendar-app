package Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class ExportToCSV {

    public static void generateCSV(String filepath, List<EventModel> events) {
        try{
            File file = new File(filepath);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(String.join(",", "Subject", "Start Date", "Start Time", "End Date",
                    "End Time", "All Day Event", "Description", "Location", "Private"));
            bw.newLine();

            for (EventModel event : events) {
                LocalDate startDate = event.getStartDateTime().toLocalDate();
                LocalDate endDate = event.getEndDateTime().toLocalDate();

                LocalTime startTime = event.getStartDateTime().toLocalTime();
                LocalTime endTime = event.getEndDateTime().toLocalTime();

                String AllDayEvent = String.valueOf(event.isAllDayEvent());

                String isPrivate = String.valueOf(event.getIsPrivate());

                String description = removeRegex(event.getDescription()==null?"No Description":event.getDescription());
                String location = removeRegex(event.getLocation()==null?"No Location":event.getLocation());


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
                bw.write(AllDayEvent);
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
    }

    public static String removeRegex(String stringValue) {
        if(Objects.equals(stringValue, "null")) {
            return "";
        }

        stringValue = stringValue.replaceAll("\"", "\"\"")
                .replaceAll("\n", " ")
                .replaceAll("\t", " ")
                .replaceAll("\r", " ");
        return "\"" + stringValue + "\"";
    }
}
