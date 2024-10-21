package project.hotelsystem.util;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import project.hotelsystem.database.models.audit_logs;
import project.hotelsystem.database.models.user;
import project.hotelsystem.database.models.room;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class dropdownManager {

    public static Popup createUserDropdown(Button dropdownButton, Stage owner, ObservableList<user> users, Service<List<audit_logs>> logs) {

        Popup popup = new Popup();
        popup.setAutoHide(true);

        VBox container = new VBox(5);
        container.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-padding: 5;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setPrefWidth(200);

        ListView<user> usersListView = new ListView<>(users);
        usersListView.setPrefHeight(120);
        usersListView.setPrefWidth(200);

        usersListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(user item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String filler = item.getUid().isEmpty()? "":" - ";
                    setText(item.getUid() + filler + item.getUsername());
                }
            }
        });

        AtomicBoolean isTyping = new AtomicBoolean(false);
        searchField.setOnKeyReleased(event -> {
            isTyping.set(true);

            PauseTransition pause = new PauseTransition(Duration.millis(200));
            pause.setOnFinished(e -> {
                String filter = searchField.getText().toLowerCase();

                usersListView.getSelectionModel().clearSelection();

                ObservableList<user> filteredUsers = users.stream()
                        .filter(user ->
                                user.getUid().toLowerCase().contains(filter) ||
                                        user.getUsername().toLowerCase().contains(filter))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                usersListView.setItems(filteredUsers);
                isTyping.set(false);
            });
            pause.play();
        });

        usersListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<user>() {
            @Override
            public void changed(ObservableValue<? extends user> observableValue, user oldUser, user newUser) {
                if (newUser != null && !isTyping.get()) {
                    String selected = newUser.getUid().isEmpty() ?
                            newUser.getUsername() :
                            newUser.getUid() + " - " + newUser.getUsername();

                    dropdownButton.setText(selected + "  ▼");
                    dropdownButton.setUserData(newUser);
                    popup.hide();

                    logs.restart();

                    usersListView.setItems(users);
                    searchField.clear();
                }
            }
        });

        container.getChildren().addAll(searchField, usersListView);
        popup.getContent().add(container);

        return popup;
    }

    public static Popup createActionDropdown(Button dropdownButton, Stage owner) {

        Popup popup = new Popup();
        popup.setAutoHide(true);

        VBox container = new VBox(5);
        container.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-padding: 5;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setPrefWidth(200);

        ObservableList<String> actions = FXCollections.observableArrayList();
        actions.addAll("All","Create Booking","Update Booking",
                "Create Food Order", "Update Food Order",
                "Create Service Order","Update Service order",
                "Create Room","Update Room",
                "Create User","Update Password");
        ListView<String> actionListView = new ListView<>(actions);
        actionListView.setPrefHeight(120);
        actionListView.setPrefWidth(200);

        AtomicBoolean isTyping = new AtomicBoolean(false);
        searchField.setOnKeyReleased(event -> {
            isTyping.set(true);

            PauseTransition pause = new PauseTransition(Duration.millis(200));
            pause.setOnFinished(e -> {
                String filter = searchField.getText().toLowerCase();

                actionListView.getSelectionModel().clearSelection();

                ObservableList<String> filteredActions = actions.stream()
                        .filter(action ->
                                action.toLowerCase().contains(filter))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                actionListView.setItems(filteredActions);
                isTyping.set(false);
            });
            pause.play();
        });

        actionListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldAct, String newAction) {
                if (newAction != null && !isTyping.get()) {

                    dropdownButton.setText(newAction + "  ▼");
                    popup.hide();

                    actionListView.setItems(actions);
                    searchField.clear();
                }
            }
        });

        container.getChildren().addAll(searchField, actionListView);
        popup.getContent().add(container);

        return popup;
    }

    public static Popup createRoomDropdown(Button dropdownButton, ObservableList<room> rooms) {

        Popup popup = new Popup();
        popup.setAutoHide(true);

        VBox container = new VBox(5);
        container.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-padding: 5;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search rooms...");
        searchField.setPrefWidth(200);

        ListView<room> usersListView = new ListView<>(rooms);
        usersListView.setPrefHeight(120);
        usersListView.setPrefWidth(200);

        usersListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(room item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String filler = item.getRoom_no().isEmpty()? "":" - ";
                    setText(item.getRoom_no() + filler+item.getRoom_status());
                }
            }
        });

        AtomicBoolean isTyping = new AtomicBoolean(false);
        searchField.setOnKeyReleased(event -> {
            isTyping.set(true);

            PauseTransition pause = new PauseTransition(Duration.millis(200));
            pause.setOnFinished(e -> {
                String filter = searchField.getText().toLowerCase();

                usersListView.getSelectionModel().clearSelection();

                ObservableList<room> filteredUsers = rooms.stream()
                        .filter(user ->
                                user.getRoom_no().toLowerCase().contains(filter))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                usersListView.setItems(filteredUsers);
                isTyping.set(false);
            });
            pause.play();
        });

        usersListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<room>() {
            @Override
            public void changed(ObservableValue<? extends room> observableValue, room oldUser, room newUser) {
                if (newUser != null && !isTyping.get()) {
                    String selected = newUser.getRoom_no().isEmpty() ?
                            newUser.getRoom_no() :
                            newUser.getRoom_no() + " - " + newUser.getRoom_status();

                    dropdownButton.setText(selected + "  ▼");
                    dropdownButton.setUserData(newUser);
                    popup.hide();


                    usersListView.setItems(rooms);
                    searchField.clear();
                }
            }
        });

        container.getChildren().addAll(searchField, usersListView);
        popup.getContent().add(container);

        return popup;
    }


}
