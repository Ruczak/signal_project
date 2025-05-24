package com.cardio_generator;

import com.data_management.DataStorage;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 0 && args[0].equals("DataStorage")) {
                DataStorage.main(new String[]{});
            } else {
                HealthDataSimulator.main(args);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
