package view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.Map;


import view.utils.CommandGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CommandGeneratorTest {

  private CommandGenerator commandGenerator;
  private Map<String, String> testValues;

  @BeforeEach
  void setUp() {
    commandGenerator = new CommandGenerator();
    testValues = new HashMap<>();
  }

  @Test
  void createCalendarCommand_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Calendar Name:", "WorkCalendar");
    testValues.put("Time Zone (area/location):", "America/New_York");

    // When
    String result = commandGenerator.createCalendarCommand(testValues);

    // Then
    assertEquals("create calendar --name WorkCalendar --timezone \"America/New_York\"", result);
  }

  @Test
  void createCalendarCommand_WithSpecialChars_ShouldMaintainFormat() {
    // Given
    testValues.put("Calendar Name:", "Work-Calendar!");
    testValues.put("Time Zone (area/location):", "Europe/Paris");

    // When
    String result = commandGenerator.createCalendarCommand(testValues);

    // Then
    assertEquals("create calendar --name Work-Calendar! --timezone \"Europe/Paris\"", result);
  }

  @Test
  void editCalendarCommand_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Calendar Name:", "WorkCalendar");
    testValues.put("Property to Edit:", "timezone");
    testValues.put("New Value:", "Europe/London");

    // When
    String result = commandGenerator.editCalendarCommand(testValues);

    // Then
    assertEquals("edit calendar --name \"WorkCalendar\" --property \"timezone\" \"Europe/London\"", result);
  }

  @Test
  void createEventCommand_BasicEvent_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Event Name", "Team Meeting");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "false");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "14:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "15:00");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Team Meeting\" from 2025-04-07T14:00 to 2025-04-07T15:00", result);
  }

  @Test
  void createEventCommand_WithAutoDecline_ShouldIncludeFlag() {
    // Given
    testValues.put("Event Name", "Team Meeting");
    testValues.put("AutoDecline", "true");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "false");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "14:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "15:00");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event --autoDecline \"Team Meeting\" from 2025-04-07T14:00 to 2025-04-07T15:00", result);
  }

  @Test
  void createEventCommand_AllDayEvent_ShouldUseCorrectFormat() {
    // Given
    testValues.put("Event Name", "Conference Day");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "true");
    testValues.put("Recurring", "false");
    testValues.put("StartDate", "2025-04-15");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Conference Day\" on 2025-04-15", result);
  }

  @Test
  void createEventCommand_WithDescription_ShouldIncludeDescription() {
    // Given
    testValues.put("Event Name", "Team Meeting");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "false");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "14:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "15:00");
    testValues.put("Description", "Weekly team sync");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Team Meeting\" from 2025-04-07T14:00 to 2025-04-07T15:00 description:\"Weekly team sync\"", result);
  }

  @Test
  void createEventCommand_WithLocation_ShouldIncludeLocation() {
    // Given
    testValues.put("Event Name", "Team Meeting");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "false");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "14:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "15:00");
    testValues.put("Location", "Conference Room A");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Team Meeting\" from 2025-04-07T14:00 to 2025-04-07T15:00 location:\"Conference Room A\"", result);
  }

  @Test
  void createEventCommand_WithPrivacy_Private_ShouldIncludePrivacySetting() {
    // Given
    testValues.put("Event Name", "Doctor Appointment");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "false");
    testValues.put("StartDate", "2025-04-10");
    testValues.put("StartTime", "09:00");
    testValues.put("EndDate", "2025-04-10");
    testValues.put("EndTime", "10:00");
    testValues.put("Privacy", "Private");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Doctor Appointment\" from 2025-04-10T09:00 to 2025-04-10T10:00 privacy:\"true\"", result);
  }

  @Test
  void createEventCommand_WithPrivacy_Public_ShouldIncludePrivacySetting() {
    // Given
    testValues.put("Event Name", "Team Lunch");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "false");
    testValues.put("StartDate", "2025-04-10");
    testValues.put("StartTime", "12:00");
    testValues.put("EndDate", "2025-04-10");
    testValues.put("EndTime", "13:00");
    testValues.put("Privacy", "Public");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Team Lunch\" from 2025-04-10T12:00 to 2025-04-10T13:00 privacy:\"false\"", result);
  }

  @Test
  void createEventCommand_RecurringWeeklyWithNTimes_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Event Name", "Weekly 1-on-1");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "10:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "10:30");
    testValues.put("RepeatDay_M", "true");
    testValues.put("RepeatDay_T", "false");
    testValues.put("RepeatDay_W", "false");
    testValues.put("RepeatDay_R", "false");
    testValues.put("RepeatDay_F", "false");
    testValues.put("RepeatDay_S", "false");
    testValues.put("RepeatDay_U", "false");
    testValues.put("RepeatNTimesRB", "true");
    testValues.put("RepeatUntilRB", "false");
    testValues.put("RepeatCount", "10");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Weekly 1-on-1\" from 2025-04-07T10:00 to 2025-04-07T10:30 repeats M for 10 times", result);
  }

  @Test
  void createEventCommand_RecurringWeeklyUntilDate_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Event Name", "Team Standup");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "09:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "09:15");
    testValues.put("RepeatDay_M", "true");
    testValues.put("RepeatDay_T", "true");
    testValues.put("RepeatDay_W", "true");
    testValues.put("RepeatDay_R", "true");
    testValues.put("RepeatDay_F", "true");
    testValues.put("RepeatDay_S", "false");
    testValues.put("RepeatDay_U", "false");
    testValues.put("RepeatNTimesRB", "false");
    testValues.put("RepeatUntilRB", "true");
    testValues.put("RepeatUntilDate", "2025-06-30");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Team Standup\" from 2025-04-07T09:00 to 2025-04-07T09:15 repeats MTWRF until 2025-06-30T09:15", result);
  }

  @Test
  void createEventCommand_RecurringAllDayUntilDate_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Event Name", "Code Freeze");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "true");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-30");
    testValues.put("RepeatDay_F", "true");
    testValues.put("RepeatDay_M", "false");
    testValues.put("RepeatDay_T", "false");
    testValues.put("RepeatDay_W", "false");
    testValues.put("RepeatDay_R", "false");
    testValues.put("RepeatDay_S", "false");
    testValues.put("RepeatDay_U", "false");
    testValues.put("RepeatNTimesRB", "false");
    testValues.put("RepeatUntilRB", "true");
    testValues.put("RepeatUntilDate", "2025-12-31");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Code Freeze\" on 2025-04-30 repeats F until 2025-12-31", result);
  }

  @Test
  void createEventCommand_RecurringNoRadioButtonSelected_ShouldDefaultToNTimes() {
    // Given
    testValues.put("Event Name", "Recurring Meeting");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "13:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "14:00");
    testValues.put("RepeatDay_W", "true");
    testValues.put("RepeatDay_M", "false");
    testValues.put("RepeatDay_T", "false");
    testValues.put("RepeatDay_R", "false");
    testValues.put("RepeatDay_F", "false");
    testValues.put("RepeatDay_S", "false");
    testValues.put("RepeatDay_U", "false");
    // No radio button selected
    testValues.put("RepeatCount", "5");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Recurring Meeting\" from 2025-04-07T13:00 to 2025-04-07T14:00 repeats W for 5 times", result);
  }

  @Test
  void createEventCommand_MultipleDays_ShouldConcatenateDaySymbols() {
    // Given
    testValues.put("Event Name", "Weekend Check-in");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-05");
    testValues.put("StartTime", "10:00");
    testValues.put("EndDate", "2025-04-05");
    testValues.put("EndTime", "11:00");
    testValues.put("RepeatDay_M", "false");
    testValues.put("RepeatDay_T", "false");
    testValues.put("RepeatDay_W", "false");
    testValues.put("RepeatDay_R", "false");
    testValues.put("RepeatDay_F", "false");
    testValues.put("RepeatDay_S", "true");
    testValues.put("RepeatDay_U", "true");
    testValues.put("RepeatNTimesRB", "true");
    testValues.put("RepeatCount", "12");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Weekend Check-in\" from 2025-04-05T10:00 to 2025-04-05T11:00 repeats SU for 12 times", result);
  }

  @Test
  void editEventCommand_ThisEventOnly_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Property to Edit", "title");
    testValues.put("Event Name", "Team Meeting");
    testValues.put("New Value", "Department Meeting");
    testValues.put("ThisEventOnlyRB", "true");
    testValues.put("ThisEventLaterRB", "false");
    testValues.put("AllEventsRB", "false");
    testValues.put("FromDate", "2025-04-10");
    testValues.put("FromTime", "14:00");
    testValues.put("ToDate", "2025-04-10");
    testValues.put("ToTime", "15:00");

    // When
    String result = commandGenerator.editEventCommand(testValues);

    // Then
    assertEquals("edit event title \"Team Meeting\" from 2025-04-10T14:00 to 2025-04-10T15:00 with \"Department Meeting\"", result);
  }

  @Test
  void editEventCommand_ThisEventLater_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Property to Edit", "location");
    testValues.put("Event Name", "Team Meeting");
    testValues.put("New Value", "Conference Room B");
    testValues.put("ThisEventOnlyRB", "false");
    testValues.put("ThisEventLaterRB", "true");
    testValues.put("AllEventsRB", "false");
    testValues.put("FromDate", "2025-04-10");
    testValues.put("FromTime", "14:00");

    // When
    String result = commandGenerator.editEventCommand(testValues);

    // Then
    assertEquals("edit events location \"Team Meeting\" from 2025-04-10T14:00 with \"Conference Room B\"", result);
  }

  @Test
  void editEventCommand_AllEvents_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Property to Edit", "description");
    testValues.put("Event Name", "Team Meeting");
    testValues.put("New Value", "Updated agenda: project status and planning");
    testValues.put("ThisEventOnlyRB", "false");
    testValues.put("ThisEventLaterRB", "false");
    testValues.put("AllEventsRB", "true");

    // When
    String result = commandGenerator.editEventCommand(testValues);

    // Then
    assertEquals("edit events description \"Team Meeting\" \"Updated agenda: project status and planning\"", result);
  }

  @Test
  void printEventsCommand_OnDate_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Print Events on:", "2025-04-15");

    // When
    String result = commandGenerator.printEventsCommand(testValues);

    // Then
    assertEquals("print events on 2025-04-15", result);
  }

  @Test
  void printEventsCommand_Interval_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Print Events on:", "");
    testValues.put("Interval From Date:", "2025-04-01");
    testValues.put("Interval From Time:", "09:00");
    testValues.put("Interval To Date:", "2025-04-30");
    testValues.put("Interval To Time:", "17:00");

    // When
    String result = commandGenerator.printEventsCommand(testValues);

    // Then
    assertEquals("print events from 2025-04-01T09:00 to 2025-04-30T17:00", result);
  }

  @Test
  void exportCalendarCommand_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Filename:", "work-calendar.ics");

    // When
    String result = commandGenerator.exportCalendarCommand(testValues);

    // Then
    assertEquals("export cal work-calendar.ics", result);
  }

  @Test
  void showStatusCommand_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Status Date:", "2025-04-07");
    testValues.put("Status Time:", "15:30");

    // When
    String result = commandGenerator.showStatusCommand(testValues);

    // Then
    assertEquals("show status on 2025-04-07T15:30", result);
  }
}