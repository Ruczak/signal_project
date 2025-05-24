package com.alerts.strategies;

import com.alerts.alert_types.Alert;
import com.alerts.factories.AlertFactory;
import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Is an abstract class used to process data retrieved form {@link DataStorage}
 * to check for any abnormal readings.
 */
public abstract class AlertStrategy {
    protected AlertFactory factory;

    /**
     * Alert queue from which the data is then retrieved using <code>getQueuedAlerts()</code>
     */
    protected final Queue<Alert> alertQueue = new LinkedList<>();

    public AlertStrategy(AlertFactory factory) {
        this.factory = factory;
    }

    /**
     * Polls currently stored alerts and removes them from the queue.
     * @return List of queued alerts
     */
    public final List<Alert> pollAlerts() {
        List<Alert> alerts = new ArrayList<>(alertQueue);
        alertQueue.clear();
        return alerts;
    }

    /**
     * Adds alert to the queue
     * @param alert alert to add.
     */
    protected void enqueueAlert(Alert alert) {
        alertQueue.add(alert);
    }

    /**
     * Checks record if any abnormality exists. This may include checking previous readings as well.
     * @implSpec If the reading show any abnormality, a new {@link Alert} should be generated and
     * added to the <code>alertQueue</code>
     * @param record Patient's record to be processed
     */
    public abstract void checkAlert(PatientRecord record);
}
