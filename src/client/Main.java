package client;
import client.data.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.LinkedHashSet;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        // Example code for testing -
//        LinkedHashMap<Integer,Player> examplePlayers = new LinkedHashMap<Integer,Player>();
//        Player examplePlayer = new Player(Teams.RED, "Player 1");
//        examplePlayer.addItem(new Pistol());
//        examplePlayer.addItem(new Pistol());
//        examplePlayer.addItem(new Pistol());
//        examplePlayers.put(1, examplePlayer);
//        GameState exampleState = new GameState(new Meadow(), examplePlayers);
//        exampleState.addItem(new ItemDrop(new Pistol(), new Location(50, 250)));
//        exampleState.addEnemy(new Zombie(new Pose(120, 120, 45)));
//        exampleState.addProjectile(new SmallBullet(new Pose(400, 300, 70)));

        // Create renderer and call it
        stage.setResizable(false); // Disable resizing of the window
        Renderer renderer = new Renderer(stage);

        //renderer.renderGameView(gameView);
    }

    // Main method
    public static void main(String args[]) {
        launch(args);
    }
}
