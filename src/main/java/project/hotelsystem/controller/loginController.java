package project.hotelsystem.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.hotelsystem.settings.loaderSettings;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.util.authenticationManager;
import project.hotelsystem.database.controller.userController;
import project.hotelsystem.database.connection.DBConnection;
import project.hotelsystem.util.notificationManager;
import project.hotelsystem.web.WebSocketCon;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
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
    private Button create_account;

    @FXML
    private Button settings;

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

        if(!userController.isUserTableEmpty()){
            create_account.setDisable(true);
            return;
        }

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

        databasetoggle.setOnAction(e -> {
            boolean selected = databasetoggle.isSelected();
            DBConnection.setDb(selected);

            try {
                Connection connection = DBConnection.getConnection();

                if (selected) {
                    dblb.setStyle("-fx-text-fill: #BAC8F8;");
                } else {
                    dblb.setStyle("-fx-text-fill: black;");
                }

                connection.close();

            } catch (SQLException err) {

                notificationManager.showNotification(
                        "Cannot Connect",
                        "failure",
                        (Stage)login.getScene().getWindow());
                databasetoggle.setSelected(!selected);
                DBConnection.setDb(!selected);

                if (selected) {
                    dblb.setStyle("-fx-text-fill: black;");
                }
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

                WebSocketCon wsc = new WebSocketCon();
                wsc.connect();

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

    @FXML
    void tosettings(ActionEvent event) {
        try {

            URL path = new File("src/main/resources/dbsetup.fxml").toURI().toURL();
            FXMLLoader fxmlLoader = new FXMLLoader(path);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = fxmlLoader.load();

            path = new File("src/main/resources/css/dbsetup.css").toURI().toURL();
            root.getStylesheets().add(path.toExternalForm());

            stage.getScene().setRoot(root);

            stage.show();
        }catch (Exception e){

        }
    }

    @FXML
    void create_acc(ActionEvent event){

    createNewUser();
    }

    private void createNewUser() {

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        Stage owner = (Stage) login.getScene().getWindow();
        modalStage.initOwner(owner);
        modalStage.initStyle(StageStyle.TRANSPARENT);

        Text uid_txt = new Text("Enter UID");
        uid_txt.setFont(new Font("Arial", 16));
        uid_txt.setFill(Color.DARKSLATEGRAY);

        TextField enterUID = new TextField();
        enterUID.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px; "
        );
        VBox uidBox = new VBox(8, uid_txt, enterUID);

        Text username = new Text("Enter Username");
        username.setFont(new Font("Arial", 16));
        username.setFill(Color.DARKSLATEGRAY);

        TextField username_field = new TextField();
        username_field.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px; "
        );
        VBox userFieldBox = new VBox(8, username, username_field);

        HBox userCred = new HBox(35, uidBox, userFieldBox);

        Text email = new Text("Enter Email");
        email.setFont(new Font("Arial", 16));
        email.setFill(Color.DARKSLATEGRAY);

        TextField email_field = new TextField();
        email_field.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px; "
        );
        VBox emailBox = new VBox(8, email, email_field);

        Text phone_no = new Text("Enter Phone No.");
        phone_no.setFont(new Font("Arial", 16));
        phone_no.setFill(Color.DARKSLATEGRAY);

        TextField phno_field = new TextField();
        phno_field.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px; "
        );
        VBox phBox = new VBox(8, phone_no, phno_field);

        Text selectPrivilege = new Text("Select Privilege");
        selectPrivilege.setFont(new Font("Arial", 16));
        selectPrivilege.setFill(Color.DARKSLATEGRAY);

        ComboBox<String> privilegeBox = new ComboBox<>();
        privilegeBox.getItems().addAll("admin");
        privilegeBox.getSelectionModel().selectFirst();
        privilegeBox.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px; "
        );
        VBox privBox = new VBox(8, selectPrivilege, privilegeBox);

        Text pw_txt = new Text("Enter Password");
        pw_txt.setFont(new Font("Arial", 16));
        pw_txt.setFill(Color.DARKSLATEGRAY);

        PasswordField pw_field = new PasswordField();
        pw_field.setStyle(
                "-fx-background-color: #f0f0f0; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px; "
        );
        VBox pwFieldBox = new VBox(8, pw_txt, pw_field);

        VBox credentialsPane = new VBox(15, userCred, emailBox, pwFieldBox);
        credentialsPane.setStyle(
                "-fx-spacing: 15px; " +  // Spacing between the form sections
                        "-fx-padding: 30px; " +  // Padding around the entire form
                        "-fx-background-color: #f9f9f9; " +  // Light background for the form
                        "-fx-border-radius: 10; " +  // Rounded corners
                        "-fx-border-color: #eaeaea; " +  // Subtle border color
                        "-fx-border-width: 1; "
        );

        BorderPane modalRoot = new BorderPane();

        Text modalTitle = new Text("Add User");
        modalTitle.setFont(new Font(28.0));

        Text modalHint = new Text("Create a new user for your system");
        VBox topBox = new VBox(modalTitle, modalHint);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-padding: 10px;");

        modalRoot.setTop(topBox);
        BorderPane.setAlignment(topBox, Pos.CENTER);

        modalRoot.setCenter(credentialsPane);

        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");

        cancelButton.setStyle(
                "-fx-background-color: #ff4d4d; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-family: Arial; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 10;"
        );
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(
                "-fx-background-color: #ff1a1a; -fx-text-fill: white; -fx-background-radius: 10;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: Arial;" +
                        " -fx-padding: 10px 20px;"
        ));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(
                "-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 10;"+
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: Arial;" +
                        " -fx-padding: 10px 20px;"
        ));

        confirmButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-family: Arial; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 10;"
        );
        confirmButton.setOnMouseEntered(e -> confirmButton.setStyle(
                "-fx-background-color: #3b9E4F;; -fx-text-fill: #333333; -fx-background-radius: 10;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: Arial;" +
                        " -fx-padding: 10px 20px;"
        ));
        confirmButton.setOnMouseExited(e -> confirmButton.setStyle(
                "-fx-background-color: #4CAF50;; -fx-text-fill: #333333; -fx-background-radius: 10;" +
                        " -fx-font-size: 14px;" +
                        " -fx-font-family: Arial;" +
                        "  -fx-padding: 10px 20px;"
        ));
        HBox buttonBox = new HBox(15, cancelButton, confirmButton);
        modalRoot.setBottom(buttonBox);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 15px;");

        modalRoot.setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);


        Scene modalScene = new Scene(modalRoot, 380, 420);

        modalScene.setFill(Color.TRANSPARENT);
        modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");
        modalStage.setScene(modalScene);
        modalStage.setResizable(false);
        modalStage.show();

        modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
        modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

        confirmButton.setOnAction(e -> {
            if (enterUID.getText().isEmpty() ||
                    pw_field.getText().isEmpty()) return;

            addUserToDB(enterUID.getText(),
                    username_field.getText(), pw_field.getText(),
                    privilegeBox.getSelectionModel().getSelectedItem(),
                    email_field.getText(), phno_field.getText(),
                    e, modalStage);

        });
        cancelButton.setOnAction(e -> {
            modalStage.close();
        });
    }

    private void addUserToDB(String userID, String name, String pw, String priv,
                             String email, String ph, ActionEvent e, Stage st) {

        loaderSettings.applyDimmingEffect(e);

        Task<Boolean> loadSceneTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                try {

                    boolean success = authenticationManager.addNewUser(userID, name, pw, priv, email, ph);

                    if (!success) {
                        throw new Exception("Failed to add new user to the database.");
                    }

                    return success;
                } catch (Exception ex) {

                    ex.printStackTrace();
                    throw ex;
                }
            }
        };

        Stage loadingStage = loaderSettings.showLoadingBolScreen(loadSceneTask, st);

        loadSceneTask.setOnSucceeded(ev -> {
            try {
                notificationManager.showNotification(String.format("Successfully added %s", userID), "success", (Stage) login.getScene().getWindow());
                st.close();
                loaderSettings.removeDimmingEffect(e);
                loadingStage.hide();
                System.out.println("User added successfully.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadSceneTask.setOnFailed(er -> {
            try {
                loadingStage.hide();
                loaderSettings.removeDimmingEffect(e);
                notificationManager.showNotification("Failed to create a user", "failure", st);
                System.out.println("Failed to add user. Please try again.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        new Thread(loadSceneTask).start();
    }


}
