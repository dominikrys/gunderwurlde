package client;

import client.gui.menucontrollers.MainMenuController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Set;

/**
 * Main class. Extends application, sets all the necessary stage settings and displays the main menu
 */
public class Main extends Application {
    /**
     * Main method
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start the thread
     *
     * @param stage Main stage
     */
    @Override
    public void start(Stage stage) {
        // Try to load settings from local file. If not found, create new
        Settings settings = Settings.loadSettingsFromFile();
        if (settings == null) {
            System.out.println("Settings file not found or there was an error reading it. Creating new one...");
            settings = new Settings();
            settings.saveToDisk();
        } else {
            System.out.println("Settings file loaded!");
        }

        // Set up stage
        stage.setResizable(false); // Disable resizing of the window
        stage.setFullScreen(settings.getFullscreen());
        stage.setFullScreenExitHint("");
        stage.centerOnScreen();
        stage.getIcons().add(new Image("file:assets/img/entity/item/pistol.png"));
        stage.setTitle("Gunderwurlde");

        // Set stage to close and to kill handler when the window is closed
        stage.setOnCloseRequest(we -> {
            stage.close();
            System.out.println("Main ended ,all are left alive: ");
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for(Thread t : threadSet){
                System.out.println(t.getName() + " is still alive");
            }
        });

        //Platform.runLater(() -> System.exit(1));


        // Create the main menu and show it
        (new MainMenuController(stage, settings)).showInitial();

    }
}
