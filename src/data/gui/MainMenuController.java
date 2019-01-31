package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainMenuController extends AbstractMenuController {

    @FXML
    private Button playButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button quitButton;

    public MainMenuController(){
        super("/data/gui/main_menu.fxml");
    }

    @FXML
    void helpButtonPress(ActionEvent event) {

    }

    @FXML
    void playButtonPress(ActionEvent event) {

    }

    @FXML
    void quitButtonPress(ActionEvent event) {

    }

    @FXML
    void settingsButtonPress(ActionEvent event) {

    }

}
