package project.hotelsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.hotelsystem.database.controller.roomController;
import project.hotelsystem.database.controller.roomTypeController;
import project.hotelsystem.database.models.room;
import project.hotelsystem.database.models.room_type_details;
import project.hotelsystem.settings.databaseSettings;
import project.hotelsystem.settings.invoiceSettings;
import project.hotelsystem.util.dropdownManager;
import project.hotelsystem.util.notificationManager;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

/**
 * Rooms Reservation/Booking Controller class
 *
 * @author Khant Zin Hein
 * @author Nomar
 */

public class staffRoomDetailsController {

    @FXML
    private TextField Email;

    @FXML
    private TextField FirstName;

    @FXML
    private TextField FirstName11;

    @FXML
    private TextField LastName;

    @FXML
    private TextField LastName11;

    @FXML
    private Text NumberOfAR;

    @FXML
    private Text NumberOfBR;

    @FXML
    private AnchorPane RoomShowBody;

    @FXML
    private Text StaffName;

    @FXML
    private DatePicker ToCheckWithCheckInDate;

    @FXML
    private DatePicker ToCheckWithCheckOutDate;

    @FXML
    private DatePicker arrivalDate;

    @FXML
    private AnchorPane body;

    @FXML
    private Button bookings_button;

    @FXML
    private ComboBox<?> ck_payment_method;

    @FXML
    private ComboBox<?> bk_payment_method;

    @FXML
    private TextField deposit11;

    @FXML
    private TextField duration;

    @FXML
    private TextField duration1;

    @FXML
    private Text floorText;

    @FXML
    private Button floor_filter;

    @FXML
    private TextField idORnrc;

    @FXML
    private Button logout;

    @FXML
    private Button orders_button;

    @FXML
    private TextField phoneNumber;

    @FXML
    private TextField phoneNumber11;

    @FXML
    private Text roomID;

    @FXML
    private Text roomPrice;

    @FXML
    private Text roomPrice1;

    @FXML
    private Text roomType;

    @FXML
    private Button room_filter;

    @FXML
    private Button rooms_button;

    @FXML
    private TextField searchField;

    @FXML
    private Button services_button;

    @FXML
    private Button settings;

    private String selectedRoomNo;
    switchSceneController ssc = new switchSceneController();
    static databaseSettings dbs = databaseSettings.getInstance();

    private static final String JDBC_URL = dbs.getLocal_url();
    private static final String DB_USER = dbs.getLocal_user();
    private static final String DB_PASSWORD = dbs.getLocal_password();


