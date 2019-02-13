package client;

import client.gui.Settings;
import client.gui.menucontrollers.MainMenuController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    // Main method
    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Settings object
        Settings settings = new Settings();

        // Set up stage
        stage.setResizable(false); // Disable resizing of the window TODO: check how this behaves on linux!
        stage.setFullScreen(false);
        stage.setFullScreenExitHint("");
        stage.centerOnScreen();
        stage.getIcons().add(new Image("file:assets/img/entity/item/pistol.png"));
        stage.setTitle("Gunderwurlde");

        // Set stage to close and to kill handler when the window is closed
        stage.setOnCloseRequest(we -> {
            stage.close();

            // TODO: remove this, this is purely for debugging and the program shouldn't be getting ended like this
            System.exit(0);
        });

        // Create the main menu and show it
        (new MainMenuController(stage, settings)).show();

    }
}
