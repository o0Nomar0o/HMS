package project.hotelsystem.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
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
            System.out.println(Runtime.getRuntime().totalMemory());
            stage.show();

            Runtime runtime = Runtime.getRuntime();

            new Thread(() -> {
                try {

                    Thread.sleep(100);
                    System.gc();
                    Thread.sleep(100);

                    System.out.println("Memory used after GC: " + ((runtime.totalMemory() - runtime.freeMemory())/ 1024.0) / 1024.0);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();

            System.out.println("Memory usage after logout: " + Runtime.getRuntime().totalMemory());

        } catch (Exception e) {
            e.printStackTrace();
        }
        ts.logout();
    }

    public static void logout(Stage st) {
        userSettings ts = userSettings.getInstance();
        try {

            Scene currentScene = st.getScene();
            currentScene.setRoot(new Pane());
            st.close();

            URL path = new File("src/main/resources/project/hotelsystem/login.fxml").toURI().toURL();
            FXMLLoader fxmlLoader = new FXMLLoader(path);
            Scene root = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            path = new File("src/main/resources/project/hotelsystem/css/login.css").toURI().toURL();
            stage.setTitle("Hotel Manager Login");
            stage.setWidth(650.0);
            stage.setHeight(448.0);
            stage.setScene(root);
            stage.centerOnScreen();
            stage.setResizable(false);

            userController.updateStatus(ts.getUid(), "offline");
            stage.show();

            System.gc();
            System.out.println("Memory usage after logout: " + Runtime.getRuntime().totalMemory());

        } catch (Exception e) {
            e.printStackTrace();
        }
        ts.logout();
    }

    private static void clearSceneResources(Stage stage) {
        if (stage != null && stage.getScene() != null) {
            stage.getScene().setRoot(new Pane());
            stage.getScene().getStylesheets().clear();
        }

    }

}
