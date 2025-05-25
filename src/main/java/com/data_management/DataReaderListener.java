package com.data_management;

/**
 * Listener method thrown when a new record for a certain patient arrives in the {@code DataStorage} instance.
 */
public interface DataReaderListener {
    void onRead(Patient patient);
}
