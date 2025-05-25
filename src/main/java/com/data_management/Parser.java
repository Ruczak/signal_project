package com.data_management;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final Pattern regex = Pattern.compile(
            "Patient ID: ([^,]+), Timestamp: ([0-9]+), Label: (.+), Data: (.+)");

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

    public static Message decode(String line) throws IllegalArgumentException {
        Matcher matcher = regex.matcher(line);
        if (!matcher.matches()) throw new IllegalArgumentException(
                "The message format does not match the pattern. (message: \"" + line + "\")");

        try {
            int patientID = Integer.parseInt(matcher.group(1));
            long timestamp = Long.parseLong(matcher.group(2));
            String measurementType = matcher.group(3);
            double measurementValue = getMeasurementValue(measurementType, matcher);

            return new Message(patientID, timestamp, measurementType, measurementValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "The numbers in the message do not match their respective types. (message: \"" + line + "\")");
        }

    }

    private static double getMeasurementValue(String measurementType, Matcher matcher) {
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
        return measurementValue;
    }
}
