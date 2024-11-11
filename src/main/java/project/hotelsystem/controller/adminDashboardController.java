package project.hotelsystem.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import project.hotelsystem.database.controller.bookingController;
import project.hotelsystem.database.controller.revenueController;
import project.hotelsystem.database.models.booking;
import project.hotelsystem.settings.userSettings;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    private StackedAreaChart<String, Number> year_chart;
    @FXML
    private LineChart<String, Number> month_chart;
    @FXML
    private CategoryAxis months;

    @FXML
    private NumberAxis rev;
    @FXML
    private Button switch_chart_view;
    @FXML
    private Button logout;

    @FXML
    private TableColumn<?, ?> reservation;
    @FXML
    private TableColumn<?, ?> reservation1;
    @FXML
    private Button setting;

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
        switch_chart_view.setOnAction(e -> toggleChart());

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenue");
        month_chart.setTitle("Monthly Revenue");

        RevenueDataService monthlyService = new RevenueDataService();
        monthlyService.setOnSucceeded(event -> {
            Map<Integer, List<XYChart.Data<String, Number>>> data = monthlyService.getValue();
            populateChart(data);
        });

        monthlyService.start();

        YearlyRevenueService yearlyService = new YearlyRevenueService();
        yearlyService.setOnSucceeded(event -> populateYearlyChart(yearlyService.getValue()));
        yearlyService.start();

    }

    private void toggleChart() {
        if (month_chart.isVisible()) {
            month_chart.setVisible(false);
            year_chart.setVisible(true);
            switch_chart_view.setText("Switch to Monthly Chart");
        } else {
            year_chart.setVisible(false);
            month_chart.setVisible(true);
            switch_chart_view.setText("Switch to Yearly Chart");
        }
    }

    private void populateChart(Map<Integer, List<XYChart.Data<String, Number>>> data) {
        month_chart.getData().clear();
        for (Map.Entry<Integer, List<XYChart.Data<String, Number>>> entry : data.entrySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(String.valueOf(entry.getKey()));
            series.getData().addAll(entry.getValue());
            month_chart.getData().add(series);
        }
    }


    private static class RevenueDataService extends Service<Map<Integer, List<XYChart.Data<String, Number>>>> {
        @Override
        protected Task<Map<Integer, List<XYChart.Data<String, Number>>>> createTask() {
            return new Task<>() {
                @Override
                protected Map<Integer, List<XYChart.Data<String, Number>>> call() {
                    return revenueController.getMonthly();
                }
            };
        }
    }

    private void populateYearlyChart(List<XYChart.Data<String, Number>> data) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue per Year");
        series.getData().addAll(data);
        year_chart.getData().add(series);
    }


    private static class YearlyRevenueService extends Service<List<XYChart.Data<String, Number>>> {
        @Override
        protected Task<List<XYChart.Data<String, Number>>> createTask() {
            return new Task<>() {
                @Override
                protected List<XYChart.Data<String, Number>> call() {
                    return revenueController.getYearRev();
                }
            };
        }
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







