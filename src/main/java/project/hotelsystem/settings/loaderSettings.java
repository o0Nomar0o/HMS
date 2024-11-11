package project.hotelsystem.settings;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * @author Nomar
 */

public class loaderSettings {
    public static void applyDimmingEffect(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene currentScene = stage.getScene();
        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(-0.5);
        currentScene.getRoot().setEffect(dim);
    }

    public static void applyDimmingEffect(Stage stage) {
        Scene currentScene = stage.getScene();
        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(-0.5);
        currentScene.getRoot().setEffect(dim);
    }

    public static void removeDimmingEffect(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene currentScene = stage.getScene();
        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(0);
        currentScene.getRoot().setEffect(dim);
    }

    public static void removeDimmingEffect(Stage stage) {
        Scene currentScene = stage.getScene();
        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(0);
        currentScene.getRoot().setEffect(dim);
    }


    public static Stage old_showLoadingScreen(Task<Parent> loadSceneTask, Stage mainStage) {

        ProgressIndicator circle_thing = new ProgressIndicator();
        circle_thing.progressProperty().bind(loadSceneTask.progressProperty());

        Label loadingLabel = new Label("Loading...");
        loadingLabel.textProperty().bind(loadSceneTask.messageProperty());


        VBox loadingLayout = new VBox(10, loadingLabel, circle_thing);
        loadingLayout.setStyle("-fx-background-color: transparent;");
        loadingLayout.setAlignment(Pos.CENTER);

        StackPane loadingPane = new StackPane(loadingLayout);
        loadingPane.setStyle("-fx-background-color: transparent;");


        Stage loadingStage = new Stage();
        loadingStage.initOwner(mainStage);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setResizable(false);
        loadingStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(loadingPane, 200, 100);
        scene.setFill(null);

        loadingStage.setScene(scene);


        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);

            }
        });

        centerLoadingStage(mainStage, loadingStage);

        mainStage.widthProperty().addListener((ob, ov, nv) -> centerLoadingStage(mainStage, loadingStage));
        mainStage.heightProperty().addListener((ob, ov, nv) -> centerLoadingStage(mainStage, loadingStage));

        loadingStage.show();

        return loadingStage;
    }

    public static Stage showLoadingScreen(Task<Parent> loadSceneTask, Stage mainStage) {

        HBox squareContainer = new HBox(5);
        squareContainer.setAlignment(Pos.CENTER);

        Color[] colors = {Color.web("#FF6F61"),
                Color.web("#6B5B95"),
                Color.web("#88B04B"),
                Color.web("#F8B04B")};

        for (int i = 0; i < 4; i++) {
            Rectangle square = new Rectangle(20, 20);
            square.setFill(colors[i]);
            square.setArcWidth(5);
            square.setArcHeight(5);

            TranslateTransition shuffle = new TranslateTransition(Duration.seconds(0.4), square);
            shuffle.setByX(10);
            shuffle.setAutoReverse(true);
            shuffle.setCycleCount(Animation.INDEFINITE);
            shuffle.setDelay(Duration.seconds(0.2 * i));
            shuffle.play();

            squareContainer.getChildren().add(square);
        }

        Rectangle frame = new Rectangle(150, 40);
        frame.setArcWidth(10);
        frame.setArcHeight(10);
        frame.setFill(Color.TRANSPARENT);


        StackPane framedSquares = new StackPane(frame, squareContainer);
        framedSquares.setAlignment(Pos.CENTER);

        Label loadingLabel = new Label("Loading...");
        loadingLabel.textProperty().bind(loadSceneTask.messageProperty());
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

        VBox loadingLayout = new VBox(15, framedSquares, loadingLabel);
        loadingLayout.setAlignment(Pos.CENTER);
        loadingLayout.setStyle("-fx-background-color: transparent;");

        StackPane loadingPane = new StackPane(loadingLayout);
        loadingPane.setStyle("-fx-background-color: transparent");

        Stage loadingStage = new Stage();
        loadingStage.initOwner(mainStage);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setResizable(false);
        loadingStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(loadingPane, 250, 150);
        scene.setFill(null);
        loadingStage.setScene(scene);

        centerLoadingStage(mainStage, loadingStage);

        mainStage.widthProperty().addListener((ob, ov, nv) -> centerLoadingStage(mainStage, loadingStage));
        mainStage.heightProperty().addListener((ob, ov, nv) -> centerLoadingStage(mainStage, loadingStage));

        loadingStage.show();

        return loadingStage;
    }

    public static Stage showLoadingBolScreen(Task<Boolean> loadSceneTask, Stage mainStage) {

        ProgressIndicator circle_thing = new ProgressIndicator();
        circle_thing.progressProperty().bind(loadSceneTask.progressProperty());

        Label loadingLabel = new Label("Loading...");
        loadingLabel.textProperty().bind(loadSceneTask.messageProperty());


        VBox loadingLayout = new VBox(10, loadingLabel, circle_thing);
        loadingLayout.setStyle("-fx-background-color: transparent;");
        loadingLayout.setAlignment(Pos.CENTER);

        StackPane loadingPane = new StackPane(loadingLayout);
        loadingPane.setStyle("-fx-background-color: transparent;");


        Stage loadingStage = new Stage();
        loadingStage.initOwner(mainStage);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setResizable(false);
        loadingStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(loadingPane, 200, 100);
        scene.setFill(null);

        loadingStage.setScene(scene);


        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);

            }
        });

        centerLoadingStage(mainStage, loadingStage);

        mainStage.widthProperty().addListener((ob, ov, nv) -> centerLoadingStage(mainStage, loadingStage));
        mainStage.heightProperty().addListener((ob, ov, nv) -> centerLoadingStage(mainStage, loadingStage));

        loadingStage.show();

        return loadingStage;
    }

    public static void centerLoadingStage(Stage mainStage, Stage loadingStage) {

        loadingStage.setX(mainStage.getX() + (mainStage.getWidth() / 2) - 100);
        loadingStage.setY(mainStage.getY() + (mainStage.getHeight() / 2) - 50);

    }


}
