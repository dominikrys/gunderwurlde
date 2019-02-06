package client;

import client.gui.*;
import data.Constants;
import data.SystemState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuController {
    AbstractMenuController currentMenuController;
    private Stage stage;
    private SystemState systemState;
    private Menus currentMenu;
    private boolean stageChanged;

    // Constructor - take stage
    public MenuController(Stage inputStage) {
        // Set stage
        this.stage = inputStage;

        // Set system states
        systemState = SystemState.MENUS;
        currentMenu = Menus.MAIN_MENU;

        // Flag for changing stages
        stageChanged = true;

        // Currently active menu controller
        currentMenuController = null;

        // Setting title to the Stage
        stage.setTitle("Gunderwurlde");
    }

    public SystemState getSystemState() {
        return systemState;
    }

    public void setSystemState(SystemState systemState) {
        this.systemState = systemState;
    }

    public void renderMenu() {
        if (currentMenuController != null) {
            Menus controllerMenu = currentMenuController.getCurrentMenu();

            if (controllerMenu != currentMenu) {
                currentMenu = controllerMenu;
                stageChanged = true;
            }
        }

        if (stageChanged) {
            switch (currentMenu) {
                case MAIN_MENU:
                    currentMenuController = new MainMenuController();
                    break;
                case SETTINGS:
                    currentMenuController = new SettingsMenuController();
                    break;
                case PLAY:
                    currentMenuController = new PlayMenuController();
                    break;
                case MAP_SELECTION:
                    currentMenuController = new MapSelectionController();
                    break;
                case HELP:
                    currentMenuController = new HelpMenuController();
                    break;
                case SINGLE_PLAYER:
                    systemState = SystemState.SINGLE_PLAYER_CONNECTION;
                    return;
                case MULTI_PLAYER:
                    systemState = SystemState.MULTI_PLAYER_CONNECTION;
                    return;
                case QUIT:
                    systemState = SystemState.QUIT;
                    return;
            }

            // Create the main scene
            Scene scene = new Scene(currentMenuController, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

            // Update stage
            updateStageWithScene(stage, scene);

            stageChanged = false;
        }
    }

    // Method for updating the stage with a given scene
    private void updateStageWithScene(Stage stage, Scene scene) {
        // Check if JavaFX thread and update stage accordingly TODO: see if this causes isses
        if (Platform.isFxApplicationThread()) {
            stage.setScene(scene);
            scene.getRoot().requestFocus();
        } else {
            // runLater because not JavaFX thread
            Platform.runLater(() -> {
                // Add scene to stage, request focus and show the stage
                stage.setScene(scene);
                scene.getRoot().requestFocus();
                stage.show();
            });
        }
    }
}

