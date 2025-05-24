package com.alerts.strategies;

import com.alerts.alert_types.Alert;
import com.alerts.factories.ECGAlertFactory;
import com.data_management.PatientRecord;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Analyses ECG data to watch out for abnormal peaks.
 */
public class ECGDataAlertStrategy extends AlertStrategy {
    Queue<Double> previousData = new LinkedList<>();

    private final int k;

    public ECGDataAlertStrategy(int k) {
        super(new ECGAlertFactory());
        this.k = k;
    }

    @Override
    public void checkAlert(PatientRecord record) {
        if (!record.getRecordType().equals("ECG")) return;

        if (previousData.size() >= k) {
            Queue<Double> data = new LinkedList<>(previousData);

            double sum = 0.0;
            double sumSquared = 0.0;

            while (!data.isEmpty()) {
                double val = data.poll();
                sum += val;
                sumSquared += val * val;
            }

            double mean = sum / k;
            double stddev = Math.sqrt(sumSquared / k - mean * mean);

            // if the data is outside 95%-confidence inteval then raise the alert
            if (mean - 2 * stddev > record.getMeasurementValue() || record.getMeasurementValue() < mean + 2 * stddev) {
                enqueueAlert(factory.getAlert(String.valueOf(record.getPatientId()),
                        "Abnormal ECG reading is detected",
                        record.getTimestamp()));
            }
        }

        queueData(record);
    }

    private void queueData(PatientRecord record) {
        if (previousData.size() >= k) {
            previousData.poll();
        }

        previousData.add(record.getMeasurementValue());
    }
}
