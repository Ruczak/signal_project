package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface defining generator type along with, with input parameters. 
 * Every data generator class should implement this interface.
 * 
 * @author https://github.com/tpepels
 */
public interface PatientDataGenerator {
    /**
     * Generates data for the patientID and displays it using output strategy.
     * @param patientId ID of the patient
     * @param outputStrategy output strategy used to display the data.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
