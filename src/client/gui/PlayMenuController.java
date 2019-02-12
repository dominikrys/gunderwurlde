package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayMenuController extends VBox {
    Stage stage;
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

    public void display() {
        // Create the main scene
        this.stage.getScene().setRoot(this);
//        Scene scene = new Scene(this, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
//
//        // TODO: REMOVE THIS!!!
//        // Check if JavaFX thread and update stage accordingly TODO: see if this causes issues
//        if (Platform.isFxApplicationThread()) {
//            stage.setScene(scene);
//            stage.centerOnScreen();
//            scene.getRoot().requestFocus();
//            stage.show();
//        } else {
//            // runLater because not JavaFX thread
//            Platform.runLater(() -> {
//                // Add scene to stage, request focus and show the stage
//                stage.setScene(scene);
//                stage.centerOnScreen();
//                scene.getRoot().requestFocus();
//                stage.show();
//            });
//        }
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

    }

    @FXML
    void singlePlayerButtonPress(ActionEvent event) {

    }
}
