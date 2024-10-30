package project.hotelsystem.util;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import project.hotelsystem.database.models.audit_logs;
import project.hotelsystem.database.models.room;
import project.hotelsystem.database.models.room_type_details;
import project.hotelsystem.database.models.user;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author Nomar
 */

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
                    String filler = item.getUid().isEmpty() ? "" : " - ";
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
        actions.addAll("All", "Create Booking", "Update Booking",
                "Create Food Order", "Update Food Order",
                "Create Service Order", "Update Service order",
                "Create Room", "Update Room",
                "Create User", "Update Password");
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

        VBox container = new VBox(10);
        container.setStyle("""
                -fx-background-color: #f4f4f9; 
                -fx-border-color: #dcdde1; 
                -fx-border-radius: 10; 
                -fx-background-radius: 10;
                -fx-padding: 10;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 8, 0.5, 0, 0);
                """);

        TextField searchField = new TextField();
        searchField.setPromptText("Search rooms...");
        searchField.setPrefWidth(250);
        searchField.setStyle("""
                -fx-background-color: #ffffff; 
                -fx-border-color: #ced6e0;
                -fx-border-radius: 8; 
                -fx-padding: 8;
                -fx-font-size: 14px;
                """);

        ListView<room> roomListView = new ListView<>(rooms);
        roomListView.setPrefHeight(200);
        roomListView.setPrefWidth(250);

        try {
            URI uri = new File("src/main/resources/css/dropdown.css").toURI();
            roomListView.getStylesheets().add(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        roomListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(room item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getRoom_no());
                    setStyle("""
                            -fx-padding: 10; 
                            -fx-background-color: transparent;
                            -fx-font-size: 14px;
                            -fx-text-fill: #2f3640;
                            """);
                    this.setOnMouseEntered(e -> setStyle("-fx-background-color: #dcdde1;"));
                    this.setOnMouseExited(e -> setStyle("-fx-background-color: transparent;"));
                }
            }
        });

        AtomicBoolean isTyping = new AtomicBoolean(false);
        searchField.setOnKeyReleased(event -> {
            isTyping.set(true);

            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(e -> {
                String filter = searchField.getText().toLowerCase();

                roomListView.getSelectionModel().clearSelection();

                ObservableList<room> filteredRooms = rooms.stream()
                        .filter(room -> room.getRoom_no().toLowerCase().contains(filter))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                roomListView.setItems(filteredRooms);
                isTyping.set(false);
            });
            pause.play();
        });

        roomListView.getSelectionModel().selectedItemProperty().addListener((obs, oldRoom, newRoom) -> {
            if (newRoom != null && !isTyping.get()) {
                String selectedText = newRoom.getRoom_no();
                dropdownButton.setText(selectedText + "  ▼");
                dropdownButton.setUserData(newRoom);
                popup.hide();

                roomListView.setItems(rooms);
                searchField.clear();
            }
        });

        container.getChildren().addAll(searchField, roomListView);
        popup.getContent().add(container);

        return popup;
    }

    public static Popup createRoomTypeDropdown(Button dropdownButton, ObservableList<room_type_details> rooms) {

        Popup popup = new Popup();
        popup.setAutoHide(true);

        rooms.addFirst(new room_type_details("All"));

        VBox container = new VBox(10);
        container.setStyle("""
                -fx-background-color: #f4f4f9; 
                -fx-border-color: #dcdde1; 
                -fx-border-radius: 10; 
                -fx-background-radius: 10;
                -fx-padding: 10;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 8, 0.5, 0, 0);
                """);

        TextField searchField = new TextField();
        searchField.setPromptText("Search rooms...");
        searchField.setPrefWidth(250);
        searchField.setStyle("""
                -fx-background-color: #ffffff; 
                -fx-border-color: #ced6e0;
                -fx-border-radius: 8; 
                -fx-padding: 8;
                -fx-font-size: 14px;
                """);

        ListView<room_type_details> roomListView = new ListView<>(rooms);
        roomListView.setPrefHeight(200);
        roomListView.setPrefWidth(250);

        try {
            URI uri = new File("src/main/resources/css/dropdown.css").toURI();
            roomListView.getStylesheets().add(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        roomListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(room_type_details item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescription());
                    setStyle("""
                            -fx-padding: 10; 
                            -fx-background-color: transparent;
                            -fx-font-size: 14px;
                            -fx-text-fill: #2f3640;
                            """);
                    this.setOnMouseEntered(e -> setStyle("""
                            -fx-background-color: #dcdde1;
                            -fx-padding: 10; 
                            
                            -fx-font-size: 14px;
                            -fx-text-fill: #2f3640;
                            """));
                    this.setOnMouseExited(e -> setStyle("""
                            -fx-background-color: transparent;
                            -fx-padding: 10; 
                            -fx-font-size: 14px;
                            -fx-text-fill: #2f3640;
                            """));
                }
            }
        });

        AtomicBoolean isTyping = new AtomicBoolean(false);
        searchField.setOnKeyReleased(event -> {
            isTyping.set(true);

            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(e -> {
                String filter = searchField.getText().toLowerCase();

                roomListView.getSelectionModel().clearSelection();

                ObservableList<room_type_details> filteredRooms = rooms.stream()
                        .filter(room -> room.getDescription().toLowerCase().contains(filter))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                roomListView.setItems(filteredRooms);
                isTyping.set(false);
            });
            pause.play();
        });

        roomListView.getSelectionModel().selectedItemProperty().addListener((obs, oldRoom, newRoom) -> {
            if (newRoom != null && !isTyping.get()) {
                String selectedText = newRoom.getDescription();
                if (newRoom.getDescription().matches("All"))
                    dropdownButton.setText(selectedText + "Rooms  ▼");
                else
                    dropdownButton.setText(selectedText);

                Text text = new Text(dropdownButton.getText());
                double width = text.getLayoutBounds().getWidth() + 20;
                dropdownButton.setPrefWidth(width);
                dropdownButton.setMinWidth(width);

                dropdownButton.setUserData(newRoom);
                popup.hide();

                roomListView.setItems(rooms);
                searchField.clear();
            }
        });

        container.getChildren().addAll(roomListView, searchField);

        popup.getContent().add(container);

        return popup;
    }

    public static Popup createFloorDropdown(Button dropdownButton, ObservableList<room> rooms) {

        Popup popup = new Popup();
        popup.setAutoHide(true);

        rooms.addFirst(new room(0));

        VBox container = new VBox(10);
        container.setStyle("""
                -fx-background-color: #f4f4f9; 
                -fx-border-color: #dcdde1; 
                -fx-border-radius: 10; 
                -fx-background-radius: 10;
                -fx-padding: 10;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 8, 0.5, 0, 0);
                """);

        TextField searchField = new TextField();
        searchField.setPromptText("Search rooms...");
        searchField.setPrefWidth(250);
        searchField.setStyle("""
                -fx-background-color: #ffffff; 
                -fx-border-color: #ced6e0;
                -fx-border-radius: 8; 
                -fx-padding: 8;
                -fx-font-size: 14px;
                """);

        ListView<room> roomListView = new ListView<>(rooms);
        roomListView.setPrefHeight(200);
        roomListView.setPrefWidth(250);

        try {
            URI uri = new File("src/main/resources/css/dropdown.css").toURI();
            roomListView.getStylesheets().add(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        roomListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(room item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if(item.getFloor() == 0) setText("All Floors");
                    else setText("Floor "+item.getFloor());

                    setStyle("""
                            -fx-padding: 10; 
                            -fx-background-color: transparent;
                            -fx-font-size: 14px;
                            -fx-text-fill: #2f3640;
                            """);
                    this.setOnMouseEntered(e -> setStyle("""
                            -fx-background-color: #dcdde1;
                            -fx-padding: 10; 
                            
                            -fx-font-size: 14px;
                            -fx-text-fill: #2f3640;
                            """));
                    this.setOnMouseExited(e -> setStyle("""
                            -fx-background-color: transparent;
                            -fx-padding: 10; 
                            -fx-font-size: 14px;
                            -fx-text-fill: #2f3640;
                            """));
                }
            }
        });

        AtomicBoolean isTyping = new AtomicBoolean(false);

        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        });
        searchField.setTextFormatter(formatter);

        searchField.setOnKeyReleased(event -> {
            isTyping.set(true);

            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(e -> {

                if (!event.getCharacter().matches("[0-9]")) {
                    event.consume();
                }

                int filter = Integer.parseInt(searchField.getText());

                roomListView.getSelectionModel().clearSelection();

                ObservableList<room> filteredRooms = rooms.stream()
                        .filter(room -> room.getFloor() == filter)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                roomListView.setItems(filteredRooms);
                isTyping.set(false);
            });
            pause.play();
        });

        roomListView.getSelectionModel().selectedItemProperty().addListener((obs, oldRoom, newRoom) -> {
            if (newRoom != null && !isTyping.get()) {
                int selectedText = newRoom.getFloor();
                if (newRoom.getFloor() == 0)
                    dropdownButton.setText("All Floors  ▼");
                else
                    dropdownButton.setText("Floor "+selectedText);

                Text text = new Text(dropdownButton.getText());
                double width = text.getLayoutBounds().getWidth() + 20;
                dropdownButton.setPrefWidth(width);
                dropdownButton.setMinWidth(width);

                dropdownButton.setUserData(newRoom);
                popup.hide();

                roomListView.setItems(rooms);
                searchField.clear();
            }
        });

        container.getChildren().addAll(roomListView, searchField);

        popup.getContent().add(container);

        return popup;
    }

}
