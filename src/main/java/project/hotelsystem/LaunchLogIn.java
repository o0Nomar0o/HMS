package project.hotelsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import project.hotelsystem.database.controller.userController;
import project.hotelsystem.settings.userSettings;

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

//            URL path = getClass().getResource("/login.fxml");
            URL path = new File("src/main/resources/login.fxml").toURI().toURL();

            FXMLLoader fxmlLoader = new FXMLLoader(path);
            Scene root = new Scene(fxmlLoader.load());

//            path = getClass().getResource("/login.css");
            path = new File("src/main/resources/css/login.css").toURI().toURL();

            root.getStylesheets().add(path.toExternalForm());

            path = new File("src/main/resources/images/hotel_7499247.png").toURI().toURL();
            Image icn = new Image(path.toExternalForm());
            stage.getIcons().add(icn);

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
