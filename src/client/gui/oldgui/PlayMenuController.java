package client.gui.oldgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PlayMenuController extends AbstractMenuController {

    @FXML
    private TextField nameField;

    @FXML
    private Button singlePlayerButton;

    @FXML
    private Button multiPlayerButton;

    @FXML
    private Button backButton;

    @FXML
    private Label characterErrorText;

    public PlayMenuController(){
        super("/client/gui/fxml/play_menu.fxml", Menus.PLAY);
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        currentMenu = Menus.MAIN_MENU;
    }

    @FXML
    void handleNameInput(ActionEvent event) {
        // Only allow going into single or multi player if a name has been entered
        if (nameField.getCharacters().length() > 0 && nameField.getCharacters().length() < 12) {
            singlePlayerButton.setDisable(false);
            multiPlayerButton.setDisable(false);
            characterErrorText.setVisible(false);
        } else if (nameField.getCharacters().length() == 0){
            singlePlayerButton.setDisable(true);
            multiPlayerButton.setDisable(true);
            characterErrorText.setVisible(false);
        } else {
            singlePlayerButton.setDisable(true);
            multiPlayerButton.setDisable(true);
            characterErrorText.setVisible(true);
        }
    }

    @FXML
    void multiPlayerButtonPress(ActionEvent event) {
        currentMenu = Menus.MULTI_PLAYER;
    }

    @FXML
    void singlePlayerButtonPress(ActionEvent event) {
        currentMenu = Menus.SINGLE_PLAYER;
    }

}
