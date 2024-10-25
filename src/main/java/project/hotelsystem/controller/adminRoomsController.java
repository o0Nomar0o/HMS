package project.hotelsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.hotelsystem.database.controller.roomController;
import project.hotelsystem.database.controller.roomTypeController;
import project.hotelsystem.database.models.room;
import project.hotelsystem.database.models.room_type_details;
import project.hotelsystem.settings.loaderSettings;
import project.hotelsystem.util.notificationManager;

import java.io.IOException;
import java.util.List;

public class adminRoomsController {

    private final Service<List<room_type_details>> roomTypeService = new Service<>() {
        @Override
        protected Task<List<room_type_details>> createTask() {
            return new Task<>() {
                @Override
                protected List<room_type_details> call() throws Exception {
                    return roomTypeController.getAllRoomType();
                }
            };
        }
    };
    roomBuilder rb = new roomBuilder();
    roomTypeBuilder rtb = new roomTypeBuilder();
    switchSceneController ssc = new switchSceneController();
    @FXML
    private Button addRoom;
    @FXML
    private Button addType;
    @FXML
    private Button bookings;
    @FXML
    private Button dashboard;
    @FXML
    private Button editType;
    @FXML
    private Button logout;
    @FXML
    private Button rooms;
    @FXML
    private TilePane roomsView;
    @FXML
    private Button services;
    @FXML
    private Button setting;
    @FXML
    private TableView<room_type_details> typeTable;
    @FXML
    private TableColumn<?, ?> typecol;
    @FXML
    private TableColumn<?, ?> pricecol;
    @FXML
    private TableColumn<?, ?> nightcol;
    @FXML
    private TableColumn<?, ?> hourcol;
    @FXML
    private TableColumn<?, ?> idcol;
    private final ObservableList<room_type_details> roomTypeList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        logout.setOnAction(e -> logout(e));

        addRoom.setOnAction(e -> rb.createRoom());
        addType.setOnAction(e -> rtb.createRoomType());
        rb.generateRooms();

        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        typecol.setCellValueFactory(new PropertyValueFactory<>("description"));
        nightcol.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        hourcol.setCellValueFactory(new PropertyValueFactory<>("pricePerHour"));

        roomTypeService.valueProperty().addListener((obs, oldData, newData) -> {
            typeTable.getItems().clear();
            typeTable.setPlaceholder(new Label("Loading user data..."));
            if (newData != null && !newData.isEmpty()) {
                typeTable.getItems().addAll(newData);
                return;
            }
            typeTable.setPlaceholder(new Label("No users found."));
        });

