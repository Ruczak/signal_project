package com.alerts.strategies;

import com.alerts.alert_types.Alert;
import com.data_management.PatientRecord;

/**
 * Used both blood systolic pressure and saturation readings to determine if patient is at risk of
 * Hypotensive Hypoxemia.
 */
public class HypotensiveHypoxemiaAlertStrategy extends AlertStrategy {
    PatientRecord lastSystolicMeasure = null;
    PatientRecord lastSaturationMeasure = null;

    @Override
    public void checkAlert(PatientRecord record) {
        if (record.getRecordType().equals("Systolic pressure"))
            lastSystolicMeasure = record;
        else if (record.getRecordType().equals("Saturation"))
            lastSaturationMeasure = record;

        if (lastSystolicMeasure != null && lastSaturationMeasure != null) {
            if (lastSystolicMeasure.getMeasurementValue() < 90 && lastSaturationMeasure.getMeasurementValue() < 92) {
                enqueueAlert(factory.getAlert(String.valueOf(record.getPatientId()),
                        "Patient is at risk of Hypotensive Hypoxemia",
                        record.getTimestamp()));
            }
        }

    }
}
