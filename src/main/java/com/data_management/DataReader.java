package com.data_management;

import java.io.IOException;

public interface DataReader {
    void addListener(DataReaderListener listener);

    void removeListener(DataReaderListener listener);

    void triggerEvent(Patient patient, int i);

    void refresh();
}
