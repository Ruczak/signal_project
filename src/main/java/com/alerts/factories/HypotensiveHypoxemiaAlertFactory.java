package com.alerts.factories;

import com.alerts.alert_types.Alert;
import com.alerts.alert_types.HypotensiveHypoxemiaAlert;

public class HypotensiveHypoxemiaAlertFactory extends AlertFactory {
    @Override
    public Alert getAlert(String patientId, String condition, long timestamp) {
        return new HypotensiveHypoxemiaAlert(patientId, condition, timestamp);
    }
}
