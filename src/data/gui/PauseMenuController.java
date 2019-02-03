package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PauseMenuController extends AbstractMenuController {

    @FXML
    private Button backButton;

    @FXML
    private Button menuButton;

    public PauseMenuController(String menuPath){
        super("/data/gui/fxml/pause_menu.fxml", Menus.MAIN_MENU);
    }

    @FXML
    void backButtonPress(ActionEvent event) {

    }

    @FXML
    void menuButtonPress(ActionEvent event) {

    }

}
