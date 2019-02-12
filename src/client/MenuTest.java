package client;

import client.gui.MainMenuController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MenuTest extends Application {
    // Main method
    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create renderer and pass primary stage to it
        primaryStage.setResizable(false); // Disable resizing of the window TODO: check how this behaves on linux!
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Gunderwurlde");

        (new MainMenuController(primaryStage)).display();

        // Set stage to close and to kill handler when the window is closed
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                primaryStage.close();
            }
        });
    }
}
