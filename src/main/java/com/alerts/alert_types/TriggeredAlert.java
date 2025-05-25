package com.alerts.alert_types;

public class TriggeredAlert extends Alert {
    public TriggeredAlert(String patiendId, String condition, long timestamp) {
        super(patiendId, condition, timestamp);
    }
}
