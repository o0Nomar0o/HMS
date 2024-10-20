package project.hotelsystem.controller;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.hotelsystem.settings.invoiceSettings;
import project.hotelsystem.database.controller.bookingController;
import project.hotelsystem.util.notificationManager;

public class StaffRoomDetailsController {

	@FXML
	private Button bookings_button;

	@FXML
	private Button logout;

	@FXML
	private Button orders_button;

	@FXML
	private Button rooms_button;

	@FXML
	private Button services_button;

	@FXML
	private Button settings;

	@FXML
	private Button BookingDetailsbtn;

	@FXML
	private TextField FirstName;

	@FXML
	private TextField LastName;

	@FXML
	private Button LogOutbtn;

	@FXML
	private Text NumberOfAR;

	@FXML
	private Text NumberOfBR;

	@FXML
	private AnchorPane RoomShowBody;

	@FXML
	private Button RoomDetailsbtn;

	@FXML
	private Button Servicesbtn;

	@FXML
	private Button Settingbtn;

	@FXML
	private Text StaffName;

	@FXML
	private DatePicker ToCheckWithCheckOutDate;

	@FXML
	private TextField deposit;

	@FXML
	private TextField duration;

	@FXML
	private Text floorText;

	@FXML
	private TextField idORnrc;

	@FXML
	private TextField phoneNumber;

	@FXML
	private Text roomID;

	@FXML
	private Text roomPrice;

	@FXML
	private Text roomType;

	@FXML
	private TextField FirstName1;

	@FXML
	private TextField LastName1;

	@FXML
	private TextField phoneNumber1;

	@FXML
	private TextField total;

	@FXML
	private TextField deposit1;

	@FXML
	private TextField searchField;

	@FXML
	private CheckBox singleRoomCheckBox;

	@FXML
	private CheckBox doubleRoomCheckBox;

	@FXML
	private Text roomPrice1;

	@FXML
	private ComboBox<String> selectRoomscheckBox;

	@FXML
	private ComboBox<String> selectRoomscheckBox1;

	@FXML
	private TextField NoOfRooms;

	@FXML
	private TextField NoOfRooms1;

	@FXML
	private TextField Email;

	@FXML
	private AnchorPane body;

	private String selectedRoomNo;
	switchSceneController ssc = new switchSceneController();


	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/snowy_resort";//add your database_url
	private static final String DB_USER = "root";//add your user name
	private static final String DB_PASSWORD = "12345678";//add your password


	@FXML
	public void initialize() {
		orders_button.setDisable(true);
		AddingRooms(null,0);
		updateRoomCounts();
		selectRoomscheckBox.getItems().addAll("Select Rooms","Single","Double");
		selectRoomscheckBox1.getItems().addAll("Select Rooms","Single","Double");
		NoOfRooms.setText("0");
		NoOfRooms1.setText("0");
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
			if (singleRoomCheckBox.isSelected() && !doubleRoomCheckBox.isSelected()) {
				query += " AND room_type.description = 'Single'";
			} else if (doubleRoomCheckBox.isSelected() && !singleRoomCheckBox.isSelected()) {
				query += " AND room_type.description = 'Double'";
			}

			PreparedStatement stmt = conn.prepareStatement(query);
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
				String price_per_hour=rs.getString("price_per_hour");

				Button roomButton = new Button("Room " + roomId);
				roomButton.setPrefSize(100, 50);

				String commonStyles = "-fx-background-radius: 20;" +
						"-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.75), 10, 0.0, 5, 5);";

				@SuppressWarnings("unused")
				String focusedStyles = "-fx-background-color: #7b7d7d;" +
						"-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 1.0), 15, 0.0, 5, 5);";

				// Color coding based on room status
				switch (status) {
					case "Unavailable":
						roomButton.setStyle(commonStyles + "-fx-background-color: red;");
						break;
					case "Available":
						roomButton.setStyle(commonStyles + "-fx-background-color: forestgreen;");
						break;
					case "Booked":
						roomButton.setStyle(commonStyles + "-fx-background-color: yellow;");
						break;
				}
				roomButton.setOnAction(e -> {
					roomID.setText(roomId);
					this.roomType.setText(roomType);
					floorText.setText(floor);
					roomPrice.setText(price+" $");
					this.roomPrice1.setText(price_per_hour+" $");
					selectedRoomNo = roomId;
					if ("Unavailable".equals(status)) {
						fetchAndDisplayCustomerDetails(roomId);}
				});

				//roomGridPane.add(roomButton, col, row);
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
		String query = "SELECT customer.customer_name, customer.phone_no, customer.email, customer.id_card, booking.booking_id " +
				"FROM customer " +
				"JOIN booking ON customer.customer_id = booking.customer_id " +
				"JOIN booking_room_detail ON booking.booking_id = booking_room_detail.booking_id " +
				"JOIN room ON booking_room_detail.room_no = room.room_no " +
				"WHERE room.room_no = ? AND booking_room_detail.booking_status = 'Arrived'";

		try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
			 PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, roomId); // Set the room number

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String fullName = rs.getString("customer_name");
				String phoneNumber = rs.getString("phone_no");
				String idCard = rs.getString("id_card");
				String email = rs.getString("email");
				String bkid = rs.getString("booking_id");

				// Create a new pane to display these details
				AnchorPane customerDetailsPane = new AnchorPane();
				customerDetailsPane.setPrefSize(400, 300); // Set size of the pane
				customerDetailsPane.setStyle("-fx-background-color: white;"
						+ "-fx-background-radius: 10;");

