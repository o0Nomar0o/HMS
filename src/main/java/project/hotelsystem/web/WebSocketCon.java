package project.hotelsystem.web;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import project.hotelsystem.settings.userSettings;

import java.net.URI;

public class WebSocketCon {

    private WebSocketClient webSocketClient;
    userSettings uss = userSettings.getInstance();

    public void connect() {

        String ip = "youraddress";

        String serverUri = "ws://" + ip + ":8080";

        connectWebSocket(serverUri);

    }

    private void connectWebSocket(String uri) {
        try {

            webSocketClient = new WebSocketClient(new URI(uri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to WebSocket server");
                    sendUID();
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Hello");
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

    public void sendUID() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(uss.getUid());
            System.out.println("Sent: " + uss.getUid());
        }
    }

}
