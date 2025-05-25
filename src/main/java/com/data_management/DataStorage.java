package com.data_management;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alerts.AlertGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {
    private static DataStorage instance;

    private Map<Integer, Patient> patientMap; // Stores patient objects indexed by their unique patient ID.

    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */
    private DataStorage() {
        this.patientMap = new HashMap<>();
    }

    /**
     * Gets a global instance of the storage
     * @return {@code DataStorage} object
     */
    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }

        return instance;
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's records.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public Patient addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.get(patientId);
        if (patient == null) {
            patient = new Patient(patientId);
            patientMap.put(patientId, patient);
        }
        patient.addRecord(measurementValue, recordType, timestamp);

        return patient;
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        return new ArrayList<>(); // return an empty list if no patient is found
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * The main method for the DataStorage class.
     * Initializes the system, reads data into storage, and continuously monitors
     * and evaluates patient data.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

            DataReader fileReader = new FileDataReader("./output");
            DataReader websocketReader = new WebSocketClient(URI.create("ws://localhost:8080"));
            DataStorage storage = DataStorage.getInstance();

            // Initialize the AlertGenerator with the storage
            AlertGenerator alertGenerator = new AlertGenerator(storage);
            alertGenerator.setOutputStrategy(new ConsoleOutputStrategy());

            // Evaluate all patients' data to check for conditions that may trigger alerts
            DataReaderListener listener = (patient, i) -> alertGenerator.evaluateData(patient);

            fileReader.addListener(listener);
            websocketReader.addListener(listener);
            scheduler.scheduleAtFixedRate(() -> refreshReader(fileReader), 1, 30, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(() -> refreshReader(websocketReader), 5, 5, TimeUnit.SECONDS);
        } catch (WebsocketNotConnectedException e) {
            System.err.println("Couldn't connect to websocket.");
        } catch (Exception e) {
            System.err.println("Unexpected error has been thrown in the main code, " + e.getMessage());
        }
    }

    private static void refreshReader(DataReader fileReader) {
        try {
            fileReader.refresh();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
