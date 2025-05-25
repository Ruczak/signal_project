package com.data_management;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Serves web socket connection for incoming patient data.
 */
public class WebSocketClient implements DataReader {
    private final List<DataReaderListener> listeners = new ArrayList<>();

    private org.java_websocket.client.WebSocketClient websocket;

    private final Queue<String> messages = new LinkedList<>();

    private final URI serverUri;

    public WebSocketClient(URI serverUri) {
        this.serverUri = serverUri;
        establishConnection();
    }

    public void establishConnection() {
        websocket = new org.java_websocket.client.WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("Websocket connection opened. ");
            }

            @Override
            public void onMessage(String s) {
                messages.offer(s);
                //System.out.println("Message received: " + s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("Websocket connection closed. ");
            }

            @Override
            public void onError(Exception e) {
                throw new WebsocketNotConnectedException();
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
    public void triggerEvent(Patient patient) {
        for (DataReaderListener listener : listeners) {
            listener.onRead(patient);
        }
    }

    @Override
    public void refresh() throws IOException {
        DataStorage dataStorage = DataStorage.getInstance();
        while (!messages.isEmpty()) {
            try {
                String str = messages.poll();
                Parser.ParsedData parsedData = Parser.decode(str);

                triggerEvent(
                        dataStorage.addPatientData(
                                parsedData.getPatientId(), parsedData.getData(), parsedData.getLabel(), parsedData.getTimestamp()));
            } catch (IllegalArgumentException e) {
                throw new IOException("Received faulty message. ");
            }
        }
    }
}
