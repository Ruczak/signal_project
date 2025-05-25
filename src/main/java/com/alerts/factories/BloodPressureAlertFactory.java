package com.alerts.factories;

import com.alerts.alert_types.Alert;
import com.alerts.alert_types.BloodPressureAlert;

/**
 * Creates {@link BloodPressureAlert} objects using the <i>Factory</i> pattern
 */
public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    public Alert getAlert(String patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, condition, timestamp);
    }
}
