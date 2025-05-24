package com.alerts.alert_types;

public class BloodOxygenAlert extends Alert {
    public BloodOxygenAlert(String patiendId, String condition, long timestamp) {
        super(patiendId, condition, timestamp);
    }
}
