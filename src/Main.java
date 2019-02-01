import data.GameState;
import data.Location;
import data.Pose;
import data.SystemState;
import data.entity.enemy.Zombie;
import data.entity.item.ItemDrop;
import data.entity.item.weapon.GunList;
import data.entity.item.weapon.Pistol;
import data.entity.player.Teams;
import data.map.Meadow;
import data.entity.player.Player;
import data.entity.projectile.SmallBullet;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.LinkedHashSet;

import static data.SystemState.MENU;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create renderer and pass primary stage to it
        primaryStage.setResizable(false); // Disable resizing of the window

        ClientHandler handler = new ClientHandler(primaryStage);
        handler.start();
    }

    // Main method
    public static void main(String args[]) {
        launch(args);
    }
}