    @FXML
    public void initialize() {
        orders_button.setDisable(true);
        AddingRooms(null, 0, null);
        updateRoomCounts();

        arrivalDate.setValue(LocalDate.now());
        arrivalDate.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });


        ObservableList<room_type_details> ols = FXCollections.observableArrayList(roomTypeController.getAllRoomType());

        Popup roomDp = dropdownManager.createRoomTypeDropdown(room_filter, ols);
        room_filter.setOnAction(event -> {
            if (!roomDp.isShowing()) {
                Bounds bounds = room_filter.localToScreen(room_filter.getBoundsInLocal());
                roomDp.show(room_filter.getScene().getWindow(),
                        bounds.getMinX(), bounds.getMinY() - 280);
            }
        });

        ObservableList<room> olr = FXCollections.observableArrayList(roomController.getFloors());

        Popup floorDp = dropdownManager.createFloorDropdown(floor_filter, olr);
        floor_filter.setOnAction(event -> {
            if (!floorDp.isShowing()) {
                Bounds bounds = floor_filter.localToScreen(floor_filter.getBoundsInLocal());
                floorDp.show(floor_filter.getScene().getWindow(),
                        bounds.getMinX(), bounds.getMinY() - 280);
            }
        });

        floor_filter.textProperty().addListener((ob, ov, nv) -> {

            if (nv != null) {

                if (floor_filter.getText().contains("All")
                        && room_filter.getText().contains("All")) {
                    AddingRooms(null, 0, null);
                    return;
                }

                boolean flag = true;
                int floor = 0;
                String[] parts = nv.split(" ");
                floor = Integer.parseInt(parts[1]);

                try {
                    int flootToSearch = Integer.parseInt(parts[1]);
                } catch (Exception e) {
                    flag = false;
                }

                if (flag) {
                    floor = Integer.parseInt(parts[1]);
                }

                if (room_filter.getText().contains("All")) {
                    AddingRooms(null, floor, null);
                    return;
                }

                AddingRooms(null, floor, room_filter.getText());


            }
        });

        room_filter.textProperty().addListener((ob, ov, nv) -> {
            if (nv != null) {

                if (room_filter.getText().contains("All")
                        && floor_filter.getText().contains("All")) {
                    AddingRooms(null, 0, null);
                    return;
                }

                if (!room_filter.getText().contains("All")
                        && floor_filter.getText().contains("All")) {
                    AddingRooms(null, 0, nv);
                    return;
                }

                String[] parts = floor_filter.getText().split(" ");
                int floor = Integer.parseInt(parts[1]);

                if (room_filter.getText().contains("All")) {
                    AddingRooms(null, floor, null);
                    return;
                }

                AddingRooms(null, floor, nv);

            }
        });

    }

    public void AddingRooms(String roomIdToSearch, int floorFilter, String rt) {

        RoomShowBody.getChildren().clear();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {

            String query = "SELECT room.room_no, room.floor, room.room_status, room_type.description, room_price.price_per_night,room_price.price_per_hour " +
                    "FROM room " +
                    "JOIN room_type ON room.room_type_id = room_type.room_type_id " +
                    "JOIN room_price ON room_type.room_type_id = room_price.room_type_id " +
                    "WHERE 1=1";

            if (roomIdToSearch != null && !roomIdToSearch.isEmpty()) {
                query += " AND room.room_no = ?";
            }

            int qIndex = 1;

            if (floorFilter != 0) {
                query += " AND room.floor = ?";
                qIndex++;
            }

            String floor_f = "All";

//            if (!floor_f.matches("All")) {
//                query += " AND room_type.description = ? ";
//            }

            if (rt != null) query += " AND room_type.description = ? ";

            query += " ORDER BY room.room_no ASC; ";

            PreparedStatement stmt = conn.prepareStatement(query);

//            if (!floor_f.matches("All")) stmt.setString(2, floor_f);

            if (rt != null) stmt.setString(qIndex, rt);

            int index = 1;
            if (roomIdToSearch != null && !roomIdToSearch.isEmpty()) {
                stmt.setString(index++, roomIdToSearch);
            }
            if (floorFilter != 0) {
                stmt.setInt(index, floorFilter);
            }

            ResultSet rs = stmt.executeQuery();
            GridPane roomGridPane = new GridPane();
            roomGridPane.setHgap(20);
            roomGridPane.setVgap(20);

            TilePane roomPane = new TilePane();
            roomPane.setHgap(20);
            roomPane.setVgap(20);

            int row = 0, col = 0;

            while (rs.next()) {
                String roomId = rs.getString("room_no");
                String roomType = rs.getString("description");
                String floor = rs.getString("floor");
                String status = rs.getString("room_status");
                String price = rs.getString("price_per_night");
                String price_per_hour = rs.getString("price_per_hour");

                Button roomButton = new Button("Room " + roomId);
                roomButton.setPrefSize(100, 50);

                String commonStyles = "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.75), 10, 0.0, 5, 5);";

                @SuppressWarnings("unused")
                String focusedStyles = "-fx-background-color: #7b7d7d;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 1.0), 15, 0.0, 5, 5);";

                // Color coding based on room status
                switch (status) {
                    case "Unavailable", "Under Maintenance":
                        roomButton.setStyle(
                                "-fx-background-color: #FF4C4C; " + // Bright red for unavailable
                                        "-fx-text-fill: white; " +
                                        "-fx-border-color: #B22222; " + // Darker red border
                                        "-fx-border-radius: 10; " +
                                        "-fx-background-radius: 10; " +
                                        "-fx-padding: 10;");
                        roomButton.setOnMouseEntered(e -> roomButton.setStyle(

                                "-fx-background-color: #FF0000; " + // Darker red on hover
                                        "-fx-text-fill: white; " +
                                        "-fx-border-color: #B22222; " +
                                        "-fx-border-radius: 10; " +
                                        "-fx-background-radius: 10; " +
                                        "-fx-padding: 10;"));
                        roomButton.setOnMouseExited(e -> roomButton.setStyle(

                                "-fx-background-color: #FF4C4C; " +
                                        "-fx-text-fill: white; " +
                                        "-fx-border-color: #B22222; " +
                                        "-fx-border-radius: 10; " +
                                        "-fx-background-radius: 10; " +
                                        "-fx-padding: 10;"));
                        break;
                    case "Available":
                        roomButton.setStyle(
                                "-fx-background-color: #3CB371; " + // Medium sea green for available
                                        "-fx-text-fill: white; " +
                                        "-fx-border-color: #2E8B57; " + // Darker green border
                                        "-fx-border-radius: 10; " +
                                        "-fx-background-radius: 10; " +
                                        "-fx-padding: 10;");
                        roomButton.setOnMouseEntered(e -> roomButton.setStyle(

                                "-fx-background-color: #2E8B57; " + // Darker green on hover
                                        "-fx-text-fill: white; " +
                                        "-fx-border-color: #2E8B57; " +
                                        "-fx-border-radius: 10; " +
                                        "-fx-background-radius: 10; " +
                                        "-fx-padding: 10;"));
                        roomButton.setOnMouseExited(e -> roomButton.setStyle(

                                "-fx-background-color: #3CB371; " +
                                        "-fx-text-fill: white; " +
                                        "-fx-border-color: #2E8B57; " +
                                        "-fx-border-radius: 10; " +
                                        "-fx-background-radius: 10; " +
                                        "-fx-padding: 10;"));
                        break;
                    case "Booked":
                        roomButton.setStyle(commonStyles +
                                "-fx-background-color: #FFD700; " +
                                "-fx-text-fill: black; " +
                                "-fx-border-color: #B8860B; " +
                                "-fx-border-radius: 10; " +
                                "-fx-background-radius: 10; " +
                                "-fx-padding: 10;");
                        roomButton.setOnMouseEntered(e -> roomButton.setStyle(

                                "-fx-background-color: #FFC300; " +
                                        "-fx-text-fill: black; " +
                                        "-fx-border-color: #B8860B; " +
                                        "-fx-border-radius: 10; " +
                                        "-fx-background-radius: 10; " +
                                        "-fx-padding: 10;"));
                        roomButton.setOnMouseExited(e -> roomButton.setStyle(

                                "-fx-background-color: #FFD700; " +
                                        "-fx-text-fill: black; " +
                                        "-fx-border-color: #B8860B; " +
                                        "-fx-border-radius: 10; " +
                                        "-fx-background-radius: 10; " +
                                        "-fx-padding: 10;"));
                        break;
                }


                roomButton.setOnAction(e -> {
                    roomID.setText(roomId);
                    this.roomType.setText(roomType);
                    floorText.setText(floor);
                    roomPrice.setText(price + " $");
                    this.roomPrice1.setText(price_per_hour + " $");
                    selectedRoomNo = roomId;
                    if ("Unavailable".equals(status)) {
                        fetchAndDisplayCustomerDetails(roomId);
                    }
                });


                roomPane.getChildren().add(roomButton);
                col++;
                if (col == 7) {
                    col = 0;
                    row++;
                }
            }

            roomGridPane.setGridLinesVisible(true);

            RoomShowBody.getChildren().add(roomPane);
            AnchorPane.setTopAnchor(roomPane, 20.0);
            AnchorPane.setLeftAnchor(roomPane, 20.0);
            AnchorPane.setRightAnchor(roomPane, 20.0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    invoiceSettings ivs = new invoiceSettings();

    private void fetchAndDisplayCustomerDetails(String roomId) {

        String query = "SELECT customer.customer_name, customer.phone_no, customer.email, customer.id_card, " +
                "booking.booking_id, booking.check_in, booking.check_out, " +
                "booking_charges.deposite, booking_charges.total_room_charges, booking_charges.total_booking_charges,booking_charges.remaining_amount " +
                "FROM customer " +
                "JOIN booking ON customer.customer_id = booking.customer_id " +
                "JOIN booking_room_detail ON booking.booking_id = booking_room_detail.booking_id " +
                "JOIN room ON booking_room_detail.room_no = room.room_no " +
                "JOIN booking_charges ON booking.booking_id = booking_charges.booking_id " +
                "WHERE room.room_no = ? AND booking_room_detail.booking_status = 'Arrived'";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, roomId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String customerName = rs.getString("customer_name");
                String phone = rs.getString("phone_no");
                String email = rs.getString("email");
                String idCard = rs.getString("id_card");
                int bookingId = rs.getInt("booking_id");
                Date checkInDate = rs.getDate("check_in");
                Date checkOutDate = rs.getDate("check_out");
                double deposit = rs.getDouble("deposite");
                double totalAmount = rs.getDouble("total_booking_charges");
                double remainingCharges = rs.getDouble("remaining_amount");

                // Create a new pane to display these details
                AnchorPane customerDetailsPane = new AnchorPane();
                customerDetailsPane.setPrefSize(400, 450);
                customerDetailsPane.setStyle("-fx-background-color: #f9f9f9;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #d1d1d1;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius:15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);" +
                        "-fx-padding: 20;");

                // Add Labels and Text for each data
                Text fullNameLabel = new Text("Customer Name: " + customerName);
                fullNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #333;");

                Text phoneLabel = new Text("Phone Number: " + phone);
                phoneLabel.setStyle("-fx-font-size: 14px; -fx-fill: #555;");

                Text idCardLabel = new Text("ID Card: " + idCard);
                idCardLabel.setStyle("-fx-font-size: 14px; -fx-fill: #555;");

                Text emailLabel = new Text("Email: " + email);
                emailLabel.setStyle("-fx-font-size: 14px; -fx-fill: #555;");

                Text checkInLabel = new Text("Check In: " + checkInDate.toString());
                checkInLabel.setStyle("-fx-font-size: 14px; -fx-fill: #555;");

                Text checkOutLabel = new Text("Check Out: " + checkOutDate.toString());
                checkOutLabel.setStyle("-fx-font-size: 14px; -fx-fill: #555;");

                Text depositLabel = new Text("Deposit: $" + deposit);
                depositLabel.setStyle("-fx-font-size: 14px; -fx-fill: #555;");

                Text totalAmountLabel = new Text("Total Amount: $" + totalAmount);
                totalAmountLabel.setStyle("-fx-font-size: 14px; -fx-fill: #555;");

                Text remainingChargesLabel = new Text("Remaining Charges: $" + remainingCharges);
                remainingChargesLabel.setStyle("-fx-font-size: 14px; -fx-fill: #555;");

                // Create buttons for Cancel and Check Out
                Button cancelButton = new Button("Cancel");
                cancelButton.setStyle("-fx-background-color: #ff4d4d;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;");

                Button checkOutButton = new Button("Check Out");
                checkOutButton.setStyle("-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;");

                // Button hover effects
                cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #ff1a1a;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;"));
                cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #ff4d4d;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;"));

                checkOutButton.setOnMouseEntered(e -> checkOutButton.setStyle("-fx-background-color: #45a049;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;"));
                checkOutButton.setOnMouseExited(e -> checkOutButton.setStyle("-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;"));

                AnchorPane.setTopAnchor(fullNameLabel, 10.0);
                AnchorPane.setLeftAnchor(fullNameLabel, 10.0);

                AnchorPane.setTopAnchor(phoneLabel, 40.0);
                AnchorPane.setLeftAnchor(phoneLabel, 10.0);

                AnchorPane.setTopAnchor(idCardLabel, 70.0);
                AnchorPane.setLeftAnchor(idCardLabel, 10.0);

                AnchorPane.setTopAnchor(emailLabel, 100.0);
                AnchorPane.setLeftAnchor(emailLabel, 10.0);

                AnchorPane.setTopAnchor(checkInLabel, 130.0);
                AnchorPane.setLeftAnchor(checkInLabel, 10.0);

                AnchorPane.setTopAnchor(checkOutLabel, 160.0);
                AnchorPane.setLeftAnchor(checkOutLabel, 10.0);

                AnchorPane.setTopAnchor(depositLabel, 190.0);
                AnchorPane.setLeftAnchor(depositLabel, 10.0);

                AnchorPane.setTopAnchor(totalAmountLabel, 220.0);
                AnchorPane.setLeftAnchor(totalAmountLabel, 10.0);

                AnchorPane.setTopAnchor(remainingChargesLabel, 250.0);
                AnchorPane.setLeftAnchor(remainingChargesLabel, 10.0);

                AnchorPane.setBottomAnchor(cancelButton, 10.0);
                AnchorPane.setRightAnchor(cancelButton, 120.0);

                AnchorPane.setBottomAnchor(checkOutButton, 10.0);
                AnchorPane.setRightAnchor(checkOutButton, 10.0);

                // Add all elements to the pane
                customerDetailsPane.getChildren().addAll(fullNameLabel, phoneLabel, idCardLabel, emailLabel,
                        checkInLabel, checkOutLabel, depositLabel, totalAmountLabel, remainingChargesLabel,
                        cancelButton, checkOutButton);

                // Add an overlay pane for darkened background
                AnchorPane overlayPane = new AnchorPane();
                overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
                overlayPane.setPrefSize(body.getWidth(), body.getHeight());
                body.getChildren().add(overlayPane);

                // Center the customerDetailsPane
                double paneWidth = customerDetailsPane.getPrefWidth();
                double paneHeight = customerDetailsPane.getPrefHeight();
                double bodyWidth = body.getWidth();
                double bodyHeight = body.getHeight();

                double centerX = (bodyWidth - paneWidth) / 2;
                double centerY = (bodyHeight - paneHeight) / 2;

                customerDetailsPane.setLayoutX(centerX);
                customerDetailsPane.setLayoutY(centerY);

                body.getChildren().add(customerDetailsPane);

                // Button actions
                cancelButton.setOnAction(e -> {
                    body.getChildren().removeAll(overlayPane, customerDetailsPane);
                });

                checkOutButton.setOnAction(e -> {
                    LocalDate currentDate = LocalDate.now();
                    ivs.openPdfModal(bookingId + "", (Stage) overlayPane.getScene().getWindow());

                    try (Connection connForUpdate = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {

                        // First, update the booking status to 'leaved'
                        String updateBookingStatusQuery = "UPDATE booking_room_detail SET booking_status = 'leaved' " +
                                "WHERE booking_id = ? AND room_no = ?";

                        try (PreparedStatement updateBookingStatusStmt = connForUpdate.prepareStatement(updateBookingStatusQuery)) {
                            updateBookingStatusStmt.setInt(1, bookingId);
                            updateBookingStatusStmt.setString(2, roomId);
                            updateBookingStatusStmt.executeUpdate();
                        }
                        String updateRoomStatusQuery = "UPDATE room SET room_status='Available' WHERE room_no = ?";
                        try (PreparedStatement updateRoomStatusStmt = connForUpdate.prepareStatement(updateRoomStatusQuery)) {
                            updateRoomStatusStmt.setString(1, roomId);
                            updateRoomStatusStmt.executeUpdate();
                        }
                        String updateCheckOutDateQuery = "UPDATE booking SET check_out = ? WHERE booking_id = ?";

                        try (PreparedStatement updateCheckOutDateStmt = connForUpdate.prepareStatement(updateCheckOutDateQuery)) {
                            updateCheckOutDateStmt.setDate(1, java.sql.Date.valueOf(currentDate));
                            updateCheckOutDateStmt.setInt(2, bookingId);   // Use the booking ID
                            updateCheckOutDateStmt.executeUpdate();        // Execute the third update
                        }
                        body.getChildren().removeAll(overlayPane, customerDetailsPane);
                        AddingRooms(null, 0, null);

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        System.out.println("Error during check-out: " + ex.getMessage());
                    }
                });


            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching customer details: " + e.getMessage());
        }
    }


    @FXML
    void SearchRoom(ActionEvent event) {
        String enteredRoomId = searchField.getText().trim();
        AddingRooms(enteredRoomId, 0, null);
    }

    // Method to fetch room counts from the database
    private void updateRoomCounts() {
        getRoomCount("Available", NumberOfAR);
        getRoomCount("Booked", NumberOfBR);
    }

    private void getRoomCount(String status, Text countText) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) AS room_count FROM room WHERE room_status = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("room_count");
                countText.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void RoomTypeAction(ActionEvent event) {
        AddingRooms("", 0, null);  // Call the AddingRooms method to update the room list based on the selected filters
    }


    @FXML
    void FloorAction(ActionEvent event) {
        Button source = (Button) event.getSource();
        int floor = 0;
        switch (source.getText()) {
            case "Floor1":
                floor = 1;
                break;
            case "Floor2":
                floor = 2;
                break;
            case "Floor3":
                floor = 3;
                break;
            case "Floor4":
                floor = 4;
                break;
            case "All":
                floor = 0;
                break;
        }
        AddingRooms("", floor, "");
    }


    @FXML
    void BookingAction(ActionEvent event) {
        if (selectedRoomNo == null || selectedRoomNo.isEmpty()) {
            notificationManager.showNotification("Select a room", "faiure",
                    (Stage) logout.getScene().getWindow());
            return;
        }

        if (FirstName11.getText().isBlank() || LastName11.getText().isBlank() || phoneNumber11.getText().isBlank()
                || duration1.getText().isBlank()) {
            System.out.println("Input the details mf");
            return;
        } else {
            String firstName = FirstName11.getText();
            String lastName = LastName11.getText();
            String guestName = firstName + " " + lastName;
            String phone_number = phoneNumber11.getText();

            LocalDate checkInDate = arrivalDate.getValue();
            int stayDurationNights = Integer.parseInt(duration1.getText());
            int stayDurationHours = 0;

            addBooking(guestName, phone_number, checkInDate, stayDurationNights);

        }
        clearAll();
    }

    @FXML
    void ChecktheRoomsWithTheCKDate(ActionEvent event) {

    }

    @FXML
    void SubmitAction(ActionEvent event) {
        if (selectedRoomNo == null || selectedRoomNo.isEmpty()) {
            System.out.println("Please select a room before booking.");
            return;
        }

        if (FirstName.getText().isBlank() || LastName.getText().isBlank() || phoneNumber.getText().isBlank()
                || idORnrc.getText().isBlank() || Email.getText().isBlank() || duration.getText().isBlank()) {
            System.out.println("Input the details mf");
        } else {
            String firstName = FirstName.getText();
            String lastName = LastName.getText();
            String guestName = firstName + " " + lastName;
            String phone_number = phoneNumber.getText();
            String guestId = idORnrc.getText();
            String email = Email.getText();
            LocalDate checkInDate = LocalDate.now();
            int stayDurationNights = Integer.parseInt(duration.getText());
            int stayDurationHours = 0;

            if (FirstName.getText().isBlank() || LastName.getText().isBlank() || phoneNumber.getText().isBlank()
                    || idORnrc.getText().isBlank() || Email.getText().isBlank() || duration.getText().isBlank()) {
                System.out.println("Input the details mf");
                return;
            }

            addCheckIn(guestName, phone_number,
                    checkInDate, stayDurationNights,
                    email, guestId);

        }

    }

    private void clearAll() {
        FirstName.setText("");
        LastName.setText("");
        idORnrc.setText("");
        phoneNumber.setText("");
        duration.setText("");
        Email.setText("");
    }


    @FXML
    void SwitchToBookingDetails(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "staff", "booking");
    }

    @FXML
    void SwitchToServices(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "staff", "services");
    }


    @FXML
    void SwitchToSetting(ActionEvent event) throws IOException {
        ssc.toSettings(event, (Stage) logout.getScene().getWindow());
    }

    @FXML
    void ResetAction(ActionEvent event) {
        FirstName.setText("");
        LastName.setText("");
        idORnrc.setText("");
        phoneNumber.setText("");
        duration.setText("");
        FirstName.setText("");
        LastName.setText("");
        phoneNumber.setText("");
        deposit11.setText("");
    }

    @FXML
    void logoutAct(ActionEvent e) {
        logoutController.logout(e);
    }

    private void addCheckIn(String guestName, String phoneNumber,
                            LocalDate checkInDate, int stayDurationNights,
                            String email, String guestId) {

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        Stage owner = (Stage) logout.getScene().getWindow();
        modalStage.initOwner(owner);
        modalStage.initStyle(StageStyle.UNDECORATED);
        modalStage.initStyle(StageStyle.TRANSPARENT);

        Text guestNameText = new Text("Guest Name");
        guestNameText.setFont(new Font("Arial", 16));
        guestNameText.setFill(Color.DARKSLATEGRAY);
        Text guestNameField = new Text(guestName);
        guestNameField.setFont(new Font("Arial", 14));
        guestNameField.setFill(Color.DIMGRAY);
        VBox guestNameBox = new VBox(guestNameText, guestNameField);
        guestNameBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

        Text phoneNumberText = new Text("Phone Number");
        phoneNumberText.setFont(new Font("Arial", 16));
        phoneNumberText.setFill(Color.DARKSLATEGRAY);
        Text phoneNumberField = new Text(phoneNumber);
        phoneNumberField.setFont(new Font("Arial", 14));
        phoneNumberField.setFill(Color.DIMGRAY);
        VBox phoneNumberBox = new VBox(phoneNumberText, phoneNumberField);
        phoneNumberBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

        Text emailText = new Text("Email");
        emailText.setFont(new Font("Arial", 16));
        emailText.setFill(Color.DARKSLATEGRAY);
        Text emailField = new Text(email);
        emailField.setFont(new Font("Arial", 14));
        emailField.setFill(Color.DIMGRAY);
        VBox emailBox = new VBox(emailText, emailField);
        emailBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

        Text guestIdText = new Text("Guest ID");
        guestIdText.setFont(new Font("Arial", 16));
        guestIdText.setFill(Color.DARKSLATEGRAY);
        Text guestIdField = new Text(guestId);
        guestIdField.setFont(new Font("Arial", 14));
        guestIdField.setFill(Color.DIMGRAY);
        VBox guestIdBox = new VBox(guestIdText, guestIdField);
        guestIdBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

        Text checkInDateText = new Text("Check-In Date");
        checkInDateText.setFont(new Font("Arial", 16));
        checkInDateText.setFill(Color.DARKSLATEGRAY);
        Text checkInDateField = new Text(checkInDate.toString());
        checkInDateField.setFont(new Font("Arial", 14));
        checkInDateField.setFill(Color.DIMGRAY);
        VBox checkInDateBox = new VBox(checkInDateText, checkInDateField);
        checkInDateBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

        Text stayDurationText = new Text("Stay Duration (Nights)");
        stayDurationText.setFont(new Font("Arial", 16));
        stayDurationText.setFill(Color.DARKSLATEGRAY);
        Text stayDurationField = new Text(String.valueOf(stayDurationNights));
        stayDurationField.setFont(new Font("Arial", 14));
        stayDurationField.setFill(Color.DIMGRAY);
        VBox stayDurationBox = new VBox(stayDurationText, stayDurationField);
        stayDurationBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

        VBox bookingDetailsPane = new VBox(guestNameBox, phoneNumberBox, guestIdBox, emailBox, checkInDateBox, stayDurationBox);

        bookingDetailsPane.setStyle(
                "-fx-spacing: 15; " +
                        "-fx-padding: 30; " +
                        "-fx-background-color: #f7f7f7; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-width: 1; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 10, 0, 0, 3);"
        );

        BorderPane modalRoot = new BorderPane();

        Text modalTitle = new Text("Add Booking/Check-In");
        modalTitle.setFont(new Font("Arial", 28));
        modalTitle.setFill(Color.DARKGREEN);

        Text modalHint = new Text("Are you sure you want to check in this guest?");
        modalHint.setFont(new Font("Arial", 14));
        modalHint.setFill(Color.DIMGRAY);

        VBox topBox = new VBox(modalTitle, modalHint);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-spacing: 5; -fx-padding: 10px;");

        modalRoot.setTop(topBox);
        BorderPane.setAlignment(topBox, Pos.CENTER);
        modalRoot.setCenter(bookingDetailsPane);

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
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 15px;");

        modalRoot.setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        Scene modalScene = new Scene(modalRoot, 425, 645);
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
            addCheckInToDB(guestName, phoneNumber,
                    checkInDate, stayDurationNights,
                    email, guestId);
            clearAll();
            modalStage.close();
        });
    }

    private void addCheckInToDB(String guestName, String phoneNumber,
                                LocalDate checkInDate, int stayDurationNights,
                                String email, String guestId) {

        String checkInSql = "CALL add_checkIn(?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             CallableStatement psmtCheckIn = con.prepareCall(checkInSql)) {

            con.setAutoCommit(false);

            psmtCheckIn.setString(1, guestName);
            psmtCheckIn.setString(2, phoneNumber);
            psmtCheckIn.setString(3, guestId);
            psmtCheckIn.setString(4, email);
            psmtCheckIn.setString(5, selectedRoomNo);
            psmtCheckIn.setDate(6, java.sql.Date.valueOf(checkInDate));
            psmtCheckIn.setInt(7, stayDurationNights);
            psmtCheckIn.setInt(8, 0);

            int checkInResult = psmtCheckIn.executeUpdate();

            if (checkInResult > 0) {
                con.commit();
                System.out.println("Check-in processed successfully.");
                AddingRooms(null, 0, null);
                notificationManager.showNotification("Successfully Checked In",
                        "Success", (Stage) room_filter.getScene().getWindow());
            } else {
                notificationManager.showNotification("Error During Checked In",
                        "failure", (Stage) room_filter.getScene().getWindow());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage() + " Error occurred during check-in.");
        }
    }

    private void addBooking(String guestName, String phoneNumber,
                            LocalDate checkInDate, int stayDurationNights
    ) {

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        Stage owner = (Stage) logout.getScene().getWindow();
        modalStage.initOwner(owner);
        modalStage.initStyle(StageStyle.UNDECORATED);
        modalStage.initStyle(StageStyle.TRANSPARENT);

        Text guestNameText = new Text("Guest Name");
        guestNameText.setFont(new Font("Arial", 16));
        guestNameText.setFill(Color.DARKSLATEGRAY);
        Text guestNameField = new Text(guestName);
        guestNameField.setFont(new Font("Arial", 14));
        guestNameField.setFill(Color.DIMGRAY);
        VBox guestNameBox = new VBox(guestNameText, guestNameField);
        guestNameBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

        Text rnTxt = new Text("Room No");
        rnTxt.setFont(new Font("Arial", 16));
        rnTxt.setFill(Color.DARKSLATEGRAY);
        Text rnText = new Text(selectedRoomNo);
        rnText.setFont(new Font("Arial", 14));
        rnText.setFill(Color.DIMGRAY);
        VBox roomBox = new VBox(rnTxt, rnText);
        roomBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

        Text phoneNumberText = new Text("Phone Number");
        phoneNumberText.setFont(new Font("Arial", 16));
        phoneNumberText.setFill(Color.DARKSLATEGRAY);
        Text phoneNumberField = new Text(phoneNumber);
        phoneNumberField.setFont(new Font("Arial", 14));
        phoneNumberField.setFill(Color.DIMGRAY);
        VBox phoneNumberBox = new VBox(phoneNumberText, phoneNumberField);
        phoneNumberBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");


        Text checkInDateText = new Text("Check-In Date");
        checkInDateText.setFont(new Font("Arial", 16));
        checkInDateText.setFill(Color.DARKSLATEGRAY);
        Text checkInDateField = new Text(checkInDate.toString());
        checkInDateField.setFont(new Font("Arial", 14));
        checkInDateField.setFill(Color.DIMGRAY);
        VBox checkInDateBox = new VBox(checkInDateText, checkInDateField);
        checkInDateBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

        Text stayDurationText = new Text("Stay Duration (Nights)");
        stayDurationText.setFont(new Font("Arial", 16));
        stayDurationText.setFill(Color.DARKSLATEGRAY);
        Text stayDurationField = new Text(String.valueOf(stayDurationNights));
        stayDurationField.setFont(new Font("Arial", 14));
        stayDurationField.setFill(Color.DIMGRAY);
        VBox stayDurationBox = new VBox(stayDurationText, stayDurationField);
        stayDurationBox.setStyle("-fx-spacing: 8; -fx-padding: 10px;");

        VBox bookingDetailsPane = new VBox(roomBox, guestNameBox, phoneNumberBox, checkInDateBox, stayDurationBox);

        bookingDetailsPane.setStyle(
                "-fx-spacing: 15; " +
                        "-fx-padding: 30; " +
                        "-fx-background-color: #f7f7f7; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-width: 1; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 10, 0, 0, 3);"
        );

        BorderPane modalRoot = new BorderPane();

        Text modalTitle = new Text("Add Booking/Check-In");
        modalTitle.setFont(new Font("Arial", 28));
        modalTitle.setFill(Color.DARKGREEN);

        Text modalHint = new Text("Are you sure you want to check in this guest?");
        modalHint.setFont(new Font("Arial", 14));
        modalHint.setFill(Color.DIMGRAY);

        VBox topBox = new VBox(modalTitle, modalHint);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-spacing: 5; -fx-padding: 10px;");

        modalRoot.setTop(topBox);
        BorderPane.setAlignment(topBox, Pos.CENTER);
        modalRoot.setCenter(bookingDetailsPane);

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
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 15px;");

        modalRoot.setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        Scene modalScene = new Scene(modalRoot, 425, 585);
        modalStage.setScene(modalScene);
        modalScene.setFill(Color.TRANSPARENT);
        modalStage.setResizable(false);
        modalStage.show();
        modalRoot.setStyle("-fx-background-color: white;" + "-fx-background-radius: 2.5em;");

        modalStage.setY((owner.getY() + owner.getHeight() / 2d) - (modalScene.getHeight() / 2d));

        cancelButton.setOnAction(e -> {

            modalStage.close();
        });
        confirmButton.setOnAction(e -> {
            addBookingToDB(guestName, phoneNumber,
                    checkInDate, stayDurationNights);
            clearAll();
            modalStage.close();
        });
    }


    private void addBookingToDB(String guestName, String phoneNumber,
                                LocalDate checkInDate, int stayDurationNights) {

        String checkInSql = "CALL add_booking(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             CallableStatement psmtCheckIn = con.prepareCall(checkInSql)) {

            con.setAutoCommit(false);

            psmtCheckIn.setString(2, guestName);
            psmtCheckIn.setString(3, phoneNumber);
            psmtCheckIn.setString(5, "-");
            psmtCheckIn.setString(4, "-");
            psmtCheckIn.setString(1, selectedRoomNo);
            psmtCheckIn.setDate(6, java.sql.Date.valueOf(checkInDate));
            psmtCheckIn.setInt(7, stayDurationNights);
            psmtCheckIn.setInt(8, 0);

            int checkInResult = psmtCheckIn.executeUpdate();

            if (checkInResult > 0) {
                con.commit();
                System.out.println("Check-in processed successfully.");
                AddingRooms(null, 0, null);
                notificationManager.showNotification("Successfully Booked",
                        "success", (Stage) logout.getScene().getWindow());
            } else {
                System.out.println("Error during check-in.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage() + " Error occurred during check-in.");
        }
    }

}