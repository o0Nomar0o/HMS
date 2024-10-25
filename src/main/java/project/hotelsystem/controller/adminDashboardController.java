package project.hotelsystem.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.hotelsystem.database.controller.bookingController;
import project.hotelsystem.database.models.booking;
import project.hotelsystem.settings.userSettings;

import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * Dashboard Controller class
 *
 * @author Nomar
 * @author Kaung Thant Thu
 */

public class adminDashboardController {

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
    @FXML
    private Button bookings;
    @FXML
    private Button dashboard;
    @FXML
    private Button services;
    @FXML
    private LineChart<?, ?> linechard;
    @FXML
    private Button logout;
    @FXML
    private Text monthoverview;
    @FXML
    private PieChart piechard;
    @FXML
    private PieChart piechard1;
    @FXML
    private TableColumn<?, ?> reservation;
    @FXML
    private TableColumn<?, ?> reservation1;
    @FXML
    private Button setting;
    @FXML
    private Text yearoverview;
    @FXML
    private TableView<booking> res_table;
    @FXML
    private TableColumn<booking, String> room_col;
    @FXML
    private TableColumn<booking, String> guest_col;
    @FXML
    private TableColumn<booking, String> check_in;
    private final userSettings ts = userSettings.getInstance();
    private final switchSceneController ssc = new switchSceneController();

    @FXML
    void initialize() {

        Preferences p = ts.getNodePreference();
        ts.getUid();

        room_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoom().getRoom_no()));
        guest_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGuest().getGuest_name()));
        check_in.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCheck_in() + ""));

        bookingService.valueProperty().addListener((obs, oldData, newData) -> {
            res_table.getItems().clear();
            res_table.setPlaceholder(new Label("Loading booking data..."));
            if (newData != null && !newData.isEmpty()) {
                List<booking> filteredBookings = newData.stream()
                        .filter(b -> !b.getBooking_status().matches("Checked-Out")
                                && !b.getBooking_status().matches("Arrived")
                                && !b.getBooking_status().matches("Cancelled"))
                        .collect(Collectors.toList());

                res_table.getItems().addAll(filteredBookings);

                return;
            }
            res_table.setPlaceholder(new Label("No bookings found."));
        });

        bookingService.start();
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
    void switchtorooms(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "rooms");

    }

    @FXML
    void switchtobookings(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "booking");
    }

    @FXML
    void switchtoservices(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "services");
    }


}







