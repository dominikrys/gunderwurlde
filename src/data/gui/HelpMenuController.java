package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HelpMenuController extends AbstractMenuController{
    @FXML
    private Button backButton;

    public HelpMenuController() {
        super("/data/gui/help_menu.fxml", Menus.HELP);
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        currentMenu = Menus.MAIN_MENU;
    }
}
