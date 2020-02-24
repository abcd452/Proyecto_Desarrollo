import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.ConfirmBox;
import view.Login;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;


public class Main extends Application {
    public static SimpleIntegerProperty currentWindow = new SimpleIntegerProperty(-9999999);

    @Override
    public void start(Stage window) {
        //resolutions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double percentageWidth = (1920 - screenSize.getWidth()) / 1920;
        double percentageHeight = (1080 - screenSize.getHeight()) / 1080;
        double percentage = Math.max(percentageWidth, percentageHeight);
        double buttonFont = 22 - (22 * percentage);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsConfiguration graphicsConfiguration = gd.getDefaultConfiguration();
        java.awt.Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
        double width = screenSize.getWidth();  //2560 1920 1280 1152 1024; 768 40
        double height = screenSize.getHeight() - scnMax.bottom * 1.685;//1440 1080 720 648 576; 432 40

        Login login = new Login(width, height, percentage, buttonFont, currentWindow);
        System.out.println("En el main: " + percentage);
        System.out.println("En el main: " + width);
        System.out.println("En el main: " + height);
        AtomicReference<Scene> rootScene = new AtomicReference<>(login.renderLoginScene());
        currentWindow.addListener((obs, oldState, newState) -> {
            rootScene.set(login.renderLoginScene());
            window.setScene(rootScene.get());
        });

        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram(window);
        });

        window.setTitle("Mobile plans solution");
        window.setResizable(false);
        window.setScene(rootScene.get());
        window.show();
    }

    private void closeProgram(Stage window) {
        if (ConfirmBox.display("Cerrar Programa", "¿ Quieres cerrar el programa ?")) {
            window.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
