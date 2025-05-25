package data_management;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DataReaderTest {
    @TempDir
    public Path temp;

    @BeforeEach
    public void setUp() throws Exception {
        Field field = DataStorage.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    void testFileDataReader() {
        try {
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

            reader.readData(storage);

            assertEquals(7, storage.getAllPatients().size());
            assertTrue(storage.getAllPatients().stream().allMatch(patient ->
                    patientIds.contains(patient.getPatientId()) && (
                            patient.getRecords(0, 1800000000000L).size() == 1 ||
                            (patient.getRecords(0, 1800000000000L).size() == 2 && patient.getPatientId() == 44)
                    )
            ));
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            fail();
        }
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
