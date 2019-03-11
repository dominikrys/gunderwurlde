package client.render;

import client.gui.Settings;
import client.input.KeyboardHandler;
import client.input.MouseHandler;
import client.net.ClientSender;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.AmmoList;
import shared.lists.EntityList;
import shared.view.GameView;
import shared.view.ItemView;
import shared.view.SoundView;
import shared.view.entity.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class GameRenderer implements Runnable {
    // HashMap to store all graphics
    private Map<EntityList, Image> loadedSprites;
    // Client
    private ClientSender sender;
    private AnchorPane mapBox; // Pane for map canvas
    // Reusable variables used in rendering gameview
    private Canvas mapCanvas;
    private GraphicsContext mapGC;
    // Fonts
    private Font fontManaspace28;
    private Font fontManaspace18;
    // HUD items
    private Label playerScoreNumber;
    private FlowPane heldItems;
    private FlowPane heartBox;
    private VBox ammoBox;
    // Pane and imageview for cursor
    private AnchorPane cursorPane;
    private ImageView cursorImage;
    // Current player info
    private int playerID;
    // GameView object which is to be updated
    private GameView gameView;
    // Stage to render to
    private Stage stage;
    // Whether the game is paused or not
    private boolean paused;
    private VBox pausedOverlay;
    // Input variables
    private KeyboardHandler kbHandler;
    private MouseHandler mHandler;
    // Settings object
    private Settings settings;
    private SoundView soundView;
    // X and Y coordinates of the mouse
    private double mouseX;
    private double mouseY;
    // Animation hashmaps
    private Map<Integer, AnimatedSpriteManager> playersOnMapAnimations;
    private Map<Integer, AnimatedSpriteManager> enemiesOnMapAnimations;
    // Last location of players and enemies hashmaps
    private Map<Integer, Pose> lastPlayerLocations;
    private Map<Integer, Pose> lastEnemyLocations;

    // Constructor
    public GameRenderer(Stage stage, GameView initialGameView, int playerID, Settings settings) {
        // Initialise gameView, stage and playerID
        this.gameView = initialGameView;
        this.stage = stage;
        this.playerID = playerID;
        this.settings = settings;

        // Set paused to false
        paused = false;

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
            Image loadedImage = new Image(entity.getPath());

            // Check if loaded properly
            if (loadedImage.isError()) {
                System.out.println("Couldn't load image: " + entity.name() + " from directory: " + entity.getPath());
                loadedSprites.put(entity, new Image(EntityList.DEFAULT.getPath()));
            } else {
                // Check if loaded image dimensions consistent with dim:ensions in EntityList (if the entity has a specified size)
                if (entity.getSize() != 0) {
                    /*
                    TODO: add width check back in?
                    if (loadedImage.getWidth() != entity.getSize()) {
                        System.out.println("Error when loading " + entity.name() + ": width incorrect! Should be: " + entity.getSize());
                    }
                    */
                    if (loadedImage.getHeight() != entity.getSize()) {
                        System.out.println("Image " + entity.name() + ": loaded with incorrect height, should be: " + entity.getSize());
                    }
                }

                // Check if transformation necessary, and if so perform it
                if (entity.getColorAdjust() != null) {
                    // Create new imageview and apply the color adjustment
                    ImageView loadedImageView = new ImageView(loadedImage);
                    loadedImageView.setEffect(entity.getColorAdjust());

                    // Convert loaded image from imageview and store  in the loaded sprites hashmap
                    Platform.runLater(() -> {
                        SnapshotParameters sp = new SnapshotParameters();
                        sp.setFill(Color.TRANSPARENT);

                        loadedSprites.put(entity, SwingFXUtils.toFXImage(
                                SwingFXUtils.fromFXImage(loadedImageView.snapshot(sp, null), null), null));
                    });
                } else {
                    // No transformation necessary, just store in sprites hashmap
                    loadedSprites.put(entity, loadedImage);
                }
            }
        }

        // Initialize HUD elements
        playerScoreNumber = null;
        heartBox = null;
        heldItems = null;
        ammoBox = null;

        // Initialise cursor pane
        cursorPane = new AnchorPane();

        // Initialise animation hashmaps
        playersOnMapAnimations = new HashMap<>();
        enemiesOnMapAnimations = new HashMap<>();

        // Initialise location hashmaps
        lastPlayerLocations = new HashMap<>();
        lastEnemyLocations = new HashMap<>();

        // Initialise mouse positions to not bug out camera
        mouseX = (double) settings.getScreenWidth() / 2 - getCurrentPlayer().getPose().getX() - (double) Constants.TILE_SIZE / 2;
        mouseY = (double) settings.getScreenHeight() / 2 - getCurrentPlayer().getPose().getY() - (double) Constants.TILE_SIZE / 2;

        // Initialise input variables
        kbHandler = new KeyboardHandler(this.playerID, settings);
        mHandler = new MouseHandler(this.playerID);

        // Initialise soundview
        soundView = new SoundView(initialGameView, settings);
    }

    // Run the thread - set up window and update game on a timer
    @Override
    public void run() {
        // Set up GameView - change the stage
        setUpGameView(gameView);

        // Update the HUD and game at intervals - animationtimer used for maximum frame rate
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderGameView();
            }
        }.start();

        // Alternative: use timeline. This way can also specify FPS
