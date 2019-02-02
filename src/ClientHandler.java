import data.*;
import data.entity.enemy.Zombie;
import data.entity.item.ItemDrop;
import data.entity.item.weapon.Pistol;
import data.entity.player.Player;
import data.entity.player.Teams;
import data.entity.projectile.SmallBullet;
import data.map.Meadow;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;

import static data.SystemState.MENU;

public class ClientHandler extends Thread{
    private Stage stage;
    private boolean running;

    public ClientHandler(Stage stage) {
        this.stage = stage;
        running = true;
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
        SystemState systemState = MENU;

        // Load font
        Font.loadFont(getClass().getResourceAsStream(Constants.MANASPACE_FONT_PATH), 36);

        while(running) {
            switch (systemState) {
                case MENU:
                    // Render menu
                    renderer.renderMenu();
                    systemState = renderer.getSystemState();
                    break;
                case GAME:
                    // Render game state
                    renderer.renderGameState(exampleState);
                    systemState = renderer.getSystemState();
                    break;
                case SINGLE_PLAYER:
                    // CODE FOR ESTABLISHING LOCAL SERVER
                    systemState = SystemState.GAME; // REMOVE THIS
                    break;
                case MULTI_PLAYER:
                    // CODE FOR ESTABLISHING CONNECTION WITH REMOVE SERVER
                    break;
                case QUIT:
                    // Quit program
                    running = false;
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            // Add scene to stage, request focus and show the stage
                            stage.close();
                        }
                    });
                    break;
            }
        }
    }

    public void end() {
        this.running = false;
    }
}
