package client.gui.menucontrollers;

import client.GameHandler;
import client.data.ConnectionType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayMenuController extends VBox implements MenuController{
    private Stage stage;

    @FXML
    private TextField nameField;
    @FXML
    private Button singlePlayerButton;
    @FXML
    private Button multiPlayerButton;
    @FXML
    private Button backButton;
    @FXML
    private Label characterErrorText;

    public PlayMenuController(Stage stage) {
        this.stage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/play_menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (
                IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void show() {
        this.stage.getScene().setRoot(this);
    }

    @FXML
    void backButtonPress(ActionEvent event) {
        (new MainMenuController(stage)).show();
    }

    @FXML
    void handleNameInput(ActionEvent event) {
        // Only allow going into single or multi player if a name has been entered
        if (nameField.getCharacters().length() > 0 && nameField.getCharacters().length() < 12) {
            singlePlayerButton.setDisable(false);
            multiPlayerButton.setDisable(false);
            characterErrorText.setVisible(false);
        } else if (nameField.getCharacters().length() == 0) {
            singlePlayerButton.setDisable(true);
            multiPlayerButton.setDisable(true);
            characterErrorText.setVisible(false);
        } else {
            singlePlayerButton.setDisable(true);
            multiPlayerButton.setDisable(true);
            characterErrorText.setVisible(true);
        }
    }

    @FXML
    void multiPlayerButtonPress(ActionEvent event) {
        (new GameHandler(stage, ConnectionType.MULTI_PLAYER)).start();
    }

    @FXML
    void singlePlayerButtonPress(ActionEvent event) {
        (new GameHandler(stage, ConnectionType.SINGLE_PLAYER)).start();
    }
}
