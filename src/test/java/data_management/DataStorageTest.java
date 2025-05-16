package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.AlertGenerator;
import com.data_management.FileDataReader;
import com.data_management.Patient;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.io.IOException;
import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        // DataReader reader
        String directory = "test_output";

        FileDataReader reader = new FileDataReader(directory);

        //DataStorage storage = new DataStorage(reader);
        DataStorage storage = new DataStorage();

        try {
            reader.readData(storage);

            assertEquals(storage.getAllPatients().size(), 7);

            List<PatientRecord> records = storage.getRecords(26, 0, 1747042271984L);

            assertEquals(records.size(), 1);
            assertEquals(records.get(0).getMeasurementValue(), 154.8512924660576);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }




}
