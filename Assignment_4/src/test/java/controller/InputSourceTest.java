package controller;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for InputSource Methods.
 */
public class InputSourceTest {

  @Test
  public void testInputSource() throws IOException {
    File tempFile = File.createTempFile("testCommands", ".txt");
    tempFile.deleteOnExit();

    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write("test command \n");
    }

    InputSource inputSource = new InputSource(tempFile.getAbsolutePath());
    List<String> commands = inputSource.getListOfCommands();

    assertEquals(1, commands.size());
    assertEquals("test command ", commands.get(0));
  }

  @Test
  public void testInputSourceStringReader() {
    String stringAsFile = "command1\ncommand2\ncommand3\n";
    BufferedReader reader = new BufferedReader(new StringReader(stringAsFile));

    List<String> commands = Arrays.asList(reader.lines().toArray(String[]::new));

    assertEquals(3, commands.size());
    assertEquals("command1", commands.get(0));
    assertEquals("command2", commands.get(1));
    assertEquals("command3", commands.get(2));
  }

  @Test
  public void testFileCreationAndLogging() throws IOException {
    String filename = "testCommands123.txt";

    InputSource inputSource = new InputSource(filename);
    inputSource.getListOfCommands();


    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
    System.out.println(inputSource.getListOfCommands());
    String output = outputStream.toString().trim();

    assertTrue(output.contains("[]"));

    File file = new File(filename);
    if (file.exists()) {
      file.delete();
    }

    System.setOut(originalOut);
  }
}