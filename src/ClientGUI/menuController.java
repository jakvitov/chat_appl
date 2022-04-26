package ClientGUI;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class menuController {

    @FXML
    private BorderPane menuPane;

    @FXML
    private ListView onlineList;

    @FXML
    private ListView messageArea;

    @FXML
    private Label welcomeText;

    @FXML
    private TextField messageInput;

    @FXML
    private Menu logInMenu;

    @FXML
    private MenuItem logInButton;

    @FXML
    private MenuItem closeButton;

    @FXML
    private Menu logoutButton;

    @FXML
    private Menu serverInfo;

    @FXML
    private MenuItem serverInfoButton;


    @FXML
    private Label clientName;


    @FXML
    protected void showInfo(){
        showInfoWindow((Stage)clientName.getScene().getWindow());
    }

    protected void showInfoWindow(Stage primaryStage){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("infoGUI.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage infoWindow = new Stage();
            infoWindow.setScene(new Scene(root1));
            infoWindow.initModality(Modality.NONE);
            infoWindow.initOwner(primaryStage);
            infoWindow.setX(primaryStage.getX() + 200);
            infoWindow.setY(primaryStage.getY() + 100);
            infoWindow.show();

        }
        catch (IOException IOE){
            System.out.println("Error while loading the login screen!");
            System.exit(1);
        }
    }

    @FXML
    protected void closeClient(){
        System.exit(0);
    }

    @FXML
    protected void logInAction (){
        ColorAdjust adj = new ColorAdjust(0,0,0,0);
        GaussianBlur blur = new GaussianBlur(10); // 55 is just to show edge effect more clearly.
        adj.setInput(blur);
        menuPane.setEffect(adj);
        logInScreen((Stage) clientName.getScene().getWindow());
    }

    public void initialize(){
        onlineList.getItems().add("\uD83D\uDFE2" + " Petr");
        onlineList.getItems().add("\uD83D\uDFE2" + " Pavel");
        onlineList.getItems().add("\uD83D\uDFE2" + " Ondřej");
        //We setup click action on individual people
        onlineList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                clientName.setText((String) onlineList.getSelectionModel().getSelectedItem());
            }
        });
    }

    //Display login screen to the user
    public void logInScreen (Stage primaryStage){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginGUI.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage logInWindow = new Stage();
            logInWindow.setScene(new Scene(root1));
            logInWindow.initModality(Modality.WINDOW_MODAL);
            logInWindow.initOwner(primaryStage);
            logInWindow.setX(primaryStage.getX() + 200);
            logInWindow.setY(primaryStage.getY() + 100);
            logInWindow.show();
            logInWindow.setOnCloseRequest(event -> {
                menuPane.setEffect(null);
            });
        }
        catch (IOException IOE){
            System.out.println("Error while loading the login screen!");
            System.exit(1);
        }
    }

    @FXML
    protected void textAdded(){
        String inputText = messageInput.getText();
        messageInput.clear();

        messageArea.getItems().add(inputText);

    }

}