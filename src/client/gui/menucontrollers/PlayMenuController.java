package client.gui.menucontrollers;

import client.ConnectionType;
import client.Settings;
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

/**
 * PlayMenuController class. Contains loader and controller for the play menu
 * @author Dominik Rys
 */
public class PlayMenuController extends VBox implements MenuController {
    /**
     * Stage to display menu on
     */
    private Stage stage;

    /**
     * Settings object
     */
    private Settings settings;

    /**
     * Selected team
     */
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

    /**
     * Constructor
     * @param stage Stage to display menu on
     * @param settings Settings object
     * @param playerName Player name
     * @param selectedTeam Team selected by player
     */
    public PlayMenuController(Stage stage, Settings settings, String playerName, Teams selectedTeam) {
        // Set variables
        this.stage = stage;
        this.settings = settings;

        // Set team
        this.selectedTeam = selectedTeam;

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

        // Set name in case coming from another menu
        nameField.setText(playerName);

        // Set selected team if coming from another menu
        switch (selectedTeam) {
            case RED:
                selectRedTeam();
                break;
            case BLUE:
                selectBlueTeam();
                break;
            case GREEN:
                selectGreenTeam();
                break;
            case YELLOW:
                selectYellowTeam();
                break;
            default:
                break;
        }

        // Check what buttons should be selected
        checkButtons();
    }

    /**
     * Show menu on stage
     */
    public void show() {
        this.stage.getScene().setRoot(this);
    }

    /**
     * Go back to main menu
     * @param event Back button press
     */
    @FXML
    void backButtonPress(ActionEvent event) {
        // Switch to main menu and clear this object
        (new MainMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    /**
     * Check which buttons should be enabled according to the name entered
     * @param event Text entered
     */
    @FXML
    void handleNameInput(ActionEvent event) {
        // Check if correct input
        checkButtons();
    }

    /**
     * Select blue team
     * @param event Blue team button press
     */
    @FXML
    void teamBlueButtonPress(ActionEvent event) {
        // Set correct team and highlight buttons
        selectBlueTeam();
    }

    /**
     * Select blue team and set all the appropriate elements in the menu
     */
    private void selectBlueTeam() {
        selectedTeam = Teams.BLUE;
        teamBlueButton.setEffect(ControllerUtils.getMenuDropshadow());
        teamGreenButton.setEffect(null);
        teamRedButton.setEffect(null);
        teamYellowButton.setEffect(null);
        checkButtons();
    }

    /**
     * Select green team
     * @param event Green team button press
     */
    @FXML
    void teamGreenButtonPress(ActionEvent event) {
        // Set correct team and highlight buttons
        selectGreenTeam();
    }

    /**
     * Select green team and set all the appropriate elements in the menu
     */
    private void selectGreenTeam() {
        selectedTeam = Teams.GREEN;
        teamBlueButton.setEffect(null);
        teamGreenButton.setEffect(ControllerUtils.getMenuDropshadow());
        teamRedButton.setEffect(null);
        teamYellowButton.setEffect(null);
        checkButtons();
    }

    /**
     * Select red team
     * @param event Red team button press
     */
    @FXML
    void teamRedButtonPress(ActionEvent event) {
        // Set correct team and highlight buttons
        selectRedTeam();
    }

    /**
     * Select red team and set all the appropriate elements in the menu
     */
    private void selectRedTeam() {
        selectedTeam = Teams.RED;
        teamBlueButton.setEffect(null);
        teamGreenButton.setEffect(null);
        teamRedButton.setEffect(ControllerUtils.getMenuDropshadow());
        teamYellowButton.setEffect(null);
        checkButtons();
    }

    /**
     * Select yellow team
     * @param event Yellow team button press
     */
    @FXML
    void teamYellowButtonPress(ActionEvent event) {
        // Set correct team and highlight buttons
        selectYellowTeam();
    }

    /**
     * Select yellow team and set all the appropriate elements in the menu
     */
    private void selectYellowTeam() {
        selectedTeam = Teams.YELLOW;
        teamBlueButton.setEffect(null);
        teamGreenButton.setEffect(null);
        teamRedButton.setEffect(null);
        teamYellowButton.setEffect(ControllerUtils.getMenuDropshadow());
        checkButtons();
    }

    /**
     * Go to map selection screen which will be set for single player
     * @param event Single player button press
     */
    @FXML
    void singlePlayerButtonPress(ActionEvent event) {
        // Go to map selection screen  and clear menu
        (new MapSelectionController(stage, settings, ConnectionType.SINGLE_PLAYER, nameField.getText(), selectedTeam)).show();
        this.getChildren().clear();
    }

    /**
     * Go to map selection screen which will be set up for multi player hosting
     * @param event Multiplayer hosting button press
     */
    @FXML
    void multiCreateGameButtonPress(ActionEvent event) {
        // Go to map selection screen and clear menu
        (new MapSelectionController(stage, settings, ConnectionType.MULTI_PLAYER_HOST, nameField.getText(), selectedTeam)).show();
        this.getChildren().clear();
    }

    /**
     * Go to server selection screen
     * @param event Multiplayer join game button press
     */
    @FXML
    void multiJoinGameButtonPress(ActionEvent event) {
        // Go to server joining screen and clear this menu
        (new ServerJoinMenuController(stage, settings, ConnectionType.MULTI_PLAYER_JOIN, nameField.getText(), selectedTeam)).show();
        this.getChildren().clear();
    }

    /**
     * Check all elements according to the name entered, and modify them accordingly
     */
    private void checkButtons() {
        // Only allow going into single or multi player if a name has been entered
        if (nameField.getCharacters().length() > 0 && nameField.getCharacters().length() < 12) {
            characterErrorText.setManaged(false);
            characterErrorText.setVisible(false);
            tick.setVisible(true);
            if (selectedTeam != Teams.NONE) {
                singlePlayerButton.setDisable(false);
                multiJoinGameButton.setDisable(false);
                multiCreateGameButton.setDisable(false);
            }
        } else if (nameField.getCharacters().length() == 0) {
            singlePlayerButton.setDisable(true);
            multiJoinGameButton.setDisable(true);
            multiCreateGameButton.setDisable(true);
            characterErrorText.setManaged(false);
            characterErrorText.setVisible(false);
            tick.setVisible(false);
        } else {
            singlePlayerButton.setDisable(true);
            multiJoinGameButton.setDisable(true);
            multiCreateGameButton.setDisable(true);
            characterErrorText.setManaged(true);
            characterErrorText.setVisible(true);
            tick.setVisible(false);
        }
    }
}
