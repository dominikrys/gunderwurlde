import data.GameState;
import data.Location;
import data.Pose;
import data.enemy.Enemy;
import data.enemy.Zombie;
import data.item.Item;
import data.item.ItemList;
import data.item.weapon.Pistol;
import data.map.Meadow;
import data.player.Player;
import data.projectile.Projectile;
import data.projectile.SmallBullet;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Stack;

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
