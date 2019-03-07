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

public class MainMenuController extends VBox implements MenuController {
    private Stage stage;
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
    private Button highScoreButton;

    public MainMenuController(Stage stage, Settings settings) {
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

    public void show() {
        // Main menu is displayed first, so see it setRootToStage necessary
        if (stage.getScene() == null) {
            MenuController.setRootToStage(stage, this, settings);
            //ew ImageCursor(new Image("file:assets/img/gui/crosshair.png"))

        } else {
            this.stage.getScene().setRoot(this);
        }
    }

    @FXML
    void playButtonPress(ActionEvent event) {
        // Switch to play menu and clear this object
        (new PlayMenuController(stage, settings, "", Teams.NONE)).show();
        this.getChildren().clear();
    }

    @FXML
    void settingsButtonPress(ActionEvent event) {
        // Switch to settings menu and clear this object
        (new SettingsMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    @FXML
    void helpButtonPress(ActionEvent event) {
        // Switch to help menu and clear this object
        (new HelpMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    @FXML
    void highScoreButtonPress(ActionEvent event) {

    }

    @FXML
    void quitButtonPress(ActionEvent event) {
        stage.close();
    }
}