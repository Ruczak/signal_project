package data_management;

import com.cardio_generator.outputs.OutputStrategy;

import java.util.ArrayList;
import java.util.List;

public class TestOutputStrategy implements OutputStrategy {
    private final List<AlertRecord> records = new ArrayList<>();

    public static class AlertRecord {
        private final String label;
        private final String data;
        private final int patientId;
        private final long timestamp;

        public AlertRecord(String label, String data, int patientId, long timestamp) {
            this.label = label;
            this.data = data;
            this.patientId = patientId;
            this.timestamp = timestamp;
        }

        public String getLabel() {
            return label;
        }

        public String getData() {
            return data;
        }

        public int getPatientId() {
            return patientId;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        records.add(new AlertRecord(label, data, patientId, timestamp));
    }

    public List<AlertRecord> getRecords() {
        return new ArrayList<>(records);
    }
}
