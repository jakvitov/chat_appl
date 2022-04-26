package ClientGUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class infoController {

    @FXML
    private Label serverInfoLabel;


    @FXML
    private Button serverInfoButton;

    @FXML
    private VBox infoWindow;

    @FXML
    protected void windowClose(){
        Stage stage = (Stage)serverInfoButton.getScene().getWindow();
        stage.close();
    }


}
