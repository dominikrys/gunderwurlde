package client.render;

import client.Client;
import client.ConnectionType;
import client.Settings;
import client.gui.menucontrollers.MainMenuController;
import client.gui.menucontrollers.PauseMenuController;
import client.input.KeyAction;
import client.input.KeyboardHandler;
import client.input.MouseHandler;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import shared.Constants;
import shared.lists.EntityList;
import shared.lists.EntityStatus;
import shared.lists.Team;
import shared.view.GameView;
import shared.view.SoundView;
import shared.view.entity.PlayerView;

import java.util.Set;

/**
 * GameRenderer class. Contains the whole rendering backbone.
 *
 * @author Dominik Rys
 */
public class GameRenderer implements Runnable {
    /**
     * Ratio for how sensitive the map is to mouse movements
     */
    double cameraMouseSensitivity;
    /**
     * RendererResourceLoader - Contains resources used by the renderer
     */
    private RendererResourceLoader rendererResourceLoader;
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
    private KeyboardHandler kbHandler; //TODO: input should be out of renderer, move this out
    /**
     * Mouse handler
     */
    private MouseHandler mHandler; //TODO: input should be out of renderer, move this out
    /**
     * Settings object
     */
    private Settings settings;
    /**
     * SoundView object to be passed to the sound manager
     */
    private SoundView soundView;
    /**
     * Running boolean
     */
    private boolean running;
    /**
     * Boolean for spectator mode
     */
    private boolean spectator;
    /**
     * Controller for the pause menu
     */
    private PauseMenuController pauseMenuController;
    /**
     * ConnectionType for whether single player or not
     */
    private ConnectionType connectionType;

    /**
     * Constructor
     *
     * @param stage           Stage to display game on
     * @param initialGameView Initial gameview to initialise elements off of
     * @param playerID        ID of player for whom this renderer is for
     * @param settings        Settings object
     */

    private Client handler;

    public GameRenderer(Stage stage, GameView initialGameView, int playerID, Settings settings, Client handler,
                        ConnectionType connectionType) {
        // Initialise gameView, stage and playerID
        this.gameView = initialGameView;
        this.stage = stage;
        this.playerID = playerID;
        this.settings = settings;
        this.handler = handler;
        this.connectionType = connectionType;

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

        // Set running to true
        running = true;

        // Set spectator mode to false
        spectator = false;

        // Set mouse sensitivity for camera
        cameraMouseSensitivity = 0.25;

        // Initialise mouse positions to not bug out camera
        mouseX = (double) settings.getScreenWidth() / 2 - getCurrentPlayer().getPose().getX() - (double) Constants.TILE_SIZE / 2;
        mouseY = (double) settings.getScreenHeight() / 2 - getCurrentPlayer().getPose().getY() - (double) Constants.TILE_SIZE / 2;

        // Initialise input variables
        kbHandler = new KeyboardHandler(this.playerID, settings);
        mHandler = new MouseHandler(this.playerID, settings);

        // Initialise soundview
        soundView = new SoundView(this.playerID, initialGameView, settings);
    }

