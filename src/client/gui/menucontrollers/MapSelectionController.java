package client.gui.menucontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MapSelectionController extends VBox implements MenuController{
    private Stage stage;

    @FXML
    private Button map1Button;

    @FXML
    private Button map2Button;

    @FXML
    private Button map3Button;

    @FXML
    private Button backButton;

    public MapSelectionController(){
        this.stage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/map_selection.fxml"));
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
        (new MainMenuController(stage)).show();
        this.getChildren().clear();
    }

    @FXML
    void map1ButtonPress(ActionEvent event) {

    }

    @FXML
    void map2ButtonPress(ActionEvent event) {

    }

    @FXML
    void map3ButtonPress(ActionEvent event) {

    }
}
