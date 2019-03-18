package client;

import client.gui.menucontrollers.MainMenuController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;

/**
 * Main class. Extends application, sets all the necessary stage settings and displays the main menu
 */
public class Main extends Application {
    /**
     * Main method
     *
     * @param args Command line arguments
     */
    public static void main(String args[]) {
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
        Settings settings = new Settings();
        settings = loadSettingsFromFile(settings);

        // Set up stage
        stage.setResizable(false); // Disable resizing of the window
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

    /**
     * Load settings from local file
     *
     * @param settings Initial settings to fall back to if file not loaded
     * @return Loaded settings
     */
    private Settings loadSettingsFromFile(Settings settings) {
        // Look for existing settings object and try to load it
        try (
                InputStream file = new FileInputStream("settings.ser");
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream(buffer)
        ) {
            // Deserialize the file
            settings = (Settings) input.readObject();
            System.out.println("Settings file found and loaded!");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            // No existing file found so create a new one
            System.out.println("No settings file found, creating new one...");
            settings.saveToDisk();
        }
        return settings;
    }
}
