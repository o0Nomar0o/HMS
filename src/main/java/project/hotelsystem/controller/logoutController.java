package project.hotelsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.hotelsystem.database.controller.userController;
import project.hotelsystem.settings.userSettings;

import java.io.File;
import java.net.URL;

/**
 * Logout Controller class
 *
 * @author Nomar
 */

public class logoutController {

    public static void logout(ActionEvent event) {
        userSettings ts = userSettings.getInstance();
        try {
            URL path = new File("src/main/resources/project/hotelsystem/login.fxml").toURI().toURL();
            FXMLLoader fxmlLoader = new FXMLLoader(path);
            Scene root = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            path = new File("src/main/resources/project/hotelsystem/css/login.css").toURI().toURL();
            stage.setTitle("Hotel Manager Login");
            stage.setWidth(650.0);
            stage.setHeight(448.0);
            stage.setScene(root);
            stage.centerOnScreen();
            stage.setResizable(false);

            System.out.println(ts.getUid());
            userController.updateStatus(ts.getUid(), "offline");

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ts.logout();
    }

    public static void logout(Stage st) {
        userSettings ts = userSettings.getInstance();
        try {
            URL path = new File("src/main/resources/project/hotelsystem/login.fxml").toURI().toURL();
            FXMLLoader fxmlLoader = new FXMLLoader(path);
            Scene root = new Scene(fxmlLoader.load());
            Stage stage = st;
            path = new File("src/main/resources/project/hotelsystem/css/login.css").toURI().toURL();
            stage.setTitle("Hotel Manager Login");
            stage.setWidth(650.0);
            stage.setHeight(448.0);
            stage.setScene(root);
            stage.centerOnScreen();
            stage.setResizable(false);

            userController.updateStatus(ts.getUid(), "offline");

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ts.logout();
    }

}
