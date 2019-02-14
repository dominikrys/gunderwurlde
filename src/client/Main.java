package client;

import client.gui.Settings;
import client.gui.menucontrollers.MainMenuController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {
    // Main method
    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Settings settings = new Settings();

        // Look for existing settings object and try to load it
        try (
                InputStream file = new FileInputStream("settings.ser");
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream(buffer)
        ) {
            // Deserialize the file
            settings = (Settings) input.readObject();
            System.out.println("Settings file loaded!");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getException());
        } catch (IOException e) {
            // No existing file found so create a new one
            System.out.println("No settings file found, creating new one...");
            try (
                    OutputStream file = new FileOutputStream("settings.ser");
                    OutputStream buffer = new BufferedOutputStream(file);
                    ObjectOutput output = new ObjectOutputStream(buffer)
            ) {
                output.writeObject(settings);
            } catch (IOException ex) {
                System.out.println("Cannot perform output." + ex.getMessage());
            }

        }

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
