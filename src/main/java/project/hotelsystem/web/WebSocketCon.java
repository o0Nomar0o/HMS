package project.hotelsystem.web;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import project.hotelsystem.settings.databaseSettings;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.database.controller.transactionController;

import java.net.URI;
import java.util.UUID;

public class WebSocketCon {

    WebSocketClient webSocketClient;
    static WebSocketCon instance;
    userSettings uss = userSettings.getInstance();
    databaseSettings dbs = databaseSettings.getInstance();

    public WebSocketCon() {
    }

    public static WebSocketCon getWebSocketClient() {

        if (instance == null) {
            instance = new WebSocketCon();

        }

        return instance;
    }


    public void connect() {

        if (webSocketClient != null && webSocketClient.isOpen()) {
            return;
        }

        dbs.loadSettings();


        String serverUri = dbs.getWeb_url();

        connectWebSocket(serverUri);

    }

    private void connectWebSocket(String uri) {
        try {

            webSocketClient = new WebSocketClient(new URI(uri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to WebSocket server");
                    sendID();
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Received: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("WebSocket error: " + ex.getMessage());
                }
            };

            webSocketClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
            System.out.println("WebSocket connection closed");
        }
    }

    public void sendID() {

        if (webSocketClient != null && webSocketClient.isOpen()) {
            String transactionId = "LOGIN";
            String userId = uss.getUid();

            String message = "{\"transaction_id\": \"" + transactionId + "\", " +
                    "\"user_id\": \"" + userId + "\"}";

            webSocketClient.send(message);
            System.out.println("Sent: " + message);
        }
    }

    public void sendUID_logout() {

        if (webSocketClient != null && webSocketClient.isOpen()) {
            String transactionId = UUID.randomUUID().toString();
            String userId = uss.getUid();

            transactionController.newTransaction(transactionId, userId);

            String message = "{\"transaction_id\": \"" + transactionId + "\", " +
                    "\"user_id\": \"" + userId + "\"}";

            webSocketClient.send(message);
            System.out.println("Sent: " + message);
        }
    }

}
