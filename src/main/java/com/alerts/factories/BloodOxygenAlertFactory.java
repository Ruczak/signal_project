package com.alerts.factories;

import com.alerts.alert_types.Alert;
import com.alerts.alert_types.BloodOxygenAlert;

public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public Alert getAlert(String patientId, String condition, long timestamp) {
        return new BloodOxygenAlert(patientId, condition, timestamp);
    }
}
