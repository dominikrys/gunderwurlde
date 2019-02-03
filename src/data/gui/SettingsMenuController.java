package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;

public class SettingsMenuController extends AbstractMenuController {

    @FXML
    private Slider soundVolumeSlider;

    @FXML
    private Button soundOnButton;

    @FXML
    private Button soundOffButton;

    @FXML
    private Slider musicVolumeSlider;

    @FXML
    private Button musicOnButton;

    @FXML
    private Button musicOffButton;

    @FXML
    private Button displayWindowedButton;

    @FXML
    private Button displayFullscreenButton;

    @FXML
    private Button backButton;

    public SettingsMenuController(){
        super("/data/gui/fxml/settings_menu.fxml", Menus.SETTINGS);
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        currentMenu = Menus.MAIN_MENU;
    }

    @FXML
    void displayFullscreenButtonPress(ActionEvent event) {

    }

    @FXML
    void displayWindowedButtonPress(ActionEvent event) {

    }

    @FXML
    void musicOffButtonPress(ActionEvent event) {

    }

    @FXML
    void musicOnButtonPress(ActionEvent event) {

    }

    @FXML
    void musicVolumeSliderDragged(MouseEvent event) {

    }

    @FXML
    void soundOffButtonPress(ActionEvent event) {

    }

    @FXML
    void soundOnButtonPress(ActionEvent event) {

    }

    @FXML
    void soundVolumeSliderDragged(MouseEvent event) {

    }

}
