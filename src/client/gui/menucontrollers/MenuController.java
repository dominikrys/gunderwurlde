package client.gui.menucontrollers;

import client.gui.Settings;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MenuController interface. Has some common methods that all menu controllers need
 * @author  Dominik Rys
 */
public interface MenuController {
    /**
     * Stage to display menu on
     */
    Stage stage = null;

    /**
     * Set scene of given stage to the input scene
     * @param stage Stage to set the scene to
     * @param scene The scene to set to the stage
     */
    static void setSceneToStage(Stage stage, Scene scene) {
        // Check if JavaFX thread and update stage accordingly
        if (Platform.isFxApplicationThread()) {
            stage.setScene(scene);
            stage.centerOnScreen();
            scene.getRoot().requestFocus();
            stage.show();
        } else {
            // runLater because not JavaFX thread
            Platform.runLater(() -> {
                // Add scene to stage, request focus and show the stage
                stage.setScene(scene);
                stage.centerOnScreen();
                scene.getRoot().requestFocus();
                stage.show();
            });
        }
    }

    /**
     * Set scene to stage but for objects of type 'Parent'
     * @param stage Stage to set the scene to
     * @param root Parent object to set to the stage
     * @param settings Settings object, necessary for getting screen dimensions
     */
    static void setRootToStage(Stage stage, Parent root, Settings settings) {
        setSceneToStage(stage, new Scene(root, settings.getScreenWidth(), settings.getScreenHeight()));
    }

    /**
     * Show menu on the screen
     */
    void show();
}
