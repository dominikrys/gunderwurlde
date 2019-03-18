package client.render;

import client.Settings;
import client.input.KeyboardHandler;
import client.input.MouseHandler;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import shared.Constants;
import shared.lists.EntityList;
import shared.view.GameView;
import shared.view.SoundView;
import shared.view.entity.PlayerView;

/**
 * GameRenderer class. Contains the whole rendering backbone.
 *
 * @author Dominik Rys
 */
public class GameRenderer implements Runnable {
    /**
     * RendererResourceLoader - Contains resources used by the renderer
     */
    private RendererResourceLoader rendererResourceLoader;

    /**
     * Pane for the map
     */
    private AnchorPane mapPane;

    /**
     * Canvas for the map - this contains the game
     */
    private MapCanvas mapCanvas;

    /**
     * The hud object
     */
    private HUD hud;

    /**
     * Pane for cursor - necessary as custom cursor used
     */
    private AnchorPane cursorPane;

    /**
     * Custom cursor image
     */
    private ImageView cursorImage;

    /**
     * X coordinate of mouse
     */
    private double mouseX;

    /**
     * Y coordinate of mouse
     */
    private double mouseY;

    /**
     * ID of the player that this GameRenderer is for
     */
    private int playerID;

    /**
     * The current gamestate
     */
    private GameView gameView;

    /**
     * Stage to display renderer on
     */
    private Stage stage;

    /**
     * Whether the game is paused or not
     */
    private boolean paused;

    /**
     * VBox containing the pause overlay
     */
    private VBox pausedOverlay;

    /**
     * KeyboardHandler
     */
    private KeyboardHandler kbHandler;

    /**
     * Mouse handler
     */
    private MouseHandler mHandler;

    /**
     * Settings object
     */
    private Settings settings;

    /**
     * SoundView object to be passed to the sound manager
     */
    private SoundView soundView;

    /**
     * Constructor
     *
     * @param stage           Stage to display game on
     * @param initialGameView Initial gameview to initialise elements off of
     * @param playerID        ID of player for whom this renderer is for
     * @param settings        Settings object
     */
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

        // Initialise mouse positions to not bug out camera
        mouseX = (double) settings.getScreenWidth() / 2 - getCurrentPlayer().getPose().getX() - (double) Constants.TILE_SIZE / 2;
        mouseY = (double) settings.getScreenHeight() / 2 - getCurrentPlayer().getPose().getY() - (double) Constants.TILE_SIZE / 2;

        // Initialise input variables
        kbHandler = new KeyboardHandler(this.playerID, settings);
        mHandler = new MouseHandler(this.playerID);

        // Initialise soundview
        soundView = new SoundView(initialGameView, settings);
    }

    /**
     * Run the thread - set up gameview and keep updating the screen according to received gameviews
     */
    @Override
    public void run() {
        // Set up GameView - change the stage
        setUpRenderer(gameView);

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

    /**
     * Initialise game renderer's elements according to the input gameview
     *
     * @param inputGameView First received gameviw object
     */
    private void setUpRenderer(GameView inputGameView) {
        // Initialise pane for map
        mapPane = new AnchorPane();
        mapCanvas = new MapCanvas(settings.getScreenWidth(), settings.getScreenHeight());
        mapPane.getChildren().addAll(mapCanvas);

        // Create HUD
        hud.createHUD(getCurrentPlayer(), rendererResourceLoader.getFontManaspace28(), rendererResourceLoader.getFontManaspace18());

        // Create pause overlay
        createPauseOverlay();

        // Create root stackpane
        StackPane root = new StackPane();
        root.setAlignment(Pos.TOP_LEFT);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0),
                new Insets(0, 0, 0, 0))));

        // Add elements to root
        root.getChildren().addAll(mapPane, hud, pausedOverlay, cursorPane);

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

    /**
     * Create the pause overlay
     */
    private void createPauseOverlay() {
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
    }

    /**
     * Update the mouse location and render cursor in the new spot
     *
     * @param e Mouse moved ebent
     */
    private void updateMouse(MouseEvent e) {
        mouseX = e.getSceneX();
        mouseY = e.getSceneY();

        // Render cursor image to location of cursor
        renderCursor();
    }

    /**
     * Render cursor according to the mouse's X and Y coordinates
     */
    private void renderCursor() {
        AnchorPane.setLeftAnchor(cursorImage, mouseX - Constants.TILE_SIZE / 2);
        AnchorPane.setTopAnchor(cursorImage, mouseY - Constants.TILE_SIZE / 2);
    }

    /**
     * Update stored gameview and update keyboard, mouse and sound handlers
     *
     * @param gameView
     */
    public void updateGameView(GameView gameView) {
        this.gameView = gameView;
        this.kbHandler.setGameView(gameView);
        this.mHandler.setGameView(gameView);
        this.soundView.setGameView(gameView);
    }

    /**
     * Render gameview
     */
    private void renderGameView() {
        // Render map
        mapCanvas.renderMap(gameView, rendererResourceLoader);

        // Render entities onto canvas
        mapCanvas.renderEntitiesFromGameViewToCanvas(gameView, playerID, rendererResourceLoader);

        // Center camera on player
        centerCamera();

        // Update HUD
        hud.updateHUD(getCurrentPlayer(), rendererResourceLoader, rendererResourceLoader.getFontManaspace28(),
                rendererResourceLoader.getFontManaspace18());

        // If game is paused, add the paused overlay
        if (paused) {
            pausedOverlay.setVisible(true);
        } else {
            pausedOverlay.setVisible(false);
        }
    }

    /**
     * Center the camera on the player
     */
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

    /**
     * Get the PlayerView object of the player specified in playerID
     */
    private PlayerView getCurrentPlayer() {
        for (PlayerView playerView : gameView.getPlayers()) {
            if (playerView.getID() == playerID) {
                return playerView;
            }
        }

        return null;
    }

    /**
     * Get keyboard handler
     *
     * @return Keyboard handler
     */
    public KeyboardHandler getKeyboardHandler() {
        return this.kbHandler;
    }

    /**
     * Get mouse handler
     *
     * @return Mouse handler
     */
    public MouseHandler getMouseHandler() {
        return this.mHandler;
    }

    /**
     * Check whether game is paused
     *
     * @return Boolean object corresponding to whether the game is paused or not
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Set whether renderer is paused or not
     *
     * @param paused Paused boolean
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
