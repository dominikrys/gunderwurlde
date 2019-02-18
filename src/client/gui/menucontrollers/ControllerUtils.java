package client.gui.menucontrollers;

import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class ControllerUtils {
    public static DropShadow getMenuDropshadow() {
        DropShadow ds = new DropShadow(25, Color.WHITE);
        ds.setInput(new Bloom(0.25));
        return ds;
    }
}
