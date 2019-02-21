package client.gui.menucontrollers;

import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ControlsSettingsController extends VBox implements MenuController {
    private Stage stage;
    private Settings settings;

    @FXML
    private Button upButton;

    @FXML
    private Button interactButton;

    @FXML
    private Button dropButton;

    @FXML
    private Button reloadButton;

    @FXML
    private Button rightButton;

    @FXML
    private Button leftButton;

    @FXML
    private Button downButton;

    @FXML
    private Button item3Button;

    @FXML
    private Button item2Button;

    @FXML
    private Button item1Button;

    @FXML
    private Button escapeButton;

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
    void downButtonPress(ActionEvent event) {

    }

    @FXML
    void dropButtonPress(ActionEvent event) {

    }

    @FXML
    void escapeButtonPress(ActionEvent event) {

    }

    @FXML
    void interactButtonPress(ActionEvent event) {

    }

    @FXML
    void item1ButtonPress(ActionEvent event) {

    }

    @FXML
    void item2ButtonPress(ActionEvent event) {

    }

    @FXML
    void item3ButtonPress(ActionEvent event) {

    }

    @FXML
    void leftButtonPress(ActionEvent event) {

    }

    @FXML
    void reloadButtonPress(ActionEvent event) {

    }

    @FXML
    void rightButtonPress(ActionEvent event) {

    }

    @FXML
    void upButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            upButton.setText(pressed);
        });
    }
}
