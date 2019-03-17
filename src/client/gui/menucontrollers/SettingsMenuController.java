package client.gui.menucontrollers;

import client.Settings;
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

/**
 * SettingsMenuController class. Has loader and controller for the settings menu
 * @author Dominik Rys
 */
public class SettingsMenuController extends VBox implements MenuController {
    /**
     * Stage to display menu on
     */
    private Stage stage;

    /**
     * Settings object
     */
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

    /**
     * Constructor
     * @param stage Stage to show menu on
     * @param settings Settings object
     */
    public SettingsMenuController(Stage stage, Settings settings) {
        // Set variables
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

    /**
     * Show menu on stage
     */
    @Override
    public void show() {
        this.stage.getScene().setRoot(this);
    }

    /**
     * Save settings and go back to the main menu
     * @param event Back button press
     */
    @FXML
    void backButtonPress(ActionEvent event) {
        // Save settings to file
        settings.saveToDisk();

        // Switch to main menu and clear this object
        (new MainMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    /**
     * Highlight fullscreen button and set fullscreen in settings
     * @param event Fullscreen button press
     */
    @FXML
    void displayFullscreenButtonPress(ActionEvent event) {
        settings.setFullScreen(true);
        displayFullscreenButton.setEffect(ControllerUtils.getMenuDropshadow());
        displayWindowedButton.setEffect(null);
        applyButton.setDisable(false);
    }

    /**
     * Highlight window button and set windowed in settings
     * @param event Windowed button press
     */
    @FXML
    void displayWindowedButtonPress(ActionEvent event) {
        settings.setFullScreen(false);
        displayFullscreenButton.setEffect(null);
        displayWindowedButton.setEffect(ControllerUtils.getMenuDropshadow());
        applyButton.setDisable(false);
    }

    /**
     * Set music to off
     * @param event Music off button press
     */
    @FXML
    void musicOffButtonPress(ActionEvent event) {
        settings.setMusicMute(true);
        musicOffButton.setEffect(ControllerUtils.getMenuDropshadow());
        musicOnButton.setEffect(null);
    }

    /**
     * Set music to on
     * @param event Music on button press
     */
    @FXML
    void musicOnButtonPress(ActionEvent event) {
        settings.setMusicMute(false);
        musicOffButton.setEffect(null);
        musicOnButton.setEffect(ControllerUtils.getMenuDropshadow());
    }

    /**
     * Set sound to off
     * @param event Sound off button press
     */
    @FXML
    void soundOffButtonPress(ActionEvent event) {
        settings.setSoundMute(true);
        soundOffButton.setEffect(ControllerUtils.getMenuDropshadow());
        soundOnButton.setEffect(null);
    }

    /**
     * Set sound to on
     * @param event Sound on button press
     */
    @FXML
    void soundOnButtonPress(ActionEvent event) {
        settings.setSoundMute(false);
        soundOffButton.setEffect(null);
        soundOnButton.setEffect(ControllerUtils.getMenuDropshadow());
    }

    /**
     * Set music volume according to slider position
     * @param event Music volume slider dragged
     */
    @FXML
    void musicVolumeSliderDragged(MouseEvent event) {
        settings.setMusicVolume((int) musicVolumeSlider.getValue());
    }

    /**
     * Set sound volume according to slider position
     * @param event Sound volume slider dragged
     */
    @FXML
    void soundVolumeSliderDragged(MouseEvent event) {
        settings.setSoundVolume((int) soundVolumeSlider.getValue());
    }

    /**
     * Set a new screen resolution
     * @param event Resolution selected from combo box
     */
    @FXML
    void resolutionComboBoxChanged(ActionEvent event) {
        // Get resolution from combobos and adjust the settings object accordingly
        String selectedResolution = resolutionComboBox.getValue();
        settings.setScreenWidth(Integer.parseInt(selectedResolution.substring(0, selectedResolution.indexOf('x'))));
        settings.setScreenHeight(Integer.parseInt(selectedResolution.substring(selectedResolution.indexOf('x') + 1)));
        applyButton.setDisable(false);
    }

    /**
     * Apply graphics settings
     * @param event Apply graphics settings button press
     */
    @FXML
    void applyButtonPress(ActionEvent event) {
        // Apply graphics settings
        updateScreen();

        // Disable button since settings already applied
        applyButton.setDisable(true);
    }

    /**
     * Set stage's dimensions according to saved settings
     */
    private void updateScreen() {
        stage.setFullScreen(settings.isFullScreen());
        stage.setWidth(settings.getScreenWidth());
        stage.setHeight(settings.getScreenHeight());
    }

    /**
     * Go to controls menu
     * @param event Controls button press
     */
    @FXML
    void controlsButtonPress(ActionEvent event) {
        // Switch to controls menu and clear this object
        (new ControlsSettingsController(stage, settings)).show();
        this.getChildren().clear();
    }

    /**
     * Reset settings to defaults
     * @param event Reset settings button press
     */
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
