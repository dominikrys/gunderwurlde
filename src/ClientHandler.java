import data.GameState;
import data.Location;
import data.Pose;
import data.SystemState;
import data.entity.enemy.Zombie;
import data.entity.item.ItemDrop;
import data.entity.item.weapon.Pistol;
import data.entity.player.Player;
import data.entity.player.Teams;
import data.entity.projectile.SmallBullet;
import data.map.Meadow;
import javafx.stage.Stage;

import java.util.LinkedHashSet;

import static data.SystemState.MENU;

public class ClientHandler extends Thread{
    Stage stage;

    public ClientHandler(Stage stage) {
        this.stage = stage;
    }

    public void run() {
        Renderer renderer = new Renderer(stage);

        // Example game state to render
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

        boolean running = true;
        SystemState systemState = MENU;

        while(running) {
            switch (systemState) {
                case MENU:
                    renderer.renderMenu();
                    systemState = renderer.getSystemState();
                    break;
                case GAME:
                    // Render game state
                    renderer.renderGameState(exampleState);
                    systemState = renderer.getSystemState();
                    break;
                case QUIT:
                    running = false;
                    stage.close();
                    break;
            }
        }
    }
}
