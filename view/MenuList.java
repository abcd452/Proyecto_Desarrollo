package view;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utilities.FA;

public class MenuList {

    protected VBox layout = new VBox();
    protected double fontButton;

    protected Rectangle separator(double width) {
        Rectangle separatorRect = new Rectangle();
        separatorRect.setHeight(width * 0.0036765);
        separatorRect.setWidth(width * 0.1);
        separatorRect.setFill(Color.web("#212828"));
        return separatorRect;
    }

    protected Rectangle separator2(double width) {
        Rectangle separatorRect = new Rectangle();
        separatorRect.setHeight(width * 0.01475);
        separatorRect.setWidth(width * 0.1);
        separatorRect.setFill(Color.web("#212828"));
        return separatorRect;
    }

    protected Label labelGenerator(String message, double width, double height, double percentage) {
        double labelFont = 22 - (22 * percentage);
        Label label = new Label();
        label.setText(message);
        label.setPrefWidth(width * 0.2058875);
        label.setPrefHeight(height * 0.05);
        label.setStyle("-fx-font-size: " + labelFont + "px;");
        label.setOnMouseEntered(e -> label.setStyle(label.getStyle() + "-fx-background-color: #151919;"));
        label.setOnMouseExited(e -> label.setStyle(label.getStyle() + "-fx-background-color: #212828;"));

        return label;
    }

    public void displayMenu() {
        layout.setVisible(true);
    }

    ;

}


