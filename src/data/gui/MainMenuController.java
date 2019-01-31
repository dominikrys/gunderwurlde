package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController extends AbstractMenuController {

    @FXML
    private Button playButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button quitButton;

    public MainMenuController() {
        super("/data/gui/main_menu.fxml", Menus.MAIN_MENU);
    }

    @FXML
    void playButtonPress(ActionEvent event) {
        currentMenu = Menus.PLAY;
    }

    @FXML
    void settingsButtonPress(ActionEvent event) {
        currentMenu = Menus.SETTINGS;
    }

    @FXML
    void helpButtonPress(ActionEvent event) {
        currentMenu = Menus.HELP;
    }

    @FXML
    void quitButtonPress(ActionEvent event) {
        currentMenu = Menus.QUIT;
    }

}
