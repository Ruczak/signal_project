package com.alerts.strategies;

import com.alerts.alert_types.Alert;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.data_management.PatientRecord;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Checks for abnormal blood saturation readings.
 */
public class BloodSaturationAlertStrategy extends AlertStrategy {
    Queue<PatientRecord> saturationQueue = new LinkedList<>();

    private enum AlertType {
        SATURATION_LOW,
        SATURATION_DROP
    }

    private final HashMap<AlertType, Boolean> lastState;

    public BloodSaturationAlertStrategy() {
        super(new BloodOxygenAlertFactory());
        lastState = new HashMap<>();

        lastState.put(AlertType.SATURATION_LOW, false);
        lastState.put(AlertType.SATURATION_DROP, false);
    }

    @Override
    public void checkAlert(PatientRecord record) {
        if (!record.getRecordType().equals("Saturation")) return;

        saturationQueue.add(record);

        Queue<PatientRecord> queue = new LinkedList<>(saturationQueue);

        if (record.getMeasurementValue() < 92) {
            if (!lastState.get(AlertType.SATURATION_LOW)) {
                addAlert(AlertType.SATURATION_LOW, record);
                lastState.put(AlertType.SATURATION_LOW, true);
            }
        } else lastState.put(AlertType.SATURATION_LOW, false);

        double maxRelativeDrop = 0;

        while (!queue.isEmpty()) {
            PatientRecord currentRecord = queue.poll();
            if (currentRecord == null || record.getTimestamp() - currentRecord.getTimestamp() > 10 * 60 * 1000) {
                queue.poll();
                continue;
            }

            double relativeDrop = (currentRecord.getMeasurementValue() - record.getMeasurementValue())
                    / currentRecord.getMeasurementValue();

            if (relativeDrop > maxRelativeDrop) {
                maxRelativeDrop = relativeDrop;
            }
        }

        if (maxRelativeDrop > 0.05) {
            if (!lastState.get(AlertType.SATURATION_DROP)) {
                addAlert(AlertType.SATURATION_DROP, record);
                lastState.put(AlertType.SATURATION_DROP, true);
            }
        } else lastState.put(AlertType.SATURATION_DROP, false);
    }

    private void addAlert(AlertType type, PatientRecord record) {
        switch (type) {
            case SATURATION_LOW:
                enqueueAlert(factory.getAlert(String.valueOf(record.getPatientId()),
                        "Blood saturation is low (<92%)",
                        record.getTimestamp()));
                break;
            case SATURATION_DROP:
                enqueueAlert(factory.getAlert(String.valueOf(record.getPatientId()),
                        "Blood saturation fell suddenly by over 5%",
                        record.getTimestamp()));
        }
    }
}
