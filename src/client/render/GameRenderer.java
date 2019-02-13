package client.render;

import client.input.KeyboardHandler;
import client.input.MouseHandler;
import client.net.ClientSender;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import shared.Constants;
import shared.lists.AmmoList;
import shared.lists.EntityList;
import shared.view.GameView;
import shared.view.ItemView;
import shared.view.entity.*;
import sun.plugin.javascript.navig.Anchor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class GameRenderer implements Runnable {
    // HashMap to store all graphics
    Map<EntityList, Image> loadedSprites;
    // Client
    ClientSender sender;
    // Reusable variables used in rendering gameview
    private Canvas mapCanvas;
    private GraphicsContext mapGC;
    AnchorPane mapBox; // Pane for map canvas
    // Whether the camera is centered on the player or not
    private boolean cameraCentered;
    // Fonts
    private Font fontManaspace28;
    private Font fontManaspace18;
    // HUD items
    private Label playerScoreNumber;
    private FlowPane heldItems;
    private FlowPane heartBox;
    private VBox ammoBox;
    // Current player info
    private int playerID;
    // GameView object which is to be updated
    private GameView gameView;
    // Stage to render to
    private Stage stage;
    // Input variables
    private KeyboardHandler kbHandler;
    private MouseHandler mHandler;

    //TODO: REMOVE THIS, PURELY FOR TEST
    public GameRenderer(Stage stage, GameView initialGameView, int playerID) {
        this(stage, initialGameView, playerID, true);
    }
    // Constructor
    public GameRenderer(Stage stage, GameView initialGameView, int playerID, boolean cameraCentered) {
        // Initialise gameView, stage and playerID
        this.gameView = initialGameView;
        this.stage = stage;
        this.playerID = playerID;
        this.cameraCentered = cameraCentered;

        // Load fonts
        try {
            fontManaspace28 = Font.loadFont(new FileInputStream(new File(Constants.MANASPACE_FONT_PATH)), 28);
            fontManaspace18 = Font.loadFont(new FileInputStream(new File(Constants.MANASPACE_FONT_PATH)), 18);
        } catch (FileNotFoundException e) {
            System.out.println("Loading default font, font not found in " + Constants.MANASPACE_FONT_PATH);
            fontManaspace28 = new Font("Consolas", 28);
            fontManaspace18 = new Font("Consolas", 18);
        }

        // Iterate over sprites array and load all sprites used in the game
        loadedSprites = new HashMap<>();

        for (EntityList entity : EntityList.values()) {
            // Load image
            Image tempImage = new Image(entity.getPath());

            // Check if loaded properly, and if loaded properly then store in the loaded sprites hashmap
            if (tempImage.isError()) {
                System.out.println("Error when loading image: " + entity.name() + " from directory: " + entity.getPath());
                loadedSprites.put(entity, new Image(EntityList.DEFAULT.getPath()));
            } else {
                loadedSprites.put(entity, tempImage);
            }
        }

        // Initialize HUD elements
        playerScoreNumber = null;
        heartBox = null;
        heldItems = null;
        ammoBox = null;

        // Initialise input variables
        kbHandler = new KeyboardHandler();
        mHandler = new MouseHandler();
    }

    // Run the thread - set up window and update game on a timer
    @Override
    public void run() {
        // Set up GameView - change the stage
        setUpGameView(gameView, playerID);

        // Update the HUD and game at intervals - animationtimer used for maximum frame rate TODO: see if this causes issues
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderGameView();
            }
        }.start();

        /*
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> renderGameView()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        */
    }

    // Set up the window for tha game
    private void setUpGameView(GameView inputGameView, int playerID) {
        mapBox = new AnchorPane();

        if (cameraCentered) {
            mapCanvas = new Canvas(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        } else {
            // Create canvas according to dimensions of the map
            mapCanvas = new Canvas(inputGameView.getXDim() * Constants.TILE_SIZE,
                    inputGameView.getYDim() * Constants.TILE_SIZE);
            AnchorPane.setRightAnchor(mapCanvas, 15.0);
            AnchorPane.setTopAnchor(mapCanvas, 15.0);
        }
        mapGC = mapCanvas.getGraphicsContext2D();
        mapBox.getChildren().addAll(mapCanvas);

        // Create HUD
        VBox HUDBox = createHUD(inputGameView, playerID);
        HUDBox.setAlignment(Pos.TOP_LEFT);

        // Create root stackpane and add elements to be rendered to it
        StackPane root = new StackPane();
        root.setAlignment(Pos.TOP_LEFT);
        root.getChildren().addAll(mapBox, HUDBox);

        // Set stage root to game renderer
        stage.getScene().setRoot(root);

        // Initialise input handler methods
        kbHandler.setGameView(inputGameView);
        kbHandler.setScene(stage.getScene());
        kbHandler.activate();
        mHandler.setCanvas(mapCanvas);
        mHandler.setGameView(inputGameView);
        mHandler.setScene(stage.getScene());
        mHandler.activate();
    }

    // Update stored gameView
    public void updateGameView(GameView gameView) {
        this.gameView = gameView;
        this.kbHandler.setGameView(gameView);
        this.mHandler.setGameView(gameView);
    }

    // Render gameView - KEEP PRIVATE
    private void renderGameView() {
        // Render map
        renderMap();

        // Render entities onto canvas
        renderEntitiesFromGameViewToCanvas();

        // Update HUD
        updateHUD();
    }

    // Render entities to the map canvas
    private void renderEntitiesFromGameViewToCanvas() {
        // Render items
        for (ItemDropView currentItem : gameView.getItemDrops()) {
            renderEntityView(currentItem);
        }

        // Render players
        for (PlayerView currentPlayer : gameView.getPlayers()) {
            // Get the correct sprite according to playerID, otherwise load the default player graphic
            Image spriteToRender;
            switch (currentPlayer.getID()) {
                case 0:
                    spriteToRender = loadedSprites.get(EntityList.PLAYER_1);
                    break;
                case 1:
                    spriteToRender = loadedSprites.get(EntityList.PLAYER_2);
                    break;
                case 2:
                    spriteToRender = loadedSprites.get(EntityList.PLAYER_3);
                    break;
                case 3:
                    spriteToRender = loadedSprites.get(EntityList.PLAYER_4);
                    break;
                default:
                    spriteToRender = loadedSprites.get(EntityList.PLAYER);
                    break;
            }

            renderEntity(currentPlayer, mapGC, spriteToRender);
        }

        // Render enemies
        for (EnemyView currentEnemy : gameView.getEnemies()) {
            renderEntityView(currentEnemy);
        }

        // Render projectiles
        for (ProjectileView currentProjectile : gameView.getProjectiles()) {
            renderEntityView(currentProjectile);
        }
    }

    private void renderEntityView(EntityView entityView) {
        // Get image from loaded sprites
        Image imageToRender = loadedSprites.get(entityView.getEntityListName());

        // Render image
        renderEntity(entityView, mapGC, imageToRender);
    }

    // Update all HUD elements
    private void updateHUD() {
        // Get the player from gameview
        PlayerView currentPlayer = null;
        for (PlayerView playerView : gameView.getPlayers()) {
            if (playerView.getID() == playerID) {
                currentPlayer = playerView;
                break;
            }
        }

        // Update score
        playerScoreNumber.setText(Integer.toString(currentPlayer.getScore()));

        // Update hearts
        heartBox.getChildren().clear();
        int halfHearts = currentPlayer.getHealth() % 2;
        int wholeHearts = currentPlayer.getHealth() / 2;
        int lostHearts = (currentPlayer.getMaxHealth() - currentPlayer.getHealth()) / 2;

        // Populate heart box in GUI
        for (int i = 0; i < wholeHearts; i++) {
            heartBox.getChildren().add(new ImageView(loadedSprites.get(EntityList.HEART_FULL)));
        }
        // Populate half heart
        for (int i = 0; i < halfHearts; i++) {
            heartBox.getChildren().add(new ImageView(loadedSprites.get(EntityList.HEART_HALF)));
        }
        // Populate lost hearts
        for (int i = 0; i < lostHearts; i++) {
            heartBox.getChildren().add(new ImageView(loadedSprites.get(EntityList.HEART_LOST)));
        }

        // Update held items
        heldItems.getChildren().clear();

        int currentItemIndex = 0; // Keep track of current item since iterator is used

        for (ItemView currentItem : currentPlayer.getItems()) {
            // Make image view out of graphic
            ImageView itemImageView = new ImageView(loadedSprites.get(currentItem.getItemListName().getEntityList()));

            // Check if the item currently being checked is the current selected item, and if it is, show that
            if (currentItemIndex == currentPlayer.getCurrentItemIndex()) {
                DropShadow dropShadow = new DropShadow(20, Color.CORNFLOWERBLUE);
                dropShadow.setSpread(0.75);
                itemImageView.setEffect(dropShadow);
            }

            // Add item to list
            heldItems.getChildren().add(itemImageView);

            // Increment current item index
            currentItemIndex++;
        }

        // Get currently selected item
        ItemView currentItem = currentPlayer.getCurrentItem();

        // Clear ammo box
        ammoBox.getChildren().clear();

        // Information about current gun - ammo in clip and clip size
        HBox currentGunInfo = new HBox();

        // Add ammo amount to hud if the item has ammo
        if (currentItem.getAmmoType() != AmmoList.NONE) {
            // Make label for current ammo in item
            Label ammoInGun = new Label(Integer.toString(currentItem.getAmmoInClip()),
                    new ImageView(loadedSprites.get(EntityList.AMMO_CLIP)));
            ammoInGun.setFont(fontManaspace28);
            ammoInGun.setTextFill(Color.BLACK);
            // Make label for total ammo in clip
            Label totalAmmoInClip = new Label("/" + currentItem.getClipSize());
            totalAmmoInClip.setFont(fontManaspace18);
            totalAmmoInClip.setTextFill(Color.BLACK);

            //Make label for total amount of ammo the current item uses
            Label totalAmmoForCurrentItem = new Label(Integer.toString(currentPlayer.getAmmo().get(currentItem.getAmmoType())));
            totalAmmoForCurrentItem.setFont(fontManaspace28);
            totalAmmoForCurrentItem.setTextFill(Color.DARKSLATEGRAY);

            // Add info on current gun to the right element
            currentGunInfo.getChildren().addAll(ammoInGun, totalAmmoInClip);
            // Add the total amount of ammo the current gun uses under the gun info
            ammoBox.getChildren().addAll(currentGunInfo, totalAmmoForCurrentItem);
        } else {
            // Make label for infinite use if it's not a weapon
            Label currentAmmo = new Label("∞");
            currentAmmo.setFont(new Font("Consolas", 32));
            currentAmmo.setTextFill(Color.BLACK);

            // Add info on current gun to the right element and to ammo box
            currentGunInfo.getChildren().addAll(currentAmmo);
            ammoBox.getChildren().add(currentGunInfo);
        }
    }

    // Render map from tiles
    private void renderMap() {
        // Get map X and Y dimensions
        int mapX = gameView.getXDim();
        int mapY = gameView.getYDim();

        // Iterate through the map, rending each tile on canvas
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                // Get tile graphic
                Image tileImage = loadedSprites.get(gameView.getTileMap()[x][y].getTileType().getEntityListName());

                // Add tile to canvas
                mapGC.drawImage(tileImage, x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE,
                        Constants.TILE_SIZE);
            }
        }
    }

    // Create HUD and initialise all elements
    private VBox createHUD(GameView inputGameView, int playerID) {
        // Make HUD
        VBox HUDBox = new VBox();
        HUDBox.setPadding(new Insets(5, 5, 5, 5));
        HUDBox.setMaxWidth(Constants.TILE_SIZE * 7);
        HUDBox.setSpacing(5);

        // Get the current player from the player list
        PlayerView currentPlayer = null;
        for (PlayerView player : inputGameView.getPlayers()) {
            if (player.getID() == playerID) {
                currentPlayer = player;
                break;
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

        // Ammo vbox
        ammoBox = new VBox();

        // Add elements of HUD for player to HUD
        HUDBox.getChildren().addAll(playerLabel, heartBox, playerScoreLabel, playerScoreNumber, heldItems, ammoBox);

        return HUDBox;
    }

    // Render entity onto the map canas
    private void renderEntity(EntityView entity, GraphicsContext gc, Image image) {
        // If entity's sizeScaleFactor isn't zero, enlarge the graphic
        if (entity.getSizeScaleFactor() != 1) {
            image = resampleImage(image, entity.getSizeScaleFactor());
        }

        // Render entity to specified location on canvas
        drawRotatedImage(gc, image, entity.getPose().getDirection(), entity.getPose().getX(),
                entity.getPose().getY());
    }

    // Set transform for the GraphicsContext to rotate around a pivot point.
    private void rotate(GraphicsContext gc, double angle, double xPivotCoordinate, double yPivotCoordinate) {
        Rotate r = new Rotate(angle, xPivotCoordinate, yPivotCoordinate);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    // Get image from the HashMap of loaded images according to the entity name
    private Image getImageFromEntity(EntityList entityName) {
        // Try to get the correct sprite, if not found then return default
        Image image = loadedSprites.get(entityName);

        if (image != null && !image.isError()) {
            return image;
        } else {
            System.out.println("Couldn't find the graphic for " + entityName.name() + " so loading default...");
            return loadedSprites.get(EntityList.DEFAULT);
        }
    }

    // Draw rotated image
    private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
        gc.save(); // Saves the current state on stack, including the current transform for later
        rotate(gc, angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
        gc.drawImage(image, tlpx, tlpy);
        gc.restore(); // Back to original state (before rotation)
    }

    // Create an Image object from specified colour
    private Image createImageFromColor(Color color) {
        WritableImage image = new WritableImage(1, 1);
        image.getPixelWriter().setColor(0, 0, color);
        return image;
    }

    // Scale image by integer value through resampling - useful for large enemies/powerups
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

    public void setClientSender(ClientSender sender) {
        this.sender = sender;
    }

    public GameView getView() {
        return this.gameView;
    }

    public KeyboardHandler getKeyboardHandler() {
        return this.kbHandler;
    }

    public MouseHandler getMouseHandler() {
        return this.mHandler;
    }
}
