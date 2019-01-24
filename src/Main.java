import data.GameState;
import data.Location;
import data.Pose;
import data.enemy.Zombie;
import data.item.weapon.Pistol;
import data.map.Meadow;
import data.player.Player;
import data.projectile.SmallBullet;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Optional;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        //Example code for testing - TODO: remove this later
        LinkedHashSet<Player> examplePlayers = new LinkedHashSet<Player>();
        Player examplePlayer = new Player(new Pose(64, 64, 45), 1);
        examplePlayer.addItem(new Pistol(Optional.empty()));
        examplePlayer.addItem(new Pistol(Optional.empty()));
        examplePlayer.addItem(new Pistol(Optional.empty()));
        examplePlayers.add(examplePlayer);
        GameState exampleState = new GameState(new Meadow(), examplePlayers);
        exampleState.addItem(new Pistol(Optional.of(new Location(50, 50))));
        exampleState.addEnemy(new Zombie(new Pose(120, 120, 45)));
        exampleState.addProjectile(new SmallBullet(new Pose(400, 300, 70)));
        ///////////////////////////////////////////////////////////////////////////////////

        // Create renderer and call it
        Renderer renderer = new Renderer(stage);
        renderer.render(exampleState);
    }

    // Main method
    public static void main(String args[]) {
        launch(args);
    }
}
