import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Window;

public class Main extends Application {
    private final int SCREEN_HEIGHT = 720;
    private final int SCREEN_WIDTH = 1280;

    @Override
    public void start(Stage stage) {
        //Example map
        int mapX = 17;
        int mapY = 17;

        int tileSize = 10;

        Image[][] map = new Image[mapX][mapY];

        GridPane mapGroup = new GridPane();

        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                int randomNumber = (int) Math.round(Math.random());

                switch (randomNumber) {
                    case 0:
                        map[x][y] = createImage(Color.BLACK);
                        break;
                    case 1:
                        map[x][y] = createImage(Color.RED);
                        break;
                    default:
                        map[x][y] = createImage(Color.GREEN);
                        break;
                }

                ImageView imageView = new ImageView(map[x][y]);
                imageView.setFitWidth(tileSize);
                imageView.setFitHeight(tileSize);
                mapGroup.add(imageView, x, y);
            }
        }

        //Creating a Group object
        Group root = new Group(mapGroup);

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