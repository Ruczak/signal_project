package com.alerts.alert_types;

public class ECGAlert extends Alert {
    public ECGAlert(String patiendId, String condition, long timestamp) {
        super(patiendId, condition, timestamp);
    }
}
