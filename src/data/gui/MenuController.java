package data.gui;

import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MenuController extends VBox {
    @FXML private TextField textField;

    @FXML
    private Button playButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button quitButton;

    public MenuController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main_menu.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void helpButtonPress(ActionEvent event) {

    }

    @FXML
    void playButtonPress(ActionEvent event) {

    }

    @FXML
    void quitButtonPress(ActionEvent event) {

    }

    @FXML
    void settingsButtonPress(ActionEvent event) {

    }
}