        roomTypeService.start();
        editType.setDisable(true);
        SelectionModel<room_type_details> smu = typeTable.getSelectionModel();
        smu.selectedItemProperty().addListener((ob, ov, nv) -> {
            editType.setDisable(nv == null);
        });
        editType.setOnAction(e -> {
            rtb.editRoomType();
        });

    }

    @FXML
    void logout(ActionEvent event) {
        logoutController.logout(event);
    }

    @FXML
    void switchtosetting(ActionEvent event) throws IOException {
        ssc.toSettings(event, (Stage) logout.getScene().getWindow());
    }

    @FXML
    void switchtodashboard(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "dashboard");

    }

    @FXML
    void switchtobookings(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "booking");
    }

    @FXML
    void switchtoservices(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "services");
    }

    class roomBuilder {
        private void createRoom() {

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage owner = (Stage) rooms.getScene().getWindow();
            modalStage.initOwner(owner);
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Text room_no = new Text("Enter Room No.");
            room_no.setFont(new Font(16.0));
            TextField roomNo = new TextField();
            VBox roomBox = new VBox(room_no, roomNo);

            Text roomFloor = new Text("Choose Floor");
            roomFloor.setFont(new Font(16.0));
            TextField floor_field = new TextField();
            VBox floorBox = new VBox(roomFloor, floor_field);

            Text selectRoomType = new Text("Select Room Type");
            selectRoomType.setFont(new Font(16.0));
            ComboBox<String> rtype = new ComboBox<>();
            rtype.getItems().addAll(roomTypeController.getRoomTypeID());
            rtype.getSelectionModel().selectFirst();
            VBox rtBox = new VBox(selectRoomType, rtype);

            Text roomStatus = new Text("Enter Room Status");
            roomStatus.setFont(new Font(16.0));
            ComboBox<String> rmstus_field = new ComboBox<>();
            rmstus_field.getItems().addAll("Available", "Unavailable", "Under Maintenance");
            VBox statusFieldBox = new VBox(roomStatus, rmstus_field);

            VBox credentialsPane = new VBox(roomBox, floorBox, statusFieldBox, rtBox);
            credentialsPane.setStyle("-fx-spacing: 15px;" + "-fx-padding: 30px;");

            BorderPane modalRoot = new BorderPane();

            Text modalTitle = new Text("Create Room");
            modalTitle.setFont(new Font(28.0));

            Text modalHint = new Text("Create a new room for your hotel");
            VBox topBox = new VBox(modalTitle, modalHint);
            topBox.setAlignment(Pos.CENTER);
            topBox.setStyle("-fx-padding: 10px;");

            modalRoot.setTop(topBox);
            BorderPane.setAlignment(topBox, Pos.CENTER);

            modalRoot.setCenter(credentialsPane);

            Button confirmButton = new Button("Confirm");
            Button cancelButton = new Button("Cancel");

            HBox buttonBox = new HBox(cancelButton, confirmButton);
            modalRoot.setBottom(buttonBox);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setStyle("-fx-spacing: 15px;" + "-fx-padding: 0 25px 20px 0;");

            BorderPane.setAlignment(buttonBox, Pos.TOP_RIGHT);

            Scene modalScene = new Scene(modalRoot, 425, 425);
            modalScene.setFill(Color.TRANSPARENT);
            modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");
            modalStage.setScene(modalScene);
            modalStage.setResizable(false);
            modalStage.show();

            modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
            modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

            cancelButton.setOnAction(e -> modalStage.close());
            confirmButton.setOnAction(e -> {
                createRoom(roomNo.getText(), rtype.getSelectionModel().getSelectedItem(),
                        floor_field.getText(), rmstus_field.getSelectionModel().getSelectedItem(), e, modalStage);
            });
        }

        private void createRoom(String room_no, String rtid, String floor, String r_status, ActionEvent e, Stage st) {

            loaderSettings.applyDimmingEffect(e);

            Task<Boolean> loadSceneTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    try {

                        int f = Integer.parseInt(floor);
                        boolean success = roomController.saveRoom(room_no, rtid, f, r_status);

                        if (!success) {
                            throw new Exception("Failed to add new room to the database.");
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
                    notificationManager.showNotification(String.format("Successfully added %s", room_no), "success", (Stage) logout.getScene().getWindow());
                    st.close();
                    loaderSettings.removeDimmingEffect(e);
                    loadingStage.hide();
                    generateRooms();
                    System.out.println("Room added successfully.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            loadSceneTask.setOnFailed(er -> {
                try {
                    loadingStage.hide();
                    loaderSettings.removeDimmingEffect(e);
                    notificationManager.showNotification("Failed to create a room type", "failure", st);
                    System.out.println("Please try again.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            new Thread(loadSceneTask).start();
        }

        private void EditRoom(String room_no, String rtid, String floor, String r_status, ActionEvent e, Stage st) {

            loaderSettings.applyDimmingEffect(e);

            Task<Boolean> loadSceneTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    try {

                        int f = Integer.parseInt(floor);
                        boolean success = roomController.editRoom(room_no, rtid, f, r_status);

                        if (!success) {
                            throw new Exception("Failed to edit room to the database.");
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
                    notificationManager.showNotification(String.format("Successfully updated %s", room_no), "success", (Stage) logout.getScene().getWindow());
                    st.close();
                    loaderSettings.removeDimmingEffect(e);
                    loadingStage.hide();
                    generateRooms();
                    System.out.println("Room updated successfully.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            loadSceneTask.setOnFailed(er -> {
                try {
                    loadingStage.hide();
                    loaderSettings.removeDimmingEffect(e);
                    notificationManager.showNotification("Failed to update a room type", "failure", st);
                    System.out.println("Please try again.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            new Thread(loadSceneTask).start();
        }

        private void generateRooms() {
            roomsView.getChildren().clear();

            List<room> lr = roomController.getAllRooms();
            roomsView.getChildren().add(addRoom);
            addRoom.setStyle("-fx-pref-width: 150;" + "-fx-pref-height:90;" +
                    "-fx-background-color: #F6F5F2; " +
                    "-fx-text-fill: #333333; " +
                    "-fx-font-size: 16px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-border-color: #cccccc; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10; " +
                    "-fx-padding: 10; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0.0, 0, 1);");
            addRoom.setOnMouseEntered(e -> addRoom.setStyle(
                    "-fx-pref-width: 150;" + "-fx-pref-height:90;" +
                            "-fx-background-color: #e0e0e0; " +
                            "-fx-text-fill: #333333; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-border-color: #aaaaaa; " +
                            "-fx-border-radius: 10; " +
                            "-fx-background-radius: 10; " +
                            "-fx-padding: 10; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0.0, 0, 1);"));

            addRoom.setOnMouseExited(e -> addRoom.setStyle(
                    "-fx-pref-width: 150;" + "-fx-pref-height:90;" +
                            "-fx-background-color: #F6F5F2; " +
                            "-fx-text-fill: #333333; " +
                            "-fx-font-size: 16px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-border-color: #cccccc; " +
                            "-fx-border-radius: 10; " +
                            "-fx-background-radius: 10; " +
                            "-fx-padding: 10; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0.0, 0, 1);"));
            for (room r : lr) {
                Button hotelrooms = new Button(r.getRoom_no());
                hotelrooms.setStyle("-fx-pref-width: 150;" + "-fx-pref-height:90;" +
                        "-fx-background-color: #F6F5F2; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0.0, 0, 1);");

                hotelrooms.setOnMouseEntered(e -> hotelrooms.setStyle(
                        "-fx-pref-width: 150;" + "-fx-pref-height:90;" +
                                "-fx-background-color: #e0e0e0; " +
                                "-fx-text-fill: #333333; " +
                                "-fx-font-size: 16px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-border-color: #aaaaaa; " +
                                "-fx-border-radius: 10; " +
                                "-fx-background-radius: 10; " +
                                "-fx-padding: 10; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0.0, 0, 1);"));

                hotelrooms.setOnMouseExited(e -> hotelrooms.setStyle(
                        "-fx-pref-width: 150;" + "-fx-pref-height:90;" +
                                "-fx-background-color: #F6F5F2; " +
                                "-fx-text-fill: #333333; " +
                                "-fx-font-size: 16px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-border-color: #cccccc; " +
                                "-fx-border-radius: 10; " +
                                "-fx-background-radius: 10; " +
                                "-fx-padding: 10; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0.0, 0, 1);"));

                hotelrooms.setOnAction(e -> {
                    editRoom(r);
                });
                roomsView.getChildren().add(hotelrooms);
            }
        }

        private void editRoom(room r) {

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage owner = (Stage) rooms.getScene().getWindow();
            modalStage.initOwner(owner);
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Text room_no = new Text("Enter Room No.");
            room_no.setFont(new Font(16.0));
            TextField roomNo = new TextField();
            roomNo.setEditable(false);
            roomNo.setText(r.getRoom_no());
            VBox roomBox = new VBox(room_no, roomNo);

            Text roomFloor = new Text("Choose Floor");
            roomFloor.setFont(new Font(16.0));
            TextField floorField = new TextField();
            floorField.setText(r.getFloor() + "");
            VBox floorFieldBox = new VBox(roomFloor, floorField);

            Text selectRoomType = new Text("Select Room Type");
            selectRoomType.setFont(new Font(16.0));
            ComboBox<String> rtype = new ComboBox<>();
            rtype.getItems().addAll(roomTypeController.getRoomTypeID());
            rtype.getSelectionModel().select(r.getRoom_type().getRtid());
            VBox rtBox = new VBox(selectRoomType, rtype);

            Text roomStatus = new Text("Enter Room Status");
            roomStatus.setFont(new Font(16.0));
            ComboBox<String> rmstus_field = new ComboBox<>();
            rmstus_field.getItems().addAll("Available", "Unavailable", "Under Maintenance");
            rmstus_field.getSelectionModel().select(r.getRoom_status());
            VBox statusFieldBox = new VBox(roomStatus, rmstus_field);

            VBox credentialsPane = new VBox(roomBox, floorFieldBox, statusFieldBox, rtBox);
            credentialsPane.setStyle("-fx-spacing: 15px;" + "-fx-padding: 30px;");

            BorderPane modalRoot = new BorderPane();

            Text modalTitle = new Text("Edit Room");
            modalTitle.setFont(new Font(28.0));

            Text modalHint = new Text("Edit a new room for your hotel");
            VBox topBox = new VBox(modalTitle, modalHint);
            topBox.setAlignment(Pos.CENTER);
            topBox.setStyle("-fx-padding: 10px;");

            modalRoot.setTop(topBox);
            BorderPane.setAlignment(topBox, Pos.CENTER);

            modalRoot.setCenter(credentialsPane);

            Button confirmButton = new Button("Confirm");
            Button cancelButton = new Button("Cancel");

            HBox buttonBox = new HBox(cancelButton, confirmButton);
            modalRoot.setBottom(buttonBox);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setStyle("-fx-spacing: 15px;" + "-fx-padding: 0 25px 20px 0;");

            BorderPane.setAlignment(buttonBox, Pos.TOP_RIGHT);

            Scene modalScene = new Scene(modalRoot, 425, 425);
            modalScene.setFill(Color.TRANSPARENT);
            modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");
            modalStage.setScene(modalScene);
            modalStage.setResizable(false);
            modalStage.show();

            modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
            modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

            cancelButton.setOnAction(e -> modalStage.close());
            confirmButton.setOnAction(e -> {
                EditRoom(roomNo.getText(), rtype.getSelectionModel().getSelectedItem(),
                        floorField.getText(), rmstus_field.getSelectionModel().getSelectedItem(), e, modalStage);
            });
        }
    }

    class roomTypeBuilder {
        private void createRoomType() {

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage owner = (Stage) rooms.getScene().getWindow();
            modalStage.initOwner(owner);
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Text rtID = new Text("Enter RoomType ID");
            rtID.setFont(new Font(16.0));
            TextField rtIdField = new TextField();
            VBox roomBox = new VBox(rtID, rtIdField);

            Text rtDescription = new Text("Enter Description");
            rtDescription.setFont(new Font(16.0));
            TextArea rtDescriptionField = new TextArea();
            VBox roomTypeBox = new VBox(rtDescription, rtDescriptionField);

            Text nPrice = new Text("Enter Price/Night");
            Spinner<Double> nightprice = createPriceSpinner("");
            VBox npBox = new VBox(nPrice, nightprice);

            Text hPrice = new Text("Enter Price/Hour");
            Spinner<Double> hourprice = createPriceSpinner("");
            VBox hpBox = new VBox(hPrice, hourprice);

            VBox credentialsPane = new VBox(roomBox, roomTypeBox, npBox, hpBox);
            credentialsPane.setStyle("-fx-spacing: 15px;" + "-fx-padding: 30px;");

            BorderPane modalRoot = new BorderPane();

            Text modalTitle = new Text("Create Room Type");
            modalTitle.setFont(new Font(28.0));

            Text modalHint = new Text("Create a new room type for your rooms");
            VBox topBox = new VBox(modalTitle, modalHint);
            topBox.setAlignment(Pos.CENTER);
            topBox.setStyle("-fx-padding: 10px;");

            modalRoot.setTop(topBox);
            BorderPane.setAlignment(topBox, Pos.CENTER);

            modalRoot.setCenter(credentialsPane);

            Button confirmButton = new Button("Confirm");
            Button cancelButton = new Button("Cancel");

            HBox buttonBox = new HBox(cancelButton, confirmButton);
            modalRoot.setBottom(buttonBox);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setStyle("-fx-spacing: 15px;" + "-fx-padding: 0 25px 20px 0;");

            BorderPane.setAlignment(buttonBox, Pos.TOP_RIGHT);

            Scene modalScene = new Scene(modalRoot, 425, 425);
            modalScene.setFill(Color.TRANSPARENT);
            modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");
            modalStage.setScene(modalScene);
            modalStage.setResizable(false);
            modalStage.show();

            modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
            modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

            cancelButton.setOnAction(e -> modalStage.close());
            confirmButton.setOnAction(e -> {
                createRoomType(rtIdField.getText(), rtDescriptionField.getText(),
                        nightprice.getValue(), hourprice.getValue(), e, modalStage);
            });
        }

        private void editRoomType() {

            room_type_details selected_rt = typeTable.getSelectionModel().getSelectedItem();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage owner = (Stage) rooms.getScene().getWindow();
            modalStage.initOwner(owner);
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Text rtID = new Text("RoomType ID");
            rtID.setFont(new Font(16.0));
            TextField rtIdField = new TextField();
            rtIdField.setEditable(false);
            rtIdField.setText(selected_rt.getId());
            VBox roomBox = new VBox(rtID, rtIdField);
            Text rtDescription = new Text("Edit Description");
            rtDescription.setFont(new Font(16.0));
            TextArea rtDescriptionField = new TextArea();
            rtDescriptionField.setText(selected_rt.getDescription());
            VBox roomTypeBox = new VBox(rtDescription, rtDescriptionField);

            Text nPrice = new Text("Enter Price/Night");
            Spinner<Double> nightprice = createPriceSpinner("n");
            VBox npBox = new VBox(nPrice, nightprice);

            Text hPrice = new Text("Enter Price/Hour");
            Spinner<Double> hourprice = createPriceSpinner("s");
            VBox hpBox = new VBox(hPrice, hourprice);

            VBox credentialsPane = new VBox(roomBox, roomTypeBox, npBox, hpBox);
            credentialsPane.setStyle("-fx-spacing: 15px;" + "-fx-padding: 30px;");

            BorderPane modalRoot = new BorderPane();

            Text modalTitle = new Text("Create Room Type");
            modalTitle.setFont(new Font(28.0));

            Text modalHint = new Text("Create a new room type for your rooms");
            VBox topBox = new VBox(modalTitle, modalHint);
            topBox.setAlignment(Pos.CENTER);
            topBox.setStyle("-fx-padding: 10px;");

            modalRoot.setTop(topBox);
            BorderPane.setAlignment(topBox, Pos.CENTER);

            modalRoot.setCenter(credentialsPane);

            Button confirmButton = new Button("Confirm");
            Button cancelButton = new Button("Cancel");

            HBox buttonBox = new HBox(cancelButton, confirmButton);
            modalRoot.setBottom(buttonBox);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setStyle("-fx-spacing: 15px;" + "-fx-padding: 0 25px 20px 0;");

            BorderPane.setAlignment(buttonBox, Pos.TOP_RIGHT);

            Scene modalScene = new Scene(modalRoot, 425, 425);
            modalScene.setFill(Color.TRANSPARENT);
            modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");
            modalStage.setScene(modalScene);
            modalStage.setResizable(false);
            modalStage.show();

            modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
            modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

            cancelButton.setOnAction(e -> modalStage.close());
            confirmButton.setOnAction(e -> {
                updateRoomType(rtIdField.getText(), rtDescriptionField.getText(),
                        nightprice.getValue(), hourprice.getValue(), e, modalStage);
            });
        }

        private Spinner<Double> createPriceSpinner(String type) {
            room_type_details selected_rt;
            double p = 0.0;

            if (typeTable.getSelectionModel().getSelectedItem() != null) {
                selected_rt = typeTable.getSelectionModel().getSelectedItem();
                p = type.matches("n") ? selected_rt.getPricePerNight() : selected_rt.getPricePerHour();
            }
            Spinner<Double> priceSpinner = new Spinner<>();

            SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory =
                    new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE,
                            p,
                            0.1);

            TextFormatter<Double> textFormatter = new TextFormatter<>(change -> {
                String newText = change.getControlNewText();

                if (newText.isEmpty()) {
                    return change;
                }

                try {
                    double newValue = Double.parseDouble(newText);
                    if (newValue < 0) {
                        return null;
                    }
                } catch (NumberFormatException e) {
                    return null;
                }

                return change;
            });

            priceSpinner.getEditor().setTextFormatter(textFormatter);

            priceSpinner.setValueFactory(valueFactory);
            priceSpinner.setEditable(true);


            return priceSpinner;

        }

        private void createRoomType(String rtid, String desc, Double np, Double hp, ActionEvent e, Stage st) {

            loaderSettings.applyDimmingEffect(e);

            Task<Boolean> loadSceneTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    try {

                        boolean success = roomTypeController.saveRoomTypeAndPrice(rtid, desc, np, hp);

                        if (!success) {
                            throw new Exception("Failed to add new room type to the database.");
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
                    notificationManager.showNotification(String.format("Successfully added %s", rtid), "success", (Stage) logout.getScene().getWindow());
                    st.close();
                    loaderSettings.removeDimmingEffect(e);
                    loadingStage.hide();
                    roomTypeService.restart();
                    System.out.println("Room Type added successfully.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            loadSceneTask.setOnFailed(er -> {
                try {
                    loadingStage.hide();
                    loaderSettings.removeDimmingEffect(e);
                    notificationManager.showNotification("Failed to create a room type", "failure", st);
                    System.out.println("Please try again.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            new Thread(loadSceneTask).start();
        }

        private void updateRoomType(String rtid, String desc, Double np, Double hp, ActionEvent e, Stage st) {

            loaderSettings.applyDimmingEffect(e);

            Task<Boolean> loadSceneTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    try {

                        boolean success = roomTypeController.updateRoomType(rtid, desc, np, hp);

                        if (!success) {
                            throw new Exception("Failed to update room type to the database.");
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
                    notificationManager.showNotification(String.format("Successfully updated %s", rtid), "success", (Stage) logout.getScene().getWindow());
                    st.close();
                    loaderSettings.removeDimmingEffect(e);
                    loadingStage.hide();
                    roomTypeService.restart();
                    System.out.println("Room Type updated successfully.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            loadSceneTask.setOnFailed(er -> {
                try {
                    loadingStage.hide();
                    loaderSettings.removeDimmingEffect(e);
                    notificationManager.showNotification("Failed to update a room type", "failure", st);
                    System.out.println("Please try again.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            new Thread(loadSceneTask).start();
        }

    }

}
