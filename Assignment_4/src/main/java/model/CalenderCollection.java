package model;
import java.time.ZoneId;

import java.util.HashMap;


public class CalenderCollection {

private static HashMap<String,CalenderModel> calenderCollection;

private CalenderCollection(){
  calenderCollection=new HashMap<>();
}

public static HashMap<String,CalenderModel> getCalenderCollection(){
  return calenderCollection;
}

public static void addCalender(String name,EventCollectionModel calender,ZoneId timeZone ){
  calenderCollection.put(name,new CalenderModel(calender,timeZone));
}

public static EventCollectionModel getCalendar(String name){
  return calenderCollection.get(name).getEventCollection();
}

public static void removeCalenderByName(String name){
  calenderCollection.remove(name);
}

public static void RemoveAllCalenders(){
  calenderCollection.clear();
}

public static void ChangeCalenderName(String oldName,String newName){
  CalenderModel oldCalender=calenderCollection.get(oldName);
  calenderCollection.remove(oldName);
  calenderCollection.put(newName,oldCalender);
}


}
