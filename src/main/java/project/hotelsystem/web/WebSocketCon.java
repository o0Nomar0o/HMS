package project.hotelsystem.web;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import project.hotelsystem.database.controller.transactionController;
import project.hotelsystem.settings.databaseSettings;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.util.authenticationManager;

import java.net.URI;
import java.util.Random;
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

    public boolean send(String msg) {

        if (webSocketClient != null && webSocketClient.isOpen()) {

            webSocketClient.send(msg);
            System.out.println("Sent: " + msg);
            return true;
        }

        return false;
    }

    public void sendID() {

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

    public boolean sendPasswordResetNotification(String userId) {
        String generatedPassword = generateRandomPassword();

        JSONObject json = new JSONObject();
        json.put("action", "PasswordReset");
        json.put("user_id", userId);
        json.put("new_password", generatedPassword);

        if (send(json.toString()))
            if (authenticationManager.resetPassword(userId, generatedPassword)) {
                return true;
            }
        return false;
    }

    private String generateRandomPassword() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

}
