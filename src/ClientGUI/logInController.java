package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class logInController {

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField serverInput;

    @FXML
    private Label logInError;

    public void initialize(){
        logInError.setVisible(false);
    }

    @FXML
    protected void usernameEntered (){
        System.out.println("Username: " + usernameInput.getText());
    }

    @FXML
    protected void serverIPInput (){
        System.out.println("Server IP: " + serverInput.getText());
    }
}
