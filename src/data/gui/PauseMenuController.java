package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PauseMenuController extends AbstractMenuController {

    @FXML
    private Button backButton;

    @FXML
    private Button menuButton;

    public PauseMenuController(String menuPath){
        super("/data/gui/pause_menu.fxml");
    }

    @FXML
    void backButtonPress(ActionEvent event) {

    }

    @FXML
    void menuButtonPress(ActionEvent event) {

    }

}
