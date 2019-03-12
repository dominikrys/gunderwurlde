package client.gui.menucontrollers;

import client.gui.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelpMenuController extends VBox implements MenuController {
    private Stage stage;
    private Settings settings;

    @FXML
    private Button backButton;

    public HelpMenuController(Stage stage, Settings settings) {
        this.stage = stage;
        this.settings = settings;

        // Load FXML and set appropriate methods
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/help_menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (
                IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        // Switch to main menu and clear this screen
        (new MainMenuController(stage, settings)).show();
        this.getChildren().clear();
    }

    @Override
    public void show() {
        this.stage.getScene().setRoot(this);
    }
}
