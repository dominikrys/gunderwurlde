package client.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsMenuController extends VBox implements MenuController {
    private Stage stage;

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

    public SettingsMenuController(Stage stage) {
        this.stage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/settings_menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (
                IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void show() {
        this.stage.getScene().setRoot(this);
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        (new MainMenuController(stage)).show();
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
