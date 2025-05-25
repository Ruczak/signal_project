package com.alerts.alert_types;

public class BloodPressureAlert extends Alert{
    public BloodPressureAlert(String patientId, String condition, long timestamp)  {
        super(patientId, condition, timestamp);
    }
}
