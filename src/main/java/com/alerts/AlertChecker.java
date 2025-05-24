package com.alerts;

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
public abstract class AlertChecker {
    /**
     * Alert queue from which the data is then retrieved using <code>getQueuedAlerts()</code>
     */
    protected final Queue<Alert> alertQueue = new LinkedList<>();

    /**
     * Polls currently stored alerts and removes them from the queue.
     * @return List of queued alerts
     */
    public final List<Alert> getQueuedAlerts() {
        List<Alert> alerts = new ArrayList<>(alertQueue);
        alertQueue.clear();
        return alerts;
    }

    /**
     * Checks record if any abnormality exists. This may include checking previous readings as well.
     * @implSpec If the reading show any abnormality, a new {@link Alert} should be generated and
     * added to the <code>alertQueue</code>
     * @param record Patient's record to be processed
     */
    public abstract void checkData(PatientRecord record);
}
