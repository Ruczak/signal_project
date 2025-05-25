package data_management;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DataReaderTest {
    @TempDir
    public Path temp;

    @Test
    void testFileDataReader() {
        try {
            Path alertTxt = temp.resolve("Alert.txt");
            try (PrintWriter writer = new PrintWriter(alertTxt.toFile())) {
                writer.println("Patient ID: 43, Timestamp: 1747042264011, Label: Alert, Data: triggered");
            }

            Path cholesterolTxt = temp.resolve("Cholesterol.txt");
            try (PrintWriter writer = new PrintWriter(cholesterolTxt.toFile())) {
                writer.println("Patient ID: 26, Timestamp: 1747042261984, Label: Cholesterol, Data: 154.8512924660576");
            }

            Path diastolicPressureTxt = temp.resolve("DiastolicPressure.txt");
            try (PrintWriter writer = new PrintWriter(diastolicPressureTxt.toFile())) {
                writer.println("Patient ID: 17, Timestamp: 1747042262029, Label: DiastolicPressure, Data: 75.0");
            }

            Path systolicPressureTxt = temp.resolve("SystolicPressure.txt");
            try (PrintWriter writer = new PrintWriter(systolicPressureTxt.toFile())) {
                writer.println("Patient ID: 24, Timestamp: 1747042261990, Label: SystolicPressure, Data: 123.0");
            }

            Path saturationTxt = temp.resolve("Saturation.txt");
            try (PrintWriter writer = new PrintWriter(saturationTxt.toFile())) {
                writer.println("Patient ID: 31, Timestamp: 1747042261993, Label: Saturation, Data: 96.0%");
            }

            Path whiteBloodTxt = temp.resolve("WhiteBloodCells.txt");
            try (PrintWriter writer = new PrintWriter(whiteBloodTxt.toFile())) {
                writer.println("Patient ID: 44, Timestamp: 1747042262028, Label: WhiteBloodCells, Data: 8.018321362894895");
            }

            Path redBloodTxt = temp.resolve("RedBloodCells.txt");
            try (PrintWriter writer = new PrintWriter(redBloodTxt.toFile())) {
                writer.println("Patient ID: 44, Timestamp: 1747042262033, Label: RedBloodCells, Data: 5.113280609298756");
            }

            Path ecgTxt = temp.resolve("ECG.txt");
            try (PrintWriter writer = new PrintWriter(ecgTxt.toFile())) {
                writer.println("Patient ID: 2, Timestamp: 1747042261980, Label: ECG, Data: 0.01819583172057701");
            }

            FileDataReader reader = new FileDataReader(temp.toString());

            DataStorage storage = DataStorage.getInstance();

            reader.readData(storage);

            List<Integer> patientIds = Arrays.asList(43, 26, 17, 24, 31, 44, 2);

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
        }
    }
}
