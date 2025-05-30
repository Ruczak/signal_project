package com.alerts;

import com.alerts.alert_types.Alert;
import com.alerts.strategies.*;
import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 * <br/>
 * ASSUMPTION: We also need an output strategy to notify through appropriate
 * communication channel.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private OutputStrategy outputStrategy;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        // Implementation goes here
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());

        List<AlertStrategy> checkers = new ArrayList<>();
        checkers.add(new BloodPressureAlertStrategy());
        checkers.add(new BloodSaturationAlertStrategy());
        checkers.add(new HypotensiveHypoxemiaAlertStrategy());
        checkers.add(new ECGDataAlertStrategy(50));
        checkers.add(new TriggeredAlertStrategy());

        for (PatientRecord record : records) {
            for (AlertStrategy checker : checkers) {
                checker.checkAlert(record);
                for (Alert alert : checker.pollAlerts())
                    triggerAlert(alert);
            }
        }
    }

    /**
     * Sets output strategy
     * @param outputStrategy target <code>OutputStrategy</code> object
     */
    public void setOutputStrategy(OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.<br />
     * ASSUMPTION: we can use an output strategy to alert the staff through appropriate
     * communication channel.
     * If the <code>outputStrategy</code> is null, then this method does not do anything.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        if (outputStrategy == null) return;

        outputStrategy.output(
                Integer.parseInt(alert.getPatientId()),
                alert.getTimestamp(),
                "Alert",
                alert.getCondition()
        );
    }
}
