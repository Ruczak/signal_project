package com.alerts.factories;

import com.alerts.alert_types.Alert;

public abstract class AlertFactory {
    public abstract Alert getAlert(String patientId, String condition, long timestamp);
}
