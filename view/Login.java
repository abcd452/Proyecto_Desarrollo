package view;

import controller.DaoUser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import utilities.AlertBox;
import utilities.ProjectUtilities;

public class Login {

    private DaoUser user;
    private double percentage;
    private double buttonFont;
    private double buttonFontLogin;
    private Scene loginScene;
    private double width;
    private double height;
    private double labelFont;
    private double textFFont;

    public Login(double width, double height, double percentage, double buttonFont) {
        user = new DaoUser();
        this.percentage = percentage;
        this.buttonFont = buttonFont;
        this.width = width;
        this.height = height;
    }

    private TextField userIdTextField;
    private TextField passwordTextField;

    private TextField loginTextFieldTemplate(double width, double height, String message) {
        TextField textField = new TextField();
        textField.setPromptText(message);
        textField.setPrefSize(width, height);
        textField.setMaxSize(width, height);
        return textField;
    }
    
    private VBox loginGridPane(double width, double height) {
        double percentageWidth = (2560 - width) / 2560;
        double percentageHeight = (1440 - height) / 1440;
        double percentageLogin = Math.max(percentageWidth, percentageHeight);

        textFFont = 30 - ( 30 * percentageLogin);
        labelFont = 60 - ( 60 * percentageLogin);
        buttonFontLogin = 40 - (40 * percentageLogin);

        VBox vBox = new VBox();
        vBox.setSpacing(height * 0.05);
        vBox.setPadding(new Insets(0, 0, 25, 0));
        vBox.setStyle("-fx-background-color: #22282A;\n -fx-border-width: 0 3 3 3;\n -fx-border-color: #3C4448;\n -fx-border-radius: 5;"); //2D333B
        vBox.setPrefSize(width * 0.3, height * 0.6);
        vBox.setMaxWidth(width * 0.3);
        vBox.setAlignment(Pos.TOP_CENTER);

        HBox hBox = new HBox();
        hBox.setPrefSize(width * 0.3, height * 0.1);
        hBox.setAlignment(Pos.TOP_CENTER);

        Pane leftPane = new Pane();
        leftPane.setStyle("-fx-background-color: #22282A;\n -fx-border-width: 3 0 0 0;\n -fx-border-color: #3C4448;");
        leftPane.setMaxSize(width * 0.025, height * 0.1);
        leftPane.setMinSize(width * 0.025, height * 0.1);

        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(width * 0.25, height * 0.1);
        stackPane.setMaxWidth(width * 0.25);
        stackPane.setStyle("-fx-background-color: #171A1C;\n -fx-border-width: 0 3 3 3;\n -fx-border-color: #3C4448;\n -fx-border-radius: 0 0 3 3;");
        stackPane.setPadding(new Insets(20, 0, 0, 0));
        stackPane.setAlignment(Pos.TOP_CENTER);

        Pane rightPane = new HBox();
        rightPane.setStyle("-fx-background-color: #22282A;\n -fx-border-width: 3 0 0 0;\n -fx-border-color: #3C4448;");
        rightPane.setMaxSize(width * 0.025, height * 0.1);
        rightPane.setMinSize(width * 0.025, height * 0.1);

        hBox.getChildren().addAll(leftPane, stackPane, rightPane);


        Label loginLabel = new Label("INICIAR SESIÓN");
        loginLabel.setStyle(loginLabel.getStyle() + "-fx-font-size: " + labelFont + "px;");
        stackPane.getChildren().addAll(loginLabel);

        userIdTextField = loginTextFieldTemplate(width * 0.25 , height * 0.05 , "Número de documento");
        userIdTextField.setStyle(userIdTextField.getStyle() + " -fx-font-size: "+ textFFont +"px; ");

        passwordTextField = new PasswordField();
        passwordTextField.setMaxSize(width * 0.25 , height * 0.05 );
        passwordTextField.setPrefSize(width * 0.25 , height * 0.05 );
        passwordTextField.setStyle(passwordTextField.getStyle() + " -fx-font-size: "+ textFFont +"px; ");
        passwordTextField.setPromptText("Contraseña");

        Button loginButton = new Button("Iniciar sesión");
        loginButton.setPrefSize(width * 0.25 , height * 0.05 );
        loginButton.setStyle(loginButton.getStyle() + "-fx-font-size: "+ buttonFontLogin + "px;");
        loginButton.setOnMouseClicked(e -> loginAction(width, height));

        vBox.getChildren().addAll(hBox, userIdTextField, passwordTextField, loginButton);

        return vBox;
    }

    private VBox mainLoginPane(double width, double height) {

        // Solo para robar.
        //user.saveNewUser("Alexander","Gonzalez","1234",(short) 1,true,"1234");

        VBox background = new VBox();
        background.setStyle("-fx-background-color: #171A1C");
        background.setPrefSize(width, height);

        VBox gridPane = loginGridPane(width, height);

        background.getChildren().addAll(gridPane);
        background.setAlignment(Pos.CENTER);
        return background;
    }

    private void loginAction(double width, double height) {
        final int loginSuccess = user.loginUser(ProjectUtilities.clearWhiteSpaces(userIdTextField.getText()), passwordTextField.getText());
        switch (loginSuccess) {
            case 1:
                ClientMenu client = new ClientMenu(percentage, buttonFont);
                loginScene.setRoot(client.renderClientEditMenu(width, height));
                loginScene.getStylesheets().add("styles.css");
                break;
            case 0:
                /* ToDo */
                break;
            default:
                AlertBox.display("Error", "Contraseña o id incorrectos", "");
        }
    }

    public Scene renderLoginScene() {

        loginScene = new Scene(mainLoginPane(width, height), width, height);
        loginScene.getStylesheets().add("loginStyle.css");

        return loginScene;
    }

}
