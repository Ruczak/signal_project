package com.alerts.factories;

import com.alerts.alert_types.Alert;

/**
 * Interface for a <i>Factory</i> pattern for creation of specific alerts.
 */
public abstract class AlertFactory {
    /**
     * Creates a new object of {@link Alert} subtype.
     * @param patientId patient's ID
     * @param condition the condition of a patient
     * @param timestamp the time of creation of the alert
     * @return {@link Alert} object
     */
    public abstract Alert getAlert(String patientId, String condition, long timestamp);
}
