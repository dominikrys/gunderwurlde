package client.gui.menucontrollers;

import client.ConnectionType;
import client.GameHandler;
import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import shared.Constants;
import shared.lists.MapList;
import shared.lists.Teams;

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

    @FXML
    private HBox playerAmountBox;

    @FXML
    private Slider playerSlider;

    @FXML
    private Label playerNumberLabel;


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

        // Show player slider if hosting the game
        if (connectionType == ConnectionType.MULTI_PLAYER_HOST) {
            playerAmountBox.setManaged(true);
            playerSlider.setMax(Constants.MAX_PLAYERS);
            playerSlider.setMin(1);
            playerSlider.setValue(1);
            playerSlider.setMajorTickUnit(1);
            playerNumberLabel.setText(Long.toString(Math.round(playerSlider.getValue())));
        } else {
            playerAmountBox.setManaged(false);
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
    void playerSliderDragged(MouseEvent event) {
        playerNumberLabel.setText(Long.toString(Math.round(playerSlider.getValue())));
    }

    @FXML
    void meadowButtonPress(ActionEvent event) {
        // Clear the screen and show loading screen
        displayMapLoading();

        // Start gamehandler with correct connectiontype, map and team
        (new GameHandler(stage, connectionType, settings, playerName, selectedTeam, MapList.MEADOW)).start();
    }

    private void displayMapLoading() {
        // Clear the screen
        this.getChildren().clear();

        // Add creating game label
        Label loadingLabel = new Label("Creating game...");
        loadingLabel.setFont(new Font("Consolas", 50));
        loadingLabel.setTextFill(Color.WHITE);

        // Get loading gif - used because javafx loading indicator broke the renderer
        ImageView progressIndicator = new ImageView(new Image("file:assets/img/gui/loading.gif"));
        this.setSpacing(40);
        this.getChildren().addAll(loadingLabel, progressIndicator);
    }

    @FXML
    void map2ButtonPress(ActionEvent event) {

    }

    @FXML
    void map3ButtonPress(ActionEvent event) {

    }
}
