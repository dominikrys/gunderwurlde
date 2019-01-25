import com.sun.corba.se.impl.orbutil.graph.Graph;
import data.Constants;
import data.GameState;
import data.entity.Entity;
import data.entity.enemy.Enemy;
import data.entity.item.Item;
import data.entity.item.ItemDrop;
import data.entity.item.weapon.Gun;
import data.entity.player.Player;
import data.entity.projectile.Projectile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Renderer {
    private Stage stage;

    // Constructor - take stage
    public Renderer(Stage inputStage) {
	this.stage = inputStage;
    }

    // Render input gamestate to stage
    public void renderGameState(GameState inputGameState) {
	// Get dimensions of map
	int mapX = inputGameState.getCurrentMap().getXDim();
	int mapY = inputGameState.getCurrentMap().getYDim();

	// Create canvas for the map
	Canvas mapCanvas = new Canvas(mapX * Constants.TILE_SIZE, mapY * Constants.TILE_SIZE);
	GraphicsContext mapGC = mapCanvas.getGraphicsContext2D();

	// Iterate through the map, rending each tile on canvas TODO: add to individual
	// methods
	for (int x = 0; x < mapX; x++) {
	    for (int y = 0; y < mapY; y++) {
		// Create empty tile image in case no tile found
		Image tileImage = createImageFromColor(Color.BLACK);

		// Load correct graphic in to tileImage
		switch (inputGameState.getCurrentMap().getTileMap()[x][y].getType()) {
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

		// If tile size is not as specified in program, print error TODO: maybe abort
		// program completely?
		if (tileImage.getWidth() != Constants.TILE_SIZE || tileImage.getHeight() != Constants.TILE_SIZE) {
		    System.out.println("Tile loaded with unsupported dimensions when rendering map");
		}

		// Add tile to canvas
		mapGC.drawImage(tileImage, y * Constants.TILE_SIZE, x * Constants.TILE_SIZE, Constants.TILE_SIZE,
			Constants.TILE_SIZE);
	    }
	}

	// Render players
	for (Player currentPlayer : inputGameState.getPlayers()) {
	    renderEntity(currentPlayer, mapGC, "file:assets/img/player.png");
	}

	// Render enemies
	for (Enemy currentEnemy : inputGameState.getEnemies()) {
	    renderEntity(currentEnemy, mapGC, "file:assets/img/zombie.png");
	}

	// Render projectiles
	for (Projectile currentProjectile : inputGameState.getProjectiles()) {
	    renderEntity(currentProjectile, mapGC, "file:assets/img/bullet.png");
	}

	// Render items
	for (ItemDrop currentItem : inputGameState.getItems()) {
	    renderEntity(currentItem, mapGC, "file:assets/img/pistol.png");
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
	for (Player currentPlayer : inputGameState.getPlayers()) {
	    // Label with player name to tell which player this part of the GUI is for
	    Label playerLabel = new Label(currentPlayer.getName());
	    playerLabel.setFont(new Font("Consolas", 32));
	    playerLabel.setTextFill(Color.BLACK);

	    // Player score
	    Label playerScore = new Label("Score: " + currentPlayer.getScore());
	    playerScore.setFont(new Font("Consolas", 32));
	    playerScore.setTextFill(Color.BLACK);

	    // HBox to horizontally keep heart graphics. Populate HBox with amount of life
	    // necessary
	    HBox heartBox = new HBox();
	    // Populate life that hasn't been lost
	    for (int i = 0; i < currentPlayer.getHealth(); i++) {
		heartBox.getChildren().add(new ImageView(new Image("file:assets/img/heart.png")));
	    }
	    // Populate lost life
	    for (int i = 0; i < currentPlayer.getMaxHealth() - currentPlayer.getHealth(); i++) {
		heartBox.getChildren().add(new ImageView(new Image("file:assets/img/lost_heart.png")));
	    }

	    // Iterate through held items list and add to the GUI
	    HBox heldItems = new HBox(); // Make HBox for held items

	    int currentItemIndex = 0; // Keep track of current item since iterator is used

	    for (Item currentItem : currentPlayer.getItems()) {
		Image itemImage = createImageFromColor(Color.BLACK); // Initialise item
		switch (currentItem.getItemName()) {
		case PISTOL:
		    itemImage = new Image("file:assets/img/pistol.png");
		    break;
		default:
		    break;
		}

		ImageView imageView = new ImageView(itemImage); // Make imageview from selected graphic

		// Check if the item currently being checked is the current selected item, and
		// if it is, show that
		if (currentItemIndex == currentPlayer.getCurrentItemIndex()) {
		    DropShadow dropShadow = new DropShadow(20, Color.CORNFLOWERBLUE);
		    dropShadow.setSpread(0.75);
		    imageView.setEffect(dropShadow);
		}

		// Add item to list
		heldItems.getChildren().add(imageView);

		// Increment current item index
		currentItemIndex++;
	    }

	    // Ammo hbox
	    HBox ammoBox = new HBox();

	    Gun currentGun = (Gun) currentPlayer.getItems().get(currentPlayer.getCurrentItemIndex()); // Get current
												      // item TODO: make
												      // this abstract
												      // and not gun

	    // Make label for current ammo
	    Label currentAmmo = new Label(Integer.toString(currentGun.getCurrentAmmo()),
		    new ImageView(new Image("file:assets/img/ammo_clip.png")));
	    currentAmmo.setFont(new Font("Consolas", 32));
	    currentAmmo.setTextFill(Color.BLACK);

	    // Make label for ammo in clip
	    Label clipAmmo = new Label("/" + currentGun.getClipSize());
	    clipAmmo.setFont(new Font("Consolas", 18));
	    clipAmmo.setTextFill(Color.DARKSLATEGREY);

	    // Add to ammo hbox
	    ammoBox.getChildren().addAll(currentAmmo, clipAmmo);

	    // Add elements of GUI for player to GUI
	    GUIBox.getChildren().addAll(playerLabel, heartBox, playerScore, heldItems, ammoBox);
	}

	// Create root stackpane and add elements to be rendered to it
	StackPane root = new StackPane();
	root.getChildren().addAll(mainHBox, GUIBox);

	// Create the main scene
	Scene scene = new Scene(root, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

	// Setting title to the Stage
	stage.setTitle("Game");

	// A dding scene to the stage
	stage.setScene(scene);

	// Displaying the contents of the stage
	stage.show();
    }

    // Method for rendering entity onto map
    void renderEntity(Entity entity, GraphicsContext gc, String imagePath) {
	// Get image to render from path
	Image imageToRender = new Image(imagePath);

	// Check if image loaded properly
	checkImageLoaded(imageToRender, imagePath);

	// Render entity to specified location on graphicscontext
	drawRotatedImage(gc, imageToRender, entity.getPose().getDirection(), entity.getPose().getX(),
		entity.getPose().getY());
    }

    // Method for setting transform for the GraphicsContext to rotate around a pivot
    // point.
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

    // Method for scaling image by integer value by resampling - useful for large
    // enemies/powerups
    public Image resampleImage(Image inputImage, int scaleFactor) {
	final int inputImageWidth = (int) inputImage.getWidth();
	final int inputImageHeight = (int) inputImage.getHeight();

	// Set up output image
	WritableImage outputImage = new WritableImage(inputImageWidth * scaleFactor, inputImageHeight * scaleFactor);

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

    /*
     * void renderEntity(String imageLocation, GraphicsContext gc, Object
     * objectToRender) { Image imageToRender = new Image("file:" + imageLocation);
     * 
     * checkImageLoaded(imageToRender, imageLocation); // Check if loaded correctly
     * 
     * drawRotatedImage(gc, imageToRender, objectToRender.getPose().getDirection(),
     * objectToRender.getPose().getX(), objectToRender.getPose().getY()); }
     */
}
