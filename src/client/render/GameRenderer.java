package client.render;

import client.gui.Settings;
import client.input.KeyboardHandler;
import client.input.MouseHandler;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

import java.util.HashMap;
import java.util.Map;

public class GameRenderer implements Runnable {
    // RendererResourceLoader object
    private RendererResourceLoader rendererResourceLoader;
    // Variables for map
    private AnchorPane mapBox;
    private Canvas mapCanvas;
    private GraphicsContext mapGC;
    // HUD items
    private HUD hud;
    // Variables for changing cursor + camera
    private AnchorPane cursorPane;
    private ImageView cursorImage;
    private double mouseX;
    private double mouseY;
    // Player ID
    private int playerID;
    // GameView object which is to be updated
    private GameView gameView;
    // Stage to set render to
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
    // Animation hashmaps
    private Map<Integer, AnimatedSpriteManager> playersOnMapAnimations;
    private Map<Integer, AnimatedSpriteManager> enemiesOnMapAnimations;
    // Last location of players and enemies hashmaps
    private Map<Integer, Pose> lastPlayerLocations;
    private Map<Integer, Pose> lastEnemyLocations;
    // Entities that have death animations
    enum EntityDeathAnimation {PLAYER, ENEMY};

    // Constructor
    public GameRenderer(Stage stage, GameView initialGameView, int playerID, Settings settings) {
        // Initialise gameView, stage and playerID
        this.gameView = initialGameView;
        this.stage = stage;
        this.playerID = playerID;
        this.settings = settings;

        // Set paused to false
        paused = false;

        // Load sprites and fonts
        rendererResourceLoader = new RendererResourceLoader();
        rendererResourceLoader.loadAllSprites();
        rendererResourceLoader.loadFonts();

        // Initialize HUD
        hud = new HUD();

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
        hud.createHUD(getCurrentPlayer(), rendererResourceLoader.getFontManaspace28(), rendererResourceLoader.getFontManaspace18());

        // Create pause overlay
        // "PAUSE" message
        Label pauseLabel = new Label("PAUSE");
        pauseLabel.setFont(rendererResourceLoader.getFontManaspace28());
        pauseLabel.setTextFill(Color.BLACK);

        // Label with instructions how to unpause
        // TODO: add e.g. settings.getPauseKey() when key settings in settings object
        Label pauseInstructions = new Label("Press ESC to unpause");
        pauseLabel.setFont(rendererResourceLoader.getFontManaspace18());
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
        root.getChildren().addAll(mapBox, hud, pausedOverlay, cursorPane);

        // Set cursor to none - crosshair of a different size can then be renderer that's not dictated by the system
        root.setCursor(Cursor.NONE);

        // Set crosshair to cursorpane
        cursorImage = new ImageView(rendererResourceLoader.getSprite(EntityList.CROSSHAIR));
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
        hud.updateHUD(getCurrentPlayer(), rendererResourceLoader, rendererResourceLoader.getFontManaspace28(), rendererResourceLoader.getFontManaspace18());

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
                // Check if in map of currently tracked players
                if (playersOnMapAnimations.get(playerID).getAnimationType() != AnimationType.MOVE) {
                    // Get the correct sprite
                    EntityList entityToRender = EntityList.PLAYER_WALK;
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            entityToRender = EntityList.PLAYER_WALK_RED;
                            break;
                        case BLUE:
                            entityToRender = EntityList.PLAYER_WALK_BLUE;
                            break;
                        case GREEN:
                            entityToRender = EntityList.PLAYER_WALK_GREEN;
                            break;
                        case YELLOW:
                            entityToRender = EntityList.PLAYER_WALK_YELLOW;
                            break;
                    }

                    // Add animation to animationhashmap
                    playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                            rendererResourceLoader.getSprite(entityToRender), 32, 32,
                            6, 75, 0, AnimationType.MOVE));
                }
            }
            // Check if player reloading
            else if (currentPlayer.getCurrentAction() == ActionList.RELOADING) {
                // Check if in map of currently tracked players
                if (playersOnMapAnimations.get(playerID).getAnimationType() != AnimationType.RELOAD) {
                    // Get the correct sprite
                    EntityList entityToRender = EntityList.PLAYER_RELOAD;
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            entityToRender = EntityList.PLAYER_RELOAD_RED;
                            break;
                        case BLUE:
                            entityToRender = EntityList.PLAYER_RELOAD_BLUE;
                            break;
                        case GREEN:
                            entityToRender = EntityList.PLAYER_RELOAD_GREEN;
                            break;
                        case YELLOW:
                            entityToRender = EntityList.PLAYER_RELOAD_YELLOW;
                            break;
                    }

                    // Add animation to animationhashmap
                    playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                            rendererResourceLoader.getSprite(entityToRender), 32, 45,
                            5, currentPlayer.getCurrentItem().getReloadTime() / 5,
                            0, AnimationType.RELOAD));
                }
            }
            // Check if player attacking
            else if (currentPlayer.getCurrentAction() == ActionList.ATTACKING) {
                // Get correct sprite
                EntityList entityToRender = EntityList.PLAYER_WITH_GUN_RECOIL;
                switch (currentPlayer.getTeam()) {
                    case RED:
                        entityToRender = EntityList.PLAYER_WITH_GUN_RECOIL_RED;
                        break;
                    case GREEN:
                        entityToRender = EntityList.PLAYER_WITH_GUN_RECOIL_GREEN;
                        break;
                    case YELLOW:
                        entityToRender = EntityList.PLAYER_WITH_GUN_RECOIL_YELLOW;
                        break;
                    case BLUE:
                        entityToRender = EntityList.PLAYER_WITH_GUN_RECOIL_BLUE;
                        break;
                }

                // Add sprite to animation hashmap
                playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                        rendererResourceLoader.getSprite(entityToRender), AnimationType.ATTACK));
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

                // Get correct graphic
                EntityList entityToRender;
                if (hasGun) {
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            entityToRender = EntityList.PLAYER_WITH_GUN_RED;
                            break;
                        case BLUE:
                            entityToRender = EntityList.PLAYER_WITH_GUN_BLUE;
                            break;
                        case GREEN:
                            entityToRender = EntityList.PLAYER_WITH_GUN_GREEN;
                            break;
                        case YELLOW:
                            entityToRender = EntityList.PLAYER_WITH_GUN_YELLOW;
                            break;
                        default:
                            entityToRender = EntityList.PLAYER_WITH_GUN;
                            break;
                    }
                } else {
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            entityToRender = EntityList.PLAYER_RED;
                            break;
                        case BLUE:
                            entityToRender = EntityList.PLAYER_BLUE;
                            break;
                        case GREEN:
                            entityToRender = EntityList.PLAYER_GREEN;
                            break;
                        case YELLOW:
                            entityToRender = EntityList.PLAYER_YELLOW;
                            break;
                        default:
                            entityToRender = EntityList.PLAYER;
                            break;
                    }
                }

                // Add sprite to animation hashmap
                playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
                        rendererResourceLoader.getSprite(entityToRender), AnimationType.STAND));
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
                    //TODO: add support for larger enemies/those that don't use the default zombie skin

                    // Get correct sprite
                    EntityList entityToRender = EntityList.ZOMBIE_WALK;
                    switch (currentEnemy.getEntityListName()) {
                        case ZOMBIE:
                            entityToRender = EntityList.ZOMBIE_WALK;
                            break;
                        case RUNNER:
                            entityToRender = EntityList.RUNNER_WALK;
                            break;
                        case SOLDIER:
                            entityToRender = EntityList.SOLDIER_WALK;
                            break;
                        case MIDGET:
                            entityToRender = EntityList.MIDGET_WALK;
                            break;
                        case BOOMER:
                            entityToRender = EntityList.BOOMER_WALK;
                            break;
                        case MACHINE_GUNNER:
                            entityToRender = EntityList.MACHINE_GUNNER_WALK;
                            break;
                    }

                    // Add animation to animation hashmap
                    enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager(
                            rendererResourceLoader.getSprite(entityToRender), 32, 32,
                            6, 75, 0, AnimationType.MOVE));
                }
            }
            // Enemy standing, render standing image
            else {
                enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager(
                        rendererResourceLoader.getSprite(currentEnemy.getEntityListName()), AnimationType.STAND));
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

        // Render enemy and player death animations
        renderEntityDeaths(EntityDeathAnimation.ENEMY);
        renderEntityDeaths(EntityDeathAnimation.PLAYER);
    }

    private void renderEntityDeaths(EntityDeathAnimation entityType) {
        // Make a hashmap of all entities  on map to ease calculations and find dead entities
        Map<Integer, Pose> gameViewEntityPoses = new HashMap<>();
        HashMap<Integer, Pose> deadEntities = new HashMap<>();
        switch (entityType) {
            case PLAYER:
                for (PlayerView playerView : gameView.getPlayers()) {
                    gameViewEntityPoses.put(playerView.getID(), playerView.getPose());
                }

                deadEntities = (HashMap<Integer, Pose>) mapDifference(lastPlayerLocations, gameViewEntityPoses);
                break;
            case ENEMY:
                for (EnemyView enemyView : gameView.getEnemies()) {
                    gameViewEntityPoses.put(enemyView.getID(), enemyView.getPose());
                }

                deadEntities = (HashMap<Integer, Pose>) mapDifference(lastEnemyLocations, gameViewEntityPoses);
                break;
        }

        // Go through lit of dead enemies
        for (Map.Entry<Integer, Pose> entry : deadEntities.entrySet()) {
            // Set up an animationtimer with a one-off animation for every enemy
            new AnimationTimer() {
                Pose pose = entry.getValue();
                int frameCount = 32;
                AnimatedSpriteManager deathSpriteManager = new AnimatedSpriteManager(
                        rendererResourceLoader.getSprite(EntityList.BLOOD_EXPLOSION), 32, 32,
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

            // Remove entry from last locations so it isn't played again
            switch (entityType) {
                case PLAYER:
                    lastPlayerLocations.remove(entry.getKey());
                    break;
                case ENEMY:
                    lastEnemyLocations.remove(entry.getKey());
                    break;
            }
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
        Image imageToRender = rendererResourceLoader.getSprite(entityView.getEntityListName());

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

    // Render map from tiles
    private void renderMap() {
        // Get map X and Y dimensions
        int mapX = gameView.getXDim();
        int mapY = gameView.getYDim();

        // Iterate through the map, rending each tile on canvas
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                // Get tile graphic
                Image tileImage = rendererResourceLoader.getSprite(gameView.getTileMap()[x][y].getTileType().getEntityListName());

                // Add tile to canvas
                mapGC.drawImage(tileImage, x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE,
                        Constants.TILE_SIZE);
            }
        }
    }

    // Set transform for the GraphicsContext to rotate around a pivot point.
    private void rotate(GraphicsContext gc, double angle, double xPivotCoordinate, double yPivotCoordinate) {
        Rotate r = new Rotate(angle, xPivotCoordinate, yPivotCoordinate);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
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