				// Add Labels and Text for each data
				Text fullNameLabel = new Text("Full Name: " + fullName);
				Text phoneLabel = new Text("Phone Number: " + phoneNumber);
				Text idCardLabel = new Text("ID Card: " + idCard);
				Text emailLabel = new Text("Email: " + email);

				// Create Buttons
				Button cancelButton = new Button("Cancel");
				Button checkOutButton = new Button("Check Out");


				// Position elements in the pane
				AnchorPane.setTopAnchor(fullNameLabel, 10.0);
				AnchorPane.setLeftAnchor(fullNameLabel, 10.0);

				AnchorPane.setTopAnchor(phoneLabel, 40.0);
				AnchorPane.setLeftAnchor(phoneLabel, 10.0);

				AnchorPane.setTopAnchor(idCardLabel, 70.0);
				AnchorPane.setLeftAnchor(idCardLabel, 10.0);

				AnchorPane.setTopAnchor(emailLabel, 100.0);
				AnchorPane.setLeftAnchor(emailLabel, 10.0);

				AnchorPane.setTopAnchor(cancelButton, 140.0);
				AnchorPane.setLeftAnchor(cancelButton, 10.0);

				AnchorPane.setTopAnchor(checkOutButton, 140.0);
				AnchorPane.setLeftAnchor(checkOutButton, 100.0);

				// Add all elements to the pane
				customerDetailsPane.getChildren().addAll(fullNameLabel, phoneLabel, idCardLabel, emailLabel, cancelButton, checkOutButton);

				AnchorPane overlayPane = new AnchorPane();
				overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
				overlayPane.setPrefSize(body.getWidth(), body.getHeight());
				body.getChildren().add(overlayPane);


				double paneWidth = customerDetailsPane.getPrefWidth();
				double paneHeight = customerDetailsPane.getPrefHeight();
				double bodyWidth = body.getWidth();
				double bodyHeight = body.getHeight();

				double centerX = (bodyWidth - paneWidth) / 2;
				double centerY = (bodyHeight - paneHeight) / 2;

				customerDetailsPane.setLayoutX(centerX);
				customerDetailsPane.setLayoutY(centerY);


				body.getChildren().add(customerDetailsPane);


				cancelButton.setOnAction(e -> {
					body.getChildren().removeAll(overlayPane, customerDetailsPane);
				});

				checkOutButton.setOnAction(e -> {
					RoomShowBody.setOpacity(1.0); // Restore the opacity
					if(bookingController.checkOut(bkid, roomId)){
						ivs.openPdfModal(bkid);
						notificationManager.showNotification("Check-Out","success",(Stage)logout.getScene().getWindow());
						AddingRooms(null,0);
					}

				});

			} else {
				System.out.println("No ongoing booking found for room " + roomId);
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
			case "Floor1": floor = 1; break;
			case "Floor2": floor = 2; break;
			case "Floor3": floor = 3; break;
			case "Floor4": floor = 4; break;
			case "All": floor = 0; break;
		}
		AddingRooms("", floor);
	}


	@FXML
	void BookingAction(ActionEvent event) {

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

		// Collect guest information from the form
		String firstName = FirstName.getText();
		String lastName = LastName.getText();
		String guestName = firstName + " " + lastName;
		String phone_number = phoneNumber.getText();
		String guestId = idORnrc.getText();
		String email = Email.getText();
		LocalDate checkInDate = LocalDate.now(); // Assuming check-in is now (replace with actual if needed)
		int stayDurationNights = Integer.parseInt(duration.getText());
		int stayDurationHours = 0; // Set to 0 unless you have an additional input for hours

		String sql = "CALL add_booking(?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
			 CallableStatement psmt = con.prepareCall(sql)) {
			con.setAutoCommit(false);
			// Set procedure parameters
			psmt.setString(1, selectedRoomNo);
			psmt.setString(2, guestName);
			psmt.setString(3, phone_number);
			psmt.setString(4, guestId);
			psmt.setString(5,email);
			psmt.setDate(6, java.sql.Date.valueOf(checkInDate));
			psmt.setInt(7, stayDurationNights);
			psmt.setInt(8, stayDurationHours);

			// Execute the stored procedure
			int result = psmt.executeUpdate();

			// Commit the transaction if successful
			if (result > 0) {
				con.commit();
				System.out.println("Booking added successfully.");

			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage() + " Error occurred during booking.");

		}
	}



	@FXML
	void SwitchToBookingDetails(ActionEvent event) throws IOException {
		ssc.swithcTo(event,(Stage)logout.getScene().getWindow(),"staff","booking");
	}

	@FXML
	void SwitchToLogInPage(ActionEvent event) {

	}


	@FXML
	void SwitchToServices(ActionEvent event) throws IOException {
		ssc.swithcTo(event,(Stage)logout.getScene().getWindow(),"staff","services");
	}
	@FXML
	void increaseAction(ActionEvent event) {


	}
	@FXML
	void decreaseAction(ActionEvent event) {

	}
	@FXML
	void increaseAction1(ActionEvent event) {

	}
	@FXML
	void decreaseAction1(ActionEvent event) {

	}

	@FXML
	void SwitchToSetting(ActionEvent event) throws IOException {
		ssc.toSettings(event, (Stage)logout.getScene().getWindow());
	}
	@FXML
	void ResetAction(ActionEvent event) {
		FirstName.setText("");
		LastName.setText("");
		idORnrc.setText("");
		phoneNumber.setText("");
		duration.setText("");
		deposit.setText("");
		FirstName1.setText("");
		LastName1.setText("");
		phoneNumber1.setText("");
		total.setText("");
		deposit1.setText("");
	}
	@FXML
	void logoutAct(ActionEvent e){
		logoutController.logout(e);
	}

}