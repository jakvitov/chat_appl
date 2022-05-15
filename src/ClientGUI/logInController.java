package ClientGUI;

import DataStructures.logState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static ClientGUI.menuController.clientBackend;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.CHARTREUSE;

/**
 * A GUI controller class for the log in screen.
 */

public class logInController {

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField serverInput;

    @FXML
    private Label logInError;

    @FXML
    private Button loginButton;

    //This is fired when the log in button is pressed
    @FXML
    protected void loginPress() {
        //If we are already logged in we do nothing
        if (clientBackend.isLoggedIn()) {
            return;
        }

        logState loginState = logState.OFFLINE;

        String username = usernameInput.getText();
        String ipAdress = serverInput.getText();

        if (username.isEmpty() || ipAdress.isEmpty()){
            logInError.setVisible(true);
            logInError.setText("Username or server IP is empty!");
            return;
        }

        loginState = clientBackend.logIn(ipAdress, username);

        if (loginState.equals(logState.NAMETAKEN)) {
            logInError.setVisible(true);
            logInError.setText("Name is already taken!");
            return;
        }
        else if (loginState.equals(logState.OFFLINE)) {
            logInError.setVisible(true);
            logInError.setText("Given server is offline!");
            return;
        }
        //In case we sucessfully logged in
        //Note that logInError is also used to display positive response, just the color of the text changes
        logInError.setVisible(true);
        logInError.setTextFill(BLACK);
        logInError.setText("Sucessfully logged in!");
        usernameInput.clear();
        serverInput.clear();
    }

    public void initialize(){
        logInError.setVisible(false);
    }
}