    /**
     * Run the thread - set up gameview and keep updating the screen according to received gameviews
     */
    @Override
    public void run() {
        // Set up GameView - change the stage
        setUpRenderer(gameView);

        // When the window is closed by pressing the "x" button, stop rendering
        stage.setOnCloseRequest(we -> this.stop());

        // Update the HUD and game at intervals - animationtimer used for maximum frame rate
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running) {
                    renderGameView();
                } else {
                    this.stop();
                }
            }
        }.start();

        /*
        // Alternative:timeline. This way can specify FPS so can be added to settings as variable FPS?
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> renderGameView()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        */
    }

    /**
     * Initialise game renderer's elements according to the input gameview
     *
     * @param inputGameView First received gameviw object
     */
    private void setUpRenderer(GameView inputGameView) {
        // Initialise pane for map
        mapCanvas = new MapCanvas(inputGameView.getXDim() * Constants.TILE_SIZE,
                inputGameView.getYDim() * Constants.TILE_SIZE);

        // Create HUD
        hud.createHUD(getCurrentPlayer(), rendererResourceLoader.getFontManaspace28(),
                rendererResourceLoader.getFontManaspace18(), gameView.getXDim() * Constants.TILE_SIZE,
                gameView.getYDim() * Constants.TILE_SIZE);

        // Create pause overlay
        createPauseOverlay();

        // Create root stackpane
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0),
                new Insets(0, 0, 0, 0))));

        // Add elements to root
        root.getChildren().addAll(mapCanvas, hud, cursorPane, pausedOverlay);

        // Set cursor to none - crosshair of a different size can then be renderer that's not dictated by the system
        root.setCursor(Cursor.NONE);

        // Set crosshair to cursorpane
        cursorImage = new ImageView(rendererResourceLoader.getSprite(EntityList.CROSSHAIR));
        cursorPane.getChildren().add(cursorImage);

        // Event handlers for mouse movements
        stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, this::updateMouse);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::updateMouse);

        // Event handler for pause menu
        stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, this::handleRendererInput);

        // Set root to scene - runLater for slower PCs that don't load the JavaFX thread fast enough
        Platform.runLater(() -> stage.getScene().setRoot(root));

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
        try {
            // Load pause FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/gui/fxml/pause_menu.fxml"));
            pausedOverlay = fxmlLoader.load();

            // Set background - inline instead of CSS so transparency works
            pausedOverlay.setStyle("-fx-background-image: url('file:assets/img/gui/pause_bg.png');-fx-background-repeat: repeat; ");
            pausedOverlay.setSpacing(15);
            pausedOverlay.setPadding(new Insets(15, 15, 15, 15));

            // Set controller and update its settings value
            pauseMenuController = fxmlLoader.getController();
            pauseMenuController.initialise(settings);
        } catch (Exception e) {
            System.out.println("Couldn't load the pause menu FXML!");
            e.printStackTrace();
        }

        // Set pause array to the centre of the screen
        pausedOverlay.setAlignment(Pos.CENTER);
        pausedOverlay.setVisible(false);
    }

    /**
     * Update the mouse location and render cursor in the new spot
     *
     * @param e Mouse moved event
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
        // Check if should be in spectator mode or not
        checkDead();

        // Render map
        mapCanvas.renderMap(gameView, rendererResourceLoader);

        // Render entities onto canvas
        mapCanvas.renderEntitiesFromGameViewToCanvas(gameView, playerID, rendererResourceLoader);

        // Check if end of game
        if (gameView.getWinningTeam() != Team.NONE) {
            // Call gameWon to handle end of game screen and score saving
            gameWon();
        }

        // Update HUD
        hud.updateHUD(gameView, rendererResourceLoader, rendererResourceLoader.getFontManaspace28(),
                rendererResourceLoader.getFontManaspace18(), getCurrentPlayer());
    }

    /**
     * Method for performing necessary actions when the game is won
     */
    private void gameWon() {
        // Display end of game message in the HUD
        hud.displayWinMessage(rendererResourceLoader.getFontManaspace50(),
                rendererResourceLoader.getFontManaspace28(), gameView.getWinningTeam());

        // Calculate the score for the team and get a list of team members
        int teamHighScore = 0;
        StringBuilder teamMembers = new StringBuilder();

        for (PlayerView player : gameView.getPlayers()) {
            if (player.getTeam() == gameView.getWinningTeam()) {
                // If first team member, start off the string with their name
                if (teamHighScore == 0) {
                    teamMembers = new StringBuilder(gameView.getWinningTeam().toString() + ": " + player.getName());
                } else {
                    teamMembers.append(", ").append(player.getTeam());
                }

                // Add score to total
                teamHighScore += player.getScore();
            }
        }

        // Add score to high scores
        settings.addMultiPlayerHighScore(teamMembers.toString(), teamHighScore);
        settings.saveToDisk();
    }

    /**
     * Check if the player should be in spectator mode after death or not and set up appropriate code
     */
    private void checkDead() {
        // Check if player has died, in which case give them a free camera
        if (getCurrentPlayer().getStatus() == EntityStatus.DEAD) {
            if (!spectator) {
                // If first time in spectator mode
                stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, this::handleSpectatorCamera);

                // Display game over message in hud
                hud.displayDeathMessage(rendererResourceLoader.getFontManaspace50(), rendererResourceLoader.getFontManaspace18());

                // After a couple of seconds, close the message
                (new Thread(() -> {
                    PauseTransition delay = new PauseTransition(Duration.seconds(4));
                    delay.setOnFinished(event -> hud.closeDeathMessage());
                    delay.play();
                })
                ).start();

                // Set their spectator mode to true
                spectator = true;

                // Check if single player
                if (connectionType == ConnectionType.SINGLE_PLAYER) {
                    // Add high score and save
                    settings.addSinglePlayerHighScore(getCurrentPlayer().getName(), getCurrentPlayer().getScore());
                    settings.saveToDisk();
                }
            }
        } else {
            // Check if coming back from spectator mode
            if (spectator) {
                // Remove eventhandler from spectator mode
                stage.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, this::handleSpectatorCamera);

                spectator = false;
            }

            // Center camera on player
            centerCamera();
        }
    }

    /**
     * Move camera according to what key is pressed
     *
     * @param e Key event
     */
    private void handleSpectatorCamera(KeyEvent e) {
        // TODO: make this smoother/generally handle this better
        if (e.getCode().toString().equals(settings.getKey(KeyAction.UP))) {
            mapCanvas.setTranslateY(mapCanvas.getTranslateY() + 10);
        }
        if (e.getCode().toString().equals(settings.getKey(KeyAction.DOWN))) {
            mapCanvas.setTranslateY(mapCanvas.getTranslateY() - 10);
        }
        if (e.getCode().toString().equals(settings.getKey(KeyAction.LEFT))) {
            mapCanvas.setTranslateX(mapCanvas.getTranslateX() + 10);
        }
        if (e.getCode().toString().equals(settings.getKey(KeyAction.RIGHT))) {
            mapCanvas.setTranslateX(mapCanvas.getTranslateX() - 10);
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

        // Adjust map horizontally
        mapCanvas.setTranslateX((double) (gameView.getXDim() * Constants.TILE_SIZE) / 2 - playerX - Constants.TILE_SIZE / 2 /* Center Player*/
                + (settings.getScreenWidth() / 2 - mouseX) * cameraMouseSensitivity /* Mouse */);

        // Adjust map vertically
        mapCanvas.setTranslateY((double) (gameView.getYDim() * Constants.TILE_SIZE) / 2 - playerY - Constants.TILE_SIZE / 2 /* Center Player*/
                + (settings.getScreenHeight() / 2 - mouseY) * cameraMouseSensitivity /* Mouse */);
    }

    /**
     * Handle kinds of renderer input that don't have to be sent to the server
     *
     * @param e Key event
     */
    public void handleRendererInput(KeyEvent e) {
        // Check if the ESC button has been pressed
        if (e.getCode().toString().equals(settings.getKey(KeyAction.ESC))) {
            // Pause/unpause the game
            paused = !paused;

            // Show the pause overlay and perform any other necessary actions
            if (paused) {
                pauseMenuController.initialise(settings);
                pausedOverlay.setVisible(true);
                stage.getScene().getRoot().setCursor(Cursor.DEFAULT);
                cursorPane.setVisible(false);

                // Start a thread which check whether the 'back to game' or 'quit to menu' buttons have been pressed
                (new Thread(() -> {
                    while (paused && running) {
                        if (pauseMenuController.getBackToGamePressed()) {
                            getKeyboardHandler().unpause();
                            // Unpause and close the pause window
                            paused = false;
                            backToGameFromPauseMenu();
                        } else if (pauseMenuController.getQuitToMenuPressed()) {
                            System.out.println("\n\n Threads alive when quit button in pause menu is pressed \n\n");
                            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                            for(Thread t : threadSet){
                                System.out.println(t.getName() + " is still alive");
                            }

                            // Set pause to false and stop rendering
                            paused = false;
                            this.stop();

                            // Go back to play menu with all player info still there
                            (new MainMenuController(stage, settings)).show();
                        }

                        //TODO: remove this, currently doesn't work otherwise since in big animationTimer
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.out.println("GAME RENDERER PAUSE WATCHER ENDING");
                })
                ).start();
            } else {
                backToGameFromPauseMenu();
            }
        }
    }

    /**
     * Hide the pause money and apply its settings
     */
    private void backToGameFromPauseMenu() {
        // Hide pause menu
        pausedOverlay.setVisible(false);
        stage.getScene().getRoot().setCursor(Cursor.NONE);
        cursorPane.setVisible(true);


        // Get settings from controller and apply them
        settings = pauseMenuController.getSettings();

        // Save settings to disk
        settings.saveToDisk();
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

    /**
     * Stop renderer. This kills the AnimationTimer that renders GameViews to the stage
     */
    public void stop() {
        running = false;
        getKeyboardHandler().deactivate();
        getMouseHandler().deactivate();
        soundView.deactivate();
        handler.close();
    }
}
