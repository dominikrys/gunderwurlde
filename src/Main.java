import data.GameState;
import data.Location;
import data.Pose;
import data.entity.enemy.Zombie;
import data.entity.item.ItemDrop;
import data.entity.item.weapon.gun.Pistol;
import data.entity.player.Teams;
import data.map.Meadow;
import inputhandler.KeyboardHandler;
import inputhandler.MouseHandler;
import inputhandler.UserInteraction;
import data.entity.player.Player;
import data.entity.projectile.SmallBullet;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.LinkedHashSet;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        // Example code for testing - TODO: remove this later
        LinkedHashSet<Player> examplePlayers = new LinkedHashSet<Player>();
        Player examplePlayer = new Player(new Pose(64, 64, 45), Teams.RED, "Player 1");
        examplePlayer.addItem(new Pistol());
        examplePlayer.addItem(new Pistol());
        examplePlayer.addItem(new Pistol());
        examplePlayers.add(examplePlayer);
        GameState exampleState = new GameState(new Meadow(), examplePlayers);
        exampleState.addItem(new ItemDrop(new Pistol(), new Location(50, 250)));
        exampleState.addEnemy(new Zombie(new Pose(120, 120, 45)));
        exampleState.addProjectile(new SmallBullet(new Pose(400, 300, 70)));

        // Create renderer and call it
        stage.setResizable(false); // Disable resizing of the window
        Renderer renderer = new Renderer(stage);
        renderer.renderGameState(exampleState);
    }

    // Main method
    public static void main(String args[]) {
        launch(args);
    }
}
