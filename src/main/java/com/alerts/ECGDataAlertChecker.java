package com.alerts;

import com.data_management.PatientRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Analyses ECG data to watch out for abnormal peaks.
 */
public class ECGDataAlertChecker extends AlertChecker {
    Queue<Double> previousData = new LinkedList<>();

    private final int k;

    public ECGDataAlertChecker(int k) {
        this.k = k;
    }

    @Override
    public void checkData(PatientRecord record) {
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
                alertQueue.add(new Alert(String.valueOf(record.getPatientId()),
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
