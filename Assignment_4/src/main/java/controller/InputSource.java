package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

/**
 * Reads commands from a specific file, if file is not present it creates a new file.
 */
public class InputSource implements InputCommand {
  private final String filename;

  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_YELLOW = "\u001B[33m";

  private static final String infoString = ANSI_YELLOW + "[INFO] " + ANSI_RESET;
  private static final String warningString = ANSI_RED + "[WARNING] " + ANSI_RESET;

  public InputSource(String filename) {
    this.filename = filename;
  }

  @Override
  public List<String> getListOfCommands() throws IOException {
    File file = new File(filename);

    if (!file.exists()) {
      try {
        if (file.createNewFile()) {
          System.out.println(infoString + "File " + filename
                  + " did not exist. A new file has been created at: \n"
                  + file.getAbsolutePath()
                  + "\n Please input commands to this file.");
          return Collections.emptyList();
        }
      } catch (IOException e) {
        System.out.println(warningString + "Error creating file: " + file.getAbsolutePath());
      }
    }
    return Files.readAllLines(file.toPath());
  }
}
