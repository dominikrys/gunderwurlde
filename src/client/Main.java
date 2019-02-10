package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    // Main method
    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create renderer and pass primary stage to it
        primaryStage.setResizable(false); // Disable resizing of the window
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Gunderwurlde");

        // Create clienthandler thread
        ClientHandler clientHandler = new ClientHandler(primaryStage);
        clientHandler.start();

        // Set stage to close and to kill handler when the window is closed
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                clientHandler.end();
                primaryStage.close();
            }
        });
    }
}
