package com.data_management;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Implements a real-time file reading method for patient data stored in a local directory.
 */
public class FileDataReader implements DataReader {
    private final String directory;

    private final List<DataReaderListener> listeners = new ArrayList<>();

    private final String[] files = new String[] {
            "Alert.txt",
            "Cholesterol.txt",
            "DiastolicPressure.txt",
            "ECG.txt",
            "RedBloodCells.txt",
            "Saturation.txt",
            "SystolicPressure.txt",
            "WhiteBloodCells.txt",
    };

    /**
     * @param directory directory with all the data.
     */
    public FileDataReader(String directory) {
        this.directory = directory;
    }

    /**
     * Reads specific file
     * @param file instance of the read file.
     * @throws IOException if file was not found or the file contained faulty data.
     */
    public void readFile(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            DataStorage dataStorage = DataStorage.getInstance();
            Scanner scanner = new Scanner(fileReader);

            Set<Patient> newRecords = new HashSet<>();

            while (fileReader.ready()) {
                try {
                    Parser.ParsedData parsedData = Parser.decode(scanner.nextLine());
                    Patient patient = dataStorage.addPatientData(parsedData.getPatientId(), parsedData.getData(), parsedData.getLabel(), parsedData.getTimestamp());
                    newRecords.add(patient);
                } catch (IllegalArgumentException e) {
                    throw new IOException(e.getMessage());
                }
            }

            for (Patient patient : newRecords) {
                triggerEvent(patient);
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
    public void triggerEvent(Patient patient) {
        for (DataReaderListener listener : listeners) {
            listener.onRead(patient);
        }
    }

    @Override
    public void refresh() throws IOException {
        for (String filename : files) {
            File file = new File(directory, filename);
            readFile(file);
        }
    }
}
