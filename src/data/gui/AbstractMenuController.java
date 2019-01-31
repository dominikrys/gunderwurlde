package data.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public abstract class AbstractMenuController extends VBox {
    public AbstractMenuController(String menuPath) {
        // Load FXML file, set controller and root
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(menuPath));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
