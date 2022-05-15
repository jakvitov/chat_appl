package ClientGUI;

import Client.ClientBackend;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import java.util.ArrayList;
import static Client.MessageListener.observableClients;

/**
 * This is the GUI controller for the menu of the client GUI application.
 */


public class menuController {

    //The user we are currently having a conversation with (selected user)
    private String scope;
    //A thread that takes care of drawing the online clients
    private Thread onlineClients;

    //Client backend that communicates with the menu controller
    public static ClientBackend clientBackend;


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
    private MenuBar usernameBox;

    @FXML
    private Menu username;

    //Triggered when server info button is pressed
    @FXML
    protected void showInfo(){
        showInfoWindow((Stage)clientName.getScene().getWindow());
    }

    //Triggered when client selects log out button
    @FXML
    protected void logOutAction(){
        if (this.clientBackend.isLoggedIn() == false){
            return;
        }
        this.username.setText("Offline");
        this.clientName.setText("");
        this.clientBackend.logOut();
        //Clear the current online clients list
        Platform.runLater(new Runnable() {
            public void run() {
                onlineList.getItems().clear();
                messageArea.getItems().clear();
            }
        });
    }
    //This method is called by the showInfo, it opens new window including the information about the
    //server and user
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

    //This method is triggered when the client clicks log in button
    @FXML
    protected void logInAction (){
        //We blur the main menu, this effect is reset after closing of the popup window
        ColorAdjust adj = new ColorAdjust(0,0,0,0);
        GaussianBlur blur = new GaussianBlur(10); // 55 is just to show edge effect more clearly.
        adj.setInput(blur);
        menuPane.setEffect(adj);
        logInScreen((Stage) clientName.getScene().getWindow());
    }

    public void initialize(){
        clientBackend = new ClientBackend();
        //List holding the online clients - it is updated when message containing new client list is received
        //by the server
        observableClients = FXCollections.observableArrayList();
        //We setup click action on individual people
        onlineList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            //If someone clicks on a person from the online list to chat with him
            @Override
            public void handle(MouseEvent event) {
                //If the scope is set, than we need to remove active listener from that scope
                if (scope != null && clientBackend.history(scope) != null){
                    clientBackend.history(scope).removeListener(menuController.this::reloadMessage);
                }

                clientName.setText((String) onlineList.getSelectionModel().getSelectedItem());
                scope = (String) onlineList.getSelectionModel().getSelectedItem();
                messageArea.getItems().clear();
                //We add new observable list containg the current conversation to the history
                ObservableList<String> newConversation = clientBackend.history((String)
                        onlineList.getSelectionModel().getSelectedItem());
                if (newConversation != null){
                    newConversation.forEach((message)->messageArea.getItems().add(message));
                }
                //When new message arrives we can rerender the list of the messages (message area)
                clientBackend.history(scope).addListener(menuController.this::reloadMessage);
            }
        });
    }

    //Display login screen to the user
    public void logInScreen (Stage primaryStage){
        if (this.clientBackend.isLoggedIn()){
            this.Alert("You are already logged in. If you want to change server, log out first!");
            menuPane.setEffect(null);
            return;
        }
        try {
            //Now we start listening to changes in the online list
            observableClients.addListener(this::reloadActiveList);

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
                //Dispose of the blur in the menu
                menuPane.setEffect(null);
                //In case the client just shut down the loggin window without successful login we just return
                if (this.clientBackend.isLoggedIn() == false){
                    return;
                }
                this.clientName.setText("");
                //Display our current username after we logged in
                this.username.setText(clientBackend.getName());
            });
        }
        catch (IOException IOE){
            System.out.println("Error while loading the login screen!");
            System.exit(1);
        }
    }

    //Triggered when the message text is typed in and sent
    @FXML
    protected void textAdded(){
        if (clientBackend.isLoggedIn() == false ){
            messageInput.clear();
            messageArea.getItems().add("Not logged in!");
        }
        //Without a scope we cannot send a message - (to whom?)
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

    //Triggered when server sends us new online clients list -> we rerender our current depricated list
    @FXML
    public void reloadActiveList(
        ListChangeListener.Change<? extends String> change) {
        Platform.runLater(new Runnable() {
            public void run() {
                onlineList.getItems().clear();
            }
        });
        for (String user : observableClients) {
            if (user.equals(clientBackend.getName())){
                continue;
            }
            Platform.runLater(new Runnable() {
                public void run() {
                    onlineList.getItems().add(user);
                }
            });
        }
    }
    //Reload current message area, this is fired when we get message by our scope client
    @FXML
    public void reloadMessage(
        ListChangeListener.Change<? extends String> change) {
        Platform.runLater(new Runnable() {
            public void run() {
                messageArea.getItems().clear();
            }
        });

        Platform.runLater(new Runnable() {
            public void run() {
                ObservableList<String> newConversation = clientBackend.history((String)
                        onlineList.getSelectionModel().getSelectedItem());
                if (newConversation != null){
                    newConversation.forEach((message)->messageArea.getItems().add(message));
                }
            }
        });
    }

    //Firea a basic alert with the given text
    public void Alert(String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("An error occured:");
        alert.setContentText(text);
        alert.showAndWait();
    }
}