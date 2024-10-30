package project.hotelsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import project.hotelsystem.database.controller.*;
import project.hotelsystem.database.models.*;
import project.hotelsystem.settings.*;
import project.hotelsystem.util.dropdownManager;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Rooms Reservation/Booking Controller class
 *
 * @author Khant Zin Hein
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
        AddingRooms(null, 0);
        updateRoomCounts();

        //initialize dropdown box for floor and room type
        floor_filter.setUserData("All");
        room_filter.setUserData("All");

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
                        bounds.getMinX(), bounds.getMinY()-280);
            }
        });


    }

    public void AddingRooms(String roomIdToSearch, int floorFilter) {

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
            if (floorFilter != 0) {
                query += " AND room.floor = ?";
            }

            String floor_f = "All";
            if (!floor_f.matches("All")) {
                query += " AND room_type.description = ? ";
            }

            PreparedStatement stmt = conn.prepareStatement(query);

            if (!floor_f.matches("All")) stmt.setString(2, floor_f);

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
                        AddingRooms(null, 0);

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
        AddingRooms(enteredRoomId, 0);  // Show filtered rooms based on input
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
        AddingRooms("", 0);  // Call the AddingRooms method to update the room list based on the selected filters
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
        AddingRooms("", floor);
    }


    @FXML
    void BookingAction(ActionEvent event) {
        if (selectedRoomNo == null || selectedRoomNo.isEmpty()) {
            System.out.println("Please select a room before booking.");
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

            String checkInSql = "CALL add_booking(?, ?, ?, ?, ?, ?, ?, ?)";

            if (FirstName11.getText().isBlank() || LastName11.getText().isBlank()
                    || phoneNumber11.getText().isBlank() || duration1.getText().isBlank()) {
                System.out.println("Input the details mf");
            }

            try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                 CallableStatement psmtCheckIn = con.prepareCall(checkInSql)) {

                con.setAutoCommit(false);

                psmtCheckIn.setString(2, guestName);
                psmtCheckIn.setString(3, phone_number);
                psmtCheckIn.setString(5, "-");
                psmtCheckIn.setString(4, "-");
                psmtCheckIn.setString(1, selectedRoomNo);
                psmtCheckIn.setDate(6, java.sql.Date.valueOf(checkInDate));
                psmtCheckIn.setInt(7, stayDurationNights);
                psmtCheckIn.setInt(8, stayDurationHours);

                int checkInResult = psmtCheckIn.executeUpdate();

                if (checkInResult > 0) {
                    con.commit();
                    System.out.println("Check-in processed successfully.");
                    AddingRooms(null, 0);
                } else {
                    System.out.println("Error during check-in.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage() + " Error occurred during check-in.");
            }
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

            String checkInSql = "CALL add_checkIn(?, ?, ?, ?, ?, ?, ?, ?)";

            if (FirstName.getText().isBlank() || LastName.getText().isBlank() || phoneNumber.getText().isBlank()
                    || idORnrc.getText().isBlank() || Email.getText().isBlank() || duration.getText().isBlank()) {
                System.out.println("Input the details mf");
            }

            try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                 CallableStatement psmtCheckIn = con.prepareCall(checkInSql)) {

                con.setAutoCommit(false);

                psmtCheckIn.setString(1, guestName);
                psmtCheckIn.setString(2, phone_number);
                psmtCheckIn.setString(3, guestId);
                psmtCheckIn.setString(4, email);
                psmtCheckIn.setString(5, selectedRoomNo);
                psmtCheckIn.setDate(6, java.sql.Date.valueOf(checkInDate));
                psmtCheckIn.setInt(7, stayDurationNights);
                psmtCheckIn.setInt(8, stayDurationHours);

                int checkInResult = psmtCheckIn.executeUpdate();

                if (checkInResult > 0) {
                    con.commit();
                    System.out.println("Check-in processed successfully.");
                    AddingRooms(null, 0);
                } else {
                    System.out.println("Error during check-in.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage() + " Error occurred during check-in.");
            }
        }
        clearAll();
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



}