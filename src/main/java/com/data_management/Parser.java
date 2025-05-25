package com.data_management;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final Pattern regex = Pattern.compile("Patient ID: ([^,]+), Timestamp: ([0-9]+), Label: (.+), Data: (.+)");

    public static class Message {
        private int patientId;
        private long timestamp;
        private String label;
        private double data;

        public Message(int patientId, long timestamp, String label, double data) {
            this.patientId = patientId;
            this.timestamp = timestamp;
            this.label = label;
            this.data = data;
        }

        public int getPatientId() {
            return patientId;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getLabel() {
            return label;
        }

        public double getData() {
            return data;
        }
    }

    public static Message decode(String line) {
        try {
            Matcher matcher = regex.matcher(line);
            if (!matcher.matches()) return null;

            int patientID = Integer.parseInt(matcher.group(1));
            long timestamp = Long.parseLong(matcher.group(2));
            String measurementType = matcher.group(3);
            double measurementValue;
            if (measurementType.equals("Saturation")) {
                measurementValue = Double.parseDouble(matcher.group(4).substring(0, matcher.group(4).length() - 1)) / 100.0;
            }
            else if (measurementType.equals("Alert")) {
                measurementValue = matcher.group(4).equals("triggered") ? 1.0 : 0.0;
            }
            else {
                measurementValue = Double.parseDouble(matcher.group(4));
            }

            return new Message(patientID, timestamp, measurementType, measurementValue);
        }
        catch (Exception e) {
            return null;
        }
    }
}
