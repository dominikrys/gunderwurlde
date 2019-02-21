package client.gui.menucontrollers;

import client.ConnectionType;
import client.GameHandler;
import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import shared.lists.MapList;
import shared.lists.Teams;

import java.io.File;
import java.io.IOException;

public class MapSelectionController extends VBox implements MenuController {
    private Stage stage;
    private Settings settings;
    private ConnectionType connectionType;
    private String playerName;
    private Teams selectedTeam;

    @FXML
    private Button meadowButton;

    @FXML
    private Button map2Button;

    @FXML
    private Button map3Button;

    @FXML
    private Button backButton;

    public MapSelectionController(Stage stage, Settings settings, ConnectionType connectionType, String playerName, Teams selectedTeam) {
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

        // Add creating game label
        Label loadingLabel = new Label("Creating game...");
        loadingLabel.setFont(new Font("Consolas", 50));
        loadingLabel.setTextFill(Color.WHITE);

/*
        THIS BREAKS THE RENDERER FOR SOME REASON! Opted for gif instead

        // Indefinite progress wheel
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: white;");
        progressIndicator.setMinWidth(75);
        progressIndicator.setMinHeight(75);
*/

        // Get loading gif
        ImageView progressIndicator = new ImageView(new Image("file:assets/img/gui/loading.gif"));
        this.setSpacing(40);
        this.getChildren().addAll(loadingLabel, progressIndicator);

        // Start gamehandler with correct connectiontype and map TODO: add team to this
        (new GameHandler(stage, connectionType, settings, playerName, selectedTeam, MapList.MEADOW)).start();
    }

    @FXML
    void map2ButtonPress(ActionEvent event) {

    }

    @FXML
    void map3ButtonPress(ActionEvent event) {

    }
}
