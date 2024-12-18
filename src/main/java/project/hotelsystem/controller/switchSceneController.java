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


    public void swithcTo(ActionEvent event, Stage mainStage, String priv, String view) throws IOException {
        loaderSettings.applyDimmingEffect(event);

        Task<Parent> loadSceneTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {

                String rawPath = "/project/hotelsystem/" + priv + "/view/" + view + ".fxml";
                URL path = getClass().getResource(rawPath);

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

//                new Thread(() -> {
//                    try {
//                        Thread.sleep(300);
//                        System.gc();
//                        Thread.sleep(300);
//
//                        System.out.println("Memory used after GC: " + ((runtime.totalMemory() - runtime.freeMemory())/ 1024.0) / 1024.0);
//
//                    } catch (InterruptedException er) {
//                        er.printStackTrace();
//                    }
//
//                }).start();

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

        Task<Parent> loadSceneTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                String rawPath = "/project/hotelsystem/" + priv + "/view/" + view + ".fxml";
                URL path = getClass().getResource(rawPath);


                FXMLLoader fxmlLoader = new FXMLLoader(path);
                Parent root = fxmlLoader.load();
                fxmlLoader.setController(null);

                return root;
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
                stage.getScene().setRoot(root);

                loadingStage.hide();
                stage.show();

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

        new Thread(loadSceneTask).start();
    }


    public void toSettings(ActionEvent event, Stage mainStage) throws IOException {
        try {
            URL path = getClass().getResource("/project/hotelsystem/settings.fxml");
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
