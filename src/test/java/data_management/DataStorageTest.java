package data_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.lang.reflect.Field;
import java.util.List;

class DataStorageTest {

    // needed due to sharing memory between test after using 'mvn clean test'
    @BeforeEach
    void resetDataStorage() throws NoSuchFieldException, IllegalAccessException {
        Field f = DataStorage.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    void testAddAndGetRecords() {
        DataStorage storage = DataStorage.getInstance();
        int count = storage.getAllPatients().size();

        storage.addPatientData(1, 96.0, "Saturation", System.currentTimeMillis());

        assertEquals(count + 1, storage.getAllPatients().size());

        List<PatientRecord> records = storage.getRecords(1, 0, 1800000000000L);

        assertEquals(1, records.size());
        assertEquals(96.0, records.get(0).getMeasurementValue());
    }
}
