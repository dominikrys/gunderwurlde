import data.GameState;
import data.Pose;
import data.map.Meadow;
import data.map.TileTypes;
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
    private final int SCREEN_HEIGHT = 720;
    private final int SCREEN_WIDTH = 1280;

    @Override
    public void start(Stage stage) {
        //Example code for testing
        LinkedHashSet<Player> examplePlayers = new LinkedHashSet<Player>();
        examplePlayers.add(new Player(new Pose(20, 20), 1));
        GameState exampleState = new GameState(new Meadow(), examplePlayers);

        //Example imageMap
        char[][] inputMap = {
                {'B', 'B', 'B'},
                {'R', 'R', 'R'},
                {'B', 'B', 'B'}
        };

        int mapX = exampleState.getCurrentMap().getXDim();
        int mapY = exampleState.getCurrentMap().getYDim();

        int tileSize = 30;

        GridPane mapGroup = new GridPane();

        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {

                switch (exampleState.getCurrentMap().getTileMap()[x][y].getType()) {
                    case GRASS:
                        imageMap[x][y] = createImage(Color.BLUE);
                        break;
                    case WOOD:
                        imageMap[x][y] = createImage(Color.RED);
                        break;
                    default:
                        imageMap[x][y] = createImage(Color.BLACK);
                        break;
                }

                ImageView imageView = new ImageView(imageMap[x][y]);
                imageView.setFitWidth(tileSize);
                imageView.setFitHeight(tileSize);
                mapGroup.add(imageView, y, x);
            }
        }

        HBox mainHBox = new HBox(mapGroup);

        mainHBox.setAlignment(Pos.CENTER);

        //Creating a Group object
        Group root = new Group(mainHBox);

        //Creating a scene object
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

        //Setting title to the Stage
        stage.setTitle("Cool game");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }

    private Image createImage(Color color) {
        WritableImage image = new WritableImage(1, 1);
        image.getPixelWriter().setColor(0, 0, color);
        return image;
    }

    public static void main(String args[]) {
        launch(args);
    }

    public static class MapTile extends StackPane {

        public MapTile(int type, double x, double y, double width, double height) {

            // create rectangle
            Rectangle rectangle = new Rectangle(width, height);
            switch (type) {
                case 0:
                    rectangle.setFill(Color.BLUE);
                    break;
                case 1:
                    rectangle.setFill(Color.RED);
                    break;
                default:
                    rectangle.setFill(Color.GREEN);
                    break;
            }

            // set position
            setTranslateX(x);
            setTranslateY(y);

            getChildren().addAll(rectangle);
        }

    }
}