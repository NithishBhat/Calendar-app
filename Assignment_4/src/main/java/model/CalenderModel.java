package model;
import java.time.ZoneId;

public class CalenderModel {

  private EventCollectionModel eventCollection;
  private ZoneId TimeZone;

  public CalenderModel(EventCollectionModel eventCollection, ZoneId TimeZone) {
    this.eventCollection = eventCollection;
    this.TimeZone = TimeZone==null?ZoneId.of("America/New_York"):TimeZone;
  }

  public EventCollectionModel getEventCollection() {
    return eventCollection;
  }

  public void setEventCollection(EventCollectionModel eventCollection) {
    this.eventCollection = eventCollection;
  }
  public ZoneId getTimeZone() {
    return TimeZone;
  }

  public void setTimeZone(ZoneId TimeZone) {
   if(TimeZone==null)
   {throw new IllegalArgumentException("TimeZone provided is null");
   }
   this.TimeZone = TimeZone;
  }

}
