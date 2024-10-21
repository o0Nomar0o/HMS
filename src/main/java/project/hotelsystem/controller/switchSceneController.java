package project.hotelsystem.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.settings.loaderSettings;
import project.hotelsystem.util.notificationManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class switchSceneController {

    /**
     * @param priv admim or staff
     * @param view  which fxml file, do not includee .fxml
     */


    userSettings ts = userSettings.getInstance();

    public void swithcTo(ActionEvent event, Stage mainStage, String priv, String view) throws IOException {
        loaderSettings.applyDimmingEffect(event);

        Task<Parent> loadSceneTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {

                String rawPath = "src/main/resources/" + priv + "/view/" + view + ".fxml";
                URL path = new File(rawPath).toURI().toURL();

                return FXMLLoader.load(path);

            }
        };

        Stage loadingStage = loaderSettings.showLoadingScreen(loadSceneTask, mainStage);

        loadSceneTask.setOnSucceeded(e -> {
            try {

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root = loadSceneTask.getValue();

                ts.applyTheme(root, view);

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

    public void toSettings(ActionEvent event, Stage mainStage) throws IOException {
        try {
            URL path = new File("src/main/resources/settings.fxml").toURI().toURL();
            FXMLLoader fxmlLoader = new FXMLLoader(path);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = fxmlLoader.load();

            ts.applyTheme(root, "settings");

            stage.getScene().setRoot(root);
            stage.show();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
