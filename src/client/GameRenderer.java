package client;

import client.data.*;
import data.Constants;
import data.entity.item.weapon.gun.AmmoList;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GameRenderer implements Runnable {
    // Reusable variables used in rendering gameview
    private Font fontManaspace28;
    private Font fontManaspace18;
    private PlayerView currentPlayer;
    private FlowPane heldItems;
    private Canvas mapCanvas;
    private GraphicsContext mapGC;
    private Label playerScoreNumber;
    private FlowPane heartBox;
    private HBox ammoBox;
    private Image defaultGraphic;

    // GameView object which is to be updated
    private GameView gameView;

    // Stage to render to
    private Stage stage;

    // PlayerID of current client
    private int playerID;

    public GameRenderer(Stage stage, GameView gameView, int playerID) {
        // Initialise gameView, stage and playerID
        this.gameView = gameView;

        this.stage = stage;

        this.playerID = playerID;

        // Load fonts
        try {
            fontManaspace28 = Font.loadFont(new FileInputStream(new File(Constants.MANASPACE_FONT_PATH)), 28);
            fontManaspace18 = Font.loadFont(new FileInputStream(new File(Constants.MANASPACE_FONT_PATH)), 18);
        } catch (FileNotFoundException e) {
            System.out.println("Loading default font, font not found in " + Constants.MANASPACE_FONT_PATH);
            fontManaspace28 = new Font("Consolas", 28);
            fontManaspace18 = new Font("Consolas", 18);
        }

        // Load the default graphic
        defaultGraphic = new Image(Constants.DEFAULT_GRAPHIC_PATH);
        if (!checkImageLoaded(defaultGraphic, Constants.DEFAULT_GRAPHIC_PATH)) {
            System.out.println("Default texture couldn't be loaded! There could be potential issues with the game!");
        }

        // Initialize HUD elements
        playerScoreNumber = null;
        heartBox = null;
        currentPlayer = null;
        heldItems = null;
        ammoBox = null;
    }

    @Override
    public void run() {
        // Set up GameView - change the stage
        setUpGameView(gameView, playerID);

        // Update the HUD and game at intervals
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderGameView();
            }
        }.start();
    }

    // Method for updating the gameview
    public void updateGameView(GameView gameView) {
        this.gameView = gameView;
    }

    // Method for rendering the gameview
    public void renderGameView() {
        // Render map
        renderMap(gameView, mapGC);

        // Render players
        for (PlayerView currentPlayer : gameView.getPlayers()) {
            renderEntity(currentPlayer, mapGC, currentPlayer.getPathToGraphic());
        }

        // Render enemies
        for (EnemyView currentEnemy : gameView.getEnemies()) {
            renderEntity(currentEnemy, mapGC, currentEnemy.getPathToGraphic());
        }

        // Render projectiles
        for (ProjectileView currentProjectile : gameView.getProjectiles()) {
            renderEntity(currentProjectile, mapGC, currentProjectile.getPathToGraphic());
        }

        // Render items
        for (ItemDropView currentItem : gameView.getItemDrops()) {
            renderEntity(currentItem, mapGC, currentItem.getPathToGraphic());
        }

        // Update HUD
        updateHUD();
    }

    private void updateHUD() {
        // Update score
        playerScoreNumber.setText(Integer.toString(currentPlayer.getScore()));

        // Update hearts
        heartBox.getChildren().clear();
        int halfHearts = currentPlayer.getHealth() % 2;
        int wholeHearts = currentPlayer.getHealth() / 2;
        int missingHearts = (currentPlayer.getMaxHealth() - currentPlayer.getHealth()) / 2;

        // Populate heart box in GUI
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

        // Update held items
        heldItems.getChildren().clear();

        int currentItemIndex = 0; // Keep track of current item since iterator is used

        for (ItemView currentItem : currentPlayer.getItems()) {
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

        // Get currently selected item
        ItemView currentItem = currentPlayer.getCurrentItem();

        // Update ammo counts
        ammoBox.getChildren().clear();

        // Add ammo amount to hud if the item has ammo
        if (currentItem.getAmmoType() != AmmoList.NONE) {
            // Make label for current ammo in item
            Label currentAmmo = new Label(Integer.toString(currentItem.getAmmoInClip()),
                    new ImageView(new Image("file:assets/img/other/ammo_clip.png")));
            currentAmmo.setFont(fontManaspace28);
            currentAmmo.setTextFill(Color.BLACK);

            // Make label for total ammo
            Label clipAmmo = new Label("/" + currentItem.getClipSize());
            clipAmmo.setFont(fontManaspace18);
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
    }

    private void setUpGameView(GameView inputGameView, int playerID) {
        // Create canvas according to dimensions of the map
        mapCanvas = new Canvas(inputGameView.getXDim() * Constants.TILE_SIZE,
                inputGameView.getYDim() * Constants.TILE_SIZE);
        mapGC = mapCanvas.getGraphicsContext2D();

        // Create hbox to centre map canvas in and add map canvas to it
        HBox mainHBox = new HBox();
        mainHBox.setAlignment(Pos.CENTER_RIGHT);
        mainHBox.setPadding(new Insets(0, 15, 0, 0));
        mainHBox.getChildren().addAll(mapCanvas);

        // Create HUD
        VBox HUDBox = createHUD(inputGameView, playerID);
        HUDBox.setAlignment(Pos.TOP_LEFT);

        // Create root stackpane and add elements to be rendered to it
        StackPane root = new StackPane();
        root.setAlignment(Pos.TOP_LEFT);
        root.getChildren().addAll(mainHBox, HUDBox);

        // Create the main scene
        Scene scene = new Scene(root, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        // Update stage
        updateStageWithScene(stage, scene);
    }

    private void renderMap(GameView inputGameState, GraphicsContext mapGC) {
        // Get map X and Y dimensions
        int mapX = inputGameState.getXDim();
        int mapY = inputGameState.getYDim();

        // Iterate through the map, rending each tile on canvas
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                // Get tile graphic
                Image tileImage = new Image(inputGameState.getTileMap()[x][y].getPathToGraphic());

                // Check if tile graphic loaded properly and of the right dimensions, if not then print error and load default
                if (!(checkImageLoaded(tileImage, inputGameState.getTileMap()[x][y].getPathToGraphic()))
                        || tileImage.getWidth() != Constants.TILE_SIZE || tileImage.getHeight() != Constants.TILE_SIZE) {
                    tileImage = defaultGraphic;
                }

                // Add tile to canvas
                mapGC.drawImage(tileImage, x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE,
                        Constants.TILE_SIZE);
            }
        }
    }

    private VBox createHUD(GameView inputGameView, int playerID) {
        // Make HUD
        VBox HUDBox = new VBox();
        HUDBox.setPadding(new Insets(5, 5, 5, 5));
        HUDBox.setMaxWidth(Constants.TILE_SIZE * 7);
        HUDBox.setSpacing(5);

        // Get the current player from the player list
        for (PlayerView player : inputGameView.getPlayers()) {
            if (player.getID() == playerID) {
                currentPlayer = player;
            }
        }

        // If for some reason the player hasn't been found, return an empty HUD
        if (currentPlayer == null) {
            return new VBox();
        }

        playerScoreNumber = new Label();
        playerScoreNumber.setFont(fontManaspace28);
        playerScoreNumber.setTextFill(Color.BLACK);

        // Label with player name to tell which player this part of the HUD is for
        Label playerLabel = new Label(currentPlayer.getName());
        playerLabel.setFont(fontManaspace28);
        playerLabel.setTextFill(Color.BLACK);

        // Player score
        Label playerScoreLabel = new Label("SCORE: ");
        playerScoreLabel.setFont(fontManaspace28);
        playerScoreLabel.setTextFill(Color.BLACK);

        // Flowpane to hold heart graphics. Have it overflow onto the next "line" after 5 hearts displayed
        heartBox = new FlowPane();
        heartBox.setMaxWidth(Constants.TILE_SIZE * 5);

        // Iterate through held items list and add to the HUD
        heldItems = new FlowPane(); // Make flowpane for held items - supports unlimited amount of them

        // Ammo hbox
        ammoBox = new HBox();

        // Add elements of HUD for player to HUD
        HUDBox.getChildren().addAll(playerLabel, heartBox, playerScoreLabel, playerScoreNumber, heldItems, ammoBox);

        return HUDBox;
    }

    // Method for rendering entity onto map
    private void renderEntity(EntityView entity, GraphicsContext gc, String imagePath) {
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
            System.out.println("Image not loaded properly from: " + imageDirectory);
            return false;
        }

        return true;
    }

    // Method for scaling image by integer value by resampling - useful for large enemies/powerups
    private Image resampleImage(Image inputImage, int scaleFactor) {
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

    private void updateStageWithScene(Stage stage, Scene scene) {
        // Check if JavaFX thread and update stage accordingly TODO: see if this causes isses
        if (Platform.isFxApplicationThread()) {
            stage.setScene(scene);
            scene.getRoot().requestFocus();
        } else {
            // runLater because not JavaFX thread
            Platform.runLater(() -> {
                // Add scene to stage, request focus and show the stage
                stage.setScene(scene);
                scene.getRoot().requestFocus();
                stage.show();
            });
        }
    }
}
