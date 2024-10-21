package project.hotelsystem.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import project.hotelsystem.database.controller.auditController;
import project.hotelsystem.database.controller.userController;
import project.hotelsystem.database.models.audit_logs;
import project.hotelsystem.database.models.user;
import project.hotelsystem.util.dropdownManager;

import java.util.ArrayList;
import java.util.List;

public class adminAuditLogController {

    @FXML
    private VBox audit_view;

    @FXML
    private Button bookings;

    @FXML
    private Button dashboard;

    @FXML
    private Button dropdownAction;

    @FXML
    private Button dropdownUser;

    @FXML
    private Button guests;

    @FXML
    private Button logout;

    @FXML
    private Button services;

    @FXML
    private Button setting;

    private switchSceneController ssc = new switchSceneController();

    private int count = 0;
    boolean flag = false;

    @FXML
    void initialize() {

        logs.setOnSucceeded(workerStateEvent -> {
            List<audit_logs> newData = logs.getValue();

            Platform.runLater(() -> {
                audit_view.getChildren().clear();

                for (audit_logs al : newData) {
                    GridPane logsGrid = new GridPane();

                    Label userLabel = new Label(al.getUser());
                    Label actionLabel = new Label(al.getAction());
                    Label timestampLabel = new Label(al.getTimestamp().toString());

                    logsGrid.add(userLabel, 0, 0, 1, 2);
                    logsGrid.add(actionLabel, 1, 0);
                    logsGrid.add(timestampLabel, 1, 1);

                    GridPane.setValignment(userLabel, VPos.CENTER);
                    logsGrid.setStyle("-fx-background-color: #DDDDDD;" + "-fx-padding:15px;" +
                            "-fx-vgap:10px;" + "-fx-hgap:10px;" + "-fx-background-radius: 1.25em;");

                    audit_view.getChildren().add(logsGrid);
                }
            });

            if (count <= 0 && !flag) {
                startLogRefreshTimeline();
                count++;
            }

            Popup actionFilter = dropdownManager.createActionDropdown(dropdownAction, (Stage) logout.getScene().getWindow());
            dropdownAction.setOnAction(e -> {
                if (!actionFilter.isShowing()) {
                    Bounds bounds = dropdownAction.localToScreen(dropdownAction.getBoundsInLocal());
                    actionFilter.show((Stage) logout.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY());
                }

            });
        });

        logs.setOnFailed(workerStateEvent -> {
            Throwable exception = logs.getException();
            exception.printStackTrace();
            startLogRefreshTimeline();
            count--;
        });

        logs.start();


        userlist.setOnSucceeded(e -> {
            ObservableList<user> users = FXCollections.observableArrayList(userlist.getValue());
            Popup userFilter = dropdownManager.createUserDropdown(dropdownUser,
                    (Stage) logout.getScene().getWindow(), users, logs);

            dropdownUser.setOnAction(event -> {
                if (!userFilter.isShowing()) {
                    Bounds bounds = dropdownUser.localToScreen(dropdownUser.getBoundsInLocal());
                    userFilter.show((Stage) logout.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY());
                }
            });
        });

        userlist.setOnFailed(e -> {
            Throwable exception = userlist.getException();
            exception.printStackTrace();

        });

        userlist.start();

        logout.setOnAction(e -> {
            logoutController.logout(e);
            flag = true;
            logs.reset();
        });

    }

    @FXML
    void switchToDashboard(ActionEvent event) throws Exception {
        flag = true;
        logs.reset();

    }

    @FXML
    void switchtobookings(ActionEvent event) throws Exception {
        flag = true;
        logs.reset();
    }

    @FXML
    void switchtorooms(ActionEvent event) throws Exception {
        flag = true;
        logs.reset();
    }

    @FXML
    void switchtoservices(ActionEvent event) throws Exception {
        flag = true;
        logs.reset();
    }

    @FXML
    void switchtosettings(ActionEvent event) throws Exception {
        flag = true;
        timeline.stop();
        logs.reset();
        ssc.toSettings(event, (Stage) logout.getScene().getWindow());
    }

    private final Service<List<audit_logs>> logs = new Service<>() {
        @Override
        protected Task<List<audit_logs>> createTask() {
            return new Task<>() {
                @Override
                protected List<audit_logs> call() throws Exception {
                    user u = (user) dropdownUser.getUserData();
                    if (u == null || u.getUsername().matches("All Users"))
                        return auditController.getAudits();
                    return auditController.getAuditsByID(u.getUid());
                }
            };
        }
    };

    private final Service<List<user>> userlist = new Service<>() {
        @Override
        protected Task<List<user>> createTask() {
            return new Task<>() {
                @Override
                protected List<user> call() throws Exception {
                    List<user> results = new ArrayList<>();
                    results.add(new user("", "All Users", ""));
                    results.addAll(userController.getAllUsers());
                    return results;
                }
            };
        }
    };

    Timeline timeline;

    private void startLogRefreshTimeline() {
        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(5),
                        event -> {
                            if (!logs.isRunning()) {
                                System.out.println("Refreshing...");
                                logs.restart();
                                count--;
                            }
                        }
                )
        );

        timeline.play();

    }

}
