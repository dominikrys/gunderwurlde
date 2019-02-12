package client.gui.oldgui;

import client.data.ConnectionType;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shared.Constants;

public class MenuManager {
    AbstractMenuController currentMenuController;
    private Stage stage;
    private ConnectionType systemState;
    private Menus currentMenu;
    private boolean stageChanged;

    // Constructor
    public MenuManager(Stage inputStage) {
        // Set stage
        this.stage = inputStage;

        // Set system states
        systemState = ConnectionType.MENUS;
        currentMenu = Menus.MAIN_MENU;

        // Flag for changing stages
        stageChanged = true;

        // Currently active menu controller
        currentMenuController = null;

        // Setting title to the Stage
        stage.setTitle("Gunderwurlde");
    }

    public ConnectionType getSystemState() {
        return systemState;
    }

    public void setSystemState(ConnectionType systemState) {
        this.systemState = systemState;
    }

    // Render menu
    public void renderMenu() {
        // See if stage should have changed to a new menu
        if (currentMenuController != null) {

            Menus newestControllerMenu = currentMenuController.getCurrentMenu();

            if (newestControllerMenu != currentMenu) {
                currentMenu = newestControllerMenu;
                stageChanged = true;
            }
        }

        // If the stage has changed, set new controller
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
                    systemState = ConnectionType.SINGLE_PLAYER_CONNECTION;
                    return;
                case MULTI_PLAYER:
                    systemState = ConnectionType.MULTI_PLAYER_CONNECTION;
                    return;
                case QUIT:
                    systemState = ConnectionType.QUIT;
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
        // Check if JavaFX thread and update stage accordingly TODO: see if this causes issues
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
}

