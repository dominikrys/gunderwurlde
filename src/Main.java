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

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                Platform.exit();
            }
        });

        ClientHandler handler = new ClientHandler(primaryStage);
        handler.start();
    }
}
