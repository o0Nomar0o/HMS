package project.hotelsystem.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.hotelsystem.settings.loaderSettings;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.util.authenticationManager;
import project.hotelsystem.database.controller.userController;
import project.hotelsystem.database.connection.DBConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class loginController {

    @FXML
    private Button clearPassword;

    @FXML
    private PasswordField Password;

    @FXML
    private TextField PasswordTextField;

    @FXML
    private Button showPassword;

    @FXML
    private Button clearUID;

    @FXML
    private Button contact;

    @FXML
    private Button login;

    @FXML
    private Text passwordError;

    @FXML
    private TextField uidField;

    @FXML
    private Text userError;

    @FXML
    private ToggleButton databasetoggle;

    @FXML
    private Label dblb;

    userSettings uSettings = userSettings.getInstance();

    @FXML
    void initialize() {
        clearUID.setUserData("uid");
        clearUID.setOnAction(this::clearField);
        clearPassword.setUserData("pw");
        clearPassword.setOnAction(this::clearField);

        showPassword.setOnMousePressed(e -> {
            Password.setVisible(false);
            PasswordTextField.setText(Password.getText());
            PasswordTextField.setVisible(true);
        });

        showPassword.setOnMouseReleased(e -> {
            Password.setVisible(true);
            PasswordTextField.setVisible(false);
        });

        uidField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    Password.requestFocus();
                }
            }
        });

        Password.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    login.fire();
                }
            }
        });

        databasetoggle.setSelected(DBConnection.isDb());

        databasetoggle.setOnAction(e -> {
            if (databasetoggle.isSelected()) {
                System.out.println(databasetoggle);
                DBConnection.setDb(databasetoggle.isSelected());
                dblb.setStyle("-fx-text-fill: #BAC8F8;");
            } else {
                System.out.println(databasetoggle);
                dblb.setStyle("-fx-text-fill: black;");
                DBConnection.setDb(databasetoggle.isSelected());
            }
            try {
                System.out.println(DBConnection.getConnection().toString());
            } catch (SQLException err) {
                err.printStackTrace();
            }
        });

    }

    boolean auth = false;
    String viewName = "";

    @FXML
    void loginAction(ActionEvent event) throws IOException {

        loaderSettings.applyDimmingEffect(event);

        Task<Parent> loadSceneTask = new Task<>() {

            @Override
            protected Parent call() throws Exception {
                String uid = uidField.getText().toString();
                String password = Password.getText().toString();

                auth = authenticationManager.Authenticate(uid, password);

                if (!auth) {
                    userError.setVisible(true);
                    passwordError.setVisible(true);
                    return null;
                }


                uSettings.setUser(uidField.getText(), authenticationManager.getPrivilege(), authenticationManager.getUsername());
                userController.updateStatus(uSettings.getUid(), "online");

                switch (authenticationManager.getPrivilege()) {
                    case "admin":
                        URL path = new File("src/main/resources/admin/view/dashboard.fxml").toURI().toURL();
                        viewName = "dashboard";
                        return FXMLLoader.load(path);

                    case "staff":
                        URL pathpath = new File("src/main/resources/staff/view/rooms.fxml").toURI().toURL();
                        viewName = "booking";
                        return FXMLLoader.load(pathpath);

                    default:
                        return null;
                }

            }
        };

        Stage loadingStage = loaderSettings.showLoadingScreen(loadSceneTask, (Stage) login.getScene().getWindow());

        loadSceneTask.setOnSucceeded(e -> {

            try {

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root = loadSceneTask.getValue();

                if (!auth) {
                    loadingStage.hide();
                    loaderSettings.removeDimmingEffect(event);
                    return;
                }

                uSettings.applyTheme(root, viewName);

                stage.getScene().setRoot(root);
                stage.setWidth(1120);
                stage.setHeight(880);
                stage.centerOnScreen();
                stage.show();
                loadingStage.hide();
                stage.setResizable(true);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadSceneTask.setOnFailed(e -> {

            loadingStage.hide();
            loaderSettings.removeDimmingEffect(event);

        });

        new Thread(loadSceneTask).start();
    }


    void clearField(ActionEvent event) {

        Button clicked = (Button) event.getTarget();
        String data = clicked.getUserData().toString();

        switch (data) {
            case "uid":
                uidField.clear();
                break;
            case "pw":
                Password.clear();
                break;
            default:
                break;
        }

    }


}
