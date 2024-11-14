package project.hotelsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.hotelsystem.database.controller.bookingController;
import project.hotelsystem.database.controller.invoiceGeneratorController;
import project.hotelsystem.database.models.InvoiceData;
import project.hotelsystem.database.models.bookingDetails;
import project.hotelsystem.database.models.order_food;
import project.hotelsystem.database.models.order_service;
import project.hotelsystem.settings.loaderSettings;
import project.hotelsystem.settings.userSettings;

import java.io.IOException;
import java.util.List;

public class adminBookingController {

    @FXML
    private Button bookings_button;

    @FXML
    private Button dashboard_button;

    @FXML
    private HBox floorFilter;

    @FXML
    private Button logout;

    @FXML
    private VBox bookingView;

    @FXML
    private Button rooms_button;

    @FXML
    private TextField searchField;

    @FXML
    private Button services_button;

    @FXML
    private Button settings;

    switchSceneController ssc = new switchSceneController();
    userSettings uss = userSettings.getInstance();
    private List<bookingDetails> bkd;

    @FXML
    void initialize() {

        bookingView.setStyle("""
                -fx-spacing: 25px;
                -fx-padding: 10px;
                """);
        generateHistory();

    }

    private void generateHistory() {
        List<bookingDetails> bkd = bookingController.getBookingDetails();
        for (bookingDetails detail : bkd) {

            create_bk_history_layout(detail);

        }
    }

    private void create_bk_history_layout(bookingDetails bkd) {

        String bkid = bkd.getBk_booking().getBooking_id();
        String customer_name = bkd.getBk_customer().getGuest_name();
        String room_no = bkd.getBk_room().getRoom_no();
        Label bk_details = new Label(String.format("%s | %s | %s", bkid, customer_name, room_no));

        Button service_history = btn_style("Services");
        Button food_history = btn_style("Food Orders");

        food_history.setUserData(bkid);
        service_history.setUserData(bkid);

        food_history.setOnAction(this::food_history_viewer);
        service_history.setOnAction(this::service_history_viewer);

        VBox order_history = new VBox(service_history, food_history);
        order_history.setStyle("-fx-spacing: 5px;");
        order_history.setAlignment(Pos.CENTER);

        Label lbl_check_in = new Label("Check-in");
        Label lbl_check_in_time = new Label(bkd.getCheck_in().toString());
        Label lbl_check_in_payment = new Label(bkd.getCi_payment_method());
        VBox check_in_box = new VBox(lbl_check_in, lbl_check_in_time, lbl_check_in_payment);

        Label lbl_check_out = new Label("Check-out");
        Label lbl_check_out_time = new Label(bkd.getCheck_out().toString());
        Label lbl_check_out_payment = new Label(bkd.getCo_payment_method());
        VBox check_out_box = new VBox(lbl_check_out, lbl_check_out_time, lbl_check_out_payment);

        HBox essential_details = new HBox(order_history, check_in_box, check_out_box);
        essential_details.setStyle("""
                -fx-spacing: 25px;
                """);

        VBox bk_root = new VBox(bk_details, essential_details);
        bk_root.setStyle("""
                -fx-spacing: 25px;
                -fx-padding: 10px;
                -fx-background-color: #FFFFFF;
                -fx-background-radius: 10px;
                -fx-border-color: #D9D9D9;
                -fx-border-width: 1px;
                -fx-border-radius: 10px;
                """);

        bookingView.getChildren().add(bk_root);

    }

    private String layout_styling() {
        uss.loadWindowsSettings();

        String style;

        if (uss.getTheme().matches("dark")) {
            style = "-fx-";
            return style;
        }

        style = "-fx-";

        return style;
    }

    private Button btn_style(String type) {

        Button btn = new Button(type);
        String style;
        if (type.matches("Services"))
            style = "-fx-background-color: #CEC6F7";
        else
            style = "-fx-background-color: #79A576";

        btn.setStyle(style);

        return btn;
    }


