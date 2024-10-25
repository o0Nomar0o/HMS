package project.hotelsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.database.controller.userController;

import java.io.File;
import java.net.URL;

public class LaunchLogIn extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    userSettings urS = userSettings.getInstance();

    @Override
    public void start(Stage stage) throws Exception {
        try {

            URL path = new File("src/main/resources/login.fxml").toURI().toURL();
            FXMLLoader fxmlLoader = new FXMLLoader(path);
            Scene root = new Scene(fxmlLoader.load());

            path = new File("src/main/resources/css/login.css").toURI().toURL();
            root.getStylesheets().add(path.toExternalForm());
            stage.setTitle("Hotel Manager Login");
            stage.setScene(root);
            stage.setResizable(false);
            stage.show();

            System.out.println(stage.getWidth() + " " + stage.getHeight());

            stage.setOnCloseRequest(e -> {
                userController.updateStatus(urS.getUid(), "offline");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
