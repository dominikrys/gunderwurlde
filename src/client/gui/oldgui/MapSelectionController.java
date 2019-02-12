package client.gui.oldgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MapSelectionController extends AbstractMenuController {

    @FXML
    private Button map1Button;

    @FXML
    private Button map2Button;

    @FXML
    private Button map3Button;

    @FXML
    private Button backButton;

    public MapSelectionController(){
        super("/client/gui/fxml/map_selection.fxml", Menus.MAP_SELECTION);
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        currentMenu = Menus.MAIN_MENU;
    }

    @FXML
    void map1ButtonPress(ActionEvent event) {

    }

    @FXML
    void map2ButtonPress(ActionEvent event) {

    }

    @FXML
    void map3ButtonPress(ActionEvent event) {

    }

}
