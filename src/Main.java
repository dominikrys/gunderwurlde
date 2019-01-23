import data.GameState;
import data.Pose;
import data.enemy.Zombie;
import data.item.Item;
import data.item.ItemList;
import data.item.ItemType;
import data.item.weapon.Pistol;
import data.map.Meadow;
import data.player.Player;
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
        examplePlayers.add(new Player(new Pose(20, 20), 1));
        GameState exampleState = new GameState(new Meadow(), examplePlayers);
        exampleState.addItem(new Pistol());
        exampleState.addEnemy(new Zombie(new Pose(120, 120)));
        exampleState.addProjectile(new SmallBullet(new Pose(400, 300)));

        // Get dimensions of map
        int mapX = exampleState.getCurrentMap().getXDim();
        int mapY = exampleState.getCurrentMap().getYDim();

        // Create canvas for the map
        Canvas mapCanvas = new Canvas(mapX * displayedTileSize, mapY * displayedTileSize);
        GraphicsContext mapGC = mapCanvas.getGraphicsContext2D();

        // Iterate through the map, rending each tile on canvas
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                // Create empty tile image in case no tile found
                Image tileImage = createImageFromColor(Color.BLACK);

                // Load correct graphic in to tileImage
                switch (exampleState.getCurrentMap().getTileMap()[x][y].getType()) {
                    case GRASS:
                        tileImage = new Image("file:assets/img/grass.png");
                        break;
                    case WOOD:
                        tileImage = new Image("file:assets/img/wood.png");
                        break;
                    default:
                        break;
                }

                // If tile size is not as specified in program, print erro TODO: maybe abort program completely?
                if (tileImage.getWidth() != displayedTileSize || tileImage.getHeight() != displayedTileSize) {
                    System.out.println("Tile loaded with unsupported dimensions when rendering map");
                }

                // Add tile to canvas
                mapGC.drawImage(tileImage, x * displayedTileSize, y * displayedTileSize, displayedTileSize, displayedTileSize);
            }
        }


        Group root = new Group();

        // Render players
        int playerSize = 30; // Size for players TODO: remove this?

        for (Player currentPlayer : exampleState.getPlayers()) {
            ImageView imageView; // Image view for player TODO: maybe have this outside the loop

            // Load sprite and specify scaling
            mapGC.drawImage(new Image("file:assets/img/player.png"), currentPlayer.getPose().getX(),
                    currentPlayer.getPose().getY(), displayedTileSize, displayedTileSize);
        }

        // Render enemies

        // Render projectiles

        // Render items

        // Add complete map to HBox in order to center it
        HBox mainHBox = new HBox(mapCanvas);
        mainHBox.setAlignment(Pos.CENTER);

        root.getChildren().add(mainHBox);

        root.getChildren().add(mapCanvas);

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

    // Method for scaling image by resampling
    private Image resampleImage(Image inputImage, int scaleFactor) {
        final int inputImageWidth = (int) inputImage.getWidth();
        final int inputImageHeight = (int) inputImage.getHeight();

        // Set up output image
        WritableImage outputImage = new WritableImage(
                inputImageWidth *  scaleFactor,
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
