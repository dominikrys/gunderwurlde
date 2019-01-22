import data.GameState;
import data.Pose;
import data.map.Meadow;
import data.player.Player;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.LinkedHashSet;

public class Main extends Application {
    // Constants for screen dimensions
    // TODO: move these to a more general constants file
    private final int SCREEN_HEIGHT = 720;
    private final int SCREEN_WIDTH = 1280;

    @Override
    public void start(Stage stage) {
        //Example code for testing - TODO: remove this later
        LinkedHashSet<Player> examplePlayers = new LinkedHashSet<Player>();
        examplePlayers.add(new Player(new Pose(20, 20), 1));
        GameState exampleState = new GameState(new Meadow(), examplePlayers);

        // Get dimensions of map
        int mapX = exampleState.getCurrentMap().getXDim();
        int mapY = exampleState.getCurrentMap().getYDim();

        // Size the tiles should be displayed at TODO: change this to whatver the screen size is or just change it overlall
        int displayedTileSize = 40;

        // GridPane for the map
        GridPane mapPane = new GridPane();

        // Iterate through the map, rending each tile
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {

                // Temporary ImageView object for current tile
                ImageView imageView;

                // Switch for checking what type the tile is
                switch (exampleState.getCurrentMap().getTileMap()[x][y].getType()) {
                    case GRASS:
                        // Load tile image according to specified dimensions. Final boolean for avoiding ugly smoothing
                        imageView = new ImageView(new Image("file:assets/img/grass.png", displayedTileSize,
                                displayedTileSize, false, false));
                        break;
                    case WOOD:
                        imageView = new ImageView(new Image("file:assets/img/wood.png", displayedTileSize,
                                displayedTileSize, false, false));
                        break;
                    default:
                        imageView = new ImageView(createImageFromColor(Color.BLACK));
                        break;
                }

                // Set dimensions for table - could be necessary in the future
                //imageView.setFitWidth(displayedTileSize);
                //imageView.setFitHeight(displayedTileSize);

                // Add tile to map pane
                mapPane.add(imageView, y, x);
            }
        }

        // Add map to main HBox to allow centering
        HBox mainHBox = new HBox(mapPane);
        mainHBox.setAlignment(Pos.CENTER);

        // Create the root group
        Group root = new Group(mainHBox);

        // Create the main scene
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Setting title to the Stage
        stage.setTitle("Cool game");

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

    // Main method
    public static void main(String args[]) {
        launch(args);
    }
}