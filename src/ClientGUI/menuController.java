package ClientGUI;

import Client.ClientBackend;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
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
import java.net.Inet4Address;
import java.util.ArrayList;

public class menuController {

    private ClientBackend clientBackend;

    private String scope;
    private Thread onlineClients;

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Resources/infoGUI.fxml"));
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

        clientBackend = new ClientBackend();

        onlineList.getItems().add("\uD83D\uDFE2" + " Petr");
        onlineList.getItems().add("\uD83D\uDFE2" + " Pavel");
        onlineList.getItems().add("\uD83D\uDFE2" + " Ondřej");
        //We setup click action on individual people
        onlineList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            //If someone changes the person we load the new conversation
            @Override
            public void handle(MouseEvent event) {
                clientName.setText((String) onlineList.getSelectionModel().getSelectedItem());
                scope = (String) onlineList.getSelectionModel().getSelectedItem();
                messageArea.getItems().clear();
                ArrayList<String> newConversation = clientBackend.history((String) onlineList.getSelectionModel().getSelectedItem());
                if (newConversation != null){
                    for (String message : newConversation){
                     messageArea.getItems().add(message);
                    }
                }
            }
        });
    }

    //Display login screen to the user
    public void logInScreen (Stage primaryStage){
        try {
            clientBackend.logIn(Inet4Address.getLocalHost().getHostAddress(), "Petr");

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Resources/loginGUI.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage logInWindow = new Stage();
            logInWindow.setScene(new Scene(root1));
            logInWindow.initModality(Modality.WINDOW_MODAL);
            logInWindow.initOwner(primaryStage);
            logInWindow.setX(primaryStage.getX() + 200);
            logInWindow.setY(primaryStage.getY() + 100);
            logInWindow.show();
            logInWindow.setUserData(clientBackend);
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
        if (clientBackend.isLoggedIn() == false ){
            messageInput.clear();
            messageArea.getItems().add("Not logged in!");
        }
        else if (this.scope == null) {
            String inputText = messageInput.getText();
            messageInput.clear();
            messageArea.getItems().add("Select people to message to!");
        }
        else {
            String inputText = messageInput.getText();
            messageInput.clear();
            messageArea.getItems().add("You: " + inputText);
            clientBackend.message(scope, inputText);
        }
    }

}