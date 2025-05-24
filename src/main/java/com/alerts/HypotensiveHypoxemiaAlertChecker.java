package com.alerts;

import com.data_management.PatientRecord;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Used both blood systolic pressure and saturation readings to determine if patient is at risk of
 * Hypotensive Hypoxemia.
 */
public class HypotensiveHypoxemiaAlertChecker extends AlertChecker {
    PatientRecord lastSystolicMeasure = null;
    PatientRecord lastSaturationMeasure = null;

    @Override
    public void checkData(PatientRecord record) {
        if (record.getRecordType().equals("Systolic pressure"))
            lastSystolicMeasure = record;
        else if (record.getRecordType().equals("Saturation"))
            lastSaturationMeasure = record;

        if (lastSystolicMeasure != null && lastSaturationMeasure != null) {
            if (lastSystolicMeasure.getMeasurementValue() < 90 && lastSaturationMeasure.getMeasurementValue() < 92) {
                alertQueue.add(new Alert(String.valueOf(record.getPatientId()),
                        "Patient is at risk of Hypotensive Hypoxemia",
                        record.getTimestamp()));
            }
        }

    }
}
