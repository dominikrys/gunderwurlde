package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.layout.VBox;

import java.io.IOException;

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
        super("/data/gui/map_selection.fxml", Menus.MAP_SELECTION);
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
