package client.gui.menucontrollers;

import client.ConnectionType;
import client.GameHandler;
import client.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.Constants;
import shared.lists.MapList;
import shared.lists.Team;

import java.io.IOException;

/**
 * MapSelectionController class. Has loader and controller for the map selection menu
 *
 * @author Dominik Rys
 */
public class MapSelectionController extends VBox implements MenuController {
    /**
     * Stage to display menu on
     */
    private Stage stage;

    /**
     * Settings object
     */
    private Settings settings;

    /**
     * Type of connection: multi player, single player, hosting or now
     */
    private ConnectionType connectionType;

    /**
     * Name of player
     */
    private String playerName;

    /**
     * Team selected by player
     */
    private Team selectedTeam;

    @FXML
    private Button meadowButton;

    @FXML
    private Button map2Button;

    @FXML
    private Button map3Button;

    @FXML
    private Button backButton;

    @FXML
    private HBox playerAmountBox;

    @FXML
    private Slider playerSlider;

    @FXML
    private Label playerNumberLabel;

    /**
     * Constructor
     *
     * @param stage          Stage to show menu on
     * @param settings       Settings object
     * @param connectionType Type of connection
     * @param playerName     Name of player
     * @param selectedTeam   Team selected by player
     */
    public MapSelectionController(Stage stage, Settings settings, ConnectionType connectionType, String playerName, Team selectedTeam) {
        // Set variables
        this.stage = stage;
        this.settings = settings;
        this.connectionType = connectionType;
        this.playerName = playerName;
        this.selectedTeam = selectedTeam;

        // Load FXML and set appropriate methods
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/map_selection.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (
                IOException exception) {
            throw new RuntimeException(exception);
        }

        // Show player slider if hosting the game
        if (connectionType == ConnectionType.MULTI_PLAYER_HOST) {
            playerAmountBox.setManaged(true);
            playerSlider.setMax(Constants.MAX_PLAYERS);
            playerSlider.setMin(2);
            playerSlider.setValue(2);
            playerSlider.setMajorTickUnit(1);
            playerNumberLabel.setText(Long.toString(Math.round(playerSlider.getValue())));
        } else {
            playerAmountBox.setManaged(false);
            playerAmountBox.setVisible(false);
        }
    }

    /**
     * Show menu on stage
     */
    @Override
    public void show() {
        this.stage.getScene().setRoot(this);
    }

    /**
     * Go back to play menu when the back button is presed
     *
     * @param event Back button press
     */
    @FXML
    void backButtonPress(ActionEvent event) {
        // Switch to play menu and clear this screen
        (new PlayMenuController(stage, settings, playerName, selectedTeam)).show();
        this.getChildren().clear();
    }

    /**
     * Set correct player number when slider dragged
     *
     * @param event Slider dragged event
     */
    @FXML
    void playerSliderDragged(MouseEvent event) {
        playerNumberLabel.setText(Long.toString(Math.round(playerSlider.getValue())));
    }

    /**
     * Select meadow map
     *
     * @param event Button press
     */
    @FXML
    void meadowButtonPress(ActionEvent event) {
        startGame(MapList.MEADOW);
    }

    /**
     * Select map 2
     *
     * @param event Button press
     */
    @FXML
    void map2ButtonPress(ActionEvent event) {
        startGame(MapList.MEADOWWITHWALLS);
    }

    /**
     * Select map 3
     *
     * @param event Button press
     */
    @FXML
    void map3ButtonPress(ActionEvent event) {
        startGame(MapList.BIGTESTMAP);
    }

    /**
     * Start the gamehandler with the specified map and clear the screen
     *
     * @param mapList Chosen map
     */
    private void startGame(MapList mapList) {
        // Clear the screen
        this.getChildren().clear();

        // Start gamehandler with correct connectiontype, map and team
        (new GameHandler(stage, connectionType, settings, playerName, selectedTeam, mapList, playerNumberLabel.getText())).start();
    }
}
