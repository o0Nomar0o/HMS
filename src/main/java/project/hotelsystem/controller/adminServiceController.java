package project.hotelsystem.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.hotelsystem.database.connection.DBConnection;
import project.hotelsystem.database.controller.foodController;
import project.hotelsystem.database.controller.serviceController;
import project.hotelsystem.database.models.food;
import project.hotelsystem.database.models.service;
import project.hotelsystem.settings.loaderSettings;
import project.hotelsystem.settings.userSettings;
import project.hotelsystem.util.ImageCompressor;
import project.hotelsystem.util.ImageLoader;
import project.hotelsystem.util.notificationManager;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Controller class
 *
 * @author Swun Saung
 * @author Zin Min Oo
 * @author Nomar
 */

public class adminServiceController {


    @FXML
    private ImageView CategoryimageView;

    @FXML
    private Button add;

    @FXML
    private Button add1;

    @FXML
    private Pane addStockPane;

    @FXML
    private Tab addStocktab;

    @FXML
    private Button bookings;


    @FXML
    private Button btnCreateService;

    @FXML
    private Button btnLeft;


    @FXML
    private Button btnRight;

    @FXML
    private Button cancelFoodUpdate;

    @FXML
    private TextField cat;

    @FXML
    private TextField cat1;

    @FXML
    private TilePane categoryView;

    @FXML
    private Button dashboard;

    @FXML
    private AnchorPane editfoodpane;

    @FXML
    private AnchorPane editfoodpane1;

    @FXML
    private TextField foodName;

    @FXML
    private TextField foodName2;

    @FXML
    private TextField foodName3;

    @FXML
    private TableColumn<?, ?> foodNameColumn;

    @FXML
    private TilePane foodView;


    @FXML
    private Button guests;

    @FXML
    private ImageView imageView;

    @FXML
    private Pane imagepane;

    @FXML
    private Pane imagepane1;

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
    private ScrollPane menuScrollPane;


    @FXML
    private TextField price;

    @FXML
    private TextField price2;

    @FXML
    private ScrollPane scrollPaneServices;

    @FXML
    private Button services;

    @FXML
    private Button setting;

    @FXML
    private TableColumn<?, ?> stockColumn;

    @FXML
    private TableColumn<?, ?> stockStatusColumn;

    @FXML
    private TableView<food> stockTableView;

    @FXML
    private TextField stockToadd;

    @FXML
    private TilePane tilepaneServices;

    @FXML
    private TabPane tab_root;


    userSettings uss = userSettings.getInstance();
    switchSceneController ssc = new switchSceneController();
    HashMap<Integer, service> serviceMap = new HashMap<Integer, service>();
    private List<service> servicesList = new ArrayList<>();

    @FXML
    void initialize() {

        Tab targetTab = tab_root.getTabs().get(1);
        tab_root.getSelectionModel().select(targetTab);
        tab_root.getSelectionModel().select(0);

        logout.setOnAction(logoutController::logout);
        generateService();
        loadPopularServicesFromDatabase();
        if (!servicesList.isEmpty()) {
            updateServiceDisplay();
        } else {
            lblTitle.setText("No services found");
            lblPrice.setText("");
        }


        this.setupTableView();
        this.add.setOnAction((e) -> {
            this.saveImage();
        });
        this.add1.setOnAction((e) -> {
            this.saveCategoryImage();
        });
        this.addStockPane.setVisible(false);
        this.addStocktab.setOnSelectionChanged((event) -> {
            this.addStockPane.setVisible(this.addStocktab.isSelected());
        });
        this.addStocktab.setOnSelectionChanged((event) -> {
            this.addStockPane.setVisible(this.addStocktab.isSelected());
        });
        this.displayMenu();


    }

