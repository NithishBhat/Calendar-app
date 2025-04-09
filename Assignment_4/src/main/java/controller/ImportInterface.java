package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import model.EventModel;

public interface ImportInterface {
  List<EventModel> importEvents(File file) throws IOException;
}
