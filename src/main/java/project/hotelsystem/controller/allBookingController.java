package project.hotelsystem.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.hotelsystem.database.controller.bookingController;
import project.hotelsystem.database.models.booking;
import project.hotelsystem.settings.userSettings;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class allBookingController {

    private final Service<List<booking>> bookingService = new Service<>() {
        @Override
        protected Task<List<booking>> createTask() {
            return new Task<>() {
                @Override
                protected List<booking> call() throws Exception {
                    return bookingController.getAllBookings();

                }
            };
        }
    };
    // Data for table
    switchSceneController ssc = new switchSceneController();
    userSettings tss = userSettings.getInstance();
    @FXML
    private Text StaffName;
    @FXML
    private TilePane arrivalPane;
    @FXML
    private TableView<booking> bookingTable; // Changed from wildcard to booking type
    @FXML
    private Button bookings_button;
    @FXML
    private TilePane checkoutPane;
    @FXML
    private TableColumn<booking, Void> col_cancel; // For cancel action button
    @FXML
    private TableColumn<booking, Void> col_detail; // For check-in action button
    @FXML
    private TableColumn<booking, String> col_id; // booking ID
    @FXML
    private TableColumn<booking, String> col_name; // Guest Name
    @FXML
    private TableColumn<booking, String> col_note;
    @FXML
    private TableColumn<booking, String> col_ph;
    @FXML
    private TableColumn<booking, String> col_room; // Room Number
    @FXML
    private TableColumn<booking, Void> col_submit;
    @FXML
    private HBox floorFilter;
    @FXML
    private Button logout;
    @FXML
    private Button dashboard_button;
    @FXML
    private Button orders_button;
    @FXML
    private VBox roomView;
    @FXML
    private Button rooms_button;
    @FXML
    private TextField searchField;
    @FXML
    private Button services_button;
    @FXML
    private Button settings;

    @FXML
    void initialize() {

        if (tss.getPrivilege().matches("staff")) StaffName.setText(tss.getUsername());
        else StaffName.setText("");

        col_id.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBooking_id()));
        col_name.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGuest().getGuest_name()));
        col_room.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom().getRoom_no()));

        col_cancel.setCellFactory(param -> new TableCell<booking, Void>() {
            private final Button cancelButton = new Button("Cancel");

            {
                cancelButton.setStyle(
                        "-fx-background-color: #FF4C4C; " +
                                "-fx-text-fill: white; " +
                                "-fx-border-color: #B22222; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 10;"
                );

                cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(
                        "-fx-background-color: #FF0000; " +
                                "-fx-text-fill: white; " +
                                "-fx-border-color: #B22222; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 10;"
                ));

                cancelButton.setOnMouseExited(e -> cancelButton.setStyle(
                        "-fx-background-color: #FF4C4C; " +
                                "-fx-text-fill: white; " +
                                "-fx-border-color: #B22222; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 10;"
                ));
                cancelButton.setOnAction(event -> {
                    booking booking = getTableView().getItems().get(getIndex());
                    System.out.println("Cancel booking: " + booking.getBooking_id());
                    BookingFunc bkf = new BookingFunc();
                    bkf.cancelBooking();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item != null) {
                    setGraphic(null);
                } else {
                    setGraphic(cancelButton);
                }
            }
        });

        col_submit.setCellFactory(param -> new TableCell<booking, Void>() {
            private final Button checkinButton = new Button("Check-In");

            {
                checkinButton.setStyle(
                        "-fx-background-color: #4CAF50; " +
                                "-fx-text-fill: white; " +
                                "-fx-border-color: #388E3C; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 10;"
                );

                checkinButton.setOnMouseEntered(e -> checkinButton.setStyle(
                        "-fx-background-color: #45A049; " +
                                "-fx-text-fill: white; " +
                                "-fx-border-color: #388E3C; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 10;"
                ));

                checkinButton.setOnMouseExited(e -> checkinButton.setStyle(
                        "-fx-background-color: #4CAF50; " +
                                "-fx-text-fill: white; " +
                                "-fx-border-color: #388E3C; " +
                                "-fx-border-radius: 5; " +
                                "-fx-background-radius: 5; " +
                                "-fx-padding: 10;"
                ));
                checkinButton.setOnAction(event -> {
                    booking booking = getTableView().getItems().get(getIndex());
                    System.out.println("CheckIn booking: " + booking.getBooking_id());
                    BookingFunc bkf = new BookingFunc();
                    bkf.arrivedBooking();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item != null) {
                    setGraphic(null);
                } else {
                    setGraphic(checkinButton);
                }
            }
        });


        bookingService.valueProperty().addListener((obs, oldData, newData) -> {
            bookingTable.getItems().clear();
            arrivalPane.getChildren().clear();
            bookingTable.setPlaceholder(new Label("Loading booking data..."));
            if (newData != null && !newData.isEmpty()) {
                List<booking> filteredBookings = newData.stream()
                        .filter(b -> !b.getBooking_status().matches("Checked-Out")
                                && !b.getBooking_status().matches("Arrived")
                                && !b.getBooking_status().matches("Cancelled")
                                && !b.getBooking_status().matches("leaved"))
                        .collect(Collectors.toList());

                bookingTable.getItems().addAll(filteredBookings);
                // Create an instance of BookingPaneController and populate the panes
                BookingPaneController bookingPaneController = new BookingPaneController();
                List<booking> arrived = newData.stream()
                        .filter(b -> !b.getBooking_status().matches("Checked-Out")
                                && !b.getBooking_status().matches("Cancelled"))
                        .collect(Collectors.toList());
                bookingPaneController.populatePanes(arrivalPane, checkoutPane, filteredBookings);
                return;
            }
            bookingTable.setPlaceholder(new Label("No bookings found."));
        });

        bookingService.start();

    }

    @FXML
    void SearchRoom(ActionEvent event) {
    }

    @FXML
    void logoutAct(ActionEvent event) {
        logoutController.logout(event);
    }

    @FXML
    void switchTobooking(ActionEvent event) {
    }

    @FXML
    void switchToorders(ActionEvent event) throws Exception {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), tss.getPrivilege(), "rooms");
    }

    @FXML
    void switchTorooms(ActionEvent event) throws Exception {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), tss.getPrivilege(), "rooms");
    }

    @FXML
    void switchToservices(ActionEvent event) throws Exception {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), tss.getPrivilege(), "services");
    }


    @FXML
    void switchToDashboard(ActionEvent event) throws Exception {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), tss.getPrivilege(), "dashboard");
    }

    @FXML
    void switchTosettings(ActionEvent event) throws Exception {
        ssc.toSettings(event, (Stage) logout.getScene().getWindow());
    }

    class BookingPaneController {

        public void populatePanes(TilePane arrivalPane, TilePane checkoutPane, List<booking> bookings) {
            LocalDate today = LocalDate.now();

            for (booking b : bookings) {
                if (b.getCheck_in().toLocalDate().isEqual(today)) {
                    addRoomToTilePane(arrivalPane, b.getRoom().getRoom_no(), b.getGuest().getGuest_name());
                }
                if (b.getCheck_out().toLocalDate().isEqual(today)) {
                    addRoomToTilePane(checkoutPane, b.getRoom().getRoom_no(), b.getGuest().getGuest_name());
                }
            }
        }

        private void addRoomToTilePane(TilePane pane, String roomNo, String guestName) {
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));

            Text roomNoText = new Text("Room No: " + roomNo);
            Text guestNameText = new Text("Guest: " + guestName);

            String lightModeStyle = "-fx-font-size: 16px; " +
                    "-fx-text-fill: #333333; " +
                    "-fx-background-color: #F6F5F2; " +
                    "-fx-padding: 15px; " +
                    "-fx-border-color: #cccccc; " +
                    "-fx-border-radius: 5; " +
                    "-fx-background-radius: 5; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.0, 0, 1);" +
                    "-fx-vgap:10px; -fx-hgap: 15px;";

            String darkModeStyle = "-fx-font-size: 16px; " +
                    "-fx-text-fill: #FFFFFF; " +
                    "-fx-background-color: #121212; " +
                    "-fx-padding: 15px; " +
                    "-fx-border-color: #444444; " +
                    "-fx-border-radius: 5; " +
                    "-fx-background-radius: 5; " +
                    "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.1), 10, 0.0, 0, 1);" +
                    "-fx-vgap:10px; -fx-hgap: 15px;";

            String style = tss.getTheme();
            switch (style) {
                case "light":
                    grid.setStyle(lightModeStyle);
                    break;
                case "dark":
                    grid.setStyle(darkModeStyle);
                    break;
                default:
                    break;
            }


            grid.add(roomNoText, 0, 0);
            grid.add(guestNameText, 0, 1);

            pane.getChildren().add(grid);
        }
    }

    class BookingFunc {
        public void cancelBooking() {
            booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();

            if (selectedBooking == null) {
                showAlert("No Booking Selected", "Please select a booking to cancel.");
                return;
            }

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage owner = (Stage) logout.getScene().getWindow();
            modalStage.initOwner(owner);
            modalStage.initStyle(StageStyle.UNDECORATED);
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Text bookingIdText = new Text("Booking ID");
            bookingIdText.setFont(new Font(16.0));
            Text bookingIdField = new Text(selectedBooking.getBooking_id());
            VBox bookingIdBox = new VBox(bookingIdText, bookingIdField);

            Text guestNameText = new Text("Guest Name");
            guestNameText.setFont(new Font(16.0));
            Text guestNameField = new Text(selectedBooking.getGuest().getGuest_name());
            VBox guestNameBox = new VBox(guestNameText, guestNameField);

            VBox credentialsPane = new VBox(bookingIdBox, guestNameBox);
            credentialsPane.setStyle("-fx-spacing: 15px;" + "-fx-padding: 30px;");

            BorderPane modalRoot = new BorderPane();

            Text modalTitle = new Text("Cancel Booking");
            modalTitle.setFont(new Font(28.0));

            Text modalHint = new Text("Are you sure you want to cancel this booking?");
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
            modalStage.setScene(modalScene);
            modalScene.setFill(Color.TRANSPARENT);
            modalStage.setResizable(false);
            modalStage.show();
            modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");

            modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
            modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

            cancelButton.setOnAction(e -> {
                modalStage.close();
            });

            confirmButton.setOnAction(e -> {
                if (removeBookingFromDB(selectedBooking.getBooking_id(), selectedBooking.getRoom().getRoom_no())) {
                    showAlert("Success", "Booking has been successfully cancelled.");
                    bookingTable.getItems().remove(selectedBooking);
                } else {
                    showAlert("Error", "Failed to cancel the booking.");
                }
                modalStage.close();
            });
        }

        private boolean removeBookingFromDB(String bookingId, String roomID) {
            return bookingController.cancelBooking(bookingId, roomID);
        }

        public void arrivedBooking() {
            booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();

            if (selectedBooking == null) {
                showAlert("No Booking Selected", "Please select a booking.");
                return;
            }

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            Stage owner = (Stage) logout.getScene().getWindow();
            modalStage.initOwner(owner);
            modalStage.initStyle(StageStyle.UNDECORATED);
            modalStage.initStyle(StageStyle.TRANSPARENT);

            Text bookingIdText = new Text("Booking ID");
            bookingIdText.setFont(new Font(16.0));
            Text bookingIdField = new Text(selectedBooking.getBooking_id());
            VBox bookingIdBox = new VBox(bookingIdText, bookingIdField);

            Text guestNameText = new Text("Guest Name");
            guestNameText.setFont(new Font(16.0));
            Text guestNameField = new Text(selectedBooking.getGuest().getGuest_name());
            VBox guestNameBox = new VBox(guestNameText, guestNameField);

            VBox credentialsPane = new VBox(bookingIdBox, guestNameBox);
            credentialsPane.setStyle("-fx-spacing: 15px;" + "-fx-padding: 30px;");

            BorderPane modalRoot = new BorderPane();

            Text modalTitle = new Text("Check-In");
            modalTitle.setFont(new Font(28.0));

            Text modalHint = new Text("Are you sure?");
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
            modalStage.setScene(modalScene);
            modalScene.setFill(Color.TRANSPARENT);
            modalStage.setResizable(false);
            modalStage.show();
            modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");

            modalStage.setX((owner.getX() + owner.getWidth() / 2d) - (modalScene.getWidth() / 2d));
            modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

            cancelButton.setOnAction(e -> {
                modalStage.close();
            });

            confirmButton.setOnAction(e -> {
                if (bookingController.arrived(selectedBooking.getBooking_id(), selectedBooking.getRoom().getRoom_no())) {
                    showAlert("Success", "Successfully Checked-in.");
                    bookingTable.getItems().remove(selectedBooking);
                } else {
                    showAlert("Error", "Failed.");
                }
                modalStage.close();
            });
        }

        private void showAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

    }

}


