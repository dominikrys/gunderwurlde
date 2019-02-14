package client.gui.menucontrollers;

import client.GameHandler;
import client.data.ConnectionType;
import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import shared.lists.MapList;

import java.io.IOException;
import java.util.Map;

public class MapSelectionController extends VBox implements MenuController{
    private Stage stage;
    private Settings settings;
    private ConnectionType connectionType;
    private String playerName;

    @FXML
    private Button meadowButton;

    @FXML
    private Button map2Button;

    @FXML
    private Button map3Button;

    @FXML
    private Button backButton;

    public MapSelectionController(Stage stage, Settings settings, ConnectionType connectionType, String playerName) {
        this.stage = stage;
        this.settings = settings;
        this.connectionType = connectionType;
        this.playerName = playerName;

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
    }

    @Override
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
    void meadowButtonPress(ActionEvent event) {
        // Clear the screen
        this.getChildren().clear();

        //TODO: remove this with a nicer loading screen
        Label loadingLabel = new Label("Creating game...");
        loadingLabel.setFont(new Font("Consolas", 40));
        this.getChildren().add(loadingLabel);

        // Start gamehandler as single player with map meadow
        (new GameHandler(stage, ConnectionType.SINGLE_PLAYER, settings, playerName, MapList.MEADOW)).start();
    }

    @FXML
    void map2ButtonPress(ActionEvent event) {

    }

    @FXML
    void map3ButtonPress(ActionEvent event) {

    }
}
