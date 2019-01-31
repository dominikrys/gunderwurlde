package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PauseMenuController extends VBox {

    @FXML
    private Button backButton;

    @FXML
    private Button menuButton;

    public PauseMenuController(){
        // Load FXML file, set controller and root
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/data/gui/pause_menu.fxml"));
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
    void menuButtonPress(ActionEvent event) {

    }

}
