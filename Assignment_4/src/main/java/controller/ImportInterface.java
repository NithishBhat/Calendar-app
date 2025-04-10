package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import model.EventModel;

/**
 * An interface that defines the contract for importing events from external sources.
 * Implementations of this interface should handle the specific logic needed to
 * read event data from different file formats or sources and convert them into
 * EventModel objects.
 */
public interface ImportInterface {

  /**
   * Imports events from a file and converts them to EventModel objects.
   *
   * @param file The file containing event data to be imported
   * @return A List of EventModel objects containing the imported events
   * @throws IOException If there is an error reading from the file or if the file format is invalid
   */
  List<EventModel> importEvents(File file) throws IOException;
}