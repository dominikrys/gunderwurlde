package client.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.Constants;

import java.io.IOException;

public class MainMenuController extends VBox {
    Stage stage;

    @FXML
    private Button playButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button quitButton;

    public MainMenuController(Stage stage) {
        this.stage = stage;

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

    public void display() {
        // Create the main scene
        if (stage.getScene() == null) {
            WindowControl.setRootToStage(stage, this);
        } else {

        }
        this.stage.getScene().setRoot(this);
    }

    @FXML
    void playButtonPress(ActionEvent event) {
        (new PlayMenuController(stage)).display();
    }

    @FXML
    void settingsButtonPress(ActionEvent event) {

    }

    @FXML
    void helpButtonPress(ActionEvent event) {

    }

    @FXML
    void quitButtonPress(ActionEvent event) {

    }
}