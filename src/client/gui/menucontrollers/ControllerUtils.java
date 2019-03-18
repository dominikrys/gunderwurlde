package client.gui.menucontrollers;

import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * ControllerUtils class for certain variables and common methods for menu controllers. This is necessary as all
 * menu controllers have to extend VBox and therefore can't extend another class.
 *
 * @author Dominik Rys
 */
public class ControllerUtils {
    /**
     * Method for making dropshadow with bloom
     *
     * @return dropshadow combined with bloom
     */
    public static DropShadow getMenuDropshadow() {
        DropShadow ds = new DropShadow(10, Color.WHITE);
        ds.setInput(new Bloom(0));
        return ds;
    }
}
