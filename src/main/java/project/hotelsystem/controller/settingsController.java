package project.hotelsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.hotelsystem.database.controller.userController;
import project.hotelsystem.database.models.user;
import project.hotelsystem.settings.loaderSettings;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.util.authenticationManager;
import project.hotelsystem.util.notificationManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Settings Controller class
 *
 * @author Nomar
 * @author Kaung Khant Thu
 */

public class settingsController implements Initializable {

    @FXML
    private Button audit_view;

    @FXML
    private Button bottomButton;

    @FXML
    private Button cancel;

    @FXML
    private RadioButton dark;

    @FXML
    private Button add;

    @FXML
    private Button remove;

    @FXML
    private VBox hotelMod;

    @FXML
    private VBox settingsView;

    @FXML
    private RadioButton light;

    @FXML
    private Button logout;

    @FXML
    private Button midButton;

    @FXML
    private Button path_button;

    @FXML
    private Button save;

    @FXML
    private Button services;

    @FXML
    private Button setting;

    @FXML
    private Button edit;

    @FXML
    private Button topButton;

    @FXML
    private TableColumn<?, ?> email;
    @FXML
    private TableColumn<?, ?> phcol;
    @FXML
    private TableColumn<?, ?> status;

    @FXML
    private TableView<user> userTable;

    @FXML
    private TableColumn<user, String> id;

    @FXML
    private TableColumn<user, String> name;

    @FXML
    private TableColumn<user, String> role;

    @FXML
    private Text pathtxt;


    private ObservableList<user> userList = FXCollections.observableArrayList();
    private List<user> initList = new ArrayList<>();

    private String theme;
    private String uid;

    private userSettings ts = userSettings.getInstance();
    private profileContorller pfc = new profileContorller();
    private switchSceneController ssc = new switchSceneController();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        dark.setToggleGroup(themeSelector);
        light.setToggleGroup(themeSelector);

        loadWindowsSettings();

        pathtxt.setText("Invoice Path: " + ts.getInvoice_path());

        switch (theme) {
            case "light":
                light.setSelected(true);
                break;
            case "dark":
                dark.setSelected(true);
                break;
            case null, default:
                break;
        }

