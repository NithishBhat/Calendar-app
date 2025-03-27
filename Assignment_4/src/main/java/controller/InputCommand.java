package controller;

import java.io.IOException;
import java.util.List;

/**
 * Represents a list of commands from any source.
 */
public interface InputCommand {
  List<String> getListOfCommands() throws IOException;
}
