package client.gui.menucontrollers;

import client.gui.Settings;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public interface MenuController {
    Stage stage = null;

    // Set scene of stage to input scene
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

    // setSceneToStage but for Parent inputs
    static void setRootToStage(Stage stage, Parent root, Settings settings) {
        setSceneToStage(stage, new Scene(root, settings.getScreenWidth(), settings.getScreenHeight()));
    }

    // Show menu to screen
    void show();
}
