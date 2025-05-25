package com.data_management;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class FileDataReader implements DataReader {
    private final String directory;

    private final List<DataReaderListener> listeners = new ArrayList<>();

    public FileDataReader(String directory) {
        this.directory = directory;
    }

    public void readFile(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            DataStorage dataStorage = DataStorage.getInstance();
            Scanner scanner = new Scanner(fileReader);

            Map<Patient, Integer> newRecords = new HashMap<>();

            Pattern regex = Pattern.compile("Patient ID: ([^,]+), Timestamp: ([0-9]+), Label: (.+), Data: (.+)");
            while (fileReader.ready()) {
                String line = scanner.nextLine();

                Matcher matcher = regex.matcher(line);

                if (!matcher.matches()) continue;

                int patientID = Integer.parseInt(matcher.group(1));
                long timestamp = Long.parseLong(matcher.group(2));
                String measurementType = matcher.group(3);
                double measurementValue = Parser.parse(measurementType, matcher.group(4));

                Patient patient = dataStorage.addPatientData(patientID, measurementValue, measurementType, timestamp);

                if (!newRecords.containsKey(patient)) {
                    newRecords.put(patient, 0);
                }
                newRecords.put(patient, newRecords.get(patient) + 1);
            }

            for (Map.Entry<Patient, Integer> entry : newRecords.entrySet()) {
                triggerEvent(entry.getKey(), entry.getValue());
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found: " + file.getAbsolutePath());
        }
    }

    @Override
    public void addListener(DataReaderListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(DataReaderListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void triggerEvent(Patient patient, int i) {
        for (DataReaderListener listener : listeners) {
            listener.onRead(patient, i);
        }
    }

    @Override
    public void refresh() {
        String[] files = new String[] {
                "Alert.txt",
                "Cholesterol.txt",
                "DiastolicPressure.txt",
                "ECG.txt",
                "RedBloodCells.txt",
                "Saturation.txt",
                "SystolicPressure.txt",
                "WhiteBloodCells.txt",
        };

        for (String filename : files) {
            try {
                File file = new File(directory, filename);
                readFile(file);
            } catch (IOException e) {
                System.err.println("Error reading file: " + filename);
            }
        }
    }
}
