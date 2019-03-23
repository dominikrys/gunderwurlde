package client.gui.menucontrollers;

import client.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * HighScoreMenuController class. Contains loader and controller for the high score menu
 *
 * @author Dominik Rys
 */
public class HighScoreMenuController extends VBox implements MenuController {
    /**
     * Stage to display menu on
     */
    private Stage stage;

    /**
     * Settings object
     */
    private Settings settings;

    @FXML
    private Button backButton;

    /**
     * Constructor
     *
     * @param stage    Stage to show menu on
     * @param settings Settings object
     */
    public HighScoreMenuController(Stage stage, Settings settings) {
        // Set variables
        this.stage = stage;
        this.settings = settings;

        // Load FXML and set appropriate methods
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/high_score_menu.fxml"));
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
     * Go back to the main menu
     *
     * @param event Button press
     */
    @FXML
    void backButtonPress(ActionEvent event) {
        // Switch to main menu and clear this screen
        (new MainMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    /**
     * Show menu on stage
     */
    @Override
    public void show() {
        this.stage.getScene().setRoot(this);
    }
}
