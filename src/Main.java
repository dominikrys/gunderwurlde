import data.GameState;
import data.Location;
import data.Pose;
import data.enemy.Enemy;
import data.enemy.Zombie;
import data.item.Item;
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
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

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
        examplePlayers.add(new Player(new Pose(64, 64, 45), 1));
        GameState exampleState = new GameState(new Meadow(), examplePlayers);
        exampleState.addItem(new Pistol(Optional.of(new Location(50, 50))));
        exampleState.addEnemy(new Zombie(new Pose(120, 120, 45)));
        exampleState.addProjectile(new SmallBullet(new Pose(400, 300, 70)));
        ///////////////////////////////////////////////////////////////////////////////////

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
        Image playerImage = new Image("file:assets/img/player.png"); // Load image for player

        checkImageLoaded(playerImage, "Player"); // Check if loaded correctly

        for (Player currentPlayer : exampleState.getPlayers()) {
            drawRotatedImage(mapGC, playerImage, currentPlayer.getPose().getDirection(), currentPlayer.getPose().getX(),
                    currentPlayer.getPose().getY());
        }

        // Render enemies TODO: add options for different sizes of enemies. Add this to enemy class as well!!!
        // TODO: add switch for various enemy graphics and sizes

        Image zombieImage = new Image("file:assets/img/zombie.png");

        checkImageLoaded(playerImage, "Zombie"); // Check if loaded correctly

        for (Enemy currentEnemy : exampleState.getEnemies()) {
            drawRotatedImage(mapGC, zombieImage, currentEnemy.getPose().getDirection(), currentEnemy.getPose().getX(),
                    currentEnemy.getPose().getY());
        }

        // Render projectiles
        int projectileSize = 8;

        Image bulletImage = new Image("file:assets/img/bullet.png");

        checkImageLoaded(bulletImage, "Bullet"); // Check if loaded correctly

        for (Projectile currentProjectile : exampleState.getProjectiles()) {
            drawRotatedImage(mapGC, bulletImage, currentProjectile.getPose().getDirection(),
                    currentProjectile.getPose().getX(), currentProjectile.getPose().getY());
        }

        // Render items
        Image pistolImage = new Image("file:assets/img/pistol.png");

        checkImageLoaded(pistolImage, "pistol"); // Check if loaded correctly

        for (Item currentItem : exampleState.getItems()) {
            mapGC.drawImage(pistolImage, currentItem.getLocation().getX(),
                    currentItem.getLocation().getY(), displayedTileSize, displayedTileSize);
        }

        // Create hbox to centre canvas in and add canvas to it
        HBox mainHBox = new HBox();
        mainHBox.setAlignment(Pos.CENTER);
        mainHBox.getChildren().addAll(mapCanvas);

        // Make GUI
        VBox GUIBox = new VBox();
        GUIBox.setPadding(new Insets(5, 5, 5, 5));
        GUIBox.setSpacing(5);

        // Make a separate GUI for each player in case coop
        for (Player currentPlayer : exampleState.getPlayers()) {
            // Label with player name to tell which player this part of the GUI is for
            Label playerLabel = new Label("Player 1"); // TODO: Change this to player name from player class
            playerLabel.setFont(new Font("Consolas", 32));

            // HBox to horizontally keep heart graphics. Populate HBox with amount of life necessary
            HBox heartBox = new HBox();
            for (int i = 0; i < currentPlayer.getHealth(); i++) {
                heartBox.getChildren().add(new ImageView(new Image("file:assets/img/heart.png")));
            }

            // Add elemnts of GUI for player to GUI
            GUIBox.getChildren().addAll(playerLabel, heartBox);
        }

        // Create root stackpane and add elements to be rendered to it
        StackPane root = new StackPane();
        root.getChildren().addAll(mainHBox, GUIBox);

        // Create the main scene
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Setting title to the Stage
        stage.setTitle("Game");

        //A dding scene to the stage
        stage.setScene(scene);

        // Displaying the contents of the stage
        stage.show();
    }
/*
    void renderEntity(String imageLocation, GraphicsContext gc, Object objectToRender) {
        Image imageToRender = new Image("file:" + imageLocation);

        checkImageLoaded(imageToRender, imageLocation); // Check if loaded correctly

        drawRotatedImage(gc, imageToRender, objectToRender.getPose().getDirection(), objectToRender.getPose().getX(),
                objectToRender.getPose().getY());
    }*/

    // Method for setting transform for the GraphicsContext to rotate around a pivot point.
    private void rotate(GraphicsContext gc, double angle, double xPivotCoordinate, double yPivotCoordinate) {
        Rotate r = new Rotate(angle, xPivotCoordinate, yPivotCoordinate);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    // Method for drawing the rotated image
    private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
        gc.save(); // Saves the current state on stack, including the current transform for later
        rotate(gc, angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
        gc.drawImage(image, tlpx, tlpy);
        gc.restore(); // Back to original state (before rotation)
    }

    // Method for creating an image from a specified colour
    private Image createImageFromColor(Color color) {
        WritableImage image = new WritableImage(1, 1);
        image.getPixelWriter().setColor(0, 0, color);
        return image;
    }

    // Method for checking if an image has loaded in correctly
    private boolean checkImageLoaded(Image inputImage, String imageDirectory) {
        if (inputImage.isError()) {
            System.out.println("Image not found in " + imageDirectory);
            return false;
        }

        return true;
    }

    // Method for scaling image by integer value by resampling - useful for later
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
