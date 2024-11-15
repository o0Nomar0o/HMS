package project.hotelsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import project.hotelsystem.database.controller.userController;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.web.WebSocketCon;

import java.io.File;
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


           URL path = new File("src/main/resources/project/hotelsystem/login.fxml").toURI().toURL();

            FXMLLoader fxmlLoader = new FXMLLoader(path);
            Scene root = new Scene(fxmlLoader.load());

            path = new File("src/main/resources/project/hotelsystem/css/login.css").toURI().toURL();

            root.getStylesheets().add(path.toExternalForm());

            path = new File("src/main/resources/project/hotelsystem/images/1024x1024.icn").toURI().toURL();
            Image icn = new Image(path.toExternalForm());
            stage.getIcons().add(icn);

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
