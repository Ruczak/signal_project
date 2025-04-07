package com.cardio_generator.outputs;


/**
 * Interface managing the way generated data is displayed.
 * Every output strategy class should implement this interface.
 * 
 * @author https://github.com/tpepels
 */
public interface OutputStrategy {
    /**
     * Outputs the data generate for a concrete patient
     * @param patientId patient ID
     * @param timestamp time of the generation
     * @param label label of the message
     * @param data formatted data
     */
    void output(int patientId, long timestamp, String label, String data);
}
