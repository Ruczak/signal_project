package cardio_generator;

import static org.junit.jupiter.api.Assertions.*;

import com.cardio_generator.outputs.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Paths;
import java.util.Scanner;

public class OutputStrategyTest {
    @Test
    void consoleOutputTest() {
        ConsoleOutputStrategy strategy = new ConsoleOutputStrategy();

        PrintStream stdout = System.out; // temporary variable
        ByteArrayOutputStream stdoutStreamCapture = new ByteArrayOutputStream(); // stdout capturer

        System.setOut(new PrintStream(stdoutStreamCapture));

        strategy.output(1, 1748088333740L, "Label", "Data");

        assertEquals("Patient ID: 1, Timestamp: 1748088333740, Label: Label, Data: Data\r\n",
                stdoutStreamCapture.toString());

        System.setOut(stdout);
    }

    @Test
    void fileOutputTest() {
        String testDirectory = "./test_output";
        FileOutputStrategy strategy = new FileOutputStrategy(testDirectory);

        strategy.output(1, 1748088333740L, "Test", "Data");

        File file = Paths.get(testDirectory, "Test.txt").toFile();

        try (Scanner scanner = new Scanner(file)) {
            assertEquals("Patient ID: 1, Timestamp: 1748088333740, Label: Test, Data: Data",
                    scanner.nextLine());
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
        finally {
            assertTrue(file.delete());
        }
    }
}
