package project.hotelsystem.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class notificationManager {

    public static void showNotification(String message, String type, Stage owner) {
        Label notificationLabel = new Label(message);
        notificationLabel.setStyle("-fx-padding: 10px; -fx-background-color: "
                + (type.equals("success") ? "#4CAF50" : "#F44336")
                + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 5;");

        Popup popup = new Popup();
        StackPane pane = new StackPane(notificationLabel);
        pane.setStyle("-fx-background-radius: 2.5em;");
        popup.getContent().add(pane);
        popup.setAutoHide(true);

        popup.show(owner);

        double stageX = owner.getX();
        double stageY = owner.getY();
        double stageWidth = owner.getWidth();
        double popupWidth = pane.getWidth();

        popup.setX((stageX + (stageWidth / 2)) - (popupWidth / 2));
        popup.setY(stageY + 25);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), pane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition sequence = new SequentialTransition(fadeIn, delay, fadeOut);
        sequence.setOnFinished(evt -> popup.hide());

        sequence.play();
    }

}
