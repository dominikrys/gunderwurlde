package client.gui.menucontrollers;

import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.Constants;

import java.io.IOException;

public class SettingsMenuController extends VBox implements MenuController {
    private Stage stage;
    private Settings settings;

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

    @FXML
    private Button controlsButton;

    @FXML
    private ComboBox<String> resolutionComboBox;

    @FXML
    private Button applyButton;

    @FXML
    private Button resetButton;

    public SettingsMenuController(Stage stage, Settings settings) {
        this.stage = stage;
        this.settings = settings;

        // Load FXML and set appropriate methods
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/settings_menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (
                IOException exception) {
            throw new RuntimeException(exception);
        }

        // Set up window according to settings object
        soundVolumeSlider.setValue(settings.getSoundVolume());
        musicVolumeSlider.setValue(settings.getMusicVolume());

        if (settings.isMusicMute()) {
            musicOffButton.setEffect(ControllerUtils.getMenuDropshadow());
            musicOnButton.setEffect(null);
        } else {
            musicOffButton.setEffect(null);
            musicOnButton.setEffect(ControllerUtils.getMenuDropshadow());
        }

        if (settings.isSoundMute()) {
            soundOffButton.setEffect(ControllerUtils.getMenuDropshadow());
            soundOnButton.setEffect(null);
        } else {
            soundOffButton.setEffect(null);
            soundOnButton.setEffect(ControllerUtils.getMenuDropshadow());
        }

        if (settings.isFullScreen()) {
            displayFullscreenButton.setEffect(ControllerUtils.getMenuDropshadow());
            displayWindowedButton.setEffect(null);
        } else {
            displayFullscreenButton.setEffect(null);
            displayWindowedButton.setEffect(ControllerUtils.getMenuDropshadow());
        }

        // Disable apply button
        applyButton.setDisable(true);

        // Populate resolution box with resolutions
        for (int[] resolution : Constants.SCREEN_RESOLUTIONS) {
            resolutionComboBox.getItems().add(resolution[0] + "x" + resolution[1]);
        }

        // Add current resolution to resolutioncombobox
        resolutionComboBox.getSelectionModel().select(settings.getScreenResolutionString());
    }

    @Override
    public void show() {
        this.stage.getScene().setRoot(this);
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        // Save settings to file
        settings.saveToDisk();

        // Switch to main menu and clear this object
        (new MainMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    @FXML
    void displayFullscreenButtonPress(ActionEvent event) {
        settings.setFullScreen(true);
        displayFullscreenButton.setEffect(ControllerUtils.getMenuDropshadow());
        displayWindowedButton.setEffect(null);
        applyButton.setDisable(false);
    }

    @FXML
    void displayWindowedButtonPress(ActionEvent event) {
        settings.setFullScreen(false);
        displayFullscreenButton.setEffect(null);
        displayWindowedButton.setEffect(ControllerUtils.getMenuDropshadow());
        applyButton.setDisable(false);
    }

    @FXML
    void musicOffButtonPress(ActionEvent event) {
        settings.setMusicMute(true);
        musicOffButton.setEffect(ControllerUtils.getMenuDropshadow());
        musicOnButton.setEffect(null);
        applyButton.setDisable(false);
    }

    @FXML
    void musicOnButtonPress(ActionEvent event) {
        settings.setMusicMute(false);
        musicOffButton.setEffect(null);
        musicOnButton.setEffect(ControllerUtils.getMenuDropshadow());
        applyButton.setDisable(false);
    }

    @FXML
    void soundOffButtonPress(ActionEvent event) {
        settings.setSoundMute(true);
        soundOffButton.setEffect(ControllerUtils.getMenuDropshadow());
        soundOnButton.setEffect(null);
        applyButton.setDisable(false);
    }

    @FXML
    void soundOnButtonPress(ActionEvent event) {
        settings.setSoundMute(false);
        soundOffButton.setEffect(null);
        soundOnButton.setEffect(ControllerUtils.getMenuDropshadow());
        applyButton.setDisable(false);
    }

    @FXML
    void musicVolumeSliderDragged(MouseEvent event) {
        settings.setMusicVolume((int) musicVolumeSlider.getValue());
        applyButton.setDisable(false);
    }

    @FXML
    void soundVolumeSliderDragged(MouseEvent event) {
        settings.setSoundVolume((int) soundVolumeSlider.getValue());
        applyButton.setDisable(false);
    }

    @FXML
    void resolutionComboBoxChanged(ActionEvent event) {
        // Get resolution from combobos and adjust the settings object accordingly
        String selectedResolution = resolutionComboBox.getValue();
        settings.setScreenWidth(Integer.parseInt(selectedResolution.substring(0, selectedResolution.indexOf('x'))));
        settings.setScreenHeight(Integer.parseInt(selectedResolution.substring(selectedResolution.indexOf('x') + 1)));
        applyButton.setDisable(false);
    }

    @FXML
    void applyButtonPress(ActionEvent event) {
        // Apply graphics settings
        updateScreen();

        // Disable button since settings already applied
        applyButton.setDisable(true);
    }

    private void updateScreen() {
        stage.setFullScreen(settings.isFullScreen());
        stage.setWidth(settings.getScreenWidth());
        stage.setHeight(settings.getScreenHeight());
    }

    @FXML
    void controlsButtonPress(ActionEvent event) {
        // Switch to controls menu and clear this object
        (new ControlsSettingsController(stage, settings)).show();
        this.getChildren().clear();
    }

    @FXML
    void resetButtonPress(ActionEvent event) {
        // Override settings object
        settings = new Settings();

        // Save settings
        settings.saveToDisk();

        // Reset window to show changes
        updateScreen();

        (new SettingsMenuController(stage, settings)).show();
        this.getChildren().clear();
    }
}
