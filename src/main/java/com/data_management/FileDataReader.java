package com.data_management;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.*;

public class FileDataReader implements DataReader {
    private final String directory;

    public FileDataReader(String directory) {
        this.directory = directory;
    }

    private String parseLabel(String label) {
        switch (label) {
            case "WhiteBloodCells": return "White blood cells";
            case "RedBloodCells":  return "Red blood cells";
            case "SystolicPressure":  return "Systolic pressure";
            case "DiastolicPressure":  return "Diastolic pressure";
            default: return label;
        }
    }

    private double parseData(String data) {
        try {
            if (data.equals("Saturation")) {
                return Double.parseDouble(data.substring(0, data.length() - 1)) / 100.0;
            }
            return Double.parseDouble(data);
        }
        catch (Exception e) {
            return 0.0;
        }
    }

    public void readFile(DataStorage dataStorage, File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            Scanner scanner = new Scanner(fileReader);

            Pattern regex = Pattern.compile("Patient ID: ([^,]+), Timestamp: ([0-9]+), Label: (.+), Data: (.+)");
            while (fileReader.ready()) {
                String line = scanner.nextLine();

                Matcher matcher = regex.matcher(line);

                if (!matcher.matches()) continue;

                int patientID = Integer.parseInt(matcher.group(1));
                long timestamp = Long.parseLong(matcher.group(2));
                String measurementType = parseLabel(matcher.group(3));
                double measurementValue = parseData(matcher.group(4));

                dataStorage.addPatientData(patientID, measurementValue, measurementType, timestamp);
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found: " + file.getAbsolutePath());
        }
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        String[] files = new String[] {
                "Cholesterol.txt",
                "DiastolicPressure.txt",
                "ECG.txt",
                "RedBloodCells.txt",
                "Saturation.txt",
                "SystolicPressure.txt",
                "WhiteBloodCells.txt",
        };

        for (String filename : files) {
            File file = new File(directory, filename);
            readFile(dataStorage, file);
        }
    }
}
