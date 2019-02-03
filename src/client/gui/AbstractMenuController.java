package client.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public abstract class AbstractMenuController extends VBox {
    protected Menus currentMenu;

    protected AbstractMenuController(String menuPath, Menus currentMenu) {
        // Load FXML file, set controller and root
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(menuPath));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.currentMenu = currentMenu;
    }

    public Menus getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(Menus currentMenu) {
        this.currentMenu = currentMenu;
    }
}
