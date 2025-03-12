package View;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class EventViewTest {
    private final InputStream sysIn = System.in;
    private final PrintStream sysOut = System.out;
    private ByteArrayInputStream inContent;
    private ByteArrayOutputStream outContent;

    @Before
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setIn(sysIn);
        System.setOut(sysOut);
    }

    @Test
    public void testRunInteractiveMode() {


        EventView eventView = new EventView();
        eventView.runInteractiveMode();

        String consoleOutput = outContent.toString();
        System.out.println("Captured Output:\n" + consoleOutput);

        String simulatedUserInput = "create event \"hello world\" from 2025-10-10T10:00 to 2025-10-10T11:00"
                +"\nexport cal test.csv\n"+"\nexit\n";
        inContent = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(inContent);

        assertTrue(consoleOutput.contains("Enter Command:"));
        assertTrue(consoleOutput.contains("Goodbye!"));
    }
}