    @FXML
    void get_row(MouseEvent event) {
        if (event.getClickCount() == 2) {
            food selectedFood = stockTableView.getSelectionModel().getSelectedItem();
            if (selectedFood != null) {
                foodName3.setText(selectedFood.getName());
            }
        }
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

    private int currentIndex = 0;

    @FXML
    private void previousService() {
        if (currentIndex > 0) {
            currentIndex--;
            updateServiceDisplay();
        }
    }

    @FXML
    private void nextService() {
        if (currentIndex < servicesList.size() - 1) {
            currentIndex++;
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


    @FXML
    private void handleCreateServiceButtonClick() {


        Stage popupStage = new Stage();
        popupStage.setTitle("Create Service");
        popupStage.initModality(Modality.APPLICATION_MODAL);

        Stage owner = (Stage) logout.getScene().getWindow();

        loaderSettings.applyDimmingEffect(owner);

        popupStage.initOwner(owner);
        popupStage.initStyle(StageStyle.UNDECORATED);

        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-padding: 20px;");

        TextField serviceNameField = new TextField();
        serviceNameField.setPromptText("Service Name");
        serviceNameField.setStyle("-fx-pref-width: 300px; -fx-font-size: 14px;");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        descriptionField.setStyle("-fx-pref-width: 300px; -fx-font-size: 14px;");

        TextField priceField = new TextField();
        priceField.setPromptText("Service Price");
        priceField.setStyle("-fx-pref-width: 300px; -fx-font-size: 14px;");

        Button btnSelectImage = new Button("Select Image");
        Label selectedFileLabel = new Label();
        final File[] selectedFile = new File[1];

        btnSelectImage.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10px 20px;");
        btnSelectImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(popupStage);

            if (file != null) {
                selectedFileLabel.setText(file.getName());
                selectedFile[0] = file;

                try {
                    InputStream compressedImageStream = ImageCompressor.compressImage(file, 0.7f);
                    byte[] imageBytes = compressedImageStream.readAllBytes();

                    System.out.println("Compressed image size: " + imageBytes.length + " bytes");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        Button btnClose = new Button("Close");
        btnClose.setStyle("-fx-background-color: #ed1e3f; -fx-text-fill: white; -fx-padding: 10px 20px;");
        btnClose.setOnAction(e -> {
                    loaderSettings.removeDimmingEffect(owner);
                    popupStage.close();
                }
        );

        Button btnSave = new Button("Save");
        btnSave.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 10px 20px;");
        btnSave.setOnAction(e -> {
            String serviceName = serviceNameField.getText();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());

            serviceController.saveService(serviceName, description, price, selectedFile[0]);
            loaderSettings.removeDimmingEffect(owner);
            notificationManager.showNotification("Successfully added a new service", "success", owner);
            popupStage.close();
        });

        HBox btns = new HBox(btnClose, btnSave);
        btns.setStyle("-fx-padding: 15px; -fx-spacing: 20px;");
        btns.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(serviceNameField, descriptionField, priceField, btnSelectImage, selectedFileLabel, btns);

        Scene scene = new Scene(vbox, 400, 350);

        double xPosition = owner.getX() + owner.getWidth() / 2d - scene.getWidth() / 2d;
        double yPosition = owner.getY() + owner.getHeight() / 2d - scene.getHeight() / 2d;
        popupStage.setX(xPosition);
        popupStage.setY(yPosition);

        popupStage.setScene(scene);
        popupStage.setResizable(false);
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
    void switchToDashboard(ActionEvent event) throws IOException {
        dump_pane();
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "dashboard");
    }

    @FXML
    void switchtobookings(ActionEvent event) throws IOException {
        dump_pane();

        Platform.runLater(() -> {
            try {
                Thread.sleep(120);
                ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "booking");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @FXML
    void switchtorooms(ActionEvent event) throws IOException {
        dump_pane();
        ssc.swithcTo(event, (Stage) logout.getScene().getWindow(), "admin", "rooms");

    }

    @FXML
    void switchtosetting(ActionEvent event) throws IOException {
        dump_pane();
        ssc.toSettings(event, (Stage) logout.getScene().getWindow());

    }

    userSettings tss = userSettings.getInstance();

    public void createService(service s) {
        HBox hbox = new HBox(10);
        GridPane serviceRoot = new GridPane();

        ImageView imageView = new ImageView();
        try {
            byte imgByte[] = s.getImage().getBytes(1, (int) s.getImage().length());
            Image img = new Image(new ByteArrayInputStream(imgByte));
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

        String lightModeStyle = "-fx-text-fill: #333333;";
        String darkModeStyle = "-fx-text-fill: #FFFFFF;";

        idLabel.setStyle(lightModeStyle);
        nameLabel.setStyle(lightModeStyle);
        descriptionLabel.setStyle(lightModeStyle);
        priceLabel.setStyle(lightModeStyle);

        hbox.setStyle("-fx-background-color: #F6F5F2; -fx-padding: 10; -fx-spacing: 10;");
        String currentTheme = tss.getTheme();
        switch (currentTheme) {
            case "light":
                hbox.setStyle("-fx-background-color: #F6F5F2; -fx-text-fill: #333333;");
                break;
            case "dark":
                hbox.setStyle("-fx-background-color: #121212; -fx-text-fill: #FFFFFF;");
                break;
            default:
                break;
        }

        Button editbtn = new Button("Edit");
        editbtn.setUserData(s);
        Button delbtn = new Button("Delete");
        delbtn.setUserData(s);
        editbtn.setOnAction(e -> {
            loaderSettings.applyDimmingEffect((Stage) logout.getScene().getWindow());
            editService(e);
        });

        delbtn.setOnAction(e -> {
            loaderSettings.applyDimmingEffect((Stage) logout.getScene().getWindow());
            removeService(e);
        });

        VBox actionbox = new VBox(editbtn, delbtn);
        actionbox.setAlignment(Pos.CENTER_RIGHT);

        actionbox.setStyle("-fx-spacing: 5;");

        vbox.getChildren().addAll(idLabel, nameLabel, descriptionLabel, priceLabel);
        serviceRoot.add(imageView, 0, 0);
        serviceRoot.add(vbox, 1, 0);
        serviceRoot.add(actionbox, 2, 0);

        serviceRoot.setStyle("-fx-padding: 15; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #cccccc; -fx-border-width: 1;");

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(imageView.getFitWidth());

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(100);

        ColumnConstraints column = new ColumnConstraints();
        column.setHgrow(Priority.ALWAYS);

        serviceRoot.getColumnConstraints().addAll(col1, col2, column);
        serviceRoot.setPrefWidth(tilepaneServices.getWidth());
        GridPane.setHalignment(actionbox, HPos.RIGHT);
        serviceRoot.setStyle("-fx-hgap: 15px;");

        tilepaneServices.widthProperty().addListener((ob, ov, nv) -> {
            serviceRoot.setPrefWidth(nv.doubleValue() - 15);
        });

        tilepaneServices.getChildren().add(serviceRoot);
    }

    public void updateStockUI() {
        Map<String, Integer> fns = foodController.getAllFoodStockAndName();
        for (Node n : foodView.getChildren()) {
            if (n instanceof Pane) {
                Label nameLabel = (Label) n.lookup("#nameLabel");
                Label quantityLabel = (Label) n.lookup("#stockLabel");
                String name = nameLabel.getText();
                quantityLabel.setText(fns.get(name) + "");
                System.out.println(name);
            }
        }
    }


    double tc = 0;


    private void removeService(ActionEvent e) {


        Button clicked = (Button) e.getSource();
        service f = (service) clicked.getUserData();
        System.out.println(f.getId());
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        Stage owner = (Stage) logout.getScene().getWindow();
        modalStage.initOwner(owner);
        modalStage.initStyle(StageStyle.UNDECORATED);
        modalStage.initStyle(StageStyle.TRANSPARENT);

        Text serviceID = new Text("ID");
        serviceID.setFont(new Font(16.0));
        Text sidTxt = new Text(f.getId() + "");
        VBox sidBox = new VBox(serviceID, sidTxt);

        Text serviceName = new Text("Name");
        serviceName.setFont(new Font(16.0));
        Text snameField = new Text(f.getName());
        VBox nameFieldBox = new VBox(serviceName, snameField);

        Text priceTxt1 = new Text("Price");
        priceTxt1.setFont(new Font(16.0));
        Text priceTxt2 = new Text(f.getPrice() + "");
        VBox priceBox = new VBox(priceTxt1, priceTxt2);

        Text desc = new Text("Category");
        desc.setFont(new Font(16.0));
        Text descTxt = new Text(f.getDescription() + "");
        VBox descBox = new VBox(desc, descTxt);

        VBox credentialsPane = new VBox(sidBox, nameFieldBox, priceBox, descBox);
        credentialsPane.setStyle("-fx-spacing: 15px;" + "-fx-padding: 30px;");

        BorderPane modalRoot = new BorderPane();

        Text modalTitle = new Text("Remove Service");
        modalTitle.setFont(new Font(28.0));

        Text modalHint = new Text("Are you sure you want to remove this service?");
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

        cancelButton.setOnAction(act -> {
            loaderSettings.removeDimmingEffect((Stage) logout.getScene().getWindow());
            modalStage.close();
        });
        confirmButton.setOnAction(act -> {
            removeServiceFromDB(f.getId(), e, modalStage);
        });
    }

    private void removeServiceFromDB(int sid, ActionEvent e, Stage st) {

        loaderSettings.applyDimmingEffect(e);

        Task<Boolean> loadSceneTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    boolean success = serviceController.deleteService(sid);
                    if (!success) {
                        throw new Exception("Failed to delete.");
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

                notificationManager.showNotification(String.format("Successfully removed %d", sid), "success", (Stage) logout.getScene().getWindow());
                st.close();
                generateService();
                loaderSettings.removeDimmingEffect((Stage) logout.getScene().getWindow());
                loaderSettings.removeDimmingEffect(e);
                loadingStage.hide();
                System.out.println("Service removed successfully.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadSceneTask.setOnFailed(er -> {
            try {
                loadingStage.hide();
                notificationManager.showNotification("Failed to removed service", "failure", st);
                loaderSettings.removeDimmingEffect(e);
                System.out.println("Failed to remove service. Please try again.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        new Thread(loadSceneTask).start();
    }

    private void editService(ActionEvent e) {

        Button clicked = (Button) e.getSource();
        service f = (service) clicked.getUserData();
        System.out.println(f.getId());
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        Stage owner = (Stage) logout.getScene().getWindow();
        modalStage.initOwner(owner);
        modalStage.initStyle(StageStyle.UNDECORATED);
        modalStage.initStyle(StageStyle.TRANSPARENT);

        Text serviceID = new Text("ID");
        serviceID.setFont(new Font(16.0));
        Text sidTxt = new Text(f.getId() + "");
        VBox sidBox = new VBox(serviceID, sidTxt);

        Text serviceName = new Text("Name");
        serviceName.setFont(new Font(16.0));
        TextField snameField = new TextField(f.getName());
        VBox nameFieldBox = new VBox(serviceName, snameField);

        Text priceTxt1 = new Text("Price");
        priceTxt1.setFont(new Font(16.0));
        TextField priceTxt2 = new TextField(f.getPrice() + "");

        priceTxt2.onKeyReleasedProperty().addListener((ob, ov, nv) -> {
            try {
                double d = Double.parseDouble(nv.toString());
            } catch (Exception err) {
                notificationManager.showNotification("Enter a double value", "failure", modalStage);
                return;
            }
        });
        double d = Double.parseDouble(priceTxt2.getText());

        VBox priceBox = new VBox(priceTxt1, priceTxt2);

        Text desc = new Text("Category");
        desc.setFont(new Font(16.0));
        TextField descTxt = new TextField(f.getDescription() + "");
        VBox descBox = new VBox(desc, descTxt);

        VBox editViewBox = new VBox(sidBox, nameFieldBox, priceBox, descBox);
        editViewBox.setStyle("-fx-spacing: 15px;" + "-fx-padding: 30px;");

        BorderPane modalRoot = new BorderPane();

        Text modalTitle = new Text("Edit Service");
        modalTitle.setFont(new Font(28.0));

        Text modalHint = new Text("Are you sure you want to Update this service?");
        VBox topBox = new VBox(modalTitle, modalHint);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-padding: 10px;");

        modalRoot.setTop(topBox);
        BorderPane.setAlignment(topBox, Pos.CENTER);

        modalRoot.setCenter(editViewBox);

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

        cancelButton.setOnAction(act -> {
            loaderSettings.removeDimmingEffect((Stage) logout.getScene().getWindow());
            modalStage.close();
        });
        confirmButton.setOnAction(act -> {
            editServiceFromDB(f.getId(), snameField.getText(), descTxt.getText(),
                    d, null, e, modalStage);
        });
    }

    private void editServiceFromDB(int sid, String serviceName, String description,
                                   double price, File imageFile, ActionEvent e, Stage st) {

        loaderSettings.applyDimmingEffect(e);

        Task<Boolean> loadSceneTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    boolean success = serviceController.updateServiceInDatabase(sid, serviceName,
                            description, price, imageFile);
                    if (!success) {
                        throw new Exception("Failed to update.");
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

                notificationManager.showNotification(String.format("Successfully updated %d", sid), "success", (Stage) logout.getScene().getWindow());
                st.close();
                generateService();

                loaderSettings.removeDimmingEffect((Stage) logout.getScene().getWindow());
                loaderSettings.removeDimmingEffect(e);

                loadingStage.hide();
                System.out.println("Service updated successfully.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        loadSceneTask.setOnFailed(er -> {
            try {
                loadingStage.hide();
                notificationManager.showNotification("Failed to update service", "failure", st);
                loaderSettings.removeDimmingEffect(e);
                System.out.println("Failed to remove service. Please try again.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        new Thread(loadSceneTask).start();
    }

    private Pane selectedPane;
    private Image selectedImage;
    private Image selectedCatgoryImage;
    private byte[] imageBytes;
    private File image;

    public void getFoodImage() {
        File file = this.chooseImageFile();
        if (file != null) {
            this.loadImage(file);
            this.image = file;
        } else {
            this.showAlert("No File Selected", "No file selected.");
        }

    }

    private File chooseImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", new String[]{"*.png", "*.jpg", "*.gif"}));
        return fileChooser.showOpenDialog((Stage) this.imageView.getScene().getWindow());
    }

    private void loadImage(File file) {
        try {
            this.imageBytes = Files.readAllBytes(file.toPath());
            this.selectedImage = new Image(file.toURI().toString());
            this.imageView.setImage(this.selectedImage);
            this.showAlert("Success", "Image loaded successfully. You can now save it to the database.");
        } catch (IOException var3) {
            IOException e = var3;
            e.printStackTrace();
        }

    }

    public void saveImage() {
        try {
            String fn = this.foodName.getText();
            double foodprice = Double.parseDouble(this.price.getText());
            String foodCat = this.cat.getText();
            this.saveImageToDatabase(foodprice, fn, foodCat);
            List<food> items = this.fetchFoodItems();
            ObservableList<food> foodData = FXCollections.observableArrayList();
            foodData.add((food) this.stockTableView.getItems());
            foodData.clear();
            ObservableList<food> foodItems = FXCollections.observableArrayList(items);
            this.stockTableView.setItems(foodItems);
        } catch (Exception var8) {
            Exception err = var8;
            err.printStackTrace();
        }

    }

    public void saveImageToDatabase(double price, String foodName, String category) {
        String sql = "{CALL add_new_food_menu(?,?,?,?,?)}";
        if (this.imageBytes == null) {
            this.showAlert("Error", "No image loaded. Please load an image first.");
        } else {
            try {
                Throwable var38 = null;
                Object var7 = null;

                try {
                    Connection con = DBConnection.getConnection();

                    try {
                        CallableStatement psmt = con.prepareCall(sql);

                        try {
                            psmt.setString(1, foodName);
                            psmt.setDouble(2, price);

                            try {

                                FileInputStream toSave = new FileInputStream(this.image);
                                InputStream compressedImage = ImageCompressor.compressImage(this.image, 0.35f);
                                psmt.setBinaryStream(3, compressedImage, compressedImage.available());
                                psmt.setString(4, category);
                                psmt.executeUpdate();

                                this.foodView.getChildren().clear();
                                this.foodView.getChildren().removeAll(new Node[0]);
                                List<food> Food = foodController.getFoodByCat(category);
                                Iterator var13 = Food.iterator();

                                Pane pane;
                                while (var13.hasNext()) {
                                    food selectedFood = (food) var13.next();
                                    if (selectedFood.getCategory().equals(category)) {
                                        pane = this.createAdminFoodPane(selectedFood);
                                        this.foodView.getChildren().add(pane);
                                    }
                                }

                                var13 = this.categoryView.getChildren().iterator();

                                label413:
                                while (true) {
                                    Node node;
                                    do {
                                        if (!var13.hasNext()) {
                                            break label413;
                                        }

                                        node = (Node) var13.next();
                                    } while (!(node instanceof Pane));

                                    pane = (Pane) node;
                                    pane.setStyle("-fx-background-color:  #2f847c;");
                                    Iterator var16 = pane.getChildren().iterator();

                                    while (var16.hasNext()) {
                                        Node child = (Node) var16.next();
                                        if (child instanceof Label) {
                                            Label label = (Label) child;
                                            if (label.getText().equalsIgnoreCase(category)) {
                                                pane.setStyle("-fx-border-color: red; -fx-border-width: 2;");
                                            }
                                        }
                                    }
                                }
                            } catch (Exception var33) {
                                Exception e = var33;
                                e.printStackTrace();
                            }

                            this.showAlert("Success", "Image saved to database successfully.");
                        } finally {
                            if (psmt != null) {
                                psmt.close();
                            }

                        }
                    } catch (Throwable var35) {
                        if (var38 == null) {
                            var38 = var35;
                        } else if (var38 != var35) {
                            var38.addSuppressed(var35);
                        }

                        if (con != null) {
                            con.close();
                        }

                        throw var38;
                    }

                    if (con != null) {
                        con.close();
                    }
                } catch (Throwable var36) {
                    if (var38 == null) {
                        var38 = var36;
                    } else if (var38 != var36) {
                        var38.addSuppressed(var36);
                    }

                    throw var38;
                }
            } catch (Throwable var37) {
                SQLException e = (SQLException) var37;
                e.printStackTrace();
            }

        }
    }

    public void getCategoryImage() {
        File file = this.chooseCategoryImageFile();
        if (file != null) {
            this.loadCategoryImage(file);
            this.image = file;
        } else {
            this.showAlert("No File Selected", "No file selected.");
        }

    }

    private File chooseCategoryImageFile() {
        FileChooser fileChooser2 = new FileChooser();
        fileChooser2.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", new String[]{"*.png", "*.jpg", "*.gif"}));
        return fileChooser2.showOpenDialog((Stage) this.CategoryimageView.getScene().getWindow());
    }

    private void loadCategoryImage(File file) {
        try {
            this.imageBytes = Files.readAllBytes(file.toPath());
            this.selectedCatgoryImage = new Image(file.toURI().toString());
            this.CategoryimageView.setImage(this.selectedCatgoryImage);
            this.showAlert("Success", "Image loaded successfully. You can now save it to the database.");
        } catch (IOException var3) {
            IOException e = var3;
            e.printStackTrace();
        }

    }

    public void saveCategoryImage() {
        try {
            String foodCategory = this.cat1.getText();
            this.saveCategoryImageToDatabase(foodCategory);
            this.displayMenu();
        } catch (Exception var2) {
            Exception err = var2;
            err.printStackTrace();
        }

    }

    public void saveCategoryImageToDatabase(String categoryName) {
        String sql = "INSERT INTO food_category(food_category, category_image) values(?,?)";
        if (this.imageBytes == null) {
            this.showAlert("Error", "No image loaded. Please load an image first.");
        } else {
            try {
                Throwable var28 = null;
                Object var4 = null;

                try {
                    Connection con = DBConnection.getConnection();

                    try {
                        PreparedStatement psmt = con.prepareStatement(sql);

                        try {
                            psmt.setString(1, categoryName);

                            try {
                                FileInputStream toSave = new FileInputStream(this.image);
                                psmt.setBinaryStream(2, toSave, (int) this.image.length());
                            } catch (Exception var23) {
                                Exception e = var23;
                                e.printStackTrace();
                            }

                            psmt.executeUpdate();
                            this.showAlert("Success", "Image saved to database successfully.");
                        } finally {
                            if (psmt != null) {
                                psmt.close();
                            }

                        }
                    } catch (Throwable var25) {
                        if (var28 == null) {
                            var28 = var25;
                        } else if (var28 != var25) {
                            var28.addSuppressed(var25);
                        }

                        if (con != null) {
                            con.close();
                        }

                        throw var28;
                    }

                    if (con != null) {
                        con.close();
                    }
                } catch (Throwable var26) {
                    if (var28 == null) {
                        var28 = var26;
                    } else if (var28 != var26) {
                        var28.addSuppressed(var26);
                    }

                    throw var28;
                }
            } catch (Throwable var27) {
                SQLException e = (SQLException) var27;
                e.printStackTrace();
            }

        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText((String) null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void cancelFoodAdd() {
        this.foodName.clear();
        this.price.clear();
        this.cat.clear();
        this.imageView.setImage((Image) null);
    }

    public void cancelFoodUpdate() {
        this.foodName2.clear();
        this.price2.clear();
    }

    public void cancelStockUpdate() {
        this.foodName3.clear();
        this.stockToadd.clear();
    }

    public void cancelCategoryAdd() {
        this.cat1.clear();
        this.CategoryimageView.setImage((Image) null);
    }

    public void DeleteFoodMenu() {
        String sql = "{CALL delete_food_menu(?)}";

        try {
            Throwable var22 = null;
            Object var3 = null;

            try {
                Connection con = DBConnection.getConnection();

                try {
                    PreparedStatement psmt = con.prepareStatement(sql);

                    try {
                        psmt.setString(1, this.foodName2.getText());
                        psmt.executeUpdate();
                        this.displayMenu();
                        this.showAlert("Success", "Food Menu deleted.");
                    } finally {
                        if (psmt != null) {
                            psmt.close();
                        }

                    }
                } catch (Throwable var19) {
                    if (var22 == null) {
                        var22 = var19;
                    } else if (var22 != var19) {
                        var22.addSuppressed(var19);
                    }

                    if (con != null) {
                        con.close();
                    }

                    throw var22;
                }

                if (con != null) {
                    con.close();
                }
            } catch (Throwable var20) {
                if (var22 == null) {
                    var22 = var20;
                } else if (var22 != var20) {
                    var22.addSuppressed(var20);
                }

                throw var22;
            }
        } catch (Throwable var21) {
            SQLException e = (SQLException) var21;
            e.printStackTrace();
            this.showAlert("Error", "Error adding stock to database: " + e.getMessage());
        }

    }

    public void UpdateFoodMenu() {
        String sql = "{CALL change_food_price(?,?)}";

        try {
            Throwable var22 = null;
            Object var3 = null;

            try {
                Connection con = DBConnection.getConnection();

                try {
                    PreparedStatement psmt = con.prepareStatement(sql);

                    try {
                        psmt.setString(1, this.foodName2.getText());
                        psmt.setDouble(2, Double.parseDouble(this.price2.getText()));
                        psmt.executeUpdate();
                        this.displayMenu();
                        this.showAlert("Success", "Food price updated.");
                    } finally {
                        if (psmt != null) {
                            psmt.close();
                        }

                    }
                } catch (Throwable var19) {
                    if (var22 == null) {
                        var22 = var19;
                    } else if (var22 != var19) {
                        var22.addSuppressed(var19);
                    }

                    if (con != null) {
                        con.close();
                    }

                    throw var22;
                }

                if (con != null) {
                    con.close();
                }
            } catch (Throwable var20) {
                if (var22 == null) {
                    var22 = var20;
                } else if (var22 != var20) {
                    var22.addSuppressed(var20);
                }

                throw var22;
            }
        } catch (Throwable var21) {
            SQLException e = (SQLException) var21;
            e.printStackTrace();
            this.showAlert("Error", "Error changing to database: " + e.getMessage());
        }

    }

    public void addStock() {
        String sql = "{CALL add_food_stock(?,?)}";

        try {
            Throwable var24 = null;
            Object var3 = null;

            try {
                Connection con = DBConnection.getConnection();

                try {
                    PreparedStatement psmt = con.prepareStatement(sql);

                    try {
                        psmt.setString(1, this.foodName3.getText());
                        psmt.setInt(2, Integer.parseInt(this.stockToadd.getText()));
                        psmt.executeUpdate();
                        List<food> items = this.fetchFoodItems();
                        ObservableList<food> foodItems = FXCollections.observableArrayList(items);
                        this.stockTableView.setItems(foodItems);
                        this.showAlert("Success", "Stock added.");
                    } finally {
                        if (psmt != null) {
                            psmt.close();
                        }

                    }
                } catch (Throwable var21) {
                    if (var24 == null) {
                        var24 = var21;
                    } else if (var24 != var21) {
                        var24.addSuppressed(var21);
                    }

                    if (con != null) {
                        con.close();
                    }

                    throw var24;
                }

                if (con != null) {
                    con.close();
                }
            } catch (Throwable var22) {
                if (var24 == null) {
                    var24 = var22;
                } else if (var24 != var22) {
                    var24.addSuppressed(var22);
                }

                throw var24;
            }

            updateStockUI();
        } catch (Throwable var23) {
            SQLException e = (SQLException) var23;
            e.printStackTrace();
            this.showAlert("Error", "Error adding stock to database: " + e.getMessage());
        }

    }

    public void displayMenu() {
        this.foodView.getChildren().clear();
        this.foodView.getChildren().removeAll(new Node[0]);
        this.categoryView.getChildren().clear();
        this.categoryView.getChildren().removeAll(new Node[0]);
        List<food> items = this.fetchFoodItems();
        ObservableList<food> foodData = this.stockTableView.getItems();
        foodData.clear();
        ObservableList<food> foodItems = FXCollections.observableArrayList(items);

        ObservableList<food> filteredFood = FXCollections.observableArrayList(
                foodItems.stream()
                        .filter(b -> !b.getStock_status().matches("NIL"))
                        .collect(Collectors.toList())
        );

        this.stockTableView.setItems(filteredFood);

        List<food> allFood = foodController.getAllFood();
        HashSet<String> catHash = new HashSet<>();
        for (food fd : allFood) {
            System.out.println(fd.getCategory());
            if (!catHash.contains(fd.getCategory())) {
                Pane cfp = this.createCatPane(fd);
                this.categoryView.getChildren().add(cfp);
                catHash.add(fd.getCategory());
            }
        }

        List<food> allCat = foodController.getAllCategory();

        Iterator var7 = allFood.iterator();

        food f;
        Pane cfp;
        while (var7.hasNext()) {
            f = (food) var7.next();
            cfp = this.createAdminFoodPane(f);
            this.foodView.getChildren().add(cfp);
        }

        var7 = allCat.iterator();


    }

    private Pane createAdminFoodPane(food fm) {
        Pane newPane = new Pane();

        try {
            Blob b = fm.getImage();
            Image img = ImageLoader.loadImageFromBlob(b, 200, 200);
            ImageView iv = new ImageView(img);
            iv.setFitHeight(100.0);
            iv.setFitWidth(70.0);
            iv.setPreserveRatio(true);
            newPane.setStyle("-fx-background-color:  #FFFFFF;");
            newPane.setPrefWidth(193.0);
            newPane.getChildren().add(iv);
            ((Node) newPane.getChildren().get(0)).setLayoutX(8.0);
            ((Node) newPane.getChildren().get(0)).setLayoutY(10.0);
            Label foodNameLabel = new Label(fm.getName());
            Label ks = new Label("ks");
            Label stock = new Label("stock");
            Label price = new Label(Double.toString(fm.getPrice()));
            Label currentStock = new Label(Integer.toString(fm.getStock()));
            foodNameLabel.setId("nameLabel");
            currentStock.setId("stockLabel");
            foodNameLabel.setWrapText(true);
            foodNameLabel.setMaxWidth(80.0);
            newPane.getChildren().addAll(new Node[]{foodNameLabel, ks, stock, price, currentStock});
            ((Node) newPane.getChildren().get(1)).setLayoutX(100.0);
            ((Node) newPane.getChildren().get(1)).setLayoutY(20.0);
            ((Node) newPane.getChildren().get(2)).setLayoutX(100.0);
            ((Node) newPane.getChildren().get(2)).setLayoutY(55.0);
            ((Node) newPane.getChildren().get(3)).setLayoutX(100.0);
            ((Node) newPane.getChildren().get(3)).setLayoutY(82.0);
            ((Node) newPane.getChildren().get(4)).setLayoutX(120.0);
            ((Node) newPane.getChildren().get(4)).setLayoutY(55.0);
            ((Node) newPane.getChildren().get(5)).setLayoutX(140.0);
            ((Node) newPane.getChildren().get(5)).setLayoutY(82.0);
        } catch (Exception var12) {
            Exception e = var12;
            e.printStackTrace();
            this.showAlert("Error", "Error adding food menu to database: " + e.getMessage());
        }

        return newPane;
    }

    private void setupTableView() {
        this.foodNameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        this.stockColumn.setCellValueFactory(new PropertyValueFactory("stock"));
        this.stockStatusColumn.setCellValueFactory(new PropertyValueFactory("stock_status"));
        List<food> items = this.fetchFoodItems();
        ObservableList<food> foodItems = FXCollections.observableArrayList(items);
        this.stockTableView.setItems(foodItems);
    }

    public List<food> fetchFoodItems() {
        List<food> foodItems = new ArrayList();
        String query = "SELECT food_name, current_stock, stock_status FROM food";

        try {
            Throwable var36 = null;
            Object var4 = null;

            try {
                Connection con = DBConnection.getConnection();

                try {
                    Statement stmt = con.createStatement();

                    try {
                        ResultSet rs = stmt.executeQuery(query);

                        try {
                            while (rs.next()) {
                                String foodName = rs.getString("food_name");
                                int stock = rs.getInt("current_stock");
                                String stockStatus = rs.getString("stock_status");
                                foodItems.add(new food(foodName, stock, stockStatus));
                            }
                        } finally {
                            if (rs != null) {
                                rs.close();
                            }

                        }
                    } catch (Throwable var32) {
                        if (var36 == null) {
                            var36 = var32;
                        } else if (var36 != var32) {
                            var36.addSuppressed(var32);
                        }

                        if (stmt != null) {
                            stmt.close();
                        }

                        throw var36;
                    }

                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (Throwable var33) {
                    if (var36 == null) {
                        var36 = var33;
                    } else if (var36 != var33) {
                        var36.addSuppressed(var33);
                    }

                    if (con != null) {
                        con.close();
                    }

                    throw var36;
                }

                if (con != null) {
                    con.close();
                }
            } catch (Throwable var34) {
                if (var36 == null) {
                    var36 = var34;
                } else if (var36 != var34) {
                    var36.addSuppressed(var34);
                }

                throw var36;
            }
        } catch (Throwable var35) {
            SQLException e = (SQLException) var35;
            e.printStackTrace();
        }

        return foodItems;
    }

    private Pane createCatPane(food fcm) {
        Pane newCatPane = new Pane();
        try {
            Blob b = fcm.getImage();
            Image img = ImageLoader.loadImageFromBlob(b, 200, 200);
            ImageView iv = new ImageView(img);
            iv.setFitHeight(60.0);
            iv.setFitWidth(84.0);
            iv.setPreserveRatio(true);
            newCatPane.setStyle("-fx-background-color:  #FFFFFF;");
            newCatPane.setPrefHeight(130.0);
            newCatPane.setPrefWidth(106.0);
            Label foodCategory = new Label(fcm.getCategory());
            System.out.println(fcm.getCategory());
            String foodCat = foodCategory.getText();
            newCatPane.setOnMouseClicked((event) -> {
                this.showFoodByCat(event, foodCat, newCatPane);
            });
            foodCategory.setWrapText(true);
            foodCategory.setMaxWidth(95.0);
            newCatPane.getChildren().addAll(new Node[]{iv, foodCategory});
            ((Node) newCatPane.getChildren().get(0)).setLayoutX(15.0);
            ((Node) newCatPane.getChildren().get(0)).setLayoutY(22.0);
            ((Node) newCatPane.getChildren().get(1)).setLayoutX(7.0);
            ((Node) newCatPane.getChildren().get(1)).setLayoutY(85.0);
            newCatPane.setId(foodCat.toLowerCase());
        } catch (Exception var9) {
            Exception e = var9;
            e.printStackTrace();
        }

        return newCatPane;
    }

    private void showFoodByCat(MouseEvent event, String category, Pane clickedPane) {
        this.foodView.getChildren().clear();
        this.foodView.getChildren().removeAll(new Node[0]);
        List<food> allFood = foodController.getFoodByCat(category);
        Iterator var6 = allFood.iterator();

        Pane pane;
        while (var6.hasNext()) {
            food selectedFood = (food) var6.next();
            if (selectedFood.getCategory().equals(category)) {
                pane = this.createAdminFoodPane(selectedFood);
                this.foodView.getChildren().add(pane);
            }
        }

        var6 = this.categoryView.getChildren().iterator();

        while (var6.hasNext()) {
            Node node = (Node) var6.next();
            if (node instanceof Pane) {
                pane = (Pane) node;
                pane.setStyle("-fx-background-color:  #e0e0e0;");
                pane.setOpacity(0.75);
            }
        }

        clickedPane.setStyle("-fx-border-color: #A9D4FF; -fx-border-width: 2;");
        clickedPane.setOpacity(1);
        this.selectedPane = clickedPane;
    }

    private void dump_pane() {

        for (Node n : foodView.getChildren()) {
            if (n instanceof Pane) {
                for (Node p : ((Pane) n).getChildren()) {
                    if (p instanceof ImageView) {
                        ((ImageView) p).setImage(null);
                    }
                }
                ((Pane) n).getChildren().clear();
            }
        }

        for (Node n : categoryView.getChildren()) {
            if (n instanceof Pane) {
                ((Pane) n).getChildren().clear();
            }
        }

        System.gc();
    }


}