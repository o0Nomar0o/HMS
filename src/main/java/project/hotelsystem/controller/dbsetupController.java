package project.hotelsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.hotelsystem.settings.databaseSettings;
import project.hotelsystem.util.notificationManager;

import java.io.File;
import java.net.URL;

/**
 * Database Setup Controller class
 *
 * @author Nomar
 */

public class dbsetupController {

    @FXML
    private PasswordField cloud_pw;

    @FXML
    private TextField cloud_url;

    @FXML
    private TextField cloud_user;

    @FXML
    private Button go_back;

    @FXML
    private PasswordField local_pw;

    @FXML
    private TextField local_url;

    @FXML
    private TextField local_user;

    @FXML
    private Button save_cloud;

    @FXML
    private Button save_local;

    databaseSettings dbs = databaseSettings.getInstance();

    @FXML
    void initialize() {

        save_local.setCursor(Cursor.HAND);
        save_cloud.setCursor(Cursor.HAND);

        setupFields();
    }

    private void setupFields() {

        dbs.loadSettings();

        local_url.setText(dbs.getLocal_url());
        local_user.setText(dbs.getLocal_user());

        cloud_url.setText(dbs.getWeb_url());
        cloud_user.setText(dbs.getCloud_user());
    }

    @FXML
    void save_cloud_db(ActionEvent event) {

        String url = cloud_url.getText().replaceAll("\\s+","");

        dbs.saveWebSettings(url);

        notificationManager.showNotification(
                "Saved Web Address",
                "success",
                (Stage) go_back.getScene().getWindow()
        );

    }

    @FXML
    void save_local_db(ActionEvent event) {

        if(local_url.getText().isEmpty()||
                local_user.getText().isEmpty()||
                local_pw.getText().isEmpty()){

            notificationManager.showNotification(
                    "Please fill all the fields",
                    "failure",
                    (Stage)go_back.getScene().getWindow()
            );
            return;
        }


        String url = local_url.getText().replaceAll("\\s+","");
        String user = local_user.getText();
        String pw = local_pw.getText();

        dbs.saveLocalSettings(url, user, pw);
        notificationManager.showNotification(
                "Saved Local Database",
                "success",
                (Stage) go_back.getScene().getWindow()
        );

    }

    @FXML
    void go_back(ActionEvent event) {
        try {

            URL path = getClass().getResource("/project/hotelsystem/login.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(path);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = fxmlLoader.load();

            path = getClass().getResource("/project/hotelsystem/css/login.css");
            root.getStylesheets().add(path.toExternalForm());

            stage.getScene().setRoot(root);

            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {

        }
    }

}
