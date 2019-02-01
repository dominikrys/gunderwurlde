package client;

import renderer.Renderer;
import data.GameState;
import data.Location;
import data.Pose;
import data.entity.enemy.Zombie;
import data.entity.item.ItemDrop;
import data.entity.item.weapon.gun.Pistol;
import data.entity.player.Player;
import data.entity.player.Teams;
import data.entity.projectile.SmallBullet;
import data.map.Meadow;
import javafx.application.Application;
import javafx.stage.Stage;
import server.serverclientthreads.ClientOnline;
import server.serverclientthreads.Server;

import javax.swing.*;
import java.util.LinkedHashSet;

public class Client extends Application {

    GameState gameState;

    public static void main(String args[]) {

        ClientOnline online = new ClientOnline();
        Server server = new Server();
        launch(args);

    }

    @Override
    public void start(Stage stage) {
        // Example code for testing - TODO: remove this later
        LinkedHashSet<Player> examplePlayers = new LinkedHashSet<Player>();
        Player examplePlayer = new Player(new Pose(64, 64, 45), Teams.RED, "Player 1");
        examplePlayer.addItem(new Pistol());
        examplePlayer.addItem(new Pistol());
        examplePlayer.addItem(new Pistol());
        examplePlayers.add(examplePlayer);
        GameState gameState = new GameState(new Meadow(), examplePlayers);
        gameState.addItem(new ItemDrop(new Pistol(), new Location(50, 250)));
        gameState.addEnemy(new Zombie(new Pose(120, 120, 45)));
        gameState.addProjectile(new SmallBullet(new Pose(400, 300, 70)));

        // Create renderer and call it
        stage.setResizable(false); // Disable resizing of the window
        Renderer renderer = new Renderer(stage);
        renderer.renderGameState(gameState);
    }
}
