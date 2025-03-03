import org.junit.Test;

import java.time.LocalDateTime;

import Model.EventCollectionModel;
import Model.EventModel;

import static org.junit.Assert.assertEquals;

public class EventCollectionModelTest {

  @Test
  public void collisionPositive() {
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 1, 15, 0);
    EventModel eventModel = new EventModel("Conference", startDateTime, endDateTime, "Tech conference on AI",false);

    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    eventCollectionModel.addEvent(eventModel);



    LocalDateTime startDateTime_1 = LocalDateTime.of(2025, 3, 1, 10, 15);
    LocalDateTime endDateTime_1 = LocalDateTime.of(2025, 3, 1, 16, 0);

    eventCollectionModel.collisionSearch(startDateTime_1, endDateTime_1);
    assertEquals(true,eventCollectionModel.getEventList().size()>0);

  }

  @Test
  public void collisionnegative() {
    LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 1, 15, 0);
    EventModel eventModel = new EventModel("Conference", startDateTime, endDateTime, "Tech conference on AI", true);

    EventCollectionModel eventCollectionModel = new EventCollectionModel();
    eventCollectionModel.addEvent(eventModel);



    LocalDateTime startDateTime_1 = LocalDateTime.of(2025, 3, 1, 16, 5);
    LocalDateTime endDateTime_1 = LocalDateTime.of(2025, 3, 1, 16, 30);

    eventCollectionModel.collisionSearch(startDateTime_1, endDateTime_1);
    assertEquals(false,eventCollectionModel.getEventList().size()>0);

  }
}
