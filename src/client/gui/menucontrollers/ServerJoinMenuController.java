package client.gui.menucontrollers;

import client.ConnectionType;
import client.GameHandler;
import client.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.lists.MapList;
import shared.lists.Team;

import java.io.IOException;

/**
 * ServerJoinMenuController class. Has loader and controller for the server joining menu.
 *
 * @author Dominik Rys
 */
public class ServerJoinMenuController extends VBox implements MenuController {
    /**
     * Stage to display menu on
     */
    private Stage stage;

    /**
     * Settings object
     */
    private Settings settings;

    /**
     * Connection type: single player, multiplayer etc.
     */
    private ConnectionType connectionType;

    /**
     * Name of player
     */
    private String playerName;

    /**
     * Selected team
     */
    private Team selectedTeam;

    @FXML
    private TextField IPField;

    @FXML
    private TextField portField;

    @FXML
    private Button backButton;

    @FXML
    private Button joinServerButton;

    /**
     * Constructor
     *
     * @param stage          Stage to show menu on
     * @param settings       Settings object
     * @param connectionType Type of connection
     * @param playerName     Name of player
     * @param selectedTeam   Team selected by player
     */
    public ServerJoinMenuController(Stage stage, Settings settings, ConnectionType connectionType, String playerName, Team selectedTeam) {
        // Set variables
        this.stage = stage;
        this.settings = settings;
        this.connectionType = connectionType;
        this.playerName = playerName;
        this.selectedTeam = selectedTeam;

        // Load FXML and set appropriate methods
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/server_join_menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (
                IOException exception) {
            throw new RuntimeException(exception);
        }

        // Disable join button
        joinServerButton.setDisable(true);
    }

    /**
     * Go back to the play menu
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
     * Disable or enable buttons when IP is typed
     *
     * @param event Text entered into IP field
     */
    @FXML
    void IPFieldInput(ActionEvent event) {
        if (IPField.getText().length() > 0 && portField.getText().length() > 0) {
            joinServerButton.setDisable(false);
        } else {
            joinServerButton.setDisable(true);
        }
    }

    /**
     * Disable or enable buttons when port is typed
     *
     * @param event Text entered into port field
     */
    @FXML
    void portFieldInput(ActionEvent event) {
        if (IPField.getText().length() > 0 && portField.getText().length() > 0) {
            joinServerButton.setDisable(false);
        } else {
            joinServerButton.setDisable(true);
        }
    }

    /**
     * Join server
     *
     * @param event Join server button press
     */
    @FXML
    void joinServerButtonPress(ActionEvent event) {
        // Disable join button to prevent double joins
        joinServerButton.setDisable(true);

        // Grab the specified map from the server attempting to connect to
        System.out.println("joining game");
        (new GameHandler(stage, connectionType, settings, playerName, selectedTeam, MapList.MEADOW, "1", IPField.getText(), portField.getText())).start();
    }

    /**
     * Show menu on stage
     */
    @Override
    public void show() {
        this.stage.getScene().setRoot(this);
    }
}
