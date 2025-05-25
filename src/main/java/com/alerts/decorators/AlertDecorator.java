package com.alerts.decorators;

import com.alerts.alert_types.Alert;

public class AlertDecorator extends Alert {
    private Alert wrappee;

    public AlertDecorator(Alert alert) {
        super(alert.getPatientId(), alert.getCondition(), alert.getTimestamp());
        this.wrappee = alert;
    }
}
