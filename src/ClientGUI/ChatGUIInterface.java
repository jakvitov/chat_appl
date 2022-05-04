package ClientGUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatGUIInterface extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatGUIInterface.class.getResource("Resources/menuGUI.fxml"));
        String css = ChatGUIInterface.class.getResource("Resources/aditionalStyle.css").toExternalForm();
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add(css);
        stage.setResizable(false);
        stage.setTitle("Message!");
        stage.getIcons().add(new Image("file:src/ClientGUI/Resources/icon.png"));
        stage.setScene(scene);
        Platform.setImplicitExit(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}