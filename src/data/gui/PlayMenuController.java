package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PlayMenuController extends VBox {

    @FXML
    private TextField nameField;

    @FXML
    private Button singlePlayerButton;

    @FXML
    private Button multiPlayerButton;

    @FXML
    private Button backButton;

    public PlayMenuController(){
        // Load FXML file, set controller and root
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/data/gui/play_menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void backButtonPress(ActionEvent event) {

    }

    @FXML
    void handleNameInput(ActionEvent event) {

    }

    @FXML
    void multiPlayerButtonPress(ActionEvent event) {

    }

    @FXML
    void singlePlayerButtonPress(ActionEvent event) {

    }

}
