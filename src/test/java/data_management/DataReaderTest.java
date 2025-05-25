package data_management;

import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;
import com.data_management.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DataReaderTest {
    @TempDir
    public Path temp;

    // needed due to sharing memory between test after using 'mvn clean test'
    @BeforeEach
    void resetDataStorage() throws NoSuchFieldException, IllegalAccessException {
        Field f = DataStorage.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);


    @Test
    void testFileDataReader() {
        DataStorage storage = DataStorage.getInstance();

        List<Integer> patientIds = Arrays.asList(43, 26, 17, 24, 31, 44, 2);

        writeToTempFile("Alert.txt", "Patient ID: 43, Timestamp: 1747042264011, Label: Alert, Data: triggered");
        writeToTempFile("Cholesterol.txt","Patient ID: 26, Timestamp: 1747042261984, Label: Cholesterol, Data: 154.8512924660576");
        writeToTempFile("DiastolicPressure.txt", "Patient ID: 17, Timestamp: 1747042262029, Label: DiastolicPressure, Data: 75.0");
        writeToTempFile("SystolicPressure.txt","Patient ID: 24, Timestamp: 1747042261990, Label: SystolicPressure, Data: 123.0");
        writeToTempFile("Saturation.txt","Patient ID: 31, Timestamp: 1747042261993, Label: Saturation, Data: 96.0%");
        writeToTempFile("WhiteBloodCells.txt", "Patient ID: 44, Timestamp: 1747042262028, Label: WhiteBloodCells, Data: 8.018321362894895");
        writeToTempFile("RedBloodCells.txt", "Patient ID: 44, Timestamp: 1747042262033, Label: RedBloodCells, Data: 5.113280609298756");
        writeToTempFile("ECG.txt", "Patient ID: 2, Timestamp: 1747042261980, Label: ECG, Data: 0.01819583172057701");

        FileDataReader reader = new FileDataReader(temp.toString());

        assertDoesNotThrow(reader::refresh);

        assertEquals(7, storage.getAllPatients().size());
        assertTrue(storage.getAllPatients().stream().allMatch(patient ->
                patientIds.contains(patient.getPatientId()) && (
                        patient.getRecords(0, 1800000000000L).size() == 1 ||
                        (patient.getRecords(0, 1800000000000L).size() == 2 && patient.getPatientId() == 44)
                )
        ));
    }

    @Test
    void testWebSocketClient() throws InterruptedException {
        DataStorage storage = DataStorage.getInstance();

        OutputStrategy server = new WebSocketOutputStrategy(8080);

        WebSocketClient client = new WebSocketClient(URI.create("ws://localhost:8080"));

        Thread.sleep(1000); // need to wait for connection

        // test valid input
        server.output(1, 1747042262033L, "WhiteBloodCells", "8.018321362894895");
        Thread.sleep(250);

        assertDoesNotThrow(client::refresh);

        assertEquals(1, storage.getAllPatients().size());

        try {
            PatientRecord record = storage.getRecords(1, 0, 1800000000000L).get(0);
            assertEquals(1, record.getPatientId());
            assertEquals("WhiteBloodCells", record.getRecordType());
            assertEquals(8.018321362894895, record.getMeasurementValue());
            assertEquals(1747042262033L, record.getTimestamp());

        } catch (NoSuchElementException e) {
            fail();
        }

        // test invalid input
        server.output(1, 1747042262033L, "WhiteBloodCells", "Example invalid record");

        Thread.sleep(250);

        assertEquals(1, storage.getRecords(1, 0, 1800000000000L).size());
    }

    @Test
    void testParser() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Parser.decode("Invalid message")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Parser.decode("Patient ID: 13.0, Timestamp: 1800000000000, Label: RedBloodCells, Data: 20.0")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Parser.decode("Patient ID: 130, Timestamp: 1800000000000L, Label: RedBloodCells, Data: Some Invalid Data")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Parser.decode("Patient ID: 130, Timestamp: 2025-05-10 14:00:00, Label: RedBloodCells, Data: 20.0")
        );

        Parser.ParsedData parsedData = Parser.decode("Patient ID: 130, Timestamp: 1800000000000, Label: RedBloodCells, Data: 20.0");

        assertEquals(130, parsedData.getPatientId());
        assertEquals(1800000000000L, parsedData.getTimestamp());
        assertEquals("RedBloodCells", parsedData.getLabel());
        assertEquals(20.0, parsedData.getData());
    }

    private void writeToTempFile(String filename, String data) {
        Path filePath = temp.resolve(filename);
        try (PrintWriter writer = new PrintWriter(filePath.toFile())) {
            writer.println(data);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
