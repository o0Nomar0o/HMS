package project.hotelsystem.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import project.hotelsystem.database.connection.DBConnection;
import project.hotelsystem.database.controller.foodController;
import project.hotelsystem.database.controller.roomController;
import project.hotelsystem.database.controller.serviceController;
import project.hotelsystem.database.models.food;
import project.hotelsystem.database.models.room;
import project.hotelsystem.database.models.service;
import project.hotelsystem.util.ImageLoader;
import project.hotelsystem.util.dropdownManager;
import project.hotelsystem.util.notificationManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;


/**
 * Staff Service Controller class
 *
 * @author San Nyein Zaw
 * @author Zin Min Oo
 * @author Swun Saung
 */


public class staffServiceController {

    switchSceneController ssc = new switchSceneController();
    HashMap<Integer, service> serviceMap = new HashMap<Integer, service>();
    double tc = 0;

    @FXML
    private AnchorPane root;
    @FXML
    private Label FoodStock;
    @FXML
    private Button addAmount;
    @FXML
    private Button addOrder;
    @FXML
    private Button bookings;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnConfirm1;
    @FXML
    private Button btnLeft;
    @FXML
    private Button btnOrderBatch;
    @FXML
    private Button btnRight;
    @FXML
    private Button cancelOrder;
    @FXML
    private TilePane catPane;
    @FXML
    private Button confirmOrder;
    @FXML
    private ImageView foodImage;
    @FXML
    private Pane foodMenu;
    @FXML
    private Label foodName;
    @FXML
    private Label foodPrice;
    @FXML
    private TilePane foodView;
    @FXML
    private Button guests;
    @FXML
    private ImageView imgView;
    @FXML
    private Label lblDiscription;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblTitle;
    @FXML
    private Button logout;
    @FXML
    private VBox orderListContainer;
    @FXML
    private ScrollPane orderListScrollPane;
    @FXML
    private VBox orderListVbox;
    @FXML
    private Button orders_button;
    @FXML
    private Button reduceAmount;
    @FXML
    private ScrollPane scrollPaneServices;
    @FXML
    private Button services;
    @FXML
    private Button setting;
    @FXML
    private TextField showAmount;
    @FXML
    private Label showTotalCost;
    @FXML
    private TilePane tilepaneServices;
    @FXML
    private Text totalCost;
    @FXML
    private Button txfRoomNo;
    @FXML
    private TextField txfServiceName;
    @FXML
    private Pane selectedPane;
    @FXML
    private Button roomNo;
    private final Map<String, Pane> paneMap = new HashMap();
    private final List<service> orderedService = new ArrayList<>();
    private final List<service> servicesList = new ArrayList<>();
    private int currentIndex = 0;
    private final Map<food, Integer> currentOrders = new HashMap<>();
    private double totalFoodCost = 0.0;

    private final String[] currency = {"$", "ks"};

    private static void styleDropdownButton(Button button) {
        button.setStyle("""
                    -fx-background-color: #f5f6fa; 
                    -fx-border-color: #dcdde1; 
                    -fx-border-radius: 5; 
                    -fx-padding: 5 10; 
                    -fx-font-size: 14px;
                    -fx-text-fill: #2f3640;
                    -fx-cursor: hand;
                """);

        button.setOnMouseEntered(e -> button.setStyle("""
                    -fx-background-color: #e1e2e6; 
                    -fx-border-color: #dcdde1; 
                    -fx-border-radius: 5; 
                    -fx-padding: 5 10; 
                    -fx-font-size: 14px;
                    -fx-text-fill: #2f3640;
                    -fx-cursor: hand;
                """));

        button.setOnMouseExited(e -> button.setStyle("""
                    -fx-background-color: #f5f6fa; 
                    -fx-border-color: #dcdde1; 
                    -fx-border-radius: 5; 
                    -fx-padding: 5 10; 
                    -fx-font-size: 14px;
                    -fx-text-fill: #2f3640;
                    -fx-cursor: hand;
                """));
    }

    @FXML
    void switchToorders(ActionEvent event) {

    }

    @FXML
    void handleAddButtonClick(ActionEvent event) {
        createServiceOrderPane(Integer.parseInt(txfServiceName.getText()));
        txfServiceName.clear();
    }

