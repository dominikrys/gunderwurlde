package client.gui.menucontrollers;

import client.gui.Settings;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shared.Constants;

public interface MenuController {
    Stage stage = null;

    // Set scene of stage to input scene
    public static void setSceneToStage (Stage stage, Scene scene)  {
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
    public static void setRootToStage(Stage stage, Parent root) {
        setSceneToStage(stage, new Scene(root, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
    }

    // Show menu to screen
    public void show();
}
