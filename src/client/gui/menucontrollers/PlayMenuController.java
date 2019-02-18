package client.gui.menucontrollers;

import client.data.ConnectionType;
import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.lists.Teams;

import java.io.IOException;

public class PlayMenuController extends VBox implements MenuController {
    private Stage stage;
    private Settings settings;
    private Teams selectedTeam;

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
    @FXML
    private ImageView tick;

    public PlayMenuController(Stage stage, Settings settings) {
        this.stage = stage;
        this.settings = settings;

        // Set team to unset
        this.selectedTeam = Teams.NONE;

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

        // Hide name too long text and tick
        characterErrorText.setVisible(false);
        tick.setVisible(false);
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
        // Check if correct input
        checkButtons();
    }

    @FXML
    void teamBlueButtonPress(ActionEvent event) {
        // Set correct team and highlight buttons
        selectedTeam = Teams.BLUE;
        teamBlueButton.setEffect(ControllerUtils.getMenuDropshadow());
        teamGreenButton.setEffect(null);
        teamRedButton.setEffect(null);
        teamYellowButton.setEffect(null);
        checkButtons();
    }

    @FXML
    void teamGreenButtonPress(ActionEvent event) {
        // Set correct team and highlight buttons
        selectedTeam = Teams.GREEN;
        teamBlueButton.setEffect(null);
        teamGreenButton.setEffect(ControllerUtils.getMenuDropshadow());
        teamRedButton.setEffect(null);
        teamYellowButton.setEffect(null);
        checkButtons();
    }

    @FXML
    void teamRedButtonPress(ActionEvent event) {
        // Set correct team and highlight buttons
        selectedTeam = Teams.RED;
        teamBlueButton.setEffect(null);
        teamGreenButton.setEffect(null);
        teamRedButton.setEffect(ControllerUtils.getMenuDropshadow());
        teamYellowButton.setEffect(null);
        checkButtons();
    }

    @FXML
    void teamYellowButtonPress(ActionEvent event) {
        // Set correct team and highlight buttons
        selectedTeam = Teams.YELLOW;
        teamBlueButton.setEffect(null);
        teamGreenButton.setEffect(null);
        teamRedButton.setEffect(null);
        teamYellowButton.setEffect(ControllerUtils.getMenuDropshadow());
        checkButtons();
    }

    @FXML
    void singlePlayerButtonPress(ActionEvent event) {
        // Go to map selection screen  and clear menu
        (new MapSelectionController(stage, settings, ConnectionType.SINGLE_PLAYER, nameField.getText(), selectedTeam)).show();
        this.getChildren().clear();
    }

    @FXML
    void multiCreateGameButtonPress(ActionEvent event) {
        // Go to map selection screen and clear menu
        (new MapSelectionController(stage, settings, ConnectionType.MULTI_PLAYER_HOST, nameField.getText(), selectedTeam)).show();
        this.getChildren().clear();
    }

    @FXML
    void multiJoinGameButtonPress(ActionEvent event) {
        // Go to server joining screen and clear this menu
        (new ServerJoinMenuController(stage, settings, ConnectionType.MULTI_PLAYER_JOIN, nameField.getText(), selectedTeam)).show();
        this.getChildren().clear();
    }

    private void checkButtons() {
        // Only allow going into single or multi player if a name has been entered
        if (nameField.getCharacters().length() > 0 && nameField.getCharacters().length() < 12) {
            characterErrorText.setVisible(false);
            if (selectedTeam != Teams.NONE) {
                singlePlayerButton.setDisable(false);
                multiJoinGameButton.setDisable(false);
                multiCreateGameButton.setDisable(false);
                tick.setVisible(true);
            }
        } else if (nameField.getCharacters().length() == 0) {
            singlePlayerButton.setDisable(true);
            multiJoinGameButton.setDisable(true);
            multiCreateGameButton.setDisable(true);
            characterErrorText.setVisible(false);
            tick.setVisible(true);
        } else {
            singlePlayerButton.setDisable(true);
            multiJoinGameButton.setDisable(true);
            multiCreateGameButton.setDisable(true);
            characterErrorText.setVisible(true);
            tick.setVisible(false);
        }
    }
}