    @FXML
    private void handleCreateServiceButtonClick() {

        Stage popupStage = new Stage();
        popupStage.setTitle("Create Service");
        popupStage.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = new VBox(10);

        TextField serviceIdField = new TextField();
        serviceIdField.setPromptText("Service ID");

        TextField serviceNameField = new TextField();
        serviceNameField.setPromptText("Service Name");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField priceField = new TextField();
        priceField.setPromptText("Service Price");

        Button btnSelectImage = new Button("Select Image");
        Label selectedFileLabel = new Label();
        final File[] selectedFile = new File[1];

        btnSelectImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(popupStage);
            if (file != null) {
                selectedFileLabel.setText(file.getName());
                selectedFile[0] = file;
            }
        });
        vbox.setStyle("-fx-background-color:black");


        Button btnSave = new Button("Save");
        btnSave.setOnAction(e -> {
            String serviceName = serviceNameField.getText();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());

            serviceController.saveService(serviceName, description, price, selectedFile[0]);
            popupStage.close();
        });

        vbox.getChildren().addAll(serviceIdField, serviceNameField, descriptionField, priceField, btnSelectImage, selectedFileLabel, btnSave);
        Scene scene = new Scene(vbox, 300, 275);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    @FXML
    void handleDeleteServiceButtonClick(ActionEvent event) {

    }

    @FXML
    void handleEditServiceButtonClick(ActionEvent event) {

    }

    @FXML
    void handleOrderAction(ActionEvent event) {

    }

    @FXML
    void initialzingOrderList(ActionEvent event) {

    }

    @FXML
    void switchtobookings(ActionEvent event) throws IOException {

        dump_pane();

        if (asyncFoodLoader.getState() != Worker.State.SUCCEEDED) {
            asyncFoodLoader.cancel();
            asyncFoodLoader.reset();
        }

        Runtime runtime = Runtime.getRuntime();
        System.out.println("Memory used (1): " + (runtime.totalMemory() - runtime.freeMemory()));

        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "staff", "booking");


    }

    @FXML
    void switchtorooms(ActionEvent event) throws IOException {
        dump_pane();
        if (asyncFoodLoader.getState() != Worker.State.SUCCEEDED) {
            asyncFoodLoader.cancel();
            asyncFoodLoader.reset();
        }

        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "staff", "rooms");

    }

    @FXML
    void switchtosetting(ActionEvent event) throws IOException {
        dump_pane();

        if (asyncFoodLoader.getState() != Worker.State.SUCCEEDED) {
            asyncFoodLoader.cancel();
            asyncFoodLoader.reset();
        }
        Platform.runLater(() -> {
            try {
                Thread.sleep(100);
                ssc.toSettings(event, (Stage) logout.getScene().getWindow());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void createService(service s) {
        HBox hbox = new HBox(10);
        GridPane serviceRoot = new GridPane();

        ImageView imageView = new ImageView();
        try {
            Image img = ImageLoader.loadImageFromBlob(s.getImage(), 200, 200);
//            byte[] imgByte = s.getImage().getBytes(1, (int) s.getImage().length());
//            Image img = new Image(new ByteArrayInputStream(imgByte));
            imageView.setImage(img);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

        VBox vbox = new VBox(5);
        Label idLabel = new Label("Service ID: " + s.getId());
        Label nameLabel = new Label("Service Name: " + s.getName());
        Label descriptionLabel = new Label("Description: " + s.getDescription());
        Label priceLabel = new Label("Price: $" + s.getPrice());


        idLabel.setStyle("-fx-text-fill:black");
        nameLabel.setStyle("-fx-text-fill:black");
        descriptionLabel.setStyle("-fx-text-fill:black");
        priceLabel.setStyle("-fx-text-fill:black");
        hbox.setStyle("-fx-background-color:black;" + "-fx-text-fill:white;");


        vbox.getChildren().addAll(idLabel, nameLabel, descriptionLabel, priceLabel);
        serviceRoot.add(imageView, 0, 0);
        serviceRoot.add(vbox, 1, 0);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(imageView.getFitWidth());

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(100);

        ColumnConstraints column = new ColumnConstraints();
        column.setHgrow(Priority.ALWAYS);

        serviceRoot.getColumnConstraints().addAll(col1, col2, column);

        serviceRoot.setPrefWidth(tilepaneServices.getWidth());

        serviceRoot.setStyle("-fx-hgap: 15px;"
                + "-fx-background-radius: 1.25em;"
                + "-fx-background-color: white;");

        tilepaneServices.widthProperty().addListener((ob, ov, nv) -> {
            serviceRoot.setPrefWidth(nv.doubleValue() - 15);

        });


        tilepaneServices.getChildren().add(serviceRoot);

    }

    private void loadPopularServicesFromDatabase() {
        String sql = " SELECT s.service_id, s.service_name, s.service_price, s.service_description, s.service_image, COUNT(os.service_id) AS total_orders " + "FROM service s " + "JOIN service_order_detail os ON s.service_id = os.service_id " + "WHERE os.order_time >= NOW() - INTERVAL 7 DAY " + "GROUP BY s.service_id, s.service_name, s.service_price, s.service_description, s.service_image " + "ORDER BY total_orders DESC limit 3";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String serviceName = rs.getString(2);
                double price = rs.getDouble(3);
                String des = rs.getString(4);
                Blob img = rs.getBlob(5);
                servicesList.add(new service(serviceName, price, des, img));
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateService() {
        tilepaneServices.getChildren().clear();
        List<service> ss = serviceController.getAllServices();
        for (service s : ss) {
            if (s.getDescription().matches("NIL")) continue;
            serviceMap.put(s.getId(), s);
            createService(s);
        }
    }

    @FXML
    private void previousService() {
        if (currentIndex > 0) {
            currentIndex--;
            updateServiceDisplay();
        } else if (currentIndex == 0) {
            currentIndex = servicesList.size() - 1;
            updateServiceDisplay();
        }
    }

    @FXML
    private void nextService() {
        if (currentIndex < servicesList.size() - 1) {
            currentIndex++;
            updateServiceDisplay();
        } else if (currentIndex == servicesList.size() - 1) {
            currentIndex = 0;
            updateServiceDisplay();
        }
    }

    private void updateServiceDisplay() {
        if (!servicesList.isEmpty() && currentIndex >= 0 && currentIndex < servicesList.size()) {
            service currentService = servicesList.get(currentIndex);
            lblTitle.setText(currentService.getName());
            lblPrice.setText("$" + currentService.getPrice());
            lblDiscription.setText(currentService.getDescription());
            Blob imageBlob = currentService.getImage();
            if (imageBlob != null) {
                try {
                    byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                    Image image = new Image(new ByteArrayInputStream(imageBytes));

                    imgView.setImage(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                imgView.setImage(null);
            }
        } else {
            lblTitle.setText("No service available");
            lblPrice.setText("");
            lblDiscription.setText("");
            imgView.setImage(null);
        }
    }

    private void createServiceOrderPane(int serviceId) {
        service s = serviceMap.get(serviceId);
        HBox serviceOrderPane = new HBox(50);
        VBox Tosep = new VBox(10);
        Label serviceLabel = new Label("Service id:" + serviceId);
        Label serviceCharge = new Label("Charges: " + s.getPrice());

        orderedService.add(s);

        Separator sep = new Separator();

        serviceLabel.setStyle("-fx-text-fill:white");
        serviceCharge.setStyle("-fx-text-fill:white");

        serviceOrderPane.setStyle("-fx-background-color: #c2d2c1;" + "-fx-padding: 5px 0px 5px 10px;" + "-fx-background-radius:8px");

        sep.setPrefWidth(100);
        sep.setStyle("-fx-background-grey: white;" + "-fx-border-color:grey;" + "-fx-border-width: 0;");
        serviceOrderPane.getChildren().addAll(serviceLabel, serviceCharge);
        Tosep.getChildren().addAll(serviceOrderPane, sep);
        orderListVbox.getChildren().add(Tosep);
        tc += s.getPrice();
        totalCost.setText("Total Cost: $" + tc);
    }

    @FXML
    void add_Order(ActionEvent event) {
        Button clicked = (Button) event.getTarget();
        String foodName = clicked.getUserData().toString();
        Pane newPane = this.createNewOrder(foodName);
        this.orderListContainer.getChildren().addAll(newPane);
    }

    private Pane createNewOrder(String fn) {
        Pane newPane = new Pane();
        Label qnt = new Label(this.showAmount.getText());
        qnt.setStyle("-fx-font-size: 14px;");
        Label foodName = new Label(fn);
        foodName.setStyle("-fx-font-size: 20px;");
        newPane.setStyle("-fx-background-color:  #EAEAEA;");
        newPane.getChildren().addAll(foodName, qnt);
        newPane.getChildren().get(0).setLayoutX(76.0);
        newPane.getChildren().get(1).setLayoutX(232.0);
        return newPane;
    }

    @FXML
    public void initialize() {
        ObservableList<room> ols = FXCollections.observableArrayList(roomController.getAllOccupiedRooms());

        Popup roomDp = dropdownManager.createRoomDropdown(txfRoomNo, ols);

        txfRoomNo.setOnAction(event -> {
            if (!roomDp.isShowing()) {
                Bounds bounds = txfRoomNo.localToScreen(txfRoomNo.getBoundsInLocal());
                roomDp.show(txfServiceName.getScene().getWindow(),
                        bounds.getMinX(), bounds.getMaxY());
            }
        });

        Popup roomDp2 = dropdownManager.createRoomDropdown(roomNo, ols);
        roomNo.setOnAction(event -> {
            if (!roomDp2.isShowing()) {
                Bounds bounds = roomNo.localToScreen(roomNo.getBoundsInLocal());
                roomDp2.show(roomNo.getScene().getWindow(),
                        bounds.getMinX(), bounds.getMaxY());
            }
        });
        styleDropdownButton(roomNo);
        styleDropdownButton(txfRoomNo);

        orders_button.setDisable(true);
        showTotalCost.setText("0");
        List<service> ss = serviceController.getAllServices();
        for (service s : ss) {
            serviceMap.put(s.getId(), s);
            createService(s);
        }

        loadPopularServicesFromDatabase();
        if (!servicesList.isEmpty()) {
            updateServiceDisplay();
        } else {
            lblTitle.setText("No services found");
            lblPrice.setText("");
        }


        logout.setOnAction(e -> logoutController.logout((Stage) logout.getScene().getWindow()));

        btnConfirm.setOnAction(e -> {
            room r = (room) txfRoomNo.getUserData();
            if (r == null) {
                notificationManager.showNotification("Please select a room", "failure", (Stage) btnConfirm.getScene().getWindow());
                return;
            }

            if (serviceController.batchOrder(orderedService, r.getRoom_no())) {
                orderedService.clear();
                orderListVbox.getChildren().clear();
                totalCost.setText("Total Cost: 0");
                notificationManager.showNotification("Successfully ordered!", "success", (Stage) logout.getScene().getWindow());
            }
        });

        btnConfirm1.setOnAction(e -> {
            orderedService.clear();
            orderListVbox.getChildren().clear();
            totalCost.setText("Total Cost: 0");
        });

        this.cancelOrder.setOnAction((e) -> {
            this.orderListContainer.getChildren().clear();
            showTotalCost.setText("0");
        });


        asyncFoodLoader.setOnSucceeded(event -> {
            List<Pane> loadedPanes = asyncFoodLoader.getValue();
            Platform.runLater(() -> {
                foodView.getChildren().addAll(loadedPanes);
                loadedPanes.clear();
            });
            asyncFoodLoader.cancel();
            asyncFoodLoader.reset();

        });


        cancelOrder.setOnAction(e -> {
            for (Node orderNode : orderListContainer.getChildren()) {
                if (orderNode instanceof Pane orderPane) {
                    food fm = (food) orderPane.getUserData();

                    Label quantityLabel = (Label) orderPane.lookup("#quantityLabel");
                    if (quantityLabel != null) {
                        int foodQuantity = Integer.parseInt(quantityLabel.getText());
                        fm.setStock(fm.getStock() + foodQuantity);
                    }
                    currentOrders.remove(fm);
                }
            }

            orderListContainer.getChildren().clear();
            totalFoodCost = 0.0;
            updateTotalCost(showTotalCost);
        });

        confirmOrder.setOnAction(e -> {

            room r = (room) roomNo.getUserData();

            if (roomNo.getUserData() == null || r == null||r.getRoom_no() == null || r.getRoom_no().isEmpty()) {
                notificationManager.showNotification("Please choose a room", "faliure", (Stage) logout.getScene().getWindow());
                return;
            }

            if(orderListContainer.getChildren().isEmpty()) {
                notificationManager.showNotification("Please select an item", "faliure", (Stage) logout.getScene().getWindow());
                return;
            }

            try (Connection con = DBConnection.getConnection(); CallableStatement stmt = con.prepareCall("{CALL add_food_order_and_update_stock(?, ?, ?)}")) {

                for (Map.Entry<food, Integer> entry : currentOrders.entrySet()) {
                    food foodItem = entry.getKey();
                    int quantity = entry.getValue();
                    stmt.setInt(1, Integer.parseInt(r.getRoom_no()));
                    stmt.setString(2, foodItem.getName());
                    stmt.setInt(3, quantity);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }

            updateStockUI();
            notificationManager.showNotification("Successfully ordered!", "success", (Stage) logout.getScene().getWindow());
            orderListContainer.getChildren().clear();
            totalFoodCost = 0.0;
            updateTotalCost(showTotalCost);
            showTotalCost.setText("0");
            currentOrders.clear();
        });

        asyncFoodLoader.start();

    }

    public void updateStockUI() {
        Map<String, Integer> fns = foodController.getAllFoodStockAndName();
        for (Node n : foodView.getChildren()) {
            if (n instanceof Pane) {
                Label nameLabel = (Label) n.lookup("#nameLabel");
                Label quantityLabel = (Label) n.lookup("#stockLabel");
                String name = nameLabel.getText();
                quantityLabel.setText(fns.get(name)+"");
                System.out.println(name);
            }
        }
    }


    List<food> allFood = new ArrayList<>();

    public final Service<List<Pane>> asyncFoodLoader = new Service<>() {
        @Override
        protected Task<List<Pane>> createTask() {
            return new Task<>() {
                @Override
                protected List<Pane> call() {
                    foodView.getChildren().removeAll();
                    allFood = foodController.getAllFood();

                    if (allFood == null || allFood.isEmpty()) return null;

                    List<Pane> foodPanes = new ArrayList<>();
                    HashSet<String> allCats = new HashSet<>();

                    for (food f : allFood) {
                        Pane fp = createFoodPane(f);
                        foodPanes.add(fp);

                        if (!allCats.contains(f.getCategory())) {
                            Platform.runLater(() -> {
                                Pane fc = createCategoryPane(f);
                                catPane.getChildren().add(fc);
                            });
                            allCats.add(f.getCategory());
                        }
                    }
                    return foodPanes;
                }
            };
        }
    };


    public void async_pane_loader() {

    }


    void clearOrderConfirmation() {

    }

    void add_Order(ActionEvent event, String qnt, food fm) throws SQLException {
        int quantity;
        try {
            quantity = Integer.parseInt(qnt);
            if (quantity < 1) return;
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity format: " + qnt);
            return;
        }


        if (fm.getStock() < quantity) {
            System.out.println("Not enough stock available!");
            notificationManager.showNotification("Item out of stock", "failure", (Stage) logout.getScene().getWindow());
            return;
        }

        fm.setStock(fm.getStock() - quantity);


        if (currentOrders.containsKey(fm)) {
            int existingQuantity = currentOrders.get(fm);
            existingQuantity += quantity;
            currentOrders.put(fm, existingQuantity);

            Pane orderPane = findOrderPane(fm);
            if (orderPane != null) {
                Label quantityLabel = (Label) orderPane.lookup("#quantityLabel");
                Label priceLabel = (Label) orderPane.lookup("#priceLabel");
                if (quantityLabel != null) {
                    int currentQuantity = Integer.parseInt(quantityLabel.getText());
                    currentQuantity += quantity;
                    quantityLabel.setText(String.valueOf(currentQuantity));
                    double pricePerUnit = fm.getPrice();
                    double totalPrice = currentQuantity * pricePerUnit;
                    priceLabel.setText(String.valueOf(totalPrice));
                } else {
                    System.out.println("Quantity label not found!");
                }
            }
        } else {
            Pane newPane = createNewOrder(fm, String.valueOf(quantity));
            orderListContainer.getChildren().add(newPane);
            currentOrders.put(fm, quantity);
        }

        double foodPrice = fm.getPrice();
        totalFoodCost += foodPrice * quantity;
        updateTotalCost(showTotalCost);
        System.out.println("Order added");
    }


    private Pane findOrderPane(food food) {
        for (Node node : orderListContainer.getChildren()) {
            if (node instanceof Pane) {
                food orderFood = (food) node.getUserData();
                if (orderFood.equals(food)) {
                    return (Pane) node;
                }
            }
        }
        return null;
    }


    @FXML
    void add_Amount(ActionEvent event) {
        System.out.println("button was clicked");

        Button clicked = (Button) event.getTarget();
        if (clicked.getUserData() != null) {
            @SuppressWarnings("unchecked") List<Object> retrievedNodes = (List<Object>) clicked.getUserData();
            int currText = 0;
            for (Object node : retrievedNodes) {
                if (node instanceof TextField tf) {

                    currText = Integer.parseInt(tf.getText());
                    currText++;
                    tf.setText(String.valueOf(currText));

                } else if (node instanceof Button btn) {

                    btn.setDisable(false);
                }
            }
        }
    }

    @FXML
    void reduce_Amount(ActionEvent event) {
        System.out.println("button was clicked");

        Button clicked = (Button) event.getTarget();
        if (clicked.getUserData() != null) {
            TextField tf = (TextField) clicked.getUserData();
            int currText = Integer.parseInt(tf.getText());

            currText--;
            tf.setText(String.valueOf(currText));
            if (currText <= 0) {
                clicked.setDisable(true);
            }
        }

    }

    void reduce_stock(ActionEvent event) {
        String procedureCall = "{Call add_food_order_and_update_stock(?,?,?)}";

        try (Connection con = DBConnection.getConnection(); CallableStatement psmt = con.prepareCall(procedureCall)) {

            psmt.setInt(1, Integer.parseInt(roomNo.getText()));
            psmt.setString(2, foodName.getText());
            psmt.execute();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    private Pane createCategoryPane(food fm) {

        Pane catePane = new Pane();
        catePane.setPrefSize(120, 166);
        catePane.setStyle("-fx-background-color: #FFFFFF;");

        try {
            Blob b = fm.getImage();
            byte[] imgByte = b.getBytes(1, (int) b.length());
            Image img = new Image(new ByteArrayInputStream(imgByte));
            ImageView iv = new ImageView(img);
            iv.setFitHeight(111);
            iv.setFitWidth(89);
            iv.setPreserveRatio(true);
            catePane.getChildren().add(iv);
            catePane.getChildren().get(0).setLayoutX(15);
            catePane.getChildren().get(0).setLayoutY(14);
            Label CategoryName = new Label(fm.getCategory());

            CategoryName.setFont(new Font(20.0));
            catePane.getChildren().add(CategoryName);
            CategoryName.setPrefWidth(100);
            CategoryName.setAlignment(Pos.CENTER);
            catePane.getChildren().get(1).setLayoutX(16);
            catePane.getChildren().get(1).setLayoutY(120);
            catePane.setOnMouseClicked(event -> {
                dump_main_food();
                Platform.runLater(() -> {
                    showFoodByCat(event, CategoryName, catePane);
                });

            });


        } catch (Exception e) {
            e.printStackTrace();
        }

        return catePane;
    }

    private Pane createFoodPane(food fm) {

        Pane newPane = new Pane();
        newPane.setPrefWidth(190);
        newPane.setPrefHeight(160);
        newPane.setStyle("-fx-background-color: #FEFFFF;");
        newPane.setUserData(fm.getName());

        try {
            Blob b = fm.getImage();
            Image img = ImageLoader.loadImageFromBlob(b, 200, 200);
//            byte[] imgByte = b.getBytes(1, (int) b.length());
//            Image img = new Image(new ByteArrayInputStream(imgByte));
            ImageView iv = new ImageView(img);
            iv.setFitHeight(100);
            iv.setFitWidth(80);
            iv.setPreserveRatio(true);

            if (fm.getStock() <= 0)
                iv.setOpacity(0.5);

            newPane.getChildren().add(iv);
            newPane.getChildren().get(0).setLayoutX(8);
            newPane.getChildren().get(0).setLayoutY(15);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Label foodName = new Label(fm.getName());
        //foodName.setStyle("-fx-font-size: 20; -fx-font-family: Arial;");
        newPane.getChildren().add(foodName);
        foodName.setPrefWidth(80);
        foodName.setPrefHeight(45);
        foodName.setId("nameLabel");
        foodName.maxWidth(80);
        foodName.setWrapText(true);
        foodName.maxWidthProperty().bind(newPane.widthProperty());
        newPane.getChildren().get(1).setLayoutX(100);
        newPane.getChildren().get(1).setLayoutY(5);

        TextField text = new TextField();
        text.setPrefWidth(35);
        text.setPrefHeight(26);
        text.setStyle("-fx-background-radius: 10;");
        text.setText("0");
        text.setPromptText("0");
        text.setAlignment(Pos.CENTER);

        text.setTextFormatter(new TextFormatter<Integer>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));

        Button addBtn = new Button("Add");

        addBtn.setUserData(fm);
        newPane.getChildren().add(addBtn);
        newPane.getChildren().get(2).setLayoutX(110);
        newPane.getChildren().get(2).setLayoutY(120);
        addBtn.setPrefSize(55, 35);
        addBtn.setStyle("""
                -fx-background-color: red; -fx-text-fill: white;
                """);

        int cur_pt = 0;
        Label currency1 = new Label(currency[cur_pt]);

        currency1.setStyle("fx-font-size: 15px");
        newPane.getChildren().add(currency1);
        newPane.getChildren().get(3).setLayoutX(110);
        newPane.getChildren().get(3).setLayoutY(55);

        Label price = new Label(fm.getPrice() + "");
        price.setStyle("fx-font-size: 15px");
        newPane.getChildren().add(price);
        newPane.getChildren().get(4).setLayoutX(130);
        newPane.getChildren().get(4).setLayoutY(55);

        Label stock = new Label("Stock:");
        stock.setStyle("fx-font-size: 15px");
        newPane.getChildren().add(stock);
        newPane.getChildren().get(5).setLayoutX(100);
        newPane.getChildren().get(5).setLayoutY(82);

        Label stockLabel = new Label(fm.getStock() + "");
        stockLabel.setStyle("fx-font-size: 15px");
        newPane.getChildren().add(stockLabel);
        stockLabel.setId("stockLabel");
        newPane.getChildren().get(6).setLayoutX(140);
        newPane.getChildren().get(6).setLayoutY(82);

        HBox hbox = new HBox();
        newPane.getChildren().add(hbox);
        hbox.setLayoutX(10);
        hbox.setLayoutY(120);

        Button incBtn = new Button(">");
        incBtn.setStyle("-fx-background-radius: 10; -fx-background-color: #DAEADC;");

        Button decbtn = new Button("<");
        decbtn.setOnAction(e -> reduce_Amount(e));
        decbtn.setStyle("-fx-background-radius: 10; -fx-background-color: #EADADC;");

        List<Object> datas = new ArrayList<>();
        datas.add(text);
        datas.add(decbtn);
        incBtn.setUserData(datas);

        incBtn.setOnAction(e -> add_Amount(e));
        decbtn.setUserData(text);
        decbtn.setDisable(true);

        if (fm.getStock() <= 0) {
            addBtn.setDisable(true);
            incBtn.setDisable(true);
            text.setDisable(true);
            newPane.setStyle("-fx-background-color: #EDEDED;");
        }

        text.textProperty().addListener((ob, ov, nv) -> {
            int s = Integer.parseInt(nv);

            if (text.getText().equals("0"))
                decbtn.setDisable(true);

            if (s >= fm.getStock()) {
                incBtn.setDisable(true);
                text.setText(fm.getStock() + "");
                return;
            }
            incBtn.setDisable(false);
        });


        hbox.getChildren().addAll(decbtn, text, incBtn);

        addBtn.setOnAction(e -> {
            try {
                add_Order(e, text.getText(), fm);
                fm.setStock(fm.getStock() - Integer.parseInt(text.getText()));
                System.out.println(fm.getStock());
                text.setText("0");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        return newPane;
    }


    private void updateTotalCost(Label tprice) {
        try {
            tprice.setText(String.format("%.2f ks", totalFoodCost));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Pane createNewOrder(Object fo, String text) throws SQLException {

        Pane newPane = new Pane();
        newPane.setPrefSize(461, 80);
        newPane.setStyle("-fx-background-color:  #EAEAEA;");

        food fm = (food) fo;

        Label fn = new Label(fm.getName());
        fn.setLayoutX(76);
        fn.setLayoutY(14);

        Blob b = fm.getImage();
        byte[] imgByte = b.getBytes(1, (int) b.length());
        Image img = new Image(new ByteArrayInputStream(imgByte));
        ImageView iv = new ImageView(img);
        iv.setFitWidth(54);
        iv.setFitHeight(69);

        Label qnt = new Label(text);
        int foodQuantity = Integer.parseInt(text);
        qnt.setStyle("-fx-font-size: 15px;");
        qnt.setId("quantityLabel");
        Label sign = new Label("x");
        sign.setStyle("fx-font-size: 15px");

        Label currency1 = new Label("ks");
        currency1.setStyle("fx-font-size: 15px");

        Label currency2 = new Label("ks");
        currency2.setStyle("fx-font-size: 15px");

        Label price = new Label(fm.getPrice() + "");
        price.setStyle("fx-font-size: 15px");

        double total = foodQuantity * fm.getPrice();
        String tp = Double.toString(total);
        Label tprice = new Label((tp));
        tprice.setStyle("fx-font-size: 15px");
        tprice.setId("priceLabel");

        Button remove = new Button("Remove");
        remove.setStyle("-fx-background-color: red; -fx-text-fill: white;");


        remove.setOnAction(e -> {

            Label quantityLabel = (Label) newPane.lookup("#quantityLabel");
            if (quantityLabel != null) {
                int currentQuantity = Integer.parseInt(quantityLabel.getText());

                fm.setStock(fm.getStock() + currentQuantity);

                double itemTotalCost = currentQuantity * fm.getPrice();

                totalFoodCost -= itemTotalCost;

                updateTotalCost(showTotalCost);

                orderListContainer.getChildren().remove(newPane);

                currentOrders.remove(fm);
            }
        });


        newPane.setUserData(fm);

        newPane.getChildren().addAll(fn, qnt, sign, currency1, tprice, remove, currency2, price, iv);
        newPane.getChildren().get(0).setLayoutX(76.0);
        newPane.getChildren().get(0).setLayoutY(14.0);
        newPane.getChildren().get(1).setLayoutX(232.0);
        newPane.getChildren().get(1).setLayoutY(18.0);
        newPane.getChildren().get(2).setLayoutX(217.0);
        newPane.getChildren().get(2).setLayoutY(18.0);
        newPane.getChildren().get(3).setLayoutX(267.0);
        newPane.getChildren().get(3).setLayoutY(18.0);
        newPane.getChildren().get(4).setLayoutX(289.0);
        newPane.getChildren().get(4).setLayoutY(18.0);
        newPane.getChildren().get(5).setLayoutX(363.0);
        newPane.getChildren().get(5).setLayoutY(20.0);
        newPane.getChildren().get(6).setLayoutX(76.0);
        newPane.getChildren().get(6).setLayoutY(48.0);
        newPane.getChildren().get(7).setLayoutX(102.0);
        newPane.getChildren().get(7).setLayoutY(48.0);
        newPane.getChildren().get(8).setLayoutX(14.0);
        newPane.getChildren().get(8).setLayoutY(12.0);


        return newPane;
    }

    private void showFoodByCat(MouseEvent event, Label category, Pane clickedPane) {
        foodView.getChildren().clear();
        foodView.getChildren().removeAll();
        List<food> allFood = foodController.getFoodByCat();
        for (food selectedFood : allFood) {
            if (selectedFood.getCategory().equals(category.getText())) {

                Pane nfp = createFoodPane(selectedFood);
                foodView.getChildren().add(nfp);
            }
        }
        // Check if there's a previously selected pane and reset its color
        for (Node node : catPane.getChildren()) {
            if (node instanceof Pane) {
                Pane pane = (Pane) node;
                pane.setStyle("-fx-background-color:  #e0e0e0;");
                pane.setOpacity(0.75);
            }
        }
        // Change the color of the clicked pane
        clickedPane.setStyle("-fx-border-color: #A3A1DA; -fx-border-width: 2;"); // Change color to indicate selection
        clickedPane.setOpacity(1);
        // Update the selectedPane to the current one
        selectedPane = clickedPane;
    }

    private void dump_main_food() {

        asyncFoodLoader.cancel();
        asyncFoodLoader.reset();

        Platform.runLater(() -> {

            List<Node> nodesToRemoveFromFoodView = new ArrayList<>();

            for (Node n : foodView.getChildren()) {
                if (n instanceof Pane) {
                    for (Node p : ((Pane) n).getChildren()) {
                        if (p instanceof ImageView) {
                            ((ImageView) p).setImage(null);
                        }

                    }
                    nodesToRemoveFromFoodView.add(n);
                }
            }

            foodView.getChildren().removeAll(nodesToRemoveFromFoodView);

            nodesToRemoveFromFoodView.clear();
            allFood.clear();

            System.gc();
        });

    }


    private void dump_pane() {

        asyncFoodLoader.cancel();
        asyncFoodLoader.reset();

        Platform.runLater(() -> {

            List<Node> nodesToRemoveFromFoodView = new ArrayList<>();
            List<Node> nodesToRemoveFromCatPane = new ArrayList<>();

            for (Node n : foodView.getChildren()) {
                if (n instanceof Pane) {
                    for (Node p : ((Pane) n).getChildren()) {
                        if (p instanceof ImageView) {
                            ((ImageView) p).setImage(null);
                        }

                    }
                    nodesToRemoveFromFoodView.add(n);
                }
            }

            for (Node n : catPane.getChildren()) {
                if (n instanceof Pane) {
                    ((Pane) n).getChildren().clear();
                    nodesToRemoveFromCatPane.add(n);
                }
            }

            foodView.getChildren().removeAll(nodesToRemoveFromFoodView);
            catPane.getChildren().removeAll(nodesToRemoveFromCatPane);

            nodesToRemoveFromCatPane.clear();
            nodesToRemoveFromFoodView.clear();
            allFood.clear();

            System.gc();
        });

    }

}