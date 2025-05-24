package alerts;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.alert_types.*;
import com.alerts.factories.*;
import org.junit.jupiter.api.Test;

public class AlertFactoryTest {
    @Test
    public void testBloodPressureFactory() {
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();

        Alert alert = factory.getAlert("1",
                "Blood pressure is critically high",
                1748103245478L
        );

        assertInstanceOf(BloodPressureAlert.class, alert);
    }

    @Test
    public void testBloodOxygenFactory() {
        BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();

        Alert alert = factory.getAlert("1",
                "Blood saturation is low (<92%)",
                1748103245478L
        );

        assertInstanceOf(BloodOxygenAlert.class, alert);
    }

    @Test
    public void testECGFactory() {
        ECGAlertFactory factory = new ECGAlertFactory();

        Alert alert = factory.getAlert("1",
                "Abnormal ECG reading is detected",
                1748103245478L
        );

        assertInstanceOf(ECGAlert.class, alert);
    }

    @Test
    public void testHypotensiveHypoxemiaFactory() {
        HypotensiveHypoxemiaAlertFactory factory = new HypotensiveHypoxemiaAlertFactory();

        Alert alert = factory.getAlert("1",
                "Patient is at risk of Hypotensive Hypoxemia",
                1748103245478L
        );

        assertInstanceOf(HypotensiveHypoxemiaAlert.class, alert);
    }

    @Test
    public void testTriggeredAlertFactory() {
        TriggeredAlertFactory factory = new TriggeredAlertFactory();

        Alert alert = factory.getAlert("1",
                "Alert triggered",
                1748103245478L
        );

        assertInstanceOf(TriggeredAlert.class, alert);
    }
}
