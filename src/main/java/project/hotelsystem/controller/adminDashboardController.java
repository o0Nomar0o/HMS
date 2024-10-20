package project.hotelsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.hotelsystem.settings.invoiceSettings;
import java.io.IOException;
import java.util.prefs.Preferences;

import project.hotelsystem.settings.userSettings;

public class adminDashboardController {

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

    private userSettings ts = userSettings.getInstance();
    private switchSceneController ssc = new switchSceneController();

    @FXML
    void initialize(){
        Preferences p = ts.getNodePreference();
        ts.getUid();
    }

    @FXML
    void logout(ActionEvent event) {
        logoutController.logout(event);
    }


    @FXML
    void switchtosetting(ActionEvent event) throws IOException {
        ssc.toSettings(event,(Stage)logout.getScene().getWindow());
    }


    @FXML
    void switchtorooms(ActionEvent event) throws IOException{
       ssc.swithcTo(event, (Stage)logout.getScene().getWindow(),"admin","rooms");

    }

    @FXML
    void switchtobookings(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage)logout.getScene().getWindow(), "admin","booking");
    }

    @FXML
    void switchtoservices(ActionEvent event) throws IOException {
        ssc.swithcTo(event, (Stage)logout.getScene().getWindow(), "admin", "services");
    }


}







