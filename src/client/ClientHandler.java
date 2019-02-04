package client;

import client.data.GameView;
import data.Constants;
import data.GameState;
import data.SystemState;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import server.game_engine.ProcessGameState;
import server.serverclientthreads.Server;

import static data.SystemState.MENU;

public class ClientHandler extends Thread {
    private Stage stage;
    private boolean running;

    public ClientHandler(Stage stage) {
        this.stage = stage;
        running = true;
    }

    public void run() {
        Renderer renderer = new Renderer(stage);

        // Example game state to render
        SystemState systemState = MENU;

        // Load font
        Font.loadFont(getClass().getResourceAsStream(Constants.MANASPACE_FONT_PATH), 36);

        while (running) {
            switch (systemState) {
                case MENU:
                    // Render menu
                    renderer.renderMenu();
                    systemState = renderer.getSystemState();
                    break;
                case GAME:
                    // Render game state
                    //renderer.renderGameView(new GameView());
                    systemState = renderer.getSystemState();
                    break;
                case SINGLE_PLAYER:
                    // CODE FOR ESTABLISHING LOCAL SERVER
                    // How am i relating the Gameview for the client to the GameState of the server?
                    Client client = new Client(renderer, gameView);
                    Server server = new Server();
                    server.start();

                    systemState = SystemState.GAME; // REMOVE THIS
                    break;
                case MULTI_PLAYER:
                    // CODE FOR ESTABLISHING CONNECTION WITH REMOTE SERVER

                    break;
                case QUIT:
                    // Quit program
                    running = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // Close stage
                            stage.close();
                        }
                    });
                    break;
            }
        }
    }

    public void createGameState(){

    }

    public void end() {
        this.running = false;
    }
}
