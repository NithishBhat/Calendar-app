package model;

import java.time.ZoneId;

public interface Calendar {

  EventCollectionModel getEventCollection();

  ZoneId getTimeZone();
  void setTimeZone(ZoneId timeZone);
}

