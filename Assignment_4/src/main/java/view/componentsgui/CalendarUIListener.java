package view.componentsgui;

import model.CalenderCollection;

/**
 * Interface for listening to calendar-related events in the UI.
 * Classes implementing this interface can receive notifications about changes
 * in the calendar state, events, and command outputs.
 */
public interface CalendarUIListener {

  /**
   * Called when the current calendar changes.
   * Implementers should update their UI to reflect the newly selected calendar.
   *
   * @param calendarName The name of the newly selected calendar
   */
  void onCalendarChanged(String calendarName);

  /**
   * Called when the events in any calendar change.
   * Implementers should refresh event displays and related components.
   *
   * @param calenderCollection The updated calendar collection containing all calendars and events
   */
  void onEventsChanged(CalenderCollection calenderCollection);

  /**
   * Called when the list of available calendars changes.
   * Implementers should update any UI components that display the calendar list.
   */
  void onCalendarListChanged();

  /**
   * Called when a command produces output that should be displayed to the user.
   * Implementers should display this output in an appropriate UI component.
   *
   * @param output The command output text to display
   */
  void onCommandOutput(String output);
}