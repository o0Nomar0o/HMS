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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Audit Log Controller class
 *
 * @author Nomar
 */

public class adminAuditLogController {

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

    boolean flag = false;
    Timeline timeline;
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
    @FXML
    private Button guests;
    @FXML
    private Button logout;
    @FXML
    private Button services;
    @FXML
    private Button setting;
    private final switchSceneController ssc = new switchSceneController();
    private int count = 0;
    private final Map<Integer, String> nodeMap = new HashMap<>();
    private final List<VBox> logsRootList = new ArrayList<>();

    @FXML
    void initialize() {

        logs.setOnSucceeded(workerStateEvent -> {

            List<audit_logs> newData = logs.getValue();

            Platform.runLater(() -> {
                audit_view.getChildren().clear();

                for (audit_logs al : newData) {
                    VBox logsRoot = new VBox();
                    GridPane logsGrid = new GridPane();

                    Label userLabel = new Label(al.getUser());
                    Button actionButton = new Button();
                    int key = al.getId();

                    if (nodeMap.containsKey(key))
                        actionButton.setUserData(nodeMap.get(key));
                    else
                        actionButton.setUserData("0");


                    actionButton.setText(al.getAction() + " >");
                    Label dataLabel = new Label(al.getData());
                    Label timestampLabel = new Label(al.getTimestamp().toString());

                    HBox dataView = new HBox(dataLabel);

                    actionButton.setOnAction(e -> {
                        handleNodeToggle(al.getId(), actionButton, logsRoot, dataView);
                    });

                    logsGrid.add(userLabel, 0, 0, 1, 2);
                    logsGrid.add(actionButton, 1, 0);
                    logsGrid.add(timestampLabel, 1, 1);

                    GridPane.setValignment(userLabel, VPos.CENTER);
                    logsGrid.setStyle("-fx-padding:15px;" +
                            "-fx-vgap:10px;" + "-fx-hgap:10px;" + "-fx-background-radius: 1.25em;");

                    dataView.setStyle("-fx-background-color: #EDEDED;" +
                            "-fx-background-radius: 0.5em 0.5em 1.25em 1.25em; " +
                            "-fx-padding: 15px;");

                    logsRoot.setStyle("-fx-background-color: #EAEAEA; -fx-background-radius: 1.25em;");
                    VBox.setVgrow(logsRoot, Priority.ALWAYS);

                    logsRoot.getChildren().add(logsGrid);
                    logsRootList.add(logsRoot);

                    restoreExpandedNodes(logsRoot, actionButton, dataView);

                    audit_view.getChildren().add(logsRoot);

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
                    actionFilter.show(logout.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY());
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
                    userFilter.show(logout.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY());
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
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "dashboard");
    }

    @FXML
    void switchtobookings(ActionEvent event) throws Exception {
        flag = true;
        logs.reset();
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "booking");
    }

    @FXML
    void switchtorooms(ActionEvent event) throws Exception {
        flag = true;
        logs.reset();
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "rooms");
    }

    @FXML
    void switchtoservices(ActionEvent event) throws Exception {
        flag = true;
        logs.reset();
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "services");
    }

    @FXML
    void switchtosettings(ActionEvent event) throws Exception {
        flag = true;
        timeline.stop();
        logs.reset();
        ssc.toSettings(event, (Stage) logout.getScene().getWindow());
    }

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

    private void handleNodeToggle(int id, Button actionButton, VBox logsRoot, HBox dataView) {
        String currentState = actionButton.getUserData().toString();

        switch (actionButton.getUserData().toString()) {
            case "0":
                logsRoot.getChildren().add(dataView);
                actionButton.setUserData("1");
                audit_view.layout();
                nodeMap.put(id, "1");
                break;
            case "1":

                logsRoot.getChildren().remove(dataView);

                actionButton.setUserData("0");
                nodeMap.remove(id);

            default:
                break;
        }
    }

    private void restoreExpandedNodes(VBox logsRoot, Button actionButton, HBox dataView) {

        String currentState = actionButton.getUserData().toString();

        if ("1".equals(currentState)) {
            if (!logsRoot.getChildren().contains(dataView)) {
                logsRoot.getChildren().add(dataView);
            }
        }
    }


}
