package com.alerts.factories;

import com.alerts.alert_types.Alert;
import com.alerts.alert_types.TriggeredAlert;

public class TriggeredAlertFactory extends AlertFactory {
    @Override
    public Alert getAlert(String patientId, String condition, long timestamp) {
        return new TriggeredAlert(patientId, condition, timestamp);
    }
}