    @FXML
    void SearchRoom(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();

        bookingView.getChildren().clear();

        List<bookingDetails> filteredBookings = bkd.stream()
                .filter(detail -> detail.getBk_room().getRoom_no().toLowerCase().contains(searchText) ||
                        detail.getBk_customer().getGuest_name().toLowerCase().contains(searchText))
                .toList();

        for (bookingDetails detail : filteredBookings) {
            create_bk_history_layout(detail);
        }
    }

    InvoiceData currData;

    void food_history_viewer(ActionEvent e) {

        Button curr = (Button) e.getSource();
        String data = curr.getUserData().toString();
        if (currData == null || !currData.getBooking_id().matches(data)) {
            currData = invoiceGeneratorController.getData(data);
        }

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        Stage owner = (Stage) curr.getScene().getWindow();
        modalStage.initOwner(owner);
        modalStage.initStyle(StageStyle.UNDECORATED);
        modalStage.initStyle(StageStyle.TRANSPARENT);

        loaderSettings.applyDimmingEffect(owner);

        Text modalTitle = new Text("Food History");
        modalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        modalTitle.setFill(Color.DARKSLATEGRAY);

        VBox foodListVBox = new VBox();
        foodListVBox.setStyle("-fx-spacing: 10; -fx-padding: 15;");

        for (order_food food : currData.getFoods()) {

            Text nameText = new Text(food.getFid().getName());
            Text priceText = new Text("up: $" + String.format("%.2f", food.getFid().getPrice()));
            Text totalPrice = new Text("tc: $" + food.getTotal_price());
            Text quantityText = new Text("Qty: " + food.getQnt());

            nameText.setFont(new Font("Arial", 14));
            priceText.setFont(new Font("Arial", 14));
            quantityText.setFont(new Font("Arial", 14));

            HBox foodItemHBox = new HBox(20, nameText, priceText, totalPrice, quantityText);
            foodItemHBox.setAlignment(Pos.CENTER_LEFT);
            foodItemHBox.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4; -fx-border-color: #dcdcdc; -fx-border-radius: 5;");
            foodListVBox.getChildren().add(foodItemHBox);
        }

        ScrollPane scrollPane = new ScrollPane(foodListVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 10;");

        Button closeButton = new Button("Close");
        closeButton.setStyle(
                "-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; -fx-background-radius: 10;"
        );

        closeButton.setOnAction(ev -> {
            loaderSettings.removeDimmingEffect(owner);
            modalStage.close();
        });
        closeButton.setOnMouseEntered(ev -> closeButton.setStyle(
                "-fx-background-color: #ff1a1a; -fx-text-fill: white; -fx-background-radius: 10;" +
                        "-fx-padding: 10px 20px; -fx-background-radius: 10;"
        ));
        closeButton.setOnMouseExited(ev -> closeButton.setStyle(
                "-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 10;"
                        + "-fx-padding: 10px 20px; -fx-background-radius: 10;"
        ));

        VBox modalContent = new VBox(20, modalTitle, scrollPane, closeButton);
        modalContent.setAlignment(Pos.TOP_CENTER);
        modalContent.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-padding: 20; -fx-spacing: 15;");

        BorderPane modalRoot = new BorderPane(modalContent);
        modalRoot.setStyle("-fx-background-color: transparent;");

        Scene modalScene = new Scene(modalRoot, owner.getWidth() / 3, 450);
        modalScene.setFill(Color.TRANSPARENT);
        modalStage.setScene(modalScene);
        modalStage.setResizable(false);
        modalStage.show();

        modalStage.setX(owner.getX() + (owner.getWidth() - modalScene.getWidth()) / 2);
        modalStage.setY(owner.getY() + (owner.getHeight() - modalScene.getHeight()) / 2);


    }

    void service_history_viewer(ActionEvent e) {

        Button curr = (Button) e.getSource();
        String data = curr.getUserData().toString();
        if (currData == null || !currData.getBooking_id().matches(data)) {
            currData = invoiceGeneratorController.getData(data);
        }

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        Stage owner = (Stage) curr.getScene().getWindow();
        modalStage.initOwner(owner);
        modalStage.initStyle(StageStyle.UNDECORATED);
        modalStage.initStyle(StageStyle.TRANSPARENT);

        loaderSettings.applyDimmingEffect(owner);

        Text modalTitle = new Text("Service History");
        modalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        modalTitle.setFill(Color.DARKSLATEGRAY);

        VBox foodListVBox = new VBox();
        foodListVBox.setStyle("-fx-spacing: 10; -fx-padding: 15;");

        for (order_service service : currData.getServices()) {

            Text nameText = new Text(service.getSid().getName());
            Text priceText = new Text("up: $" + String.format("%.2f", service.getSid().getPrice()));

            int qnty = 1;
            if (service.getQnt() != 0) qnty = service.getQnt();

            Text quantityText = new Text("Qty: " + qnty);

            nameText.setFont(new Font("Arial", 14));
            priceText.setFont(new Font("Arial", 14));
            quantityText.setFont(new Font("Arial", 14));

            HBox foodItemHBox = new HBox(20, nameText, priceText, quantityText);
            foodItemHBox.setAlignment(Pos.CENTER_LEFT);
            foodItemHBox.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4; -fx-border-color: #dcdcdc; -fx-border-radius: 5;");
            foodListVBox.getChildren().add(foodItemHBox);
        }

        ScrollPane scrollPane = new ScrollPane(foodListVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 10;");

        Button closeButton = new Button("Close");
        closeButton.setStyle(
                "-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-size: 14px; " +
                        "-fx-padding: 10px 20px; -fx-background-radius: 10;"
        );

        closeButton.setOnAction(ev -> {
            loaderSettings.removeDimmingEffect(owner);
            modalStage.close();
        });
        closeButton.setOnMouseEntered(ev -> closeButton.setStyle(
                "-fx-background-color: #ff1a1a; -fx-text-fill: white; -fx-background-radius: 10; " +
                        "-fx-padding: 10px 20px; -fx-background-radius: 10;"
        ));
        closeButton.setOnMouseExited(ev -> closeButton.setStyle(
                "-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 10;" +
                        "-fx-padding: 10px 20px; -fx-background-radius: 10;"
        ));

        VBox modalContent = new VBox(20, modalTitle, scrollPane, closeButton);
        modalContent.setAlignment(Pos.TOP_CENTER);
        modalContent.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-padding: 20; -fx-spacing: 15;");

        BorderPane modalRoot = new BorderPane(modalContent);
        modalRoot.setStyle("-fx-background-color: transparent;");

        Scene modalScene = new Scene(modalRoot, owner.getWidth() / 3, 450);
        modalScene.setFill(Color.TRANSPARENT);
        modalStage.setScene(modalScene);
        modalStage.setResizable(false);
        modalStage.show();

        modalStage.setX(owner.getX() + (owner.getWidth() - modalScene.getWidth()) / 2);
        modalStage.setY(owner.getY() + (owner.getHeight() - modalScene.getHeight()) / 2);

    }


    @FXML
    void logoutAct(ActionEvent event) {
        logoutController.logout(event);
    }

    @FXML
    void switchToDashboard(ActionEvent event) throws IOException {
        ssc.swithcTo(event,
                (Stage) logout.getScene().getWindow(),
                "admin",
                "dashboard");
    }

    @FXML
    void switchTorooms(ActionEvent event) throws IOException {
        ssc.swithcTo(event,
                (Stage) logout.getScene().getWindow(),
                "admin",
                "rooms");
    }

    @FXML
    void switchToservices(ActionEvent event) throws Exception {
        ssc.swithcTo(event,
                (Stage) logout.getScene().getWindow(),
                "admin",
                "services");
    }

    @FXML
    void switchTosettings(ActionEvent event) throws IOException {
        ssc.toSettings(event,
                (Stage) logout.getScene().getWindow());
    }

}
