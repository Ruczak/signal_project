package com.alerts;

import com.data_management.PatientRecord;

/**
 * Checks storage readings for invoked alerts by patients or nurses.
 */
public class TriggeredAlertChecker extends AlertChecker {
    @Override
    public void checkData(PatientRecord record) {
        if (!record.getRecordType().equals("Alert")) return;

        if (record.getMeasurementValue() == 1.0)
            alertQueue.add(new Alert(String.valueOf(record.getPatientId()),
                    "Alert triggered", record.getTimestamp()));
        else if (record.getMeasurementValue() == 0.0)
            alertQueue.add(new Alert(String.valueOf(record.getPatientId()),
                    "Alert resolved", record.getTimestamp()));

    }
}
