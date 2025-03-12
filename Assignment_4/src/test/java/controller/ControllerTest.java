package controller;

import Model.EventCollectionModel;
import View.EventView;
import org.junit.Before;
import org.junit.Test;

import Controller.EventController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ControllerTest {
  private EventController eventController;
  private EventView eventView;
  private EventCollectionModel eventCollection;

  @Before
  public void setUp() {
    eventView = new EventView();
    eventController = new EventController();
    eventCollection = new EventCollectionModel();
  }

  @Test
  public void testHeadless() {
    EventView view = new EventView();
    EventController eventController = new EventController();

  }

}