        if (ts.getPrivilege().matches("admin")) {

            topButton.setText("Dashboard");
            midButton.setText("Rooms");
            bottomButton.setText("Bookings");

            topButton.setOnAction(e -> {
                try {
                    switchToDashboard(e);
                } catch (IOException ex) {
                    ex.printStackTrace();

                }
            });
            midButton.setOnAction(e -> {
                try {
                    switchtorooms(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            bottomButton.setOnAction(e -> {
                try {
                    switchtobookings(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            services.setOnAction(e -> {
                try {
                    switchtoservices(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            userService.start();

        } else {
            staffController sc = new staffController();

            hotelMod.setVisible(false);
            settingsView.getChildren().remove(hotelMod);
            topButton.setText("Rooms");
            midButton.setText("Bookings");
            bottomButton.setText("Orders");

            topButton.setOnAction(e -> {
                try {
                    sc.switchtoRoom(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            midButton.setOnAction(e -> {
                try {
                    sc.switchtoBooking(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            bottomButton.setOnAction(e -> {
                try {
                    sc.switchtoOrders(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            services.setOnAction(e -> {
                try {
                    switchtoservices(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });


        }


        pfc.profileMod();

        id.setCellValueFactory(new PropertyValueFactory<>("uid"));
        name.setCellValueFactory(new PropertyValueFactory<>("username"));
        role.setCellValueFactory(new PropertyValueFactory<>("privilege"));
        phcol.setCellValueFactory(new PropertyValueFactory<>("phone_no"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        userTable.setPlaceholder(new Label("Loading user data..."));

        userService.valueProperty().addListener((obs, oldData, newData) -> {
            userTable.getItems().clear();
            userTable.setPlaceholder(new Label("Loading user data..."));
            if (newData != null && !newData.isEmpty()) {
                userTable.getItems().addAll(newData);
                return;
            }

            userTable.setPlaceholder(new Label("No users found."));

        });

        SelectionModel<user> smu = userTable.getSelectionModel();
        smu.selectedItemProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {
                edit.setDisable(false);
                remove.setDisable(false);
            } else {
                edit.setDisable(true);
                remove.setDisable(true);
            }
        });

        logout.setOnAction(logoutController::logout);

    }

    private final Service<List<user>> userService = new Service<>() {
        @Override
        protected Task<List<user>> createTask() {
            return new Task<>() {
                @Override
                protected List<user> call() throws Exception {
                    return userController.getAllUsers();
                }
            };
        }
    };


    @FXML
    void switchToDashboard(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "dashboard");
    }

    @FXML
    void switchtorooms(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "rooms");
    }

    @FXML
    void switchtoservices(ActionEvent event) throws IOException {
        System.out.println(ts.getPrivilege());
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), ts.getPrivilege(), "services");
    }

    @FXML
    void switchtobookings(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "booking");
    }

    @FXML
    void viewAudit(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "auditlog");
    }

    hotelUserManagement hum = new hotelUserManagement();

    @FXML
    void edit(ActionEvent event) {
        user selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            loaderSettings.applyDimmingEffect(event);
            hum.editUser();
        }
    }

    @FXML
    void add(ActionEvent event) {
        loaderSettings.applyDimmingEffect(event);
        hum.createNewUser();
    }

    @FXML
    void remove(ActionEvent event) {
        user selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            loaderSettings.applyDimmingEffect(event);
            hum.removeUser();
        }
    }

    class hotelUserManagement {

        private void createNewUser() {

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage owner = (Stage) logout.getScene().getWindow();
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
            privilegeBox.getItems().addAll("staff", "admin");
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

            VBox credentialsPane = new VBox(15, userCred, emailBox, phBox, pwFieldBox, privBox);
            credentialsPane.setStyle(
                    "-fx-spacing: 15px; " +
                            "-fx-padding: 30px; " +
                            "-fx-background-color: #f9f9f9; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-color: #eaeaea; " +
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
                    "-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 10;" +
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


            Scene modalScene = new Scene(modalRoot, 475, 565);

            modalScene.setFill(Color.TRANSPARENT);
            modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");
            modalStage.setScene(modalScene);
            modalStage.setResizable(false);
            modalStage.show();

            modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
            modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

            confirmButton.setOnAction(e -> {
                if (enterUID.getText().isEmpty() ||
                        phno_field.getText().isEmpty() ||
                        pw_field.getText().isEmpty()) return;

                addUserToDB(enterUID.getText(),
                        username_field.getText(), pw_field.getText(),
                        privilegeBox.getSelectionModel().getSelectedItem(),
                        email_field.getText(), phno_field.getText(),
                        e, modalStage);

            });
            cancelButton.setOnAction(e -> {
                cancel.fire();
                modalStage.close();
            });
        }

        private void editUser() {

            user selectedUser = userTable.getSelectionModel().getSelectedItem();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage owner = (Stage) logout.getScene().getWindow();
            modalStage.initOwner(owner);
            modalStage.initStyle(StageStyle.UNDECORATED);
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Text pass_txt = new Text("Password");
            pass_txt.setFont(new Font("Arial", 16));
            pass_txt.setFill(Color.DARKSLATEGRAY);

            PasswordField newPw = new PasswordField();
            newPw.setText(selectedUser.getPassword());
            newPw.setDisable(true);
            newPw.setStyle(
                    "-fx-background-color: #f0f0f0; " +
                            "-fx-border-color: #dcdcdc; " +
                            "-fx-border-radius: 5; " +
                            "-fx-padding: 8; " +
                            "-fx-font-size: 14px; "
            );
            VBox currentFieldBox = new VBox(pass_txt, newPw);
            currentFieldBox.setStyle("-fx-spacing: 8;");

            Text username = new Text("Username");
            username.setFont(new Font("Arial", 16));
            username.setFill(Color.DARKSLATEGRAY);

            TextField username_field = new TextField();
            username_field.setText(selectedUser.getUsername());
            username_field.setStyle(
                    "-fx-background-color: #f0f0f0; " +
                            "-fx-border-color: #dcdcdc; " +
                            "-fx-border-radius: 5; " +
                            "-fx-padding: 8; " +
                            "-fx-font-size: 14px; "
            );
            VBox userFieldBox = new VBox(username, username_field);
            userFieldBox.setStyle("-fx-spacing: 8;");

            Text selectPrivilege = new Text("Privilege");
            selectPrivilege.setFont(new Font("Arial", 16));
            selectPrivilege.setFill(Color.DARKSLATEGRAY);

            ComboBox<String> userPriv = new ComboBox<>();
            userPriv.getItems().addAll("staff", "admin");
            userPriv.getSelectionModel().select(selectedUser.getPrivilege());
            userPriv.setStyle(
                    "-fx-background-color: #ffffff; " +
                            "-fx-border-color: #dcdcdc; " +
                            "-fx-border-radius: 5; " +
                            "-fx-padding: 8; " +
                            "-fx-font-size: 14px; "
            );
            VBox privBox = new VBox(selectPrivilege, userPriv);
            privBox.setStyle("-fx-spacing: 8;");

            Text email = new Text("Enter Email");
            email.setFont(new Font("Arial", 16));
            email.setFill(Color.DARKSLATEGRAY);

            TextField email_field = new TextField();
            email_field.setText(selectedUser.getEmail());
            email_field.setStyle(
                    "-fx-background-color: #f0f0f0; " +
                            "-fx-border-color: #dcdcdc; " +
                            "-fx-border-radius: 5; " +
                            "-fx-padding: 8; " +
                            "-fx-font-size: 14px; "
            );
            VBox emailBox = new VBox(email, email_field);
            emailBox.setStyle("-fx-spacing: 8;");

            Text phone_no = new Text("Enter Phone No.");
            phone_no.setFont(new Font("Arial", 16));
            phone_no.setFill(Color.DARKSLATEGRAY);

            TextField phno_field = new TextField();
            phno_field.setText(selectedUser.getPhone_no());
            phno_field.setStyle(
                    "-fx-background-color: #f0f0f0; " +
                            "-fx-border-color: #dcdcdc; " +
                            "-fx-border-radius: 5; " +
                            "-fx-padding: 8; " +
                            "-fx-font-size: 14px; "
            );
            VBox phBox = new VBox(phone_no, phno_field);
            phBox.setStyle("-fx-spacing: 8;");

            HBox credBox = new HBox(15, emailBox, phBox);
            credBox.setStyle("-fx-spacing: 15; -fx-padding: 10px;");

            VBox credentialsPane = new VBox(userFieldBox, credBox, currentFieldBox, privBox);
            credentialsPane.setStyle(
                    "-fx-spacing: 15; " +
                            "-fx-padding: 30; " +
                            "-fx-background-color: #f9f9f9; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-color: #eaeaea; " +
                            "-fx-border-width: 1; "
            );

            BorderPane modalRoot = new BorderPane();

            Text modalTitle = new Text("Edit User");
            modalTitle.setFont(new Font(28.0));

            Text modalHint = new Text("Are you sure you want to edit this user?");
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
                    "-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 10;" +
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
                    "-fx-background-color: #3b9E4F; -fx-text-fill: #333333; -fx-background-radius: 10;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-family: Arial;" +
                            " -fx-padding: 10px 20px;"
            ));
            confirmButton.setOnMouseExited(e -> confirmButton.setStyle(
                    "-fx-background-color: #4CAF50; -fx-text-fill: #333333; -fx-background-radius: 10;" +
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

            Scene modalScene = new Scene(modalRoot, 425, 535);
            modalStage.setScene(modalScene);
            modalScene.setFill(Color.TRANSPARENT);
            modalStage.setResizable(false);
            modalStage.show();
            modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");

            modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
            modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

            cancelButton.setOnAction(e -> {
                cancel.fire();
                modalStage.close();
            });
            confirmButton.setOnAction(e -> {
                editUserFromDB(selectedUser.getUid(), username_field.getText(), newPw.getText(),
                        userPriv.getSelectionModel().getSelectedItem(), email_field.getText(),
                        phno_field.getText(), e, modalStage
                );
            });
        }

        private void removeUser() {

            user selectedUser = userTable.getSelectionModel().getSelectedItem();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage owner = (Stage) logout.getScene().getWindow();
            modalStage.initOwner(owner);
            modalStage.initStyle(StageStyle.UNDECORATED);
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Text uid_txt = new Text("UID");
            uid_txt.setFont(new Font("Arial", 16));
            uid_txt.setFill(Color.DARKSLATEGRAY);
            Text currUID = new Text(selectedUser.getUid());
            currUID.setFont(new Font("Arial", 14));
            currUID.setFill(Color.DIMGRAY);
            VBox currentFieldBox = new VBox(uid_txt, currUID);
            currentFieldBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

            Text username = new Text("Username");
            username.setFont(new Font("Arial", 16));
            username.setFill(Color.DARKSLATEGRAY);
            Text username_field = new Text(selectedUser.getUsername());
            username_field.setFont(new Font("Arial", 14));
            username_field.setFill(Color.DIMGRAY);
            VBox userFieldBox = new VBox(username, username_field);
            userFieldBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

            Text selectPrivilege = new Text("Privilege");
            selectPrivilege.setFont(new Font("Arial", 16));
            selectPrivilege.setFill(Color.DARKSLATEGRAY);
            Text userPriv = new Text(selectedUser.getPrivilege());
            userPriv.setFont(new Font("Arial", 14));
            userPriv.setFill(Color.DIMGRAY);
            VBox privBox = new VBox(selectPrivilege, userPriv);
            privBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

            VBox credentialsPane = new VBox(currentFieldBox, userFieldBox, privBox);
            credentialsPane.setStyle(
                    "-fx-spacing: 15; " +
                            "-fx-padding: 30; " +
                            "-fx-background-color: #f7f7f7; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-color: #dcdcdc; " +
                            "-fx-border-width: 1; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 10, 0, 0, 3);"
            );

            BorderPane modalRoot = new BorderPane();

            Text modalTitle = new Text("Remove User");
            modalTitle.setFont(new Font("Arial", 28));
            modalTitle.setFill(Color.DARKRED);

            Text modalHint = new Text("Are you sure you want to remove this user?");
            modalHint.setFont(new Font("Arial", 14));
            modalHint.setFill(Color.DIMGRAY);

            VBox topBox = new VBox(modalTitle, modalHint);
            topBox.setAlignment(Pos.CENTER);
            topBox.setStyle("-fx-spacing: 5; -fx-padding: 10px;");

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
                    "-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 10;" +
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
                    "-fx-background-color: #4CAF50; -fx-text-fill: #333333; -fx-background-radius: 10;" +
                            " -fx-font-size: 14px;" +
                            " -fx-font-family: Arial;" +
                            "  -fx-padding: 10px 20px;"
            ));

            HBox buttonBox = new HBox(15, cancelButton, confirmButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setStyle("-fx-padding: 15px;");

            modalRoot.setBottom(buttonBox);
            BorderPane.setAlignment(buttonBox, Pos.CENTER);


            Scene modalScene = new Scene(modalRoot, 425, 425);
            modalStage.setScene(modalScene);
            modalScene.setFill(Color.TRANSPARENT);
            modalStage.setResizable(false);
            modalStage.show();
            modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");

            modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
            modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

            cancelButton.setOnAction(e -> {
                cancel.fire();
                modalStage.close();
            });
            confirmButton.setOnAction(e -> {
                removeUserFromDB(selectedUser.getUid(), e, modalStage);
            });
        }

        private void addUserToDB(String userID, String name, String pw, String priv,
                                 String email, String ph, ActionEvent e, Stage st) {

            loaderSettings.applyDimmingEffect(e);

            Task<Boolean> loadSceneTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    try {

                        if (userController.user_exist_email(email)) {
                            notificationManager.showNotification("Email has been used", "fail", (Stage) cancel.getScene().getWindow());
                            throw new Exception("Duplicate Email");
                        }
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
                    notificationManager.showNotification(String.format("Successfully added %s", userID), "success", (Stage) cancel.getScene().getWindow());
                    userService.restart();
                    st.close();
                    loaderSettings.removeDimmingEffect(e);
                    cancel.fire();
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

        private void editUserFromDB(String userID, String name, String pw, String priv,
                                    String email, String ph, ActionEvent e, Stage st) {

            loaderSettings.applyDimmingEffect(e);

            Task<Boolean> loadSceneTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    try {

                        boolean success = authenticationManager.updatePasswordAndUser(userID, pw, priv, name, email, ph);

                        if (!success) {
                            throw new Exception("Failed to edit user from the database. ER[101]");
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
                    notificationManager.showNotification(String.format("Successfully updated %s", userID), "success", (Stage) cancel.getScene().getWindow());
                    userService.restart();
                    st.close();
                    loaderSettings.removeDimmingEffect(e);
                    cancel.fire();
                    loadingStage.hide();
                    System.out.println("User updated successfully.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            loadSceneTask.setOnFailed(er -> {
                try {
                    loadingStage.hide();
                    loaderSettings.removeDimmingEffect(e);
                    notificationManager.showNotification("Failed to update a user", "failure", st);
                    System.out.println("Failed to update user. Please try again.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            new Thread(loadSceneTask).start();
        }

        private void removeUserFromDB(String userID, ActionEvent e, Stage st) {

            loaderSettings.applyDimmingEffect(e);

            Task<Boolean> loadSceneTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    try {
                        boolean success = userController.deleteEmployee(userID);
                        if (!success) {
                            throw new Exception("Failed to delete.");
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
                    if (ts.getUid().matches(userID)) {
                        logoutController.logout((Stage) add.getScene().getWindow());
                        return;
                    }
                    notificationManager.showNotification(String.format("Successfully removed %s", userID), "success", (Stage) cancel.getScene().getWindow());
                    st.close();
                    cancel.fire();
                    userService.restart();
                    loadingStage.hide();
                    System.out.println("User removed successfully.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            loadSceneTask.setOnFailed(er -> {
                try {
                    loadingStage.hide();
                    notificationManager.showNotification("Failed to removed a user", "failure", st);
                    loaderSettings.removeDimmingEffect(e);
                    System.out.println("Failed to remove user. Please try again.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            new Thread(loadSceneTask).start();
        }

    }

    class staffController {

        void switchtoRoom(ActionEvent event) throws IOException {
            ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "staff", "rooms");
        }

        void switchtoBooking(ActionEvent event) throws IOException {
            ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "staff", "booking");

        }

        void switchtoOrders(ActionEvent event) throws IOException {
            ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "staff", "orders");
        }

    }

    class profileContorller {
        private void profileMod() {

            VBox section = new VBox();
            section.setId("sections");
            section.setStyle("-fx-padding: 10px; -fx-border-width: 0px;");

            Text sectionTitle = new Text("Profile Settings");
            Text currentUID = new Text(String.format("UID: %s", ts.getUid()));
            currentUID.setId("labels");
            sectionTitle.setId("labels");
            sectionTitle.setFont(new Font(24.0));

            Button changePw = new Button("Change Password");
            changePw.setOnAction(e -> changePassword());
            section.getChildren().addAll(sectionTitle, currentUID, changePw);
            settingsView.getChildren().add(section);
        }

        private void changePassword() {

            loaderSettings.applyDimmingEffect((Stage) logout.getScene().getWindow());

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);

            Stage owner = (Stage) logout.getScene().getWindow();

            modalStage.initOwner(owner);
            modalStage.initStyle(StageStyle.UNDECORATED);

            // Current Password section
            Text cur_pw_txt = new Text("Current Password");
            cur_pw_txt.setFont(new Font("Arial", 16));
            cur_pw_txt.setFill(Color.DARKSLATEGRAY);

            PasswordField current_pw = new PasswordField();
            current_pw.setStyle(
                    "-fx-background-color: #f0f0f0; " +
                            "-fx-border-color: #dcdcdc; " +
                            "-fx-border-radius: 5; " +
                            "-fx-padding: 8; " +
                            "-fx-font-size: 14px; "
            );
            VBox currentFieldBox = new VBox(8, cur_pw_txt, current_pw);  // 8px spacing

// New Password section
            Text new_pw_txt = new Text("New Password");
            new_pw_txt.setFont(new Font("Arial", 16));
            new_pw_txt.setFill(Color.DARKSLATEGRAY);

            PasswordField new_pw = new PasswordField();
            new_pw.setStyle(
                    "-fx-background-color: #f0f0f0; " +
                            "-fx-border-color: #dcdcdc; " +
                            "-fx-border-radius: 5; " +
                            "-fx-padding: 8; " +
                            "-fx-font-size: 14px; "
            );
            VBox newFieldBox = new VBox(8, new_pw_txt, new_pw);

            Text confirm_new_pw_txt = new Text("Confirm New Password");
            confirm_new_pw_txt.setFont(new Font("Arial", 16));
            confirm_new_pw_txt.setFill(Color.DARKSLATEGRAY);

            PasswordField confirm_new_pw = new PasswordField();
            confirm_new_pw.setStyle(
                    "-fx-background-color: #f0f0f0; " +
                            "-fx-border-color: #dcdcdc; " +
                            "-fx-border-radius: 5; " +
                            "-fx-padding: 8; " +
                            "-fx-font-size: 14px; "
            );
            VBox confirmFieldBox = new VBox(8, confirm_new_pw_txt, confirm_new_pw);

            VBox credentialsPane = new VBox(15, currentFieldBox, newFieldBox, confirmFieldBox);  // 15px spacing
            credentialsPane.setStyle(
                    "-fx-padding: 30px; " +
                            "-fx-background-color: #f9f9f9; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-color: #eaeaea; " +
                            "-fx-border-width: 1; "
            );

            Text modalTitle = new Text("Update Your Password");
            modalTitle.setFont(new Font("Arial", 28));
            modalTitle.setFill(Color.DARKSLATEGRAY);

            Text modalHint = new Text("Enter your current and new password");
            modalHint.setFont(new Font("Arial", 14));
            modalHint.setFill(Color.GRAY);

            VBox topBox = new VBox(10, modalTitle, modalHint);  // 10px spacing
            topBox.setAlignment(Pos.CENTER);
            topBox.setStyle("-fx-padding: 10px;");

            BorderPane modalRoot = new BorderPane();
            modalRoot.setTop(topBox);
            BorderPane.setAlignment(topBox, Pos.CENTER);
            modalRoot.setCenter(credentialsPane);

            Button confirmButton = new Button("Confirm");
            confirmButton.setStyle(
                    "-fx-background-color: #4CAF50; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 10px 20px; " +
                            "-fx-border-radius: 5; " +
                            "-fx-font-size: 14px;"
            );

            Button cancelButton = new Button("Cancel");
            cancelButton.setStyle(
                    "-fx-background-color: #f44336; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 10px 20px; " +
                            "-fx-border-radius: 5; " +
                            "-fx-font-size: 14px;"
            );

            HBox buttonBox = new HBox(15, cancelButton, confirmButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setStyle("-fx-padding: 15px;");

            modalRoot.setBottom(buttonBox);
            BorderPane.setAlignment(buttonBox, Pos.CENTER);


            cancelButton.setOnAction(e -> {
                loaderSettings.removeDimmingEffect((Stage) logout.getScene().getWindow());
                modalStage.close();
            });

            confirmButton.setOnAction(e -> {
                if (current_pw.getText().isEmpty() && !new_pw.getText().matches(confirm_new_pw.getText()))
                    return;
                if (current_pw.getText().matches(new_pw.getText()))
                    return;

                update(ts.getUid(), current_pw.getText(), new_pw.getText(), e, modalStage);


            });

            Scene modalScene = new Scene(modalRoot, 425, 435);
            modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");

            modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
            modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));
            modalStage.setScene(modalScene);
            modalStage.setResizable(false);
            modalStage.show();

        }

        private void update(String id, String current, String newpw, ActionEvent e, Stage st) {

            loaderSettings.applyDimmingEffect(e);

            Task<Boolean> loadSceneTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    try {

                        boolean success = authenticationManager.Authenticate(id, current);

                        if (!success) {
                            throw new Exception("Incorrect Password");
                        }

                        authenticationManager.updatePassword(id, newpw);

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

                    loaderSettings.removeDimmingEffect((Stage) logout.getScene().getWindow());
                    st.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            loadSceneTask.setOnFailed(er -> {
                try {
                    loadingStage.hide();
                    loaderSettings.removeDimmingEffect(e);
                    System.out.println("Failed to update");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            new Thread(loadSceneTask).start();
        }

    }

    ToggleGroup themeSelector = new ToggleGroup();

    @FXML
    void cancelAction(ActionEvent event) {
        loaderSettings.removeDimmingEffect(event);
    }

    @FXML
    void saveSettings(ActionEvent event) {
        saveWindowsSettings();
        Parent root = ((Node) event.getSource()).getScene().getRoot();
        ts.applyTheme(root, "settings");
    }

    private void loadWindowsSettings() {

        ts.loadWindowsSettings();

        this.theme = ts.getTheme();
        this.uid = ts.getUid();
    }

    private void saveWindowsSettings() {

        ts.setTheme(dark.isSelected() ? "dark" : "light");

    }

}
