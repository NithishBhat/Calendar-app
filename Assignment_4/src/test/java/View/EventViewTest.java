package View;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.*;

public class EventViewTest {
    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Before
    public void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @After
    public void restoreSystemIO() {
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
    }

    @Test
    public void testRunInteractiveMode_ValidCommand() {
        String simulatedInput = "create  event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00\nexport cal test.csv\nexit\n";
        testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        EventView eventView = new EventView();
        eventView.runInteractiveMode();

        String output = testOut.toString();
        assertTrue(output.contains("Enter Command:"));
        assertTrue(output.contains("Goodbye!"));
    }
}