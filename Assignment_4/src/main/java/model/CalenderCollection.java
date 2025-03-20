package model;
import java.time.ZoneId;

import java.util.HashMap;


public class CalenderCollection {

private HashMap<String,CalenderModel> calenderCollection;

public CalenderCollection(){
  calenderCollection=new HashMap<>();
}

public HashMap<String,CalenderModel> getCalenderCollection(String calenderName){
  return calenderCollection;
}

public  void addCalender(String name, ZoneId timeZone){
  calenderCollection.put(name,new CalenderModel(new EventCollectionModel(),timeZone));
}

public EventCollectionModel getEventCollection(String name){
  return calenderCollection.get(name).getEventCollection();
}

public void removeCalenderByName(String name){
  calenderCollection.remove(name);
}

public void RemoveAllCalenders(){
  calenderCollection.clear();
}

public void ChangeCalenderName(String oldName,String newName){
  CalenderModel oldCalender=calenderCollection.get(oldName);
  calenderCollection.remove(oldName);
  calenderCollection.put(newName,oldCalender);
}


}
