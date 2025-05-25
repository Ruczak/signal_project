package com.data_management;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.*;

public class WebSocketClient implements DataReader {
    private final List<DataReaderListener> listeners = new ArrayList<>();

    private org.java_websocket.client.WebSocketClient websocket;

    private Queue<String> messages = new LinkedList<>();

    public WebSocketClient(URI serverUri) {
        websocket = new org.java_websocket.client.WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("Websocket connection opened. ");
            }

            @Override
            public void onMessage(String s) {
                messages.offer(s);
                System.out.println("Message received: " + s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("Websocket connection closed. ");
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };

        websocket.connect();
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
        DataStorage dataStorage = DataStorage.getInstance();
        while (!messages.isEmpty()) {
            String str = messages.poll();
            Parser.Message message = Parser.decode(str);
            if (message == null) continue;

            triggerEvent(
                    dataStorage.addPatientData(
                            message.getPatientId(), message.getData(), message.getLabel(), message.getTimestamp()), 1);
        }
    }
}
