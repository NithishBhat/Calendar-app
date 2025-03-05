package Model;

import java.util.ArrayList;
import java.util.List;

public class RecurringEventCollectionModel {
  private List<RecurringEventModel> recurringEventList;

  public RecurringEventCollectionModel() {
    recurringEventList = new ArrayList<>();
  }

  public void addEvent(RecurringEventModel event) {
    recurringEventList.add(event);
  }

  public void removeEvent(RecurringEventModel event) {
    recurringEventList.remove(event);
  }

  public List<RecurringEventModel> getEventList() {
    return recurringEventList;
  }

}
