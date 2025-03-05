package Model;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ExportToCSVTest {
    @Test
    public void testExportToCSV() {
        EventCollectionModel eventCollectionModel = new EventCollectionModel();
        LocalDateTime startDateTime = LocalDateTime.of(2025, 5, 1, 12, 1);
        EventModel event1 = new EventModel("Presentation", startDateTime);

        eventCollectionModel.addEvent(event1);

        LocalDateTime startDateTime2 = LocalDateTime.of(2025, 3, 1, 10, 0);
        LocalDateTime endDateTime2 = LocalDateTime.of(2025, 3, 1, 13, 0);

        LocalDateTime RecurrenceEndDateTime = LocalDateTime.of(2025, 4, 2, 0, 0);

        List<DayOfWeek> days = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY);
        RecurringEventModel recurringCalendarEvent = new RecurringEventModel(
                "Conference",
                startDateTime2,
                endDateTime2,
                true,
                days,
                -1,
                RecurrenceEndDateTime);
        eventCollectionModel.addRecurringEvent(recurringCalendarEvent);

        String fileName = "test.csv";
        String filepath = Paths.get(System.getProperty("user.dir"), fileName).toString();

        ExportToCSV.generateCSV(filepath, eventCollectionModel.getEventList());

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String header = br.readLine();
            assertEquals("Subject,Start Date,Start Time,End Date,End Time,All Day Event,Description,Location,Private", header);

            String firstEvent = br.readLine();
            assertNotNull(firstEvent);
            assertTrue(firstEvent.contains("Presentation"));

            String secondEvent = br.readLine();
            assertNotNull(secondEvent);
            assertTrue(secondEvent.contains("Conference"));

        } catch (IOException e) {
            fail("Error reading CSV file: " + e.getMessage());
        }
    }
}