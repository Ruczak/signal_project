package com.alerts.strategies;

import com.alerts.alert_types.Alert;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.PatientRecord;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Checks for abnormal readings of blood pressure (both systolic/diastolic).
 */
public class BloodPressureAlertStrategy extends AlertStrategy {
    private final Queue<Double> systolicQueue = new LinkedList<>();
    private final Queue<Double> diastolicQueue = new LinkedList<>();

    private final HashMap<AlertType, Trend> lastTrend;

    private enum AlertType {
        CRITICAL,
        CHANGE
    }

    private enum Trend {
        STABLE,
        UP,
        DOWN,
    }

    public BloodPressureAlertStrategy() {
        lastTrend = new HashMap<>();
        lastTrend.put(AlertType.CHANGE, Trend.STABLE);
        lastTrend.put(AlertType.CRITICAL, Trend.STABLE);
        factory = new BloodPressureAlertFactory();
    }

    @Override
    public void checkAlert(PatientRecord record) {
        offerRecord(record);

        String type = record.getRecordType();
        double value = record.getMeasurementValue();

        // check for critical values
        if (type.equals("Systolic pressure")) {
            if (value > 180) addAlert(AlertType.CRITICAL, Trend.UP, record.getPatientId(), record.getTimestamp());
            else if (value < 90) addAlert(AlertType.CRITICAL,Trend.DOWN, record.getPatientId(), record.getTimestamp());
        }
        else if (type.equals("Diastolic pressure")) {
            if (value > 120) addAlert(AlertType.CRITICAL, Trend.UP, record.getPatientId(), record.getTimestamp());
            else if (value < 60) addAlert(AlertType.CRITICAL, Trend.DOWN, record.getPatientId(), record.getTimestamp());
        }

        // check for general trends
        addAlert(AlertType.CHANGE, getChangeTrend(systolicQueue), record.getPatientId(), record.getTimestamp());
        addAlert(AlertType.CHANGE, getChangeTrend(diastolicQueue), record.getPatientId(), record.getTimestamp());
    }

    private Trend getChangeTrend(Queue<Double> pressure) {
        if (pressure.size() < 4) return Trend.STABLE;
        Queue<Double> pressureQueue = new LinkedList<>(pressure);

        double minDifference = Integer.MAX_VALUE;
        double maxDifference = Integer.MIN_VALUE;

        for (int i = 0; i < pressureQueue.size() - 1; i++) {
            double current = pressureQueue.poll();
            if (pressureQueue.peek() == null) break;
            double difference = pressureQueue.peek() - current;

            if (difference < minDifference) {
                minDifference = difference;
            }
            if (difference > maxDifference) {
                maxDifference = difference;
            }
        }

        if (minDifference > 10) return Trend.UP;
        if (maxDifference < -10) return Trend.DOWN;

        return Trend.STABLE;
    }

    private void offerRecord(PatientRecord record) {
        if (record.getRecordType().equals("Systolic pressure")) {
            systolicQueue.add(record.getMeasurementValue());
            if (systolicQueue.size() > 4) {
                systolicQueue.poll();
            }
        }
        else if (record.getRecordType().equals("Diastolic pressure")) {
            diastolicQueue.add(record.getMeasurementValue());
            if (diastolicQueue.size() > 4) {
                diastolicQueue.poll();
            }
        }
    }

    private void addAlert(AlertType type, Trend trend, int patientId, long timestamp) {
        if (lastTrend.get(type).equals(trend)) return;

        if (type == AlertType.CHANGE) {
            switch (trend) {
                case UP:
                    enqueueAlert(factory.getAlert(String.valueOf(patientId),
                            "Blood pressure is increasing at a high rate (>10 mmHg/reading)",
                            timestamp));

                    break;
                case DOWN:
                    enqueueAlert(factory.getAlert(String.valueOf(patientId),
                            "Blood pressure is decreasing at a high rate (>10 mmHg/reading)",
                            timestamp
                    ));
                    break;
                case STABLE:
                default:
                    lastTrend.put(AlertType.CHANGE, Trend.STABLE);
                    break;
            }
        }

        if (type == AlertType.CRITICAL) {
            switch (trend) {
                case UP:
                    enqueueAlert(factory.getAlert(String.valueOf(patientId),
                            "Blood pressure is critically high",
                            timestamp));
                    break;
                case DOWN:
                    enqueueAlert(factory.getAlert(String.valueOf(patientId),
                            "Blood pressure is critically low",
                            timestamp));
                    break;
                case STABLE:
                default:
                    lastTrend.put(AlertType.CRITICAL, Trend.STABLE);
                    break;
            }
        }
    }
}
