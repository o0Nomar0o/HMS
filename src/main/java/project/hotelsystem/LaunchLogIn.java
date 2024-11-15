package project.hotelsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import project.hotelsystem.database.controller.userController;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.web.WebSocketCon;

import java.net.URL;

public class LaunchLogIn extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    userSettings urS = userSettings.getInstance();
    WebSocketCon wsc = WebSocketCon.getWebSocketClient();

    @Override
    public void start(Stage stage) throws Exception {
        try {



            URL path = getClass().getResource("/project/hotelsystem/login.fxml");
            System.out.println(path.toString());
            FXMLLoader fxmlLoader = new FXMLLoader(path);
            Scene root = new Scene(fxmlLoader.load());

            path = getClass().getResource("/project/hotelsystem/css/login.css");
            System.out.println(path.toString());

            root.getStylesheets().add(path.toExternalForm());


            stage.setTitle("Breeze Hub");
            stage.setScene(root);
            stage.setResizable(false);
            stage.show();

            System.out.println(stage.getWidth() + " " + stage.getHeight());

            stage.setOnCloseRequest(e -> {
                userController.updateStatus(urS.getUid(), "offline");
                wsc.closeConnection();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
