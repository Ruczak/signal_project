package com.alerts.factories;

import com.alerts.alert_types.Alert;
import com.alerts.alert_types.ECGAlert;

/**
 * Creates {@link ECGAlert} objects using the <i>Factory</i> pattern
 */
public class ECGAlertFactory extends AlertFactory {
    @Override
    public Alert getAlert(String patientId, String condition, long timestamp) {
        return new ECGAlert(patientId, condition, timestamp);
    }
}
