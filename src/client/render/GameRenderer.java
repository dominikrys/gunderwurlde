package client.render;

import client.Settings;
import client.input.KeyAction;
import client.input.KeyboardHandler;
import client.input.MouseHandler;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
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
    private KeyboardHandler kbHandler; //TODO: move this out of renderer

    /**
     * Mouse handler
     */
    private MouseHandler mHandler; // todo: move this out of renderer

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

        // Set running to true
        running = true;

        // Set spectator mode to false
        spectator = false;

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
        mapCanvas = new MapCanvas(settings.getScreenWidth(), settings.getScreenHeight());

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
        // Load pause FXML
        try {
            pausedOverlay = FXMLLoader.load(getClass().getResource("/client/gui/fxml/pause_menu.fxml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

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

                spectator = true;
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

        // Render map
        mapCanvas.renderMap(gameView, rendererResourceLoader);

        // Render entities onto canvas
        mapCanvas.renderEntitiesFromGameViewToCanvas(gameView, playerID, rendererResourceLoader);

        // Update HUD
//<<<<<<< HEAD
//        try {
//            updateHUD();
//        }
//        catch(Exception ex){
//
//        }
//        // If game is paused, add the paused overlay
//        if (paused) {
//            pausedOverlay.setVisible(true);
//        } else {
//            pausedOverlay.setVisible(false);
//=======
        hud.updateHUD(getCurrentPlayer(), rendererResourceLoader, rendererResourceLoader.getFontManaspace28(),
                rendererResourceLoader.getFontManaspace18(), getCurrentPlayer().getPose(),
                gameView.getXDim() * Constants.TILE_SIZE, gameView.getYDim() * Constants.TILE_SIZE);
    }

    /**
     * Move camera according to what key is pressed
     *
     * @param e Key event
     */
    private void handleSpectatorCamera(KeyEvent e) {
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
//>>>>>>> dev
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
        mapCanvas.setTranslateX((double) settings.getScreenWidth() / 2 - playerX - Constants.TILE_SIZE / 2 /* Center Player*/
                + (settings.getScreenWidth() / 2 - mouseX) * cameraMouseSensitivity /* Mouse */);

        // Adjust map vertically
        mapCanvas.setTranslateY((double) settings.getScreenHeight() / 2 - playerY - Constants.TILE_SIZE / 2 /* Center Player*/
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
                pausedOverlay.setVisible(true);
                stage.getScene().getRoot().setCursor(Cursor.DEFAULT);
                cursorPane.setVisible(false);
            } else {
                pausedOverlay.setVisible(false);
                stage.getScene().getRoot().setCursor(Cursor.NONE);
                cursorPane.setVisible(true);
            }
        }
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

//<<<<<<< HEAD
//    // Update all HUD elements
//    private void updateHUD() {
//        // Get the player from gameview
//        PlayerView currentPlayer = getCurrentPlayer();
//
//        if(playerScoreNumber == null){
//            System.out.println("player score number is null");
//        }
//        // Update score
//        playerScoreNumber.setText(Integer.toString(currentPlayer.getScore()));
//
//        // Update hearts
//        heartBox.getChildren().clear();
//        int halfHearts = currentPlayer.getHealth() % 2;
//        int wholeHearts = currentPlayer.getHealth() / 2;
//        int lostHearts = (currentPlayer.getMaxHealth() - currentPlayer.getHealth()) / 2;
//
//        // Populate heart box in GUI
//        for (int i = 0; i < wholeHearts; i++) {
//            heartBox.getChildren().add(new ImageView(loadedSprites.get(EntityList.HEART_FULL)));
//        }
//        // Populate half heart
//        for (int i = 0; i < halfHearts; i++) {
//            heartBox.getChildren().add(new ImageView(loadedSprites.get(EntityList.HEART_HALF)));
//        }
//        // Populate lost hearts
//        for (int i = 0; i < lostHearts; i++) {
//            heartBox.getChildren().add(new ImageView(loadedSprites.get(EntityList.HEART_LOST)));
//        }
//
//        // Update held items
//        heldItems.getChildren().clear();
//
//        int currentItemIndex = 0; // Keep track of current item since iterator is used
//
//        for (ItemView currentItem : currentPlayer.getItems()) {
//            // Make image view out of graphic
//            ImageView itemImageView = new ImageView(loadedSprites.get(currentItem.getItemListName().getEntityList()));
//
//            // Pane for item image to go in - for border
//            FlowPane itemPane = new FlowPane();
//            itemPane.setPrefWidth(Constants.TILE_SIZE);
//            itemPane.setPadding(new Insets(2, 2, 2, 2));
//
//            // Check if the item currently being checked is the current selected item, and if it is, show that
//            if (currentItemIndex == currentPlayer.getCurrentItemIndex()) {
//                DropShadow dropShadow = new DropShadow(25, Color.HOTPINK);
//                dropShadow.setSpread(0.75);
//                itemImageView.setEffect(dropShadow);
//                itemPane.setBorder(new Border(new BorderStroke(Color.HOTPINK,
//                        BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(3))));
//            } else {
//                // Not selected item, add black border
//                itemPane.setBorder(new Border(new BorderStroke(Color.BLACK,
//                        BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(3))));
//            }
//
//            // Add imageview to pane
//            itemPane.getChildren().add(itemImageView);
//
//            // Add item to list
//            heldItems.getChildren().add(itemPane);
//
//            // Increment current item index
//            currentItemIndex++;
//        }
//
//        // Add empty item slots
//        while (currentItemIndex < 3) {
//            HBox itemPane = new HBox();
//            itemPane.setMinWidth(Constants.TILE_SIZE * 1.3);
//            itemPane.setBorder(new Border(new BorderStroke(Color.BLACK,
//                    BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(3))));
//
//            // Add box to item list
//            heldItems.getChildren().add(itemPane);
//
//            currentItemIndex++;
//        }
//
//        // Get currently selected item
//        ItemView currentItem = currentPlayer.getCurrentItem();
//
//        // Clear ammo box
//        ammoBox.getChildren().clear();
//
//        // Information about current gun - ammo in clip and clip size
//        HBox currentGunInfo = new HBox();
//
//        // Add ammo amount to hud if the item has ammo
//        if (currentItem.getAmmoType() != AmmoList.NONE) {
//            // Make label for current ammo in item
//            Label ammoInGun = new Label(Integer.toString(currentItem.getAmmoInClip()),
//                    new ImageView(loadedSprites.get(EntityList.AMMO_CLIP)));
//            ammoInGun.setFont(fontManaspace28);
//            ammoInGun.setTextFill(Color.BLACK);
//            // Make label for total ammo in clip
//            Label totalAmmoInClip = new Label("/" + currentItem.getClipSize());
//            totalAmmoInClip.setFont(fontManaspace18);
//            totalAmmoInClip.setTextFill(Color.BLACK);
//
//            //Make label for total amount of ammo the current item uses
//            Label totalAmmoForCurrentItem = new Label(Integer.toString(currentPlayer.getAmmo().get(currentItem.getAmmoType())));
//            totalAmmoForCurrentItem.setFont(fontManaspace28);
//            totalAmmoForCurrentItem.setTextFill(Color.BLACK);
//
//            // Add info on current gun to the right element
//            currentGunInfo.getChildren().addAll(ammoInGun, totalAmmoInClip);
//            // Add the total amount of ammo the current gun uses under the gun info
//            ammoBox.getChildren().addAll(currentGunInfo, totalAmmoForCurrentItem);
//        } else {
//            // Make label for infinite use if it's not a weapon
//            Label currentAmmo = new Label("∞");
//            currentAmmo.setFont(new Font("Consolas", 32));
//            currentAmmo.setTextFill(Color.BLACK);
//
//            // Add info on current gun to the right element and to ammo box
//            currentGunInfo.getChildren().addAll(currentAmmo);
//            ammoBox.getChildren().add(currentGunInfo);
//        }
//    }
//
//    // Render map from tiles
//    private void renderMap() {
//        // Get map X and Y dimensions
//        int mapX = gameView.getXDim();
//        int mapY = gameView.getYDim();
//
//        // Iterate through the map, rending each tile on canvas
//        for (int x = 0; x < mapX; x++) {
//            for (int y = 0; y < mapY; y++) {
//                // Get tile graphic
//                Image tileImage = loadedSprites.get(gameView.getTileMap()[x][y].getTileType().getEntityListName());
//
//                // Add tile to canvas
//                mapGC.drawImage(tileImage, x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE,
//                        Constants.TILE_SIZE);
//            }
//        }
//    }
//
//    // Create HUD and initialise all elements
//    private VBox createHUD() {
//        // Make HUD
//        VBox HUDBox = new VBox();
//        HUDBox.setPadding(new Insets(5, 5, 5, 5));
//        HUDBox.setMaxWidth(Constants.TILE_SIZE * 6);
//        HUDBox.setMaxHeight(300);
//        HUDBox.setSpacing(5);
//
//        // Get the current player from the player list
//        PlayerView currentPlayer = getCurrentPlayer();
//
//        // If for some reason the player hasn't been found, return an empty HUD
//        if (currentPlayer == null) {
//            return new VBox();
//        }
//
//        playerScoreNumber = new Label();
//        playerScoreNumber.setFont(fontManaspace28);
//        playerScoreNumber.setTextFill(Color.BLACK);
//
//        // Label with player name to tell which player this part of the HUD is for
//        Label playerLabel = new Label(currentPlayer.getName());
//        playerLabel.setFont(fontManaspace28);
//        playerLabel.setTextFill(Color.BLACK);
//
//        // Player score
//        Label playerScoreLabel = new Label("SCORE: ");
//        playerScoreLabel.setFont(fontManaspace28);
//        playerScoreLabel.setTextFill(Color.BLACK);
//
//        // Flowpane to hold heart graphics. Have it overflow onto the next "line" after 5 hearts displayed
//        heartBox = new FlowPane();
//        heartBox.setMaxWidth(Constants.TILE_SIZE * 5);
//
//        // Iterate through held items list and add to the HUD
//        heldItems = new FlowPane(3, 0); // Make flowpane for held items - supports unlimited amount of them
//
//        // Declare ammo vbox - populated dynamically
//        ammoBox = new VBox();
//
//        // Change background according to team
//        switch (currentPlayer.getTeam()) {
//            case RED:
//                HUDBox.setStyle("-fx-background-color: rgba(255, 0, 47, 0.4); -fx-background-radius: 0 0 165 0;");
//                HUDBox.setEffect(new DropShadow(25, Color.rgb(255, 0, 47)));
//                break;
//            case BLUE:
//                HUDBox.setStyle("-fx-background-color: rgba(66, 173, 244, 0.4); -fx-background-radius: 0 0 165 0;");
//                HUDBox.setEffect(new DropShadow(25, Color.rgb(66, 173, 244)));
//                break;
//            case GREEN:
//                HUDBox.setStyle("-fx-background-color: rgba(90, 240, 41, 0.4); -fx-background-radius: 0 0 165 0;");
//                HUDBox.setEffect(new DropShadow(25, Color.rgb(90, 240, 41)));
//                break;
//            case YELLOW:
//                HUDBox.setStyle("-fx-background-color: rgba(232, 232, 0, 0.4); -fx-background-radius: 0 0 165 0;");
//                HUDBox.setEffect(new DropShadow(25, Color.rgb(232, 232, 0)));
//                break;
//            default:
//                HUDBox.setStyle("-fx-background-color: rgba(178, 177, 169, 0.65); -fx-background-radius: 0 0 165 0;");
//                HUDBox.setEffect(new DropShadow(25, Color.rgb(178, 177, 169)));
//                break;
//        }
//
//        // Add elements of HUD for player to HUD
//        HUDBox.getChildren().addAll(playerLabel, heartBox, playerScoreLabel, playerScoreNumber, heldItems, ammoBox);
//
//        return HUDBox;
//    }
//
//    // Set transform for the GraphicsContext to rotate around a pivot point.
//    private void rotate(GraphicsContext gc, double angle, double xPivotCoordinate, double yPivotCoordinate) {
//        Rotate r = new Rotate(angle, xPivotCoordinate, yPivotCoordinate);
//        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
//    }
//
//    // Get image from the HashMap of loaded images according to the entity name
//    private Image getImageFromEntity(EntityList entityName) {
//        // Try to get the correct sprite, if not found then return default
//        Image image = loadedSprites.get(entityName);
//
//        if (image != null && !image.isError()) {
//            return image;
//        } else {
//            System.out.println("Couldn't find the graphic for " + entityName.name() + " so loading default...");
//            return loadedSprites.get(EntityList.DEFAULT);
//        }
//    }
//
//    // Create an Image object from specified colour
//    private Image createImageFromColor(Color color) {
//        WritableImage image = new WritableImage(1, 1);
//        image.getPixelWriter().setColor(0, 0, color);
//        return image;
//    }
//
//    // Scale image by integer value through resampling - useful for large enemies/powerups
//    private Image resampleImage(Image inputImage, int scaleFactor) {
//        final int inputImageWidth = (int) inputImage.getWidth();
//        final int inputImageHeight = (int) inputImage.getHeight();
//
//        // Set up output image
//        WritableImage outputImage = new WritableImage(inputImageWidth * scaleFactor,
//                inputImageHeight * scaleFactor);
//
//        // Set up pixel reader and writer
//        PixelReader reader = inputImage.getPixelReader();
//        PixelWriter writer = outputImage.getPixelWriter();
//
//        // Resample image
//        for (int y = 0; y < inputImageHeight; y++) {
//            for (int x = 0; x < inputImageWidth; x++) {
//                final int argb = reader.getArgb(x, y);
//                for (int dy = 0; dy < scaleFactor; dy++) {
//                    for (int dx = 0; dx < scaleFactor; dx++) {
//                        writer.setArgb(x * scaleFactor + dx, y * scaleFactor + dy, argb);
//                    }
//                }
//            }
//        }
//        return outputImage;
//    }
//
//    public void setClientSender(ClientSender sender) {
//        this.sender = sender;
//    }
//
//    public GameView getView() {
//        return this.gameView;
//    }
//
//=======
    /**
     * Get keyboard handler
     *
     * @return Keyboard handler
     */
//>>>>>>> dev
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
    }
}
