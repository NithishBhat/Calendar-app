package view;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import view.utils.CommandGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommandGeneratorTest {

  private CommandGenerator commandGenerator;
  private Map<String, String> testValues;

  @Before
  public void setUp() {
    commandGenerator = new CommandGenerator();
    testValues = new HashMap<>();
  }

  @Test
  public void createCalendarCommand_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Calendar Name:", "WorkCalendar");
    testValues.put("Time Zone (area/location):", "America/New_York");

    // When
    String result = commandGenerator.createCalendarCommand(testValues);

    // Then
    assertEquals("create calendar --name WorkCalendar --timezone \"America/New_York\"", result);
  }

  @Test
  public void createCalendarCommand_WithSpecialChars_ShouldMaintainFormat() {
    // Given
    testValues.put("Calendar Name:", "Work-Calendar!");
    testValues.put("Time Zone (area/location):", "Europe/Paris");

    // When
    String result = commandGenerator.createCalendarCommand(testValues);

    // Then
    assertEquals("create calendar --name Work-Calendar! --timezone \"Europe/Paris\"", result);
  }

  @Test
  public void editCalendarCommand_ShouldGenerateCorrectCommand() {
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
  public void createEventCommand_BasicEvent_ShouldGenerateCorrectCommand() {
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
  public void createEventCommand_WithAutoDecline_ShouldIncludeFlag() {
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
  public void createEventCommand_AllDayEvent_ShouldUseCorrectFormat() {
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
  public void createEventCommand_WithDescription_ShouldIncludeDescription() {
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
  public void createEventCommand_WithLocation_ShouldIncludeLocation() {
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
  public void createEventCommand_WithPrivacy_Private_ShouldIncludePrivacySetting() {
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
  public void createEventCommand_WithPrivacy_Public_ShouldIncludePrivacySetting() {
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
  public void createEventCommand_RecurringWeeklyWithNTimes_ShouldGenerateCorrectCommand() {
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
  public void createEventCommand_RecurringWeeklyUntilDate_ShouldGenerateCorrectCommand() {
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
  public void createEventCommand_RecurringAllDayUntilDate_ShouldGenerateCorrectCommand() {
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
  public void createEventCommand_RecurringNoRadioButtonSelected_ShouldDefaultToNTimes() {
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
  public void createEventCommand_MultipleDays_ShouldConcatenateDaySymbols() {
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
  public void editEventCommand_ThisEventOnly_ShouldGenerateCorrectCommand() {
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
  public void editEventCommand_ThisEventLater_ShouldGenerateCorrectCommand() {
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
  public void editEventCommand_AllEvents_ShouldGenerateCorrectCommand() {
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
  public void printEventsCommand_OnDate_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Print Events on:", "2025-04-15");

    // When
    String result = commandGenerator.printEventsCommand(testValues);

    // Then
    assertEquals("print events on 2025-04-15", result);
  }

  @Test
  public void printEventsCommand_Interval_ShouldGenerateCorrectCommand() {
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
  public void exportCalendarCommand_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Filename:", "work-calendar.ics");

    // When
    String result = commandGenerator.exportCalendarCommand(testValues);

    // Then
    assertEquals("export cal work-calendar.ics", result);
  }

  @Test
  public void showStatusCommand_ShouldGenerateCorrectCommand() {
    // Given
    testValues.put("Status Date:", "2025-04-07");
    testValues.put("Status Time:", "15:30");

    // When
    String result = commandGenerator.showStatusCommand(testValues);

    // Then
    assertEquals("show status on 2025-04-07T15:30", result);
  }

  /**
   * Test to kill the conditional boundary mutation at line 88
   */
  @Test
  public void createEventCommand_RecurringEmptyWeekdays_ShouldNotAddRepeatsSection() {
    // Given
    testValues.put("Event Name", "Team Meeting");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "14:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "15:00");
    // Set all repeat days to false
    testValues.put("RepeatDay_M", "false");
    testValues.put("RepeatDay_T", "false");
    testValues.put("RepeatDay_W", "false");
    testValues.put("RepeatDay_R", "false");
    testValues.put("RepeatDay_F", "false");
    testValues.put("RepeatDay_S", "false");
    testValues.put("RepeatDay_U", "false");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Team Meeting\" from 2025-04-07T14:00 to 2025-04-07T15:00", result);

    // Explicitly verify there's no "repeats" in the output
    assertFalse(result.contains("repeats"));
  }

  /**
   * Test to kill the mutation for radio button selection logic
   */
  @Test
  public void createEventCommand_RadioButtonExplicitSelection_ShouldRespectSelection() {
    // Given
    testValues.put("Event Name", "Team Meeting");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "14:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "15:00");
    testValues.put("RepeatDay_W", "true"); // Wednesday only

    // Explicitly set both radio button values to test the logic
    testValues.put("RepeatNTimesRB", "false");
    testValues.put("RepeatUntilRB", "true");
    testValues.put("RepeatUntilDate", "2025-05-31");
    testValues.put("RepeatCount", "10"); // This should be ignored

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Team Meeting\" from 2025-04-07T14:00 to 2025-04-07T15:00 repeats W until 2025-05-31T15:00", result);

    // Verify it contains the "until" part and not the "for X times" part
    assertTrue(result.contains("until 2025-05-31"));
    assertFalse(result.contains("for 10 times"));
  }

  /**
   * Test the negated conditional on line 106
   */
  @Test
  public void createEventCommand_NullRepeatUntilValue_ShouldHandleGracefully() {
    // Given
    testValues.put("Event Name", "Team Meeting");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "14:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "15:00");
    testValues.put("RepeatDay_W", "true"); // Wednesday only

    // Set RepeatNTimesRB to false but don't set RepeatUntilRB at all
    testValues.put("RepeatNTimesRB", "false");
    // RepeatUntilRB is null
    testValues.put("RepeatCount", "10");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then - should default to the else branch - "for X times"
    assertEquals("create event \"Team Meeting\" from 2025-04-07T14:00 to 2025-04-07T15:00 repeats W for 10 times", result);
  }

  /**
   * Test to cover the "selected" string value for radio buttons
   */
  @Test
  public void createEventCommand_RadioButtonSelectedString_ShouldWorkProperly() {
    // Given
    testValues.put("Event Name", "Team Meeting");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "14:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "15:00");
    testValues.put("RepeatDay_W", "true"); // Wednesday only

    // Use "selected" string instead of "true"
    testValues.put("RepeatNTimesRB", "selected");
    testValues.put("RepeatUntilRB", "false");
    testValues.put("RepeatCount", "15");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Team Meeting\" from 2025-04-07T14:00 to 2025-04-07T15:00 repeats W for 15 times", result);
  }

  /**
   * Test boundary case of 1 weekday selected (boundary mutation)
   */
  @Test
  public void createEventCommand_ExactlyOneWeekdaySelected_ShouldAddRepeats() {
    // Given
    testValues.put("Event Name", "Monday Meeting");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "09:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "10:00");

    // Only set Monday
    testValues.put("RepeatDay_M", "true");
    testValues.put("RepeatDay_T", "false");
    testValues.put("RepeatDay_W", "false");
    testValues.put("RepeatDay_R", "false");
    testValues.put("RepeatDay_F", "false");
    testValues.put("RepeatDay_S", "false");
    testValues.put("RepeatDay_U", "false");

    testValues.put("RepeatNTimesRB", "true");
    testValues.put("RepeatCount", "5");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then
    assertEquals("create event \"Monday Meeting\" from 2025-04-07T09:00 to 2025-04-07T10:00 repeats M for 5 times", result);

    // Make sure weekdays.length() == 1 is handled properly
    assertTrue(result.contains("repeats M"));
  }

  /**
   * Test both radio button values being null
   */
  @Test
  public void createEventCommand_BothRadioButtonsNull_ShouldDefaultToNTimes() {
    // Given
    testValues.put("Event Name", "Team Meeting");
    testValues.put("AutoDecline", "false");
    testValues.put("AllDay", "false");
    testValues.put("Recurring", "true");
    testValues.put("StartDate", "2025-04-07");
    testValues.put("StartTime", "14:00");
    testValues.put("EndDate", "2025-04-07");
    testValues.put("EndTime", "15:00");
    testValues.put("RepeatDay_W", "true"); // Wednesday only

    // Don't set radio button values at all
    // testValues.put("RepeatNTimesRB", null);
    // testValues.put("RepeatUntilRB", null);
    testValues.put("RepeatCount", "7");

    // When
    String result = commandGenerator.createEventCommand(testValues);

    // Then - should default to the else branch - "for X times"
    assertEquals("create event \"Team Meeting\" from 2025-04-07T14:00 to 2025-04-07T15:00 repeats W for 7 times", result);
  }

  /**
   * This test specifically targets the debug output statements and conditional logic
   * by using a test spy approach to verify the behavior
   */
  @Test
  public void testDebugOutputAndConditionals() {
    // Redirect System.out to capture output
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outContent));

    try {
      // Given
      testValues.put("Event Name", "Debug Test Meeting");
      testValues.put("AutoDecline", "false");
      testValues.put("AllDay", "false");
      testValues.put("Recurring", "true");
      testValues.put("StartDate", "2025-04-07");
      testValues.put("StartTime", "14:00");
      testValues.put("EndDate", "2025-04-07");
      testValues.put("EndTime", "15:00");
      testValues.put("RepeatDay_W", "true");

      // Set values that will exercise the conditionals
      testValues.put("RepeatNTimesRB", "true");
      testValues.put("RepeatUntilRB", "false");
      testValues.put("RepeatCount", "3");

      // When
      String result = commandGenerator.createEventCommand(testValues);

      // Then
      // 1. Verify the debug output was produced
      String capturedOutput = outContent.toString();
      assertTrue("Debug output for RepeatNTimesRB missing",
              capturedOutput.contains("RepeatNTimesRB value: [true]"));
      assertTrue("Debug output for RepeatUntilRB missing",
              capturedOutput.contains("RepeatUntilRB value: [false]"));
      assertTrue("Selected radio button output missing",
              capturedOutput.contains("Selected radio button: N Times"));

      // 2. Verify the correct logic was applied
      assertEquals("create event \"Debug Test Meeting\" from 2025-04-07T14:00 to 2025-04-07T15:00 repeats W for 3 times", result);
    } finally {
      // Restore original System.out
      System.setOut(originalOut);
    }
  }

  /**
   * This test targets the "Until Date" branch of the debug output conditional
   */
  @Test
  public void testDebugOutputUntilDate() {
    // Redirect System.out to capture output
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outContent));

    try {
      // Given
      testValues.put("Event Name", "Debug Test Until");
      testValues.put("AutoDecline", "false");
      testValues.put("AllDay", "false");
      testValues.put("Recurring", "true");
      testValues.put("StartDate", "2025-04-07");
      testValues.put("StartTime", "14:00");
      testValues.put("EndDate", "2025-04-07");
      testValues.put("EndTime", "15:00");
      testValues.put("RepeatDay_F", "true");

      // Set values that will exercise the other conditional branch
      testValues.put("RepeatNTimesRB", "false");
      testValues.put("RepeatUntilRB", "true");
      testValues.put("RepeatUntilDate", "2025-06-30");

      // When
      String result = commandGenerator.createEventCommand(testValues);

      // Then
      // 1. Verify the debug output was produced with different values
      String capturedOutput = outContent.toString();
      assertTrue("Debug output for RepeatNTimesRB missing",
              capturedOutput.contains("RepeatNTimesRB value: [false]"));
      assertTrue("Debug output for RepeatUntilRB missing",
              capturedOutput.contains("RepeatUntilRB value: [true]"));
      assertTrue("Selected radio button output incorrect",
              capturedOutput.contains("Selected radio button: Until Date"));

      // 2. Verify the correct logic was applied
      assertEquals("create event \"Debug Test Until\" from 2025-04-07T14:00 to 2025-04-07T15:00 repeats F until 2025-06-30T15:00", result);
    } finally {
      // Restore original System.out
      System.setOut(originalOut);
    }
  }

  /**
   * This test targets the "None" branch of the debug output conditional
   */
  @Test
  public void testDebugOutputNoneSelected() {
    // Redirect System.out to capture output
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outContent));

    try {
      // Given
      testValues.put("Event Name", "Debug Test None");
      testValues.put("AutoDecline", "false");
      testValues.put("AllDay", "false");
      testValues.put("Recurring", "true");
      testValues.put("StartDate", "2025-04-07");
      testValues.put("StartTime", "14:00");
      testValues.put("EndDate", "2025-04-07");
      testValues.put("EndTime", "15:00");
      testValues.put("RepeatDay_M", "true");

      // Set both radio buttons to false or empty string
      testValues.put("RepeatNTimesRB", "");
      testValues.put("RepeatUntilRB", "");
      testValues.put("RepeatCount", "10");

      // When
      String result = commandGenerator.createEventCommand(testValues);

      // Then
      // 1. Verify the debug output for "None" branch
      String capturedOutput = outContent.toString();
      assertTrue("Debug output for None branch missing",
              capturedOutput.contains("Selected radio button: None"));

      // 2. Verify it defaulted to N times
      assertEquals("create event \"Debug Test None\" from 2025-04-07T14:00 to 2025-04-07T15:00 repeats M for 10 times", result);
    } finally {
      // Restore original System.out
      System.setOut(originalOut);
    }
  }

  /**
   * Test null inputs for both radio buttons
   */
  @Test
  public void testNullRadioButtons() {
    // Redirect System.out
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outContent));

    try {
      // Given
      testValues.put("Event Name", "Debug Test Null");
      testValues.put("AutoDecline", "false");
      testValues.put("AllDay", "false");
      testValues.put("Recurring", "true");
      testValues.put("StartDate", "2025-04-07");
      testValues.put("StartTime", "14:00");
      testValues.put("EndDate", "2025-04-07");
      testValues.put("EndTime", "15:00");
      testValues.put("RepeatDay_W", "true");

      // Don't add RepeatNTimesRB and RepeatUntilRB at all
      // This will cause the values to be null
      testValues.put("RepeatCount", "8");

      // When
      String result = commandGenerator.createEventCommand(testValues);

      // Then
      // 1. Verify debug output for null values
      String capturedOutput = outContent.toString();
      assertTrue("Debug output for null RepeatNTimesRB missing",
              capturedOutput.contains("RepeatNTimesRB value: [null]"));
      assertTrue("Debug output for null RepeatUntilRB missing",
              capturedOutput.contains("RepeatUntilRB value: [null]"));

      // 2. Verify it defaulted to the else branch
      assertEquals("create event \"Debug Test Null\" from 2025-04-07T14:00 to 2025-04-07T15:00 repeats W for 8 times", result);
    } finally {
      // Restore original System.out
      System.setOut(originalOut);
    }
  }
}