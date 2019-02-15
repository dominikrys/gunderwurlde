package client.gui.menucontrollers;

import client.data.ConnectionType;
import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayMenuController extends VBox implements MenuController {
    private Stage stage;
    private Settings settings;

    @FXML
    private TextField nameField;
    @FXML
    private Button singlePlayerButton;
    @FXML
    private Button multiCreateGameButton;
    @FXML
    private Button multiJoinGameButton;
    @FXML
    private Button backButton;
    @FXML
    private Label characterErrorText;
    @FXML
    private Button teamRedButton;
    @FXML
    private Button teamBlueButton;
    @FXML
    private Button teamGreenButton;
    @FXML
    private Button teamYellowButton;

    public PlayMenuController(Stage stage, Settings settings) {
        this.stage = stage;
        this.settings = settings;

        // Load FXML and set appropriate methods
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/play_menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (
                IOException exception) {
            throw new RuntimeException(exception);
        }

        // Divert focus away from namefield
        nameField.setFocusTraversable(false);
    }

    public void show() {
        this.stage.getScene().setRoot(this);
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        // Switch to main menu and clear this object
        (new MainMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    @FXML
    void handleNameInput(ActionEvent event) {
        // Only allow going into single or multi player if a name has been entered
        if (nameField.getCharacters().length() > 0 && nameField.getCharacters().length() < 12) {
            singlePlayerButton.setDisable(false);
            multiJoinGameButton.setDisable(false);
            multiCreateGameButton.setDisable(false);
            characterErrorText.setVisible(false);
        } else if (nameField.getCharacters().length() == 0) {
            singlePlayerButton.setDisable(true);
            multiJoinGameButton.setDisable(true);
            multiCreateGameButton.setDisable(true);
            characterErrorText.setVisible(false);
        } else {
            singlePlayerButton.setDisable(true);
            multiJoinGameButton.setDisable(true);
            multiCreateGameButton.setDisable(true);
            characterErrorText.setVisible(true);
        }
    }

    @FXML
    void teamBlueButtonPress(ActionEvent event) {

    }

    @FXML
    void teamGreenButtonPress(ActionEvent event) {

    }

    @FXML
    void teamRedButtonPress(ActionEvent event) {

    }

    @FXML
    void teamYellowButtonPress(ActionEvent event) {

    }

    @FXML
    void singlePlayerButtonPress(ActionEvent event) {
        // Go to map selection screen  and clear menu
        (new MapSelectionController(stage, settings, ConnectionType.SINGLE_PLAYER, nameField.getText())).show();
        this.getChildren().clear();
    }

    @FXML
    void multiCreateGameButtonPress(ActionEvent event) {
        // Go to map selection screen and clear menu
        (new MapSelectionController(stage, settings, ConnectionType.MULTI_PLAYER_HOST, nameField.getText())).show();
        this.getChildren().clear();
    }

    @FXML
    void multiJoinGameButtonPress(ActionEvent event) {
        // Go to server joining screen and clear this menu
        (new ServerJoinMenuController(stage, settings, ConnectionType.MULTI_PLAYER_JOIN, nameField.getText())).show();
        this.getChildren().clear();
    }
}
