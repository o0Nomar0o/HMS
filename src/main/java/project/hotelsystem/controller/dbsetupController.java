package project.hotelsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import project.hotelsystem.settings.databaseSettings;

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
    void initialize(){

        setupFields();
    }

    private void setupFields(){

        local_url.setText(dbs.getLocal_url());
        local_user.setText(dbs.getLocal_user());
        local_user.setText(dbs.getLocal_password());

    }

    @FXML
    void save_cloud_db(ActionEvent event) {

    }

    @FXML
    void save_local_db(ActionEvent event) {

    }

}
