import data.GameState;
import data.Location;
import data.Pose;
import data.enemy.Enemy;
import data.enemy.Zombie;
import data.item.Item;
import data.item.ItemList;
import data.item.ItemType;
import data.item.weapon.Pistol;
import data.map.Meadow;
import data.player.Player;
import data.projectile.Projectile;
import data.projectile.SmallBullet;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;

public class Main extends Application {
    // Constants for screen dimensions
    // TODO: move these to a more general constants file
    private final int SCREEN_HEIGHT = 720;
    private final int SCREEN_WIDTH = 1280;

    // Size the tiles should be displayed at TODO: change this to whatever the screen size is or just change it overall
    int displayedTileSize = 32;

    @Override
    public void start(Stage stage) {
        //Example code for testing - TODO: remove this later
        LinkedHashSet<Player> examplePlayers = new LinkedHashSet<Player>();
        examplePlayers.add(new Player(new Pose(64, 64), 1));
        GameState exampleState = new GameState(new Meadow(), examplePlayers);
        exampleState.addItem(new Pistol(Optional.of(new Location(50, 50))));
        exampleState.addEnemy(new Zombie(new Pose(120, 120)));
        exampleState.addProjectile(new SmallBullet(new Pose(400, 300)));

        // Get dimensions of map
        int mapX = exampleState.getCurrentMap().getXDim();
        int mapY = exampleState.getCurrentMap().getYDim();

        // Create canvas for the map
        Canvas mapCanvas = new Canvas(mapX * displayedTileSize, mapY * displayedTileSize);
        GraphicsContext mapGC = mapCanvas.getGraphicsContext2D();

        // Iterate through the map, rending each tile on canvas TODO: add to individual methods
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                // Create empty tile image in case no tile found
                Image tileImage = createImageFromColor(Color.BLACK);

                // Load correct graphic in to tileImage
                switch (exampleState.getCurrentMap().getTileMap()[x][y].getType()) {
                    case GRASS:
                        tileImage = new Image("file:assets/img/grass.png");

                        // Check if loaded correctly
                        checkImageLoaded(tileImage, "Grass");
                        break;
                    case WOOD:
                        tileImage = new Image("file:assets/img/wood.png");

                        // Check if loaded correctly
                        checkImageLoaded(tileImage, "Wood");
                        break;
                    default:
                        break;
                }

                // If tile size is not as specified in program, print error TODO: maybe abort program completely?
                if (tileImage.getWidth() != displayedTileSize || tileImage.getHeight() != displayedTileSize) {
                    System.out.println("Tile loaded with unsupported dimensions when rendering map");
                }

                // Add tile to canvas
                mapGC.drawImage(tileImage, y * displayedTileSize, x * displayedTileSize, displayedTileSize, displayedTileSize);
            }
        }

        // Render players
        int playerSize = 32; // Size for players TODO: add to constants?

        Image playerImage = new Image("file:assets/img/player.png"); // Load image for player

        checkImageLoaded(playerImage, "Player"); // Check if loaded correctly

        for (Player currentPlayer : exampleState.getPlayers()) {
            mapGC.drawImage(playerImage, currentPlayer.getPose().getX(), currentPlayer.getPose().getY(), playerSize,
                    playerSize);
        }

        // Render enemies TODO: add options for different sizes of enemies. Add this to enemy class as well!!!
        // TODO: add switch for various enemy graphics and sizes

        Image zombieImage = new Image("file:assets/img/zombie.png");

        checkImageLoaded(playerImage, "Zombie"); // Check if loaded correctly

        for (Enemy currentEnemy : exampleState.getEnemies()) {
            mapGC.drawImage(zombieImage, currentEnemy.getPose().getX(),
                    currentEnemy.getPose().getY(), playerSize, playerSize);
        }

        // Render projectiles
        int projecticleSize = 8;

        Image bulletImage = new Image("file:assets/img/bullet.png");

        checkImageLoaded(bulletImage, "Bullet"); // Check if loaded correctly

        for (Projectile currentProjectile : exampleState.getProjectiles()) {
            mapGC.drawImage(bulletImage, currentProjectile.getPose().getX(),
                    currentProjectile.getPose().getY(), projecticleSize, projecticleSize); //TODO check for dimension
        }

        // Render items
        int itemSize = 24;

        Image pistolImage = new Image("file:assets/img/pistol.png");

        checkImageLoaded(pistolImage, "pistol"); // Check if loaded correctly

        for (Item currentItem : exampleState.getItems()) {
            mapGC.drawImage(pistolImage, currentItem.getLocation().getX(),
                    currentItem.getLocation().getY(), itemSize, itemSize);
        }

        // Add complete map to HBox in order to center it
        HBox mainHBox = new HBox(mapCanvas);
        mainHBox.setAlignment(Pos.CENTER);

        // Create root group and add hbox to it
        Group root = new Group();

        root.getChildren().add(mainHBox);

        // Create the main scene
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Setting title to the Stage
        stage.setTitle("Game");

        //A dding scene to the stage
        stage.setScene(scene);

        // Displaying the contents of the stage
        stage.show();
    }

    // Method for creating an image from a specified colour
    private Image createImageFromColor(Color color) {
        WritableImage image = new WritableImage(1, 1);
        image.getPixelWriter().setColor(0, 0, color);
        return image;
    }

    // Method for checking if an image has loaded in correctly
    private boolean checkImageLoaded(Image inputImage, String imageType) {
        if (inputImage.isError()) {
            System.out.println(imageType + " image not found!");
            return false;
        }

        return true;
    }

    // Method for scaling image by resampling
    private Image resampleImage(Image inputImage, int scaleFactor) {
        final int inputImageWidth = (int) inputImage.getWidth();
        final int inputImageHeight = (int) inputImage.getHeight();

        // Set up output image
        WritableImage outputImage = new WritableImage(
                inputImageWidth * scaleFactor,
                inputImageHeight * scaleFactor
        );

        // Set up pixel reader and writer
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        // Resample image
        for (int y = 0; y < inputImageHeight; y++) {
            for (int x = 0; x < inputImageWidth; x++) {
                final int argb = reader.getArgb(x, y);
                for (int dy = 0; dy < scaleFactor; dy++) {
                    for (int dx = 0; dx < scaleFactor; dx++) {
                        writer.setArgb(x * scaleFactor + dx, y * scaleFactor + dy, argb);
                    }
                }
            }
        }

        return outputImage;
    }

    // Main method
    public static void main(String args[]) {
        launch(args);
    }
}