//        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> renderGameView()));
//        timeline.setCycleCount(Animation.INDEFINITE);
//        timeline.play();
    }

    // Set up the window for tha game
    private void setUpGameView(GameView inputGameView) {
        // Initialise pane for map
        mapBox = new AnchorPane();
        mapCanvas = new Canvas(settings.getScreenWidth(), settings.getScreenHeight());
        mapGC = mapCanvas.getGraphicsContext2D();
        mapBox.getChildren().addAll(mapCanvas);

        // Create HUD
        VBox HUDBox = createHUD();
        HUDBox.setAlignment(Pos.TOP_LEFT);

        // Create pause overlay
        // "PAUSE" message
        Label pauseLabel = new Label("PAUSE");
        pauseLabel.setFont(fontManaspace28);
        pauseLabel.setTextFill(Color.BLACK);

        // Label with instructions how to unpause
        // TODO: add e.g. settings.getPauseKey() when key settings in settings object
        Label pauseInstructions = new Label("Press ESC to unpause");
        pauseLabel.setFont(fontManaspace18);
        pauseLabel.setTextFill(Color.BLACK);

        // Set pausedoverlay VBox - make it slightly translucent
        pausedOverlay = new VBox(pauseLabel, pauseInstructions);
        pausedOverlay.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.5);" +
                        "-fx-effect: dropshadow(gaussian, white, 50, 0, 0, 0);" +
                        "-fx-background-insets: 50;"
        );
        pausedOverlay.setAlignment(Pos.CENTER);
        pausedOverlay.setSpacing(10);
        pausedOverlay.setVisible(false);

        // Create root stackpane
        StackPane root = new StackPane();
        root.setAlignment(Pos.TOP_LEFT);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0),
                new Insets(0, 0, 0, 0))));

        // Add elements to root
        root.getChildren().addAll(mapBox, HUDBox, pausedOverlay, cursorPane);

        // Set cursor to none - crosshair of a different size can then be renderer that's not dictated by the system
        root.setCursor(Cursor.NONE);

        // Set crosshair to cursorpane
        cursorImage = new ImageView(loadedSprites.get(EntityList.CROSSHAIR));
        cursorPane.getChildren().add(cursorImage);

        // Event handlers for mouse movements
        stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, this::updateMouse);

        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::updateMouse);

        // Set root to scene
        stage.getScene().setRoot(root);

        // Initialise input handler methods
        kbHandler.setGameView(inputGameView);
        kbHandler.setScene(stage.getScene());
        kbHandler.activate();
        mHandler.setCanvas(mapCanvas);
        mHandler.setGameView(inputGameView);
        mHandler.setScene(stage.getScene());
        mHandler.activate();

        // Initialise sound
        soundView.activate();
    }

    private void updateMouse(MouseEvent e) {
        mouseX = e.getSceneX();
        mouseY = e.getSceneY();

        // Render cursor image to location of cursor
        renderCursor();
    }

    // Render cursor
    private void renderCursor() {
        AnchorPane.setLeftAnchor(cursorImage, mouseX - Constants.TILE_SIZE / 2);
        AnchorPane.setTopAnchor(cursorImage, mouseY - Constants.TILE_SIZE / 2);
    }

    // Update stored gameView
    public void updateGameView(GameView gameView) {
        this.gameView = gameView;
        this.kbHandler.setGameView(gameView);
        this.mHandler.setGameView(gameView);
        this.soundView.setGameView(gameView);
    }

    // Render gameView
    private void renderGameView() {
        // Render map
        renderMap();

        // Render entities onto canvas
        renderEntitiesFromGameViewToCanvas();

        // Center camera on player
        centerCamera();

        // Update HUD
        updateHUD();

        // If game is paused, add the paused overlay
        if (paused) {
            pausedOverlay.setVisible(true);
        } else {
            pausedOverlay.setVisible(false);
        }
    }

    private void centerCamera() {
        // Get player location on map
        PlayerView currentPlayer = getCurrentPlayer();
        double playerX = currentPlayer.getPose().getX();
        double playerY = currentPlayer.getPose().getY();

        // Ratio for how sensitive the map is to mouse movements
        // Proportional to position of player: (1 - (Math.abs(mouseX - settings.getScreenWidth() / 2) / settings.getScreenWidth() * 2))
        double cameraMouseSensitivity = 0.25;

        // Adjust map horizontally
        AnchorPane.setLeftAnchor(mapCanvas,
                (double) settings.getScreenWidth() / 2 - playerX - Constants.TILE_SIZE / 2 /* Center Player*/
                        + (settings.getScreenWidth() / 2 - mouseX) * cameraMouseSensitivity /* Mouse */);
        // Adjust map vertically
        AnchorPane.setTopAnchor(mapCanvas,
                (double) settings.getScreenHeight() / 2 - playerY - Constants.TILE_SIZE / 2 /* Center Player*/
                        + (settings.getScreenHeight() / 2 - mouseY) * cameraMouseSensitivity /* Mouse */);

    }

    // Render entities to the map canvas
    private void renderEntitiesFromGameViewToCanvas() {
        // Render items
        for (ItemDropView currentItem : gameView.getItemDrops()) {
            renderEntityView(currentItem);
        }

        // Render players
        for (PlayerView currentPlayer : gameView.getPlayers()) {
            // Update animation hashmap to track entity
            if (!playersOnMapAnimations.containsKey(currentPlayer.getID())) {
                playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager());
            }

            // Check correct animation
            if (currentPlayer.isMoving()) {
                // TODO: have this go through a scale factor check
                // Check if in map of currently tracked players and if not, add it
                if (playersOnMapAnimations.get(playerID).getAnimationType() != AnimationType.MOVE) {
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_WALK_RED), 32, 32,
                                    6, 75, 0, AnimationType.MOVE));
                            break;
                        case BLUE:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_WALK_BLUE), 32, 32,
                                    6, 75, 0, AnimationType.MOVE));
                            break;
                        case GREEN:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_WALK_GREEN), 32, 32,
                                    6, 75, 0, AnimationType.MOVE));
                            break;
                        case YELLOW:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_WALK_YELLOW), 32, 32,
                                    6, 75, 0, AnimationType.MOVE));
                            break;
                        default:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_WALK), 32, 32,
                                    6, 75, 0, AnimationType.MOVE));
                    }
                }
            }
            // Check if player reloading
            else if (currentPlayer.getCurrentAction() == ActionList.RELOADING) {
                // Check if in map of currently tracked players and if not, add it
                if (playersOnMapAnimations.get(playerID).getAnimationType() != AnimationType.RELOAD) {
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_RELOAD_RED), 32, 45,
                                    5, currentPlayer.getCurrentItem().getReloadTime() / 5,
                                    0, AnimationType.RELOAD));
                            break;
                        case BLUE:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_RELOAD_BLUE), 32, 45,
                                    5, currentPlayer.getCurrentItem().getReloadTime() / 5,
                                    0, AnimationType.RELOAD));
                            break;
                        case GREEN:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_RELOAD_GREEN), 32, 45,
                                    5, currentPlayer.getCurrentItem().getReloadTime() / 5,
                                    0, AnimationType.RELOAD));
                            break;
                        case YELLOW:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_RELOAD_YELLOW), 32, 45,
                                    5, currentPlayer.getCurrentItem().getReloadTime() / 5,
                                    0, AnimationType.RELOAD));
                            break;
                        default:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_RELOAD), 32, 45,
                                    5, currentPlayer.getCurrentItem().getReloadTime() / 5,
                                    0, AnimationType.RELOAD));
                    }
                }
            }
            // Check if player attacking
            else if (currentPlayer.getCurrentAction() == ActionList.ATTACKING) {
                switch (currentPlayer.getTeam()) {
                    case RED:
                        playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                loadedSprites.get(EntityList.PLAYER_WITH_GUN_RECOIL_RED), AnimationType.ATTACK));
                        break;
                    case GREEN:
                        playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                loadedSprites.get(EntityList.PLAYER_WITH_GUN_RECOIL_GREEN), AnimationType.ATTACK));
                        break;
                    case YELLOW:
                        playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                loadedSprites.get(EntityList.PLAYER_WITH_GUN_RECOIL_YELLOW), AnimationType.ATTACK));
                        break;
                    case BLUE:
                        playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                loadedSprites.get(EntityList.PLAYER_WITH_GUN_RECOIL_BLUE), AnimationType.ATTACK));
                        break;
                    default:
                        playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                loadedSprites.get(EntityList.PLAYER_WITH_GUN_RECOIL), AnimationType.ATTACK));
                        break;
                }
            }
            // If standing, render standing image
            else {
                // Check if player has item that takes bullets - if so, render player with a gun
                boolean hasGun = false;

                for (ItemView iv : currentPlayer.getItems()) {
                    if (iv.getAmmoType() != AmmoList.NONE) {
                        hasGun = true;
                        break;
                    }
                }

                if (hasGun) {
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_WITH_GUN_RED), AnimationType.STAND));
                            break;
                        case BLUE:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_WITH_GUN_BLUE), AnimationType.STAND));
                            break;
                        case GREEN:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_WITH_GUN_GREEN), AnimationType.STAND));
                            break;
                        case YELLOW:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_WITH_GUN_YELLOW), AnimationType.STAND));
                            break;
                        default:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_WITH_GUN), AnimationType.STAND));
                            break;
                    }
                } else {
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_RED), AnimationType.STAND));
                            break;
                        case BLUE:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_BLUE), AnimationType.STAND));
                            break;
                        case GREEN:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_GREEN), AnimationType.STAND));
                            break;
                        case YELLOW:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER_YELLOW), AnimationType.STAND));
                            break;
                        default:
                            playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.PLAYER), AnimationType.STAND));
                            break;
                    }
                }
            }

            // Animation now in playerOnMap map so just render in appropriate location
            renderAnimationSpriteOnMap(playersOnMapAnimations, currentPlayer.getID(), currentPlayer.getPose());

            // Render healthbar
            renderHealthBar(currentPlayer.getPose(), currentPlayer.getHealth(), currentPlayer.getMaxHealth(), mapGC);

            // Put player into player locations hashmap
            lastPlayerLocations.put(currentPlayer.getID(), currentPlayer.getPose());
        }

        // Render enemies
        for (EnemyView currentEnemy : gameView.getEnemies()) {
            // Update animation hashmap to track entity
            if (!enemiesOnMapAnimations.containsKey(currentEnemy.getID())) {
                enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager());
            }

            // Check if enemy moving and is so, choose right animation
            if (currentEnemy.isMoving()) {
                // Check if already in the hashmap for enemies on the map
                if (enemiesOnMapAnimations.get(currentEnemy.getID()).getAnimationType() != AnimationType.MOVE) {
                    switch (currentEnemy.getEntityListName()) {
                        case ZOMBIE:
                            enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.ZOMBIE_WALK), 32, 32,
                                    6, 75, 0, AnimationType.MOVE));
                            break;
                        case RUNNER:
                            enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.RUNNER_WALK), 32, 32,
                                    6, 75, 0, AnimationType.MOVE));
                            break;
                        case SOLDIER:
                            enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.SOLDIER_WALK), 32, 32,
                                    6, 100, 0, AnimationType.MOVE));
                            break;
                        case MIDGET:
                            enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.MIDGET_WALK), 32, 32,
                                    6, 25, 0, AnimationType.MOVE));
                            break;
                        default:
                            enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager(
                                    loadedSprites.get(EntityList.ZOMBIE_WALK), 32, 32,
                                    6, 75, 0, AnimationType.MOVE));
                            break;
                    }
                }
            }
            // Enemy standing, render standing image
            else {
                enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager(
                        loadedSprites.get(currentEnemy.getEntityListName()), AnimationType.STAND));
            }

            // Render animation - Animation now in playerOnMap map so just render in appropriate location
            renderAnimationSpriteOnMap(enemiesOnMapAnimations, currentEnemy.getID(), currentEnemy.getPose());

            // Render healthbar
            renderHealthBar(currentEnemy.getPose(), currentEnemy.getHealth(), currentEnemy.getMaxHealth(), mapGC);

            // Put enemy into enemy locations hashmap
            lastEnemyLocations.put(currentEnemy.getID(), currentEnemy.getPose());
        }

        // Render projectiles
        for (ProjectileView currentProjectile : gameView.getProjectiles()) {
            renderEntityView(currentProjectile);
        }

        // Render enemy death animations
        renderEnemyDeaths();

        // TODO: render player deaths
    }

    private void renderEnemyDeaths() {
        // Make a hashmap of all  enemies on map to ease calculations
        Map<Integer, Pose> gameViewEnemyPoses = new HashMap<>();
        for (EnemyView enemyView : gameView.getEnemies()) {
            gameViewEnemyPoses.put(enemyView.getID(), enemyView.getPose());
        }

        // Find all dead enemies
        HashMap<Integer, Pose> deadEnemies = (HashMap<Integer, Pose>) mapDifference(lastEnemyLocations, gameViewEnemyPoses);

        // Go through lit of dead enemies
        for (Map.Entry<Integer, Pose> entry : deadEnemies.entrySet()) {
            // Set up an animationtimer with a one-off animation for every enemy
            new AnimationTimer() {
                Pose pose = entry.getValue();
                int frameCount = 32;
                AnimatedSpriteManager deathSpriteManager = new AnimatedSpriteManager(
                        loadedSprites.get(EntityList.BLOOD_EXPLOSION), 32, 32,
                        frameCount, 25, 1, AnimationType.NONE);

                @Override
                public void handle(long now) {
                    // Check if animation still running - if not, stop animation
                    if (deathSpriteManager.getCurrentFrame() < frameCount - 1) {
                        drawRotatedImageFromSpritesheet(mapGC, deathSpriteManager.getImage(),
                                0, pose.getX(),
                                pose.getY(), deathSpriteManager.getSx(), deathSpriteManager.getSy(),
                                deathSpriteManager.getImageWidth(), deathSpriteManager.getImageHeight());
                    } else {
                        this.stop();
                    }
                }
            }.start();

            // Remove entry from last enemy locations so animation isn't played again
            lastEnemyLocations.remove(entry.getKey());
        }
    }

    // Find difference between two maps - first argument is the hashmap whose extras will be returned
    private <K, V> Map<K, V> mapDifference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
        Map<K, V> difference = new HashMap<>();
        difference.putAll(left);
        difference.putAll(right);
        difference.entrySet().removeAll(right.entrySet());
        return difference;
    }

    // Render image according to info from its animation
    private void renderAnimationSpriteOnMap(Map<Integer, AnimatedSpriteManager> entitiesOnMap, int id, Pose pose) {
        AnimatedSpriteManager thisSpriteManager = entitiesOnMap.get(id);

        drawRotatedImageFromSpritesheet(mapGC, thisSpriteManager.getImage(),
                pose.getDirection(), pose.getX(),
                pose.getY(), thisSpriteManager.getSx(), thisSpriteManager.getSy(),
                thisSpriteManager.getImageWidth(), thisSpriteManager.getImageHeight());
    }

    // Render healthbar above entity
    private void renderHealthBar(Pose pose, int currentHealth, int maxHealth, GraphicsContext gc) {
        // Variables for calculations
        int healthBarHeight = 5;
        int verticalOffset = 12;
        double healthLeftPercentage = (double) currentHealth / (double) maxHealth;

        // Render current health portion
        gc.setFill(Color.LIME);
        gc.fillRect(pose.getX(), pose.getY() - verticalOffset,
                Constants.TILE_SIZE * healthLeftPercentage, healthBarHeight);

        // Render lost health portion
        gc.setFill(Color.RED);
        gc.fillRect(pose.getX() + Constants.TILE_SIZE * healthLeftPercentage, pose.getY() - verticalOffset,
                Constants.TILE_SIZE * (1 - healthLeftPercentage), healthBarHeight);
    }

    private void renderEntityView(EntityView entityView) {
        // Get image from loaded sprites
        Image imageToRender = loadedSprites.get(entityView.getEntityListName());

        // Render image
        renderEntity(entityView, mapGC, imageToRender);
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

    // Draw rotated image
    private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
        gc.save(); // Saves the current state on stack, including the current transform for later
        rotate(gc, angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
        gc.drawImage(image, tlpx, tlpy);
        gc.restore(); // Back to original state (before rotation)
    }

    private void drawRotatedImageFromSpritesheet(GraphicsContext gc, Image image, double angle, double tlpx,
                                                 double tlpy, double sx, double sy, double sw, double sh) {
        gc.save(); // Saves the current state on stack, including the current transform for later
        rotate(gc, angle, tlpx + sw / 2, tlpy + sh / 2);
        gc.drawImage(image, sx, sy, sw, sh, tlpx, tlpy, sw, sh);
        gc.restore(); // Back to original state (before rotation)
    }

    // Method for getting the current player
    private PlayerView getCurrentPlayer() {
        for (PlayerView playerView : gameView.getPlayers()) {
            if (playerView.getID() == playerID) {
                return playerView;
            }
        }

        return null;
    }

    // Update all HUD elements
    private void updateHUD() {
        // Get the player from gameview
        PlayerView currentPlayer = getCurrentPlayer();

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

            // Pane for item image to go in - for border
            FlowPane itemPane = new FlowPane();
            itemPane.setPrefWidth(Constants.TILE_SIZE);
            itemPane.setPadding(new Insets(2, 2, 2, 2));

            // Check if the item currently being checked is the current selected item, and if it is, show that
            if (currentItemIndex == currentPlayer.getCurrentItemIndex()) {
                DropShadow dropShadow = new DropShadow(25, Color.HOTPINK);
                dropShadow.setSpread(0.75);
                itemImageView.setEffect(dropShadow);
                itemPane.setBorder(new Border(new BorderStroke(Color.HOTPINK,
                        BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(3))));
            } else {
                // Not selected item, add black border
                itemPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(3))));
            }

            // Add imageview to pane
            itemPane.getChildren().add(itemImageView);

            // Add item to list
            heldItems.getChildren().add(itemPane);

            // Increment current item index
            currentItemIndex++;
        }

        // Add empty item slots
        while (currentItemIndex < 3) {
            HBox itemPane = new HBox();
            itemPane.setMinWidth(Constants.TILE_SIZE * 1.3);
            itemPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                    BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(3))));

            // Add box to item list
            heldItems.getChildren().add(itemPane);

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
            totalAmmoForCurrentItem.setTextFill(Color.BLACK);

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
    private VBox createHUD() {
        // Make HUD
        VBox HUDBox = new VBox();
        HUDBox.setPadding(new Insets(5, 5, 5, 5));
        HUDBox.setMaxWidth(Constants.TILE_SIZE * 6);
        HUDBox.setMaxHeight(300);
        HUDBox.setSpacing(5);

        // Get the current player from the player list
        PlayerView currentPlayer = getCurrentPlayer();

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
        heldItems = new FlowPane(3, 0); // Make flowpane for held items - supports unlimited amount of them

        // Declare ammo vbox - populated dynamically
        ammoBox = new VBox();

        // Change background according to team
        switch (currentPlayer.getTeam()) {
            case RED:
                HUDBox.setStyle("-fx-background-color: rgba(255, 0, 47, 0.4); -fx-background-radius: 0 0 165 0;");
                HUDBox.setEffect(new DropShadow(25, Color.rgb(255, 0, 47)));
                break;
            case BLUE:
                HUDBox.setStyle("-fx-background-color: rgba(66, 173, 244, 0.4); -fx-background-radius: 0 0 165 0;");
                HUDBox.setEffect(new DropShadow(25, Color.rgb(66, 173, 244)));
                break;
            case GREEN:
                HUDBox.setStyle("-fx-background-color: rgba(90, 240, 41, 0.4); -fx-background-radius: 0 0 165 0;");
                HUDBox.setEffect(new DropShadow(25, Color.rgb(90, 240, 41)));
                break;
            case YELLOW:
                HUDBox.setStyle("-fx-background-color: rgba(232, 232, 0, 0.4); -fx-background-radius: 0 0 165 0;");
                HUDBox.setEffect(new DropShadow(25, Color.rgb(232, 232, 0)));
                break;
            default:
                HUDBox.setStyle("-fx-background-color: rgba(178, 177, 169, 0.65); -fx-background-radius: 0 0 165 0;");
                HUDBox.setEffect(new DropShadow(25, Color.rgb(178, 177, 169)));
                break;
        }

        // Add elements of HUD for player to HUD
        HUDBox.getChildren().addAll(playerLabel, heartBox, playerScoreLabel, playerScoreNumber, heldItems, ammoBox);

        return HUDBox;
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

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
