package data_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.lang.reflect.Field;
import java.util.List;

class DataStorageTest {
    @BeforeEach
    public void setUp() throws Exception {
        Field field = DataStorage.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    void testAddAndGetRecords() {

        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 96.0, "Saturation", System.currentTimeMillis());

        assertEquals(storage.getAllPatients().size(), 1);

        List<PatientRecord> records = storage.getRecords(1, 0, 1800000000000L);

        assertEquals(records.size(), 1);
        assertEquals(records.get(0).getMeasurementValue(), 96.0);

    }
}
