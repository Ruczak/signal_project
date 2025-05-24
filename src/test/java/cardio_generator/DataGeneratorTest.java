package cardio_generator;

import static org.junit.jupiter.api.Assertions.*;

import com.cardio_generator.generators.*;
import com.cardio_generator.outputs.TestOutputStrategy;
import org.junit.jupiter.api.Test;

public class DataGeneratorTest {
    @Test
    void testAlertGenerator() {
        TestOutputStrategy outputStrategy = new TestOutputStrategy();

        AlertGenerator generator = new AlertGenerator(5);

        for (int i = 0; i < 1000; i++) {
            generator.generate(i % 5, outputStrategy);
        }

        assertFalse(outputStrategy.getRecords().isEmpty());

        boolean check = true;

        for (TestOutputStrategy.AlertRecord alert : outputStrategy.getRecords()) {
            check = check
                    && alert.getLabel().equals("Alert")
                    && (alert.getData().equals("triggered") || alert.getData().equals("resolved"))
                    && alert.getPatientId() < 5 && alert.getPatientId() >= 0;
        }

        assertTrue(check);
    }

    @Test
    void testBloodLevelsGenerator() {
        TestOutputStrategy outputStrategy = new TestOutputStrategy();

        BloodLevelsDataGenerator generator = new BloodLevelsDataGenerator(5);

        for (int i = 0; i < 1000; i++) {
            generator.generate(i % 5, outputStrategy);
        }

        assertEquals(3000, outputStrategy.getRecords().size());

        int red = 0;
        int white = 0;
        int cholesterol = 0;

        for (TestOutputStrategy.AlertRecord alert : outputStrategy.getRecords()) {
            switch (alert.getLabel()) {
                case "RedBloodCells":
                    red++;
                    break;
                case "WhiteBloodCells":
                    white++;
                    break;
                case "Cholesterol":
                    cholesterol++;
                    break;
                default:
                    fail();
                    break;
            }
        }

        assertEquals(1000, red);
        assertEquals(1000, white);
        assertEquals(1000, cholesterol);
    }

    @Test
    void testBloodPressureGenerator() {
        TestOutputStrategy outputStrategy = new TestOutputStrategy();

        BloodPressureDataGenerator generator = new BloodPressureDataGenerator(5);

        for (int i = 0; i < 1000; i++) {
            generator.generate(i % 5, outputStrategy);
        }

        assertEquals(2000, outputStrategy.getRecords().size());

        int systolic = 0;
        int diastolic = 0;

        for (TestOutputStrategy.AlertRecord alert : outputStrategy.getRecords()) {
            if (alert.getPatientId() < 0 || alert.getPatientId() >= 5) {
                fail();
                break;
            }

            switch (alert.getLabel()) {
                case "SystolicPressure":
                    if (Double.parseDouble(alert.getData()) > 180 || Double.parseDouble(alert.getData()) < 90)
                        fail();
                    systolic++;
                    break;
                case "DiastolicPressure":
                    if (Double.parseDouble(alert.getData()) > 120 || Double.parseDouble(alert.getData()) < 60)
                        fail();
                    diastolic++;
                    break;
                default:
                    fail();
                    break;
            }
        }

        assertEquals(1000, systolic);
        assertEquals(1000, diastolic);
    }

    @Test
    void testBloodSaturationGenerator() {
        TestOutputStrategy outputStrategy = new TestOutputStrategy();

        BloodSaturationDataGenerator generator = new BloodSaturationDataGenerator(5);

        for (int i = 0; i < 1000; i++) {
            generator.generate(i % 5, outputStrategy);
        }

        assertEquals(1000, outputStrategy.getRecords().size());

        for (TestOutputStrategy.AlertRecord alert : outputStrategy.getRecords()) {
            if (alert.getPatientId() < 0 || alert.getPatientId() >= 5 || !alert.getLabel().equals("Saturation")) {
                fail();
                break;
            }

            try {
                double value = Double.parseDouble(alert.getData().substring(0, alert.getData().length() - 1));
                if (value > 100 || value < 90) {
                    fail();
                    break;
                }
            }
            catch (Exception e) {
                fail();
                break;
            }
        }
    }

    @Test
    void testECGDataGenerator() {
        TestOutputStrategy outputStrategy = new TestOutputStrategy();
        ECGDataGenerator generator = new ECGDataGenerator(5);

        for (int i = 0; i < 1000; i++) {
            generator.generate(i % 5, outputStrategy);
        }

        assertEquals(1000, outputStrategy.getRecords().size());
    }
}
