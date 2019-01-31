import data.Constants;
import data.GameState;
import data.SystemState;
import data.entity.Entity;
import data.entity.enemy.Enemy;
import data.entity.item.Item;
import data.entity.item.ItemDrop;
import data.entity.item.weapon.Gun;
import data.entity.player.Player;
import data.entity.projectile.Projectile;
import data.gui.MainMenuController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Renderer {
    private Stage stage;
    private Image defaultGraphic;
    private SystemState systemState;

    // Constructor - take stage
    public Renderer(Stage inputStage) {
        // Set stage
        this.stage = inputStage;

        systemState = SystemState.MAIN_MENU;

        // Load the default graphic
        defaultGraphic = new Image(Constants.DEFAULT_GRAPHIC_PATH);
        if (!checkImageLoaded(defaultGraphic, Constants.DEFAULT_GRAPHIC_PATH)) {
            System.out.println("Default texture couldn't be loaded! There could be potential issues with the game!");
        }

        // Setting title to the Stage
        stage.setTitle("Gunderwurlde");
    }

    // Render input gamestate to stage
    public void renderGameState(GameState inputGameState) {
        systemState = SystemState.GAME;

        // Create canvas according to dimensions of the map
        Canvas mapCanvas = new Canvas(inputGameState.getCurrentMap().getXDim() * Constants.TILE_SIZE,
                inputGameState.getCurrentMap().getYDim() * Constants.TILE_SIZE);
        GraphicsContext mapGC = mapCanvas.getGraphicsContext2D();

        // Render map
        renderMap(inputGameState, mapGC);

        // Render players
        for (Player currentPlayer : inputGameState.getPlayers()) {
            renderEntity(currentPlayer, mapGC, currentPlayer.getPathToGraphic());
        }

        // Render enemies
        for (Enemy currentEnemy : inputGameState.getEnemies()) {
            renderEntity(currentEnemy, mapGC, currentEnemy.getPathToGraphic());
        }

        // Render projectiles
        for (Projectile currentProjectile : inputGameState.getProjectiles()) {
            renderEntity(currentProjectile, mapGC, currentProjectile.getPathToGraphic());
        }

        // Render items
        for (ItemDrop currentItem : inputGameState.getItems()) {
            renderEntity(currentItem, mapGC, currentItem.getPathToGraphic());
        }

        // Create hbox to centre map canvas in and add map canvas to it
        HBox mainHBox = new HBox();
        mainHBox.setAlignment(Pos.CENTER_RIGHT);
        mainHBox.setPadding(new Insets(0, 15, 0, 0));
        mainHBox.getChildren().addAll(mapCanvas);

        // Create HUD
        VBox HUDBox = createHUD(inputGameState);
        HUDBox.setAlignment(Pos.TOP_LEFT);

        // Create root stackpane and add elements to be rendered to it
        StackPane root = new StackPane();
        root.setAlignment(Pos.TOP_LEFT);
        root.getChildren().addAll(mainHBox, HUDBox);

        // Create the main scene
        Scene scene = new Scene(root, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        // Adding scene to the stage
        stage.setScene(scene);

        // Displaying the contents of the stage
        stage.show();
    }

    private void renderMap(GameState inputGameState, GraphicsContext mapGC) {
        // Get map X and Y dimensions
        int mapX = inputGameState.getCurrentMap().getXDim();
        int mapY = inputGameState.getCurrentMap().getYDim();

        // Iterate through the map, rending each tile on canvas
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                // Get tile graphic
                Image tileImage = new Image(inputGameState.getCurrentMap().getTileMap()[x][y].getPathToGraphic());

                // Check if tile graphic loaded properly and of the right dimensions, if not then print error and load default
                if (!(checkImageLoaded(tileImage, inputGameState.getCurrentMap().getTileMap()[x][y].getPathToGraphic()))
                        || tileImage.getWidth() != Constants.TILE_SIZE || tileImage.getHeight() != Constants.TILE_SIZE) {
                    tileImage = defaultGraphic;
                }

                // Add tile to canvas
                mapGC.drawImage(tileImage, x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE,
                        Constants.TILE_SIZE);
            }
        }
    }

    private VBox createHUD(GameState inputGameState) {
        // Make HUD
        VBox HUDBox = new VBox();
        HUDBox.setPadding(new Insets(5, 5, 5, 5));
        HUDBox.setMaxWidth(Constants.TILE_SIZE * 6);
        HUDBox.setSpacing(5);

        // Make a separate HUD for each player in case coop
        for (Player currentPlayer : inputGameState.getPlayers()) {
            // Label with player name to tell which player this part of the HUD is for
            Label playerLabel = new Label(currentPlayer.getName());
            playerLabel.setFont(new Font("Consolas", 32));
            playerLabel.setTextFill(Color.BLACK);

            // Player score
            Label playerScore = new Label("Score: " + currentPlayer.getScore());
            playerScore.setFont(new Font("Consolas", 32));
            playerScore.setTextFill(Color.BLACK);

            // HBox to horizontally keep heart graphics. Populate HBox with amount of life necessary
            FlowPane heartBox = new FlowPane();
            //heartBox.setPrefWrapLength((new Image("file:assets/img/other/heart.png")).getWidth() * 5);

            // Calculate amount of hearts to generate from health
            int halfHearts = currentPlayer.getHealth() % 2;
            int wholeHearts = currentPlayer.getHealth() / 2;
            int missingHearts = (currentPlayer.getMaxHealth() - currentPlayer.getHealth()) / 2;

            // Populate heart box in GUI TODO: load default heart texture?
            for (int i = 0; i < wholeHearts; i++) {
                heartBox.getChildren().add(new ImageView(new Image("file:assets/img/other/heart.png")));
            }
            // Populate half heart
            for (int i = 0; i < halfHearts; i++) {
                heartBox.getChildren().add(new ImageView(new Image("file:assets/img/other/half_heart.png")));
            }
            // Populate lost life
            for (int i = 0; i < missingHearts; i++) {
                heartBox.getChildren().add(new ImageView(new Image("file:assets/img/other/lost_heart.png")));
            }

            // Iterate through held items list and add to the HUD
            FlowPane heldItems = new FlowPane(); // Make flowpane for held items - supports unlimited amount of them

            int currentItemIndex = 0; // Keep track of current item since iterator is used

            for (Item currentItem : currentPlayer.getItems()) {
                // Make image view out of graphic
                ImageView imageView = new ImageView(new Image(currentItem.getPathToGraphic()));

                // Check if the item currently being checked is the current selected item, and if it is, show that
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

            // TODO: extend this for classes that also use ammo?
            Item currentItem = currentPlayer.getItems().get(currentPlayer.getCurrentItemIndex()); // Get current item

            // Add ammo amount to hud if it's a gun
            if (currentItem instanceof Gun) {
                Gun currentGun = (Gun) currentItem; // Make Gun object from the input item

                // Make label for current ammo
                Label currentAmmo = new Label(Integer.toString(currentGun.getCurrentAmmo()),
                        new ImageView(new Image("file:assets/img/other/ammo_clip.png")));
                currentAmmo.setFont(new Font("Consolas", 32));
                currentAmmo.setTextFill(Color.BLACK);

                // Make label for ammo in clip
                Label clipAmmo = new Label("/" + currentGun.getClipSize());
                clipAmmo.setFont(new Font("Consolas", 18));
                clipAmmo.setTextFill(Color.DARKSLATEGREY);

                // Add to ammo hbox
                ammoBox.getChildren().addAll(currentAmmo, clipAmmo);
            } else {
                // Make label for infinite use if it's not a weapon
                Label currentAmmo = new Label("∞");
                currentAmmo.setFont(new Font("Consolas", 32));
                currentAmmo.setTextFill(Color.BLACK);

                // Add to ammo hbox
                ammoBox.getChildren().addAll(currentAmmo);
            }

            // Add elements of HUD for player to HUD
            HUDBox.getChildren().addAll(playerLabel, heartBox, playerScore, heldItems, ammoBox);
        }

        return HUDBox;
    }

    // Method for rendering entity onto map
    private void renderEntity(Entity entity, GraphicsContext gc, String imagePath) {
        // Get image to render from path
        Image imageToRender = new Image(imagePath);

        // If image not loaded properly, print error and load default graphic
        if (!checkImageLoaded(imageToRender, imagePath)) {
           imageToRender = defaultGraphic;
        }

        // If entity's size isn't zero, enlarge the graphic
        if (entity.getSize() != 1) {
            imageToRender = resampleImage(imageToRender, entity.getSize());
        }

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
            System.out.println("Image not loaded properly from: " + imageDirectory);
            return false;
        }

        return true;
    }

    // Method for scaling image by integer value by resampling - useful for large enemies/powerups
    public Image resampleImage(Image inputImage, int scaleFactor) {
        final int inputImageWidth = (int) inputImage.getWidth();
        final int inputImageHeight = (int) inputImage.getHeight();

        // Set up output image
        WritableImage outputImage = new WritableImage(inputImageWidth * scaleFactor,
                inputImageHeight * scaleFactor);

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

    public SystemState getSystemState() {
        return systemState;
    }

    public void renderMenu(SystemState inputSystemState) {
        MainMenuController mainMenuController = new MainMenuController();

        // Create the main scene
        Scene scene = new Scene(mainMenuController, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        // Adding scene to the stage
        stage.setScene(scene);

        // Request focus and show stage
        scene.getRoot().requestFocus();
        stage.show();
    }
}
