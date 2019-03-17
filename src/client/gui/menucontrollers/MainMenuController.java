package client.gui.menucontrollers;

import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.lists.Teams;

import java.io.IOException;

/**
 * MainMenuController class. Contains loader and controller for the menu menu.
 * @author Dominik Rys
 */
public class MainMenuController extends VBox implements MenuController {
    /**
     * Stage to display menu on
     */
    private Stage stage;

    /**
     * Settings object
     */
    private Settings settings;

    @FXML
    private Button playButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button quitButton;

    @FXML
    private Button creditsButton;

    @FXML
    private Button highScoreButton;

    /**
     * Constructor
     * @param stage Stage to show menu on
     * @param settings Settings object
     */
    public MainMenuController(Stage stage, Settings settings) {
        // Set variables
        this.stage = stage;
        this.settings = settings;

        // Load FXML and set appropriate methods
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/main_menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (
                IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Show menu on stage
     */
    public void show() {
        // Main menu is displayed first, so see if setRootToStage necessary
        if (stage.getScene() == null) {
            MenuController.setRootToStage(stage, this, settings);
        } else {
            this.stage.getScene().setRoot(this);
        }
    }

    /**
     * Go to play menu when the play button is pressed
     * @param event Play button press
     */
    @FXML
    void playButtonPress(ActionEvent event) {
        // Switch to play menu and clear this object
        (new PlayMenuController(stage, settings, "", Teams.NONE)).show();
        this.getChildren().clear();
    }

    /**
     * Go to the settings menu when the settings button is pressed
     * @param event Settings button press
     */
    @FXML
    void settingsButtonPress(ActionEvent event) {
        // Switch to settings menu and clear this object
        (new SettingsMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    /**
     * Go to help menu when the help button is pressed
     * @param event Help button press
     */
    @FXML
    void helpButtonPress(ActionEvent event) {
        // Switch to help menu and clear this object
        (new HelpMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    /**
     * Go to high score menu when the high score button is pressed
     * @param event High score button press
     */
    @FXML
    void highScoreButtonPress(ActionEvent event) {
        // Switch to high score menu and clear this object
        (new HighScoreMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    /**
     * Go to the credits menu when the credits button is pressed
     * @param event Credits button press
     */
    @FXML
    void creditsButtonPress(ActionEvent event) {
        // Switch to credits screen and clear this object
        (new CreditsMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    /**
     * Close stage when the quit button is pressed
     * @param event Quit button press
     */
    @FXML
    void quitButtonPress(ActionEvent event) {
        stage.close();
    }
}