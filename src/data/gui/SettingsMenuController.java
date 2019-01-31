package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

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
        super("/data/gui/settings_menu.fxml");
    }

    @FXML
    void backButtonPress(ActionEvent event) {

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
