package client.render;

import client.Settings;
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
    Settings settings;

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

    /**
     * Close the pause menu and go back to the game
     *
     * @param event "Back to game" button press
     */
    @FXML
    void backToGameButtonPress(ActionEvent event) {

    }

    /**
     * Go back to the main menu and quit the game
     *
     * @param event "Back to menu" button press
     */
    @FXML
    void backToMenuButtonPress(ActionEvent event) {

    }

    /**
     * Turn off in game music
     *
     * @param event Music off button press
     */
    @FXML
    void musicOffButtonPress(ActionEvent event) {

    }

    /**
     * Turn on the game music
     *
     * @param event Music on button press
     */
    @FXML
    void musicOnButtonPress(ActionEvent event) {

    }

    /**
     * Change the game music volume when the slider is dragged
     *
     * @param event Music slider dragged
     */
    @FXML
    void musicVolumeSliderDragged(MouseEvent event) {

    }

    /**
     * Turn the game sound off
     *
     * @param event Sound off button press
     */
    @FXML
    void soundOffButtonPress(ActionEvent event) {

    }

    /**
     * Turn on the game sound
     *
     * @param event Sound on button press
     */
    @FXML
    void soundOnButtonPress(ActionEvent event) {

    }

    /**
     * Change the game sound volume when the slider is dragged
     *
     * @param event Sound slider dragged
     */
    @FXML
    void soundVolumeSliderDragged(MouseEvent event) {

    }

    /**
     * Update the settings object stored by this class
     *
     * @param settings New settings object
     */
    public void updateSettings(Settings settings) {
        this.settings = settings;
    }

}
