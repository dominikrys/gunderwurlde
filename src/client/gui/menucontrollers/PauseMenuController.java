package client.gui.menucontrollers;

import client.Settings;
import client.gui.menucontrollers.ControllerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;

/**
 * PauseMenuController class. This is the controller for the pause menu.
 *
 * @author Dominik Rys
 */
public class PauseMenuController {
    /**
     * Settings object
     */
    private Settings settings;

    /**
     * Boolean for whether the back to menu button has been pressed
     */
    private boolean quitToMenuPressed;

    /**
     * Boolean for whether the back to game button has been pressed
     */
    private boolean backToGamePressed;

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
    private Button backToMenuButton;

    @FXML
    private Button backToGameButton;

    @FXML
    private Button confirmButton;

    /**
     * Close the pause menu and go back to the game
     *
     * @param event "Back to game" button press
     */
    @FXML
    void backToGameButtonPress(ActionEvent event) {
        backToGamePressed = true;
    }

    /**
     * Show button to confirm to quit to main menu
     *
     * @param event "Back to menu" button press
     */
    @FXML
    void backToMenuButtonPress(ActionEvent event) {
        // Enable confirm button
        confirmButton.setManaged(true);
        confirmButton.setVisible(true);
        confirmButton.setStyle("-fx-text-fill: red");
    }

    /**
     * Turn off in game music
     *
     * @param event Music off button press
     */
    @FXML
    void musicOffButtonPress(ActionEvent event) {
        settings.setMusicMute(true);
        musicOffButton.setEffect(ControllerUtils.getMenuDropshadow());
        musicOnButton.setEffect(null);
    }

    /**
     * Turn on the game music
     *
     * @param event Music on button press
     */
    @FXML
    void musicOnButtonPress(ActionEvent event) {
        settings.setMusicMute(false);
        musicOffButton.setEffect(null);
        musicOnButton.setEffect(ControllerUtils.getMenuDropshadow());
    }

    /**
     * Change the game music volume when the slider is dragged
     *
     * @param event Music slider dragged
     */
    @FXML
    void musicVolumeSliderDragged(MouseEvent event) {
        settings.setMusicVolume((int) musicVolumeSlider.getValue());
    }

    /**
     * Turn the game sound off
     *
     * @param event Sound off button press
     */
    @FXML
    void soundOffButtonPress(ActionEvent event) {
        settings.setSoundMute(true);
        soundOffButton.setEffect(ControllerUtils.getMenuDropshadow());
        soundOnButton.setEffect(null);
    }

    /**
     * Turn on the game sound
     *
     * @param event Sound on button press
     */
    @FXML
    void soundOnButtonPress(ActionEvent event) {
        settings.setSoundMute(false);
        soundOffButton.setEffect(null);
        soundOnButton.setEffect(ControllerUtils.getMenuDropshadow());
    }

    /**
     * Change the game sound volume when the slider is dragged
     *
     * @param event Sound slider dragged
     */
    @FXML
    void soundVolumeSliderDragged(MouseEvent event) {
        settings.setSoundVolume((int) soundVolumeSlider.getValue());
    }

    /**
     * Confirm that the user wants to quit to the main menu and quit to it
     *
     * @param event Confirm button press
     */
    @FXML
    void confirmButtonPress(ActionEvent event) {
        quitToMenuPressed = true;
    }

    /**
     * Intialise the settings object and update all relevant elements
     *
     * @param settings New settings object
     */
    public void initialise(Settings settings) {
        // Update settings object
        this.settings = settings;

        // Initialise booleans TODO: find a better way of doing this
        quitToMenuPressed = false;
        backToGamePressed = false;

        // Disable the confirm button
        confirmButton.setVisible(false);
        confirmButton.setManaged(false);

        // Set up menu according to settings object
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
    }

    /**
     * Get settings object
     *
     * @return Settings object
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Return whether quit to menu has been pressed
     *
     * @return Quit to menu has been pressed
     */
    public boolean getQuitToMenuPressed() {
        return quitToMenuPressed;
    }

    /**
     * Return whether back to menu has been pressed
     *
     * @return Back to menu has been pressed
     */
    public boolean getBackToGamePressed() {
        return backToGamePressed;
    }
}
