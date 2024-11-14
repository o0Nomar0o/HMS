package project.hotelsystem.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import project.hotelsystem.database.controller.userController;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.settings.loaderSettings;
import project.hotelsystem.util.notificationManager;
import project.hotelsystem.web.WebSocketCon;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Nomar
 */
public class switchSceneController {

    userSettings ts = userSettings.getInstance();
    WebSocketCon wsc = WebSocketCon.getWebSocketClient();

    /**
     * @param priv admim or staff
     * @param view  which fxml file, do not include .fxml
     */

    public void swithcTo(ActionEvent event, Stage mainStage, String priv, String view) throws IOException {
        loaderSettings.applyDimmingEffect(event);

        Task<Parent> loadSceneTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {

                String rawPath = "src/main/resources/project/hotelsystem/" + priv + "/view/" + view + ".fxml";
                URL path = new File(rawPath).toURI().toURL();

                return FXMLLoader.load(path);

            }
        };

        Stage loadingStage = loaderSettings.showLoadingScreen(loadSceneTask, mainStage);

        loadSceneTask.setOnSucceeded(e -> {

            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.getScene().setRoot(new Pane());

                Parent root = loadSceneTask.getValue();

                stage.setOnCloseRequest(ev -> {
                    userController.updateStatus(ts.getUid(), "offline");
                    wsc.closeConnection();
                    Platform.exit();
                });

                ts.applyTheme(root, view);
                Runtime runtime = Runtime.getRuntime();

                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                        System.gc();
                        Thread.sleep(300);

                        System.out.println("Memory used after GC: " + ((runtime.totalMemory() - runtime.freeMemory())/ 1024.0) / 1024.0);

                    } catch (InterruptedException er) {
                        er.printStackTrace();
                    }

                }).start();

                stage.getScene().setRoot(root);
                stage.show();
                loadingStage.hide();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadSceneTask.setOnFailed(e -> {
            loadingStage.hide();
            notificationManager.showNotification(
                    "Failed to switch to " + view.toUpperCase(), "failure", mainStage);
            loaderSettings.removeDimmingEffect(event);

        });

        new Thread(loadSceneTask).start();
    }

    public void gc_swithcTo(ActionEvent event, Stage mainStage, String priv, String view) throws IOException {
        loaderSettings.applyDimmingEffect(event);

        // Task to load the FXML file asynchronously
        Task<Parent> loadSceneTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                String rawPath = "src/main/resources/project/hotelsystem/" + priv + "/view/" + view + ".fxml";
                URL path = new File(rawPath).toURI().toURL();

                // Create a fresh FXMLLoader instance for each load
                FXMLLoader fxmlLoader = new FXMLLoader(path);
                Parent root = fxmlLoader.load();
                fxmlLoader.setController(null); // Clear any controller references if not needed

                return root;
            }
        };

        // Show loading screen while new scene is loaded
        Stage loadingStage = loaderSettings.showLoadingScreen(loadSceneTask, mainStage);

        loadSceneTask.setOnSucceeded(e -> {
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Clear current scene's root to release old resources
                stage.getScene().setRoot(new Pane());

                // Get the new root from the task
                Parent root = loadSceneTask.getValue();

                // Add a close request handler to clean up resources on window close
                stage.setOnCloseRequest(ev -> {
                    userController.updateStatus(ts.getUid(), "offline");
                    wsc.closeConnection();
                    Platform.exit();
                });

                // Apply theme and set new root
                ts.applyTheme(root, view);
                stage.getScene().setRoot(root);

                // Hide loading screen and show main stage
                loadingStage.hide();
                stage.show();

                // Suggest garbage collection after scene switch
                System.gc();
                System.out.println("Memory usage after switching scene: " + Runtime.getRuntime().totalMemory());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadSceneTask.setOnFailed(e -> {
            loadingStage.hide();
            notificationManager.showNotification(
                    "Failed to switch to " + view.toUpperCase(), "failure", mainStage);
            loaderSettings.removeDimmingEffect(event);
        });

        // Start the task in a new thread
        new Thread(loadSceneTask).start();
    }


    public void toSettings(ActionEvent event, Stage mainStage) throws IOException {
        try {
            URL path = new File("src/main/resources/project/hotelsystem/settings.fxml").toURI().toURL();
            FXMLLoader fxmlLoader = new FXMLLoader(path);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = fxmlLoader.load();

            stage.setOnCloseRequest(ev -> {
                userController.updateStatus(ts.getUid(), "offline");
                wsc.closeConnection();
                Platform.exit();
                System.exit(0);
            });

            ts.applyTheme(root, "settings");

            new Thread(() -> {
                try {

                    Thread.sleep(100);
                    System.gc();
                    Thread.sleep(100);

                } catch (InterruptedException er) {
                    er.printStackTrace();
                }

            }).start();

            stage.getScene().setRoot(root);
            stage.show();
            stage.show();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
