package alerts;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.alert_types.Alert;
import com.alerts.AlertGenerator;
import com.alerts.strategies.ECGDataAlertStrategy;
import com.cardio_generator.outputs.TestOutputStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;

import java.util.List;

public class AlertGeneratorTest {
    @Test
    void testAlertEvaluations() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 1.0, "Alert", System.currentTimeMillis());
        storage.addPatientData(1, 0.0, "Alert", System.currentTimeMillis());

        TestOutputStrategy outputStrategy = new TestOutputStrategy();
        AlertGenerator generator = new AlertGenerator(storage);
        generator.setOutputStrategy(outputStrategy);

        Patient patient = storage.getAllPatients().get(0);
        assertNotNull(patient);

        generator.evaluateData(patient);

        List<TestOutputStrategy.AlertRecord> records = outputStrategy.getRecords();

        assertTrue(records.stream()
                .anyMatch(e -> e.getLabel().equals("Alert") && e.getData().equals("Alert triggered")));

        assertTrue(records.stream()
                .anyMatch(e -> e.getLabel().equals("Alert") && e.getData().equals("Alert resolved") ));
    }

    @Test
    void testBloodPressureEvaluations() {
        DataStorage storage = DataStorage.getInstance();
        // begin with normal pressure
        storage.addPatientData(1, 120.0,
                "Systolic pressure", System.currentTimeMillis());
        storage.addPatientData(1, 100.0,
                "Diastolic pressure", System.currentTimeMillis());

        // increase systolic pressure steadily
        storage.addPatientData(1, 131.0,
                "Systolic pressure", System.currentTimeMillis());
        storage.addPatientData(1, 142.0,
                "Systolic pressure", System.currentTimeMillis());
        long systolicIncreaseLastTimestamp = System.currentTimeMillis();
        storage.addPatientData(1, 153.0,
                "Systolic pressure", systolicIncreaseLastTimestamp);

        // surpass critical point
        long systolicCriticalLastTimestamp = System.currentTimeMillis();
        storage.addPatientData(1, 190.0,
                "Systolic pressure", systolicCriticalLastTimestamp);

        // decrease diastolic pressure steadily
        storage.addPatientData(2, 89,
                "Diastolic pressure", System.currentTimeMillis());
        storage.addPatientData(2, 78,
                "Diastolic pressure", System.currentTimeMillis());
        long diastolicDecreaseLastTimestamp = System.currentTimeMillis();
        storage.addPatientData(2, 67,
                "Diastolic pressure", diastolicDecreaseLastTimestamp);

        // suprass critical point
        long diastolicCriticalLastTimestamp = System.currentTimeMillis();
        storage.addPatientData(2, 56,
                "Diastolic pressure", diastolicCriticalLastTimestamp);

        TestOutputStrategy outputStrategy = new TestOutputStrategy();
        AlertGenerator generator = new AlertGenerator(storage);
        generator.setOutputStrategy(outputStrategy);

        Patient patient1 = storage.getAllPatients().get(0);
        Patient patient2 = storage.getAllPatients().get(1);
        assertNotNull(patient1);
        generator.evaluateData(patient1);

        assertNotNull(patient2);
        generator.evaluateData(patient2);

        List<TestOutputStrategy.AlertRecord> records = outputStrategy.getRecords();

        assertTrue(records.stream()
                .anyMatch(e -> e.getPatientId() == 1
                        && e.getLabel().equals("Alert")
                        && e.getData().equals("Blood pressure is increasing at a high rate (>10 mmHg/reading)")
                        && e.getTimestamp() == systolicIncreaseLastTimestamp
                ));
        assertTrue(records.stream()
                .anyMatch(e -> e.getPatientId() == 1
                        && e.getLabel().equals("Alert")
                        && e.getData().equals("Blood pressure is critically high")
                        && e.getTimestamp() == systolicCriticalLastTimestamp
                ));

        assertTrue(records.stream()
                .anyMatch(e -> e.getPatientId() == 2
                        && e.getLabel().equals("Alert")
                        && e.getData().equals("Blood pressure is decreasing at a high rate (>10 mmHg/reading)")
                        && e.getTimestamp() == diastolicDecreaseLastTimestamp
                ));
        assertTrue(records.stream()
                .anyMatch(e -> e.getPatientId() == 2
                        && e.getLabel().equals("Alert")
                        && e.getData().equals("Blood pressure is critically low")
                        && e.getTimestamp() == diastolicCriticalLastTimestamp
                ));
    }

    @Test
    void testBloodSaturationEvaluations() {
        DataStorage storage = DataStorage.getInstance();

        // start with normal saturation
        storage.addPatientData(1, 99,
                "Saturation", System.currentTimeMillis());

        TestOutputStrategy outputStrategy = new TestOutputStrategy();
        AlertGenerator generator = new AlertGenerator(storage);
        generator.setOutputStrategy(outputStrategy);

        // check for suddent saturation drop
        long suddenDropTimestamp = System.currentTimeMillis();
        storage.addPatientData(1, 93,
                "Saturation", suddenDropTimestamp);

        // check for drop below 92%
        long lowSaturationTimestamp = System.currentTimeMillis();
        storage.addPatientData(1, 91,
                "Saturation", lowSaturationTimestamp);

        Patient patient = storage.getAllPatients().get(0);
        assertNotNull(patient);
        generator.evaluateData(patient);

        List<TestOutputStrategy.AlertRecord> records = outputStrategy.getRecords();

        assertTrue(records.stream()
                .anyMatch(e -> e.getPatientId() == 1
                        && e.getLabel().equals("Alert")
                        && e.getData().equals("Blood saturation fell suddenly by over 5%")
                        && e.getTimestamp() == suddenDropTimestamp
                ));
        assertTrue(records.stream()
                .anyMatch(e -> e.getPatientId() == 1
                        && e.getLabel().equals("Alert")
                        && e.getData().equals("Blood saturation is low (<92%)")
                        && e.getTimestamp() == suddenDropTimestamp
                ));
    }

    @Test
    void testHypotensiveHypoxemiaEvaluations() {
        DataStorage storage = DataStorage.getInstance();

        // start with normal saturation and systolic pressure
        storage.addPatientData(1, 93,
                "Saturation", System.currentTimeMillis());
        storage.addPatientData(1, 95,
                "Systolic pressure", System.currentTimeMillis());

        TestOutputStrategy outputStrategy = new TestOutputStrategy();
        AlertGenerator generator = new AlertGenerator(storage);
        generator.setOutputStrategy(outputStrategy);

        storage.addPatientData(1, 91,
                "Saturation", System.currentTimeMillis());

        long systolicChangeTimestamp = System.currentTimeMillis();
        storage.addPatientData(1, 85,
                "Systolic pressure", systolicChangeTimestamp);

        Patient patient = storage.getAllPatients().get(0);
        assertNotNull(patient);
        generator.evaluateData(patient);

        List<TestOutputStrategy.AlertRecord> records = outputStrategy.getRecords();

        assertTrue(records.stream()
                .anyMatch(e -> e.getPatientId() == 1
                        && e.getLabel().equals("Alert")
                        && e.getData().equals("Patient is at risk of Hypotensive Hypoxemia")
                        && e.getTimestamp() == systolicChangeTimestamp
                ));
    }

    @Test
    void testECGDataEvaluations() {
        DataStorage storage = DataStorage.getInstance();

        // start with mock ECG Data readings
        double[] values = new double[] {-0.45992000214722, -0.26240513874290666, 0.20697699323368063,
                -0.38937513646297506, 0.07160953347766037, 0.43825394383224175, -0.5818110192582496,
                0.15681288653372266, 0.2869378538629314, 0.46496014509492284, -0.06917067408528021,
                -0.41558962736668764, -0.23984633193598964, 0.34160556653837243, -0.31788941277810795,
                0.6347275491367317, -0.5174723554846611, 0.1021896005717132, 0.25822871266611136,
                -0.13522993945134848, -0.11485363435162385, 0.2693448419356609, -0.10388744956906762,
                0.495130516158881, -0.4130279427722752, -0.23922997359001408, -0.31724484151115434,
                0.07973258395267391, -0.1386795383737087, 0.027669216114580626, -0.2551794049789719,
                -0.5710093693096615, -0.2583036866852645, -0.5700759854269573, -0.5809739185355152,
                -0.433872278681971, 0.14976129314350434, -0.34601702707189697, 0.37098528718270296};

        for (double val : values) {
            storage.addPatientData(1, val,
                    "ECG", System.currentTimeMillis());
        }

        ECGDataAlertStrategy checker = new ECGDataAlertStrategy(values.length - 1);

        // fill in spiked data
        storage.addPatientData(1,
                0.25973499833342284,
                "ECG", System.currentTimeMillis());

        Patient patient = storage.getAllPatients().get(0);
        assertNotNull(patient);

        for (PatientRecord record : patient.getRecords(0, System.currentTimeMillis())) {
            checker.checkAlert(record);
        }

        List<Alert> alerts = checker.pollAlerts();

        assertTrue(alerts.stream().anyMatch(e -> e.getPatientId().equals("1")
                && e.getCondition().equals("Abnormal ECG reading is detected")
        ));
    }
}
