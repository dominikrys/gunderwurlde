package client.gui.menucontrollers;

import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ControlsSettingsController extends VBox implements MenuController{
    private Stage stage;
    private Settings settings;

    @FXML
    private Button upButton;

    @FXML
    private Button backButton;

    public ControlsSettingsController(Stage stage, Settings settings) {
        this.stage = stage;
        this.settings = settings;

        // Load FXML and set appropriate methods
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/controls_menu.fxml"));
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
        // Switch to settings menu and clear this object
        (new SettingsMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    @FXML
    void upButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event -> {
            String pressed = event.getCode().toString();
            upButton.setText(pressed);
        });
    }
}
