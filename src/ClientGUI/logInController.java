package ClientGUI;

import DataStructures.logState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static ClientGUI.menuController.clientBackend;

public class logInController {

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField serverInput;

    @FXML
    private Label logInError;

    @FXML
    private Button loginButton;

    @FXML
    protected void usernameEntered (){
        System.out.println("Username: " + usernameInput.getText());
    }

    @FXML
    protected void serverIPInput (){
        System.out.println("Server IP: " + serverInput.getText());
    }

    @FXML
    protected void loginPress(){
        logState loginState = logState.OFFLINE;

        while (!loginState.equals(logState.CONNECTED)){
            String username = usernameInput.getText();
            String ipAdress = serverInput.getText();
            loginState = clientBackend.logIn(ipAdress, username);

            if (loginState.equals(logState.NAMETAKEN)){
                logInError.setVisible(true);
                logInError.setText("Name is already taken!");
            }

            else if (loginState.equals(logState.OFFLINE)){
                logInError.setVisible(true);
                logInError.setText("Given server is offline!");
            }
        }
        logInError.setVisible(true);
        logInError.setText("Sucessfully logged in!");
    }

    public void initialize(){
        logInError.setVisible(false);
    }
}
