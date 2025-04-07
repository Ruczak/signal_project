package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code FileOutputStrategy} is an output strategy printing generated data to a separate file in
 * the base directory.
 * Class will use label from a data generator for naming the file. 
 * If the file already exists, it will append the current content.
 * 
 * @author https://github.com/tpepels
 */
public class FileOutputStrategy implements OutputStrategy {

    // Changed variable name to camelCase
    private String baseDirectory;

    // Change variable name to camelCase
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Creates {@FileOutputStrategy} object with specified base directory
     * @param baseDirectory target directory, where an output file will be created.
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the filePath variable
        // Line was too long - moved the argument into the next line
        String filePath = fileMap.computeIfAbsent(
            label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
            // Line was too long - moved the argument into the next line
                Files.newBufferedWriter(
                    Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            // Line was too long - moved the argument into the next line
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", 
                patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}