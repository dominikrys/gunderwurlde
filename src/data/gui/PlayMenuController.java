package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PlayMenuController extends AbstractMenuController {

    @FXML
    private TextField nameField;

    @FXML
    private Button singlePlayerButton;

    @FXML
    private Button multiPlayerButton;

    @FXML
    private Button backButton;

    public PlayMenuController(){
        super("/data/gui/play_menu.fxml", Menus.PLAY);
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        currentMenu = Menus.MAIN_MENU;
    }

    @FXML
    void handleNameInput(ActionEvent event) {
        // Only allow going into single or multi player if a name has been entered
        if (nameField.getCharacters().length() > 0) {
            singlePlayerButton.setDisable(false);
            multiPlayerButton.setDisable(false);
        } else {
            singlePlayerButton.setDisable(true);
            multiPlayerButton.setDisable(true);
        }
    }

    @FXML
    void multiPlayerButtonPress(ActionEvent event) {

    }

    @FXML
    void singlePlayerButtonPress(ActionEvent event) {

    }

}
