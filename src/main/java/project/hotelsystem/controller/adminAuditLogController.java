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
import javafx.scene.control.ScrollPane;
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
    @FXML
    private ScrollPane audit_scroll_pane;

    private final switchSceneController ssc = new switchSceneController();
    private int count = 0;
    private final Map<Integer, String> nodeMap = new HashMap<>();
    private final List<VBox> logsRootList = new ArrayList<>();
    boolean flag = false;
    Timeline timeline;

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

                    actionButton.setText(al.getAction() + "▶");
                    Label dataLabel = new Label(al.getData());
                    Label timestampLabel = new Label(al.getTimestamp().toString());

                    HBox dataView = new HBox(dataLabel);

                    actionButton.setOnAction(e -> {
                        handleNodeToggle(al.getId(), actionButton, logsRoot, dataView);
                        if (!logsRoot.getChildren().contains(dataView)) {
                            actionButton.setText(al.getAction() + " ▶");
                            return;
                        }
                        actionButton.setText(al.getAction()+" ▼");
                    });

                    logsGrid.add(userLabel, 0, 0, 1, 2);
                    logsGrid.add(actionButton, 1, 0);
                    logsGrid.add(timestampLabel, 1, 1);

                    GridPane.setValignment(userLabel, VPos.CENTER);
                    logsGrid.setStyle(
                            "-fx-padding: 15px;" +
                                    "-fx-vgap: 8px;" +
                                    "-fx-hgap: 20px;" +
                                    "-fx-background-color: #FFFFFF;" +
                                    "-fx-background-radius: 10px;" +
                                    "-fx-border-color: #D9D9D9;" +
                                    "-fx-border-width: 1px;" +
                                    "-fx-border-radius: 10px;"
                    );

                    dataView.setStyle(
                            "-fx-background-color: #F7F7F7;" +
                                    "-fx-background-radius: 8px;" +
                                    "-fx-padding: 15px;" +
                                    "-fx-margin: 5px 0 0 0;"
                    );

                    logsRoot.setStyle(
                            "-fx-background-color: #F2F2F2;" +
                                    "-fx-background-radius: 12px;" +
                                    "-fx-padding: 20px;"
                    );
                    VBox.setVgrow(logsRoot, Priority.ALWAYS);

                    logsRoot.getChildren().add(logsGrid);
                    logsRootList.add(logsRoot);

                    actionButton.setStyle(
                            "-fx-background-color: #007BFF;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-background-radius: 8px;" +
                                    "-fx-padding: 5px 15px;" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-cursor: hand;" +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                    );

                    actionButton.setOnMouseEntered(e -> actionButton.setStyle(
                            "-fx-background-color: #0056b3;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-background-radius: 8px;" +
                                    "-fx-padding: 5px 15px;" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-cursor: hand;" +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 3);"
                    ));

                    actionButton.setOnMouseExited(e -> actionButton.setStyle(
                            "-fx-background-color: #007BFF;" +  // Original color
                                    "-fx-text-fill: white;" +
                                    "-fx-background-radius: 8px;" +
                                    "-fx-padding: 5px 15px;" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-cursor: hand;" +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                    ));


                    userLabel.setStyle(
                            "-fx-font-size: 16px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-text-fill: #333333;"
                    );

                    dataLabel.setStyle(
                            "-fx-font-size: 14px;" +
                                    "-fx-text-fill: #666666;"
                    );

                    timestampLabel.setStyle(
                            "-fx-font-size: 12px;" +
                                    "-fx-text-fill: #999999;"
                    );

                    restoreExpandedNodes(logsRoot, actionButton, dataView, al);
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
        dropdownAction.setVisible(false);

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

    private void restoreExpandedNodes(VBox logsRoot, Button actionButton, HBox dataView, audit_logs al) {

        String currentState = actionButton.getUserData().toString();

        if ("1".equals(currentState)) {
            if (!logsRoot.getChildren().contains(dataView)) {
                logsRoot.getChildren().add(dataView);
                actionButton.setText(al.getAction()+"▼");
                return;
            }
        }
        actionButton.setText(al.getAction() + "▶");
    }


}
