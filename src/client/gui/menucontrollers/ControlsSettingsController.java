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

        // Set text of all keyboard buttons to their current mappings
        upButton.setText(settings.getKey("up"));
        downButton.setText(settings.getKey("down"));
        leftButton.setText(settings.getKey("left"));
        rightButton.setText(settings.getKey("right"));
        interactButton.setText(settings.getKey("interact"));
        dropButton.setText(settings.getKey("drop"));
        reloadButton.setText(settings.getKey("reload"));
        item1Button.setText(settings.getKey("item1"));
        item2Button.setText(settings.getKey("item2"));
        item3Button.setText(settings.getKey("item3"));
        escapeButton.setText(settings.getKey("esc"));
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
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            downButton.setText(pressed);
            settings.changeKey("down", pressed);
        });
    }

    @FXML
    void dropButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            dropButton.setText(pressed);
            settings.changeKey("drop", pressed);
        });
    }

    @FXML
    void escapeButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            escapeButton.setText(pressed);
            settings.changeKey("esc", pressed);
        });
    }

    @FXML
    void interactButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            interactButton.setText(pressed);
            settings.changeKey("interact", pressed);
        });
    }

    @FXML
    void item1ButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            item1Button.setText(pressed);
            settings.changeKey("item1", pressed);
        });
    }

    @FXML
    void item2ButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            item2Button.setText(pressed);
            settings.changeKey("item2", pressed);
        });
    }

    @FXML
    void item3ButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            item3Button.setText(pressed);
            settings.changeKey("item3", pressed);
        });
    }

    @FXML
    void leftButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            leftButton.setText(pressed);
            settings.changeKey("left", pressed);
        });
    }

    @FXML
    void reloadButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            reloadButton.setText(pressed);
            settings.changeKey("reload", pressed);
        });
    }

    @FXML
    void rightButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            rightButton.setText(pressed);
            settings.changeKey("right", pressed);
        });
    }

    @FXML
    void upButtonPress(ActionEvent event) {
        stage.getScene().setOnKeyPressed(event1 -> {
            String pressed = event1.getCode().toString();
            upButton.setText(pressed);
            settings.changeKey("up", pressed);
        });
    }
}
