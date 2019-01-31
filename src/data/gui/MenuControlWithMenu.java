package data.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MenuControlWithMenu extends VBox {
    @FXML
    private Button playButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button quitButton;

    public MenuControlWithMenu() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/data/gui/main_menu.fxml"));
        fxmlLoader.setRoot(this);
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
