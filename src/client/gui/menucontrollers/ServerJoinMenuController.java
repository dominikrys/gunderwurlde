package client.gui.menucontrollers;

import client.data.ConnectionType;
import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerJoinMenuController extends VBox implements MenuController {
    private Stage stage;
    private Settings settings;
    private ConnectionType connectionType;
    private String playerName;

    @FXML
    private TextField IPField;

    @FXML
    private TextField portField;

    @FXML
    private Button backButton;

    @FXML
    private Button joinServerButton;

    public ServerJoinMenuController(Stage stage, Settings settings, ConnectionType connectionType, String playerName) {
        this.stage = stage;
        this.settings = settings;
        this.connectionType = connectionType;
        this.playerName = playerName;

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

        // Divert focus away from text boxes so tips are shown
        IPField.setFocusTraversable(false);
        portField.setFocusTraversable(false);

        // Disable join button
        joinServerButton.setDisable(true);
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        // Switch to play menu and clear this screen
        (new PlayMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    @FXML
    void IPFieldInput(ActionEvent event) {
        if (IPField.getText().length() > 0 && portField.getText().length() > 0) {
            joinServerButton.setDisable(false);
        } else {
            joinServerButton.setDisable(true);
        }
    }

    @FXML
    void portFieldInput(ActionEvent event) {
        if (IPField.getText().length() > 0 && portField.getText().length() > 0) {
            joinServerButton.setDisable(false);
        } else {
            joinServerButton.setDisable(true);
        }
    }

    @FXML
    void joinServerButtonPress(ActionEvent event) {

    }

    @Override
    public void show() {
        this.stage.getScene().setRoot(this);
    }
}
