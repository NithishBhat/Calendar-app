package view;


import model.CalenderCollection;

public interface CalendarUIListener {
  void onCalendarChanged(String calendarName);
  void onEventsChanged(CalenderCollection calenderCollection);
  void onCalendarListChanged();
  void onCommandOutput(String output);
}