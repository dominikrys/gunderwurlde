package client.gui.menucontrollers;

import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ControllerUtils {
    // Dropshadow used for selected menu items
    public static DropShadow getMenuDropshadow() {
        DropShadow ds = new DropShadow(10, Color.WHITE);
        ds.setInput(new Bloom(0));
        return ds;
    }
}
