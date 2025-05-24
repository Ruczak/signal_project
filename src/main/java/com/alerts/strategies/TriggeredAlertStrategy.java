package com.alerts.strategies;

import com.alerts.alert_types.Alert;
import com.data_management.PatientRecord;

/**
 * Checks storage readings for invoked alerts by patients or nurses.
 */
public class TriggeredAlertStrategy extends AlertStrategy {
    @Override
    public void checkAlert(PatientRecord record) {
        if (!record.getRecordType().equals("Alert")) return;

        if (record.getMeasurementValue() == 1.0)
            enqueueAlert(factory.getAlert(String.valueOf(record.getPatientId()),
                    "Alert triggered", record.getTimestamp()));
        else if (record.getMeasurementValue() == 0.0)
            enqueueAlert(factory.getAlert(String.valueOf(record.getPatientId()),
                    "Alert resolved", record.getTimestamp()));

    }
}
