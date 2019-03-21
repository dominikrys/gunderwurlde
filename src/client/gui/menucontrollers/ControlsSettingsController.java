package client.gui.menucontrollers;

import client.Settings;
import client.input.KeyAction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.lists.KeyActionList;

import java.io.IOException;

/**
 * ControlsSettingsController class. Controller and loader for the controls menu.
 *
 * @author Dominik Rys
 */
public class ControlsSettingsController extends VBox implements MenuController {
    /**
     * Stage to display menu on
     */
    private Stage stage;

    /**
     * Settings object
     */
    private Settings settings;

    @FXML
    private Button upButton;

    @FXML
    private Button interactButton;

    @FXML
    private Button dropButton;

    @FXML
    private Button reloadButton;

    @FXML
    private Button rightButton;

    @FXML
    private Button leftButton;

    @FXML
    private Button downButton;

    @FXML
    private Button item3Button;

    @FXML
    private Button item2Button;

    @FXML
    private Button item1Button;

    @FXML
    private Button escapeButton;

    @FXML
    private Button backButton;

    @FXML
    private Button defaultsButton;

    /**
     * Constructor
     *
     * @param stage    The stage to show the menu on
     * @param settings Settings object
     */
    public ControlsSettingsController(Stage stage, Settings settings) {
        // Set variables
        this.stage = stage;
        this.settings = settings;

        // Load FXML and set appropriate methods
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/controls_menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (
                IOException exception) {
            throw new RuntimeException(exception);
        }

        // Set text of all keyboard buttons to their current mappings
        upButton.setText(settings.getKey(KeyAction.UP));
        downButton.setText(settings.getKey(KeyAction.DOWN));
        leftButton.setText(settings.getKey(KeyAction.LEFT));
        rightButton.setText(settings.getKey(KeyAction.RIGHT));
        interactButton.setText(settings.getKey(KeyAction.INTERACT));
        dropButton.setText(settings.getKey(KeyAction.DROP));
        reloadButton.setText(settings.getKey(KeyAction.RELOAD));
        item1Button.setText(settings.getKey(KeyAction.ITEM1));
        item2Button.setText(settings.getKey(KeyAction.ITEM2));
        item3Button.setText(settings.getKey(KeyAction.ITEM3));
        escapeButton.setText(settings.getKey(KeyAction.ESC));
    }

    /**
     * Set menu to stage's scene
     */
    @Override
    public void show() {
        this.stage.getScene().setRoot(this);
    }

    /**
     * Method which gets triggered when back button is pressed. Save settings to a file and go back to settings
     *
     * @param event Button press
     */
    @FXML
    void backButtonPress(ActionEvent event) {
        // Save settings to file - not really necessary since the settings menu would do this too, but it's a nice
        // quality of life change in case the user quits the game without going back to the settings menu
        settings.saveToDisk();

        // Switch to settings menu and clear this object
        (new SettingsMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    /**
     * Reassign down button binding
     *
     * @param event Button press
     */
    @FXML
    void downButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        downButton.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            downButton.setText(pressed);
            settings.setKey(KeyAction.DOWN, pressed);
        });
    }

    /**
     * Reassign drop button binding
     *
     * @param event Button press
     */
    @FXML
    void dropButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        dropButton.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            dropButton.setText(pressed);
            settings.setKey(KeyAction.DROP, pressed);
        });
    }

    /**
     * Reassign escape button binding
     *
     * @param event Button press
     */
    @FXML
    void escapeButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        escapeButton.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            escapeButton.setText(pressed);
            settings.setKey(KeyAction.ESC, pressed);
        });
    }

    /**
     * Reassign interact button binding
     *
     * @param event Button press
     */
    @FXML
    void interactButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        interactButton.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            interactButton.setText(pressed);
            settings.setKey(KeyAction.INTERACT, pressed);
        });
    }

    /**
     * Reassign item1 button binding
     *
     * @param event Button press
     */
    @FXML
    void item1ButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        item1Button.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            item1Button.setText(pressed);
            settings.setKey(KeyAction.ITEM1, pressed);
        });
    }

    /**
     * Reassign button2 binding
     *
     * @param event Button press
     */
    @FXML
    void item2ButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        item2Button.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            item2Button.setText(pressed);
            settings.setKey(KeyAction.ITEM2, pressed);
        });
    }

    /**
     * Reassign item3 binding
     *
     * @param event Button press
     */
    @FXML
    void item3ButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        item3Button.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            item3Button.setText(pressed);
            settings.setKey(KeyAction.ITEM3, pressed);
        });
    }

    /**
     * Reassign left button binding
     *
     * @param event Button press
     */
    @FXML
    void leftButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        leftButton.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            leftButton.setText(pressed);
            settings.setKey(KeyAction.LEFT, pressed);
        });
    }

    /**
     * Reassign reload button binding
     *
     * @param event Button press
     */
    @FXML
    void reloadButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        reloadButton.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            reloadButton.setText(pressed);
            settings.setKey(KeyAction.RELOAD, pressed);
        });
    }

    /**
     * Reassign right button binding
     *
     * @param event Button press
     */
    @FXML
    void rightButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        rightButton.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            rightButton.setText(pressed);
            settings.setKey(KeyAction.RIGHT, pressed);
        });
    }

    /**
     * Reassign up button binding
     *
     * @param event Button press
     */
    @FXML
    void upButtonPress(ActionEvent event) {
        // Change text to tell user how to reassign button
        upButton.setText("<Press Button>");

        // Wait for input from user and set label and modify settings accordingly
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            upButton.setText(pressed);
            settings.setKey(KeyAction.UP, pressed);
        });
    }

    /**
     * Resets settings to defaults
     *
     * @param event Button press
     */
    @FXML
    void defaultsButtonPress(ActionEvent event) {
        // Set defaults in settings
        settings.mapDefaultKeys();

        // Save settings
        settings.saveToDisk();

        // Redraw scene to show changes
        (new ControlsSettingsController(stage, settings)).show();
        this.getChildren().clear();
    }
}
