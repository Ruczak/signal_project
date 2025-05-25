package com.data_management;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient implements DataReader {
    List<DataReaderListener> listeners = new ArrayList<>();

    Queue<String> messages = new LinkedList<>();

    public WebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Websocket connection opened. ");
    }

    @Override
    public void onMessage(String s) {
        messages.offer(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
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
    public void triggerEvent(Patient patient, int i) {
        for (DataReaderListener listener : listeners) {
            listener.onRead(patient, i);
        }
    }

    @Override
    public void refresh() {
        Pattern regex = Pattern.compile("Patient ID: ([^,]+), Timestamp: ([0-9]+), Label: (.+), Data: (.+)");
        DataStorage dataStorage = DataStorage.getInstance();
        while (!messages.isEmpty()) {
            String message = messages.poll();

            Matcher matcher = regex.matcher(message);
            if (!matcher.matches()) continue;

            int patientID = Integer.parseInt(matcher.group(1));
            long timestamp = Long.parseLong(matcher.group(2));
            String measurementType = matcher.group(3);
            double measurementValue = Parser.parse(measurementType, matcher.group(4));

            triggerEvent(dataStorage.addPatientData(patientID, measurementValue, measurementType, timestamp), 1);
        }
    }
}
