package com.data_management;

import java.io.IOException;

/**
 * Provides uniform interface for every data reader class.
 * Implementation should add new records to {@link DataStorage} instance.
 */
public interface DataReader {
    /**
     * Adds new listener to a listener list.
     * @param listener listener to add
     */
    void addListener(DataReaderListener listener);

    /**
     * Removes listener from the list
     * @param listener listener to remove
     */
    void removeListener(DataReaderListener listener);

    /**
     * Triggers an event and sends them to the listeners.
     * @param patient patient for which new records arrived
     * @implSpec should notify every {@link DataReaderListener} added through
     * {@code addListener} method through {@code onRead()} method.
     */
    void triggerEvent(Patient patient);

    /**
     * Checks for new readings, and sends to the listeners.
     * @throws IOException
     */
    void refresh() throws IOException;
}
