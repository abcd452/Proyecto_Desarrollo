package messages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;

public class AlertBox {

    public static void display(String title, String message) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setWidth(screenSize.getWidth() * 0.20);
        window.setHeight(screenSize.getHeight() * 0.15);

        Label label = new Label();
        label.setText(message);

        Button closeButton = new Button();
        closeButton.setText("Cerrar");
        closeButton.setOnAction(e -> window.close());
        closeButton.setPrefSize(screenSize.getWidth() * 0.12, screenSize.getHeight() * 0.025);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #22282A");
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setSpacing(20);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("Popup.css");

        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();

    }
}