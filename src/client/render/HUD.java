package client.render;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import shared.Constants;
import shared.lists.EntityList;
import shared.lists.Team;
import shared.view.GameView;
import shared.view.GunView;
import shared.view.ItemView;
import shared.view.entity.PlayerView;

/**
 * HUD class. Contains the HUD for the game
 *
 * @author Dominik Rys
 */
public class HUD extends BorderPane {
    /**
     * VBox for all player info
     */
    VBox playerInfoBox;
    /**
     * Minimap rectangle
     */
    Rectangle miniMapRectangle;
    /**
     * Pane for storing minimap
     */
    StackPane miniMapPane;
    /**
     * Canvas for minimap. Canvas used instead of pane since it's more flexible, efficient and can be expanded.
     */
    Canvas minimapCanvas;
    /**
     * GraphicsContext for minimap
     */
    GraphicsContext minimapGC;
    /**
     * Label for player score
     */
    private Label playerScoreNumber;
    /**
     * Pane for held items
     */
    private FlowPane heldItems;
    /**
     * Pane for hearts
     */
    private FlowPane heartPane;
    /**
     * VBox for ammo
     */
    private VBox ammoBox;

    /**
     * Constructor
     */
    public HUD() {
        // Initialize HUD elements
        playerScoreNumber = null;
        heartPane = null;
        heldItems = null;
        ammoBox = null;
    }

    /**
     * Create HUD
     *
     * @param currentPlayer   PlayerView of player for whom this HUD is for
     * @param fontManaspace28 Font of size 28
     * @param fontManaspace18 Font of size 18
     * @param mapWidth        Width of map, in pixels
     * @param mapHeight       Height of map, in pixels
     */
    public void createHUD(PlayerView currentPlayer, Font fontManaspace28, Font fontManaspace18, int mapWidth, int mapHeight) {
        // Set general playerInfoBox settings
        playerInfoBox = new VBox();
        playerInfoBox.setPadding(new Insets(5, 5, 5, 5));
        playerInfoBox.setMaxWidth(200);
        playerInfoBox.setMaxHeight(300);
        playerInfoBox.setSpacing(5);

        // Set up player score label
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
        heartPane = new FlowPane();
        heartPane.setMaxWidth(Constants.TILE_SIZE * 5);

        // Iterate through held items list and add to the HUD
        heldItems = new FlowPane(3, 0); // Make flowpane for held items - supports unlimited amount of them

        // Declare ammo vbox - populated dynamically
        ammoBox = new VBox();

        // Change background according to team
        switch (currentPlayer.getTeam()) {
            case RED:
                playerInfoBox.setStyle("-fx-background-color: rgba(255, 0, 47, 0.5); -fx-background-radius: 0 0 150 0;");
                playerInfoBox.setEffect(new DropShadow(25, Color.rgb(255, 0, 47)));
                break;
            case BLUE:
                playerInfoBox.setStyle("-fx-background-color: rgba(66, 173, 244, 0.5); -fx-background-radius: 0 0 150 0;");
                playerInfoBox.setEffect(new DropShadow(25, Color.rgb(66, 173, 244)));
                break;
            case GREEN:
                playerInfoBox.setStyle("-fx-background-color: rgba(90, 240, 41, 0.5); -fx-background-radius: 0 0 150 0;");
                playerInfoBox.setEffect(new DropShadow(25, Color.rgb(90, 240, 41)));
                break;
            case YELLOW:
                playerInfoBox.setStyle("-fx-background-color: rgba(232, 232, 0, 0.5); -fx-background-radius: 0 0 150 0;");
                playerInfoBox.setEffect(new DropShadow(25, Color.rgb(232, 232, 0)));
                break;
            default:
                playerInfoBox.setStyle("-fx-background-color: rgba(178, 177, 169, 0.65); -fx-background-radius: 0 0 150 0;");
                playerInfoBox.setEffect(new DropShadow(25, Color.rgb(178, 177, 169)));
                break;
        }

        // Set up minimap
        miniMapPane = new StackPane();
        miniMapPane.setPadding(new Insets(10, 10, 10, 10));

        miniMapRectangle = new Rectangle();
        miniMapRectangle.setFill(new Color(0, 0, 0, 0.75));
        miniMapRectangle.setStroke(new Color(0.75, 0.75, 0.75, 0.9));
        miniMapRectangle.setStrokeWidth(3);

        // Scale minimap according to map size
        int maxMinimapSize = 200;

        if (mapHeight > mapWidth) {
            miniMapRectangle.setHeight(maxMinimapSize);
            miniMapRectangle.setWidth(((double) mapWidth / mapHeight) * maxMinimapSize);
        } else if (mapHeight < mapWidth) {
            miniMapRectangle.setHeight(((double) mapHeight / mapWidth) * maxMinimapSize);
            miniMapRectangle.setWidth(maxMinimapSize);
        } else {
            miniMapRectangle.setHeight(maxMinimapSize);
            miniMapRectangle.setWidth(maxMinimapSize);
        }

        // Initialise minimap canvas
        minimapCanvas = new Canvas(miniMapRectangle.getWidth(), miniMapRectangle.getHeight());
        minimapGC = minimapCanvas.getGraphicsContext2D();

        // Add minimap to top right of HUD
        miniMapPane.getChildren().addAll(miniMapRectangle, minimapCanvas);
        this.setRight(miniMapPane);
        miniMapPane.setAlignment(Pos.TOP_RIGHT);

        // Add playerinfo elements to top left of hud
        playerInfoBox.getChildren().addAll(playerLabel, heartPane, playerScoreLabel, playerScoreNumber, heldItems, ammoBox);
        this.setLeft(playerInfoBox);
    }

    /**
     * Update HUD with new data
     *
     * @param gameView               GameView object
     * @param rendererResourceLoader Resources for renderer
     * @param fontManaspace28        Font of size 28
     * @param fontManaspace18        Font of size 18
     * @param currentPlayer          PlayerView for current player
     */
    public void updateHUD(GameView gameView, RendererResourceLoader rendererResourceLoader, Font fontManaspace28, Font fontManaspace18,
                          PlayerView currentPlayer) {
        // Update score
        playerScoreNumber.setText(Integer.toString(currentPlayer.getScore()));

        // Update hearts
        heartPane.getChildren().clear();
        int halfHearts = currentPlayer.getHealth() % 2;
        int wholeHearts = currentPlayer.getHealth() / 2;
        int lostHearts = (currentPlayer.getMaxHealth() - currentPlayer.getHealth()) / 2;

        // Populate heart box in GUI
        for (int i = 0; i < wholeHearts; i++) {
            heartPane.getChildren().add(new ImageView(rendererResourceLoader.getSprite(EntityList.HEART_FULL)));
        }
        // Populate half heart
        for (int i = 0; i < halfHearts; i++) {
            heartPane.getChildren().add(new ImageView(rendererResourceLoader.getSprite(EntityList.HEART_HALF)));
        }
        // Populate lost hearts
        for (int i = 0; i < lostHearts; i++) {
            heartPane.getChildren().add(new ImageView(rendererResourceLoader.getSprite(EntityList.HEART_LOST)));
        }

        // Update held items
        heldItems.getChildren().clear();

        int currentItemIndex = 0; // Keep track of current item since iterator is used

        for (ItemView currentItem : currentPlayer.getItems()) {
            // Make image view out of graphic
            ImageView itemImageView = new ImageView(rendererResourceLoader.getSprite(currentItem.getItemListName().getEntityList()));

            // Pane for item image to go in - for border
            FlowPane itemPane = new FlowPane();
            itemPane.setPrefWidth(Constants.TILE_SIZE);
            itemPane.setPadding(new Insets(2, 2, 2, 2));

            // Check if the item currently being checked is the current selected item, and if it is, show that
            if (currentItemIndex == currentPlayer.getCurrentItemIndex()) {
                DropShadow dropShadow = new DropShadow(25, Color.GOLD);
                dropShadow.setSpread(0.75);
                itemImageView.setEffect(dropShadow);
                itemPane.setBorder(new Border(new BorderStroke(Color.GOLD,
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

        // Set up ammo informaton in hud
        Label ammoInGun;
        Label totalAmmoInClip;
        Label totalAmmoForCurrentItem;

        switch (currentItem.getItemType()) {
            case GUN:
                GunView currentGun = (GunView) currentItem;

                ammoInGun = new Label(Integer.toString(currentGun.getAmmoInClip()),
                        new ImageView(rendererResourceLoader.getSprite(currentGun.getAmmoType().getItemListName().getEntityList())));
                ammoInGun.setFont(fontManaspace28);

                totalAmmoInClip = new Label("/" + currentGun.getClipSize());
                totalAmmoInClip.setFont(fontManaspace18);

                totalAmmoForCurrentItem = new Label(Integer.toString(currentPlayer.getAmmo().getOrDefault(currentGun.getAmmoType(),
                        0)));
                totalAmmoForCurrentItem.setFont(fontManaspace28);

                break;
            case CONSUMEABLE:
                ammoInGun = new Label(Integer.toString(1));
                ammoInGun.setFont(fontManaspace28);

                totalAmmoInClip = new Label("/" + 1);
                totalAmmoInClip.setFont(fontManaspace18);

                totalAmmoForCurrentItem = new Label(Integer.toString(0));
                totalAmmoForCurrentItem.setFont(fontManaspace28);
                break;
            case MELEE_WEAPON:
            default:
                ammoInGun = new Label("∞", new ImageView(rendererResourceLoader.getSprite(EntityList.MELEE)));
                ammoInGun.setFont(new Font("Consolas", 28));

                totalAmmoInClip = new Label("/∞");
                totalAmmoInClip.setFont(new Font("Consolas", 18));

                totalAmmoForCurrentItem = new Label("");
                totalAmmoForCurrentItem.setFont(fontManaspace28);
                break;
        }

        // Style labels
        ammoInGun.setTextFill(Color.BLACK);
        totalAmmoInClip.setTextFill(Color.BLACK);
        totalAmmoForCurrentItem.setTextFill(Color.BLACK);

        // Add info on current gun to the right element
        currentGunInfo.getChildren().addAll(ammoInGun, totalAmmoInClip);
        // Add the total amount of ammo the current gun uses under the gun info
        ammoBox.getChildren().addAll(currentGunInfo, totalAmmoForCurrentItem);

        // Update minimap
        updateMiniMap(gameView, currentPlayer);
    }

    /**
     * Create minimap
     *
     * @param gameView      GameView object representing the current state of the game
     * @param currentPlayer PlayerView for who the hud is
     * @return MiniMap as stackpane
     */
    private void updateMiniMap(GameView gameView, PlayerView currentPlayer) {
        // Clear minimap canvas
        minimapGC.clearRect(0, 0, miniMapRectangle.getWidth(), miniMapRectangle.getHeight());

        // Specify size for minimap indicators
        int playerRectangleSize = 4;

        // Loop through all players, placing them on the minimap
        for (PlayerView playerView : gameView.getPlayers()) {
            // Create new player indicator
            Rectangle playerRectangle = new Rectangle(playerRectangleSize, playerRectangleSize);

            // If current player, set box to white
            if (playerView.getID() == currentPlayer.getID()) {
                playerRectangle.setFill(Color.WHITE);
            } else {
                switch (playerView.getTeam()) {
                    case RED:
                        playerRectangle.setFill(Constants.RED_TEAM_COLOR);
                        break;
                    case BLUE:
                        playerRectangle.setFill(Constants.BLUE_TEAM_COLOR);
                        break;
                    case GREEN:
                        playerRectangle.setFill(Constants.GREEN_TEAM_COLOR);
                        break;
                    case YELLOW:
                        playerRectangle.setFill(Constants.YELLOW_TEAM_COLOR);
                        break;
                    default:
                        playerRectangle.setFill(Color.rgb(178, 177, 169));
                        break;
                }
            }

            // Draw player indicator at the correct spot on the minimap
            minimapGC.fillRect(((playerView.getPose().getX() + Constants.TILE_SIZE / 2) *
                            (miniMapRectangle.getWidth() / (gameView.getXDim() * Constants.TILE_SIZE)) - playerRectangleSize / 2),
                    ((playerView.getPose().getY() + Constants.TILE_SIZE / 2) *
                            (miniMapRectangle.getHeight() / (gameView.getYDim() * Constants.TILE_SIZE)) - playerRectangleSize / 2),
                    playerRectangleSize, playerRectangleSize);

            // TODO: add more elements to minimap
        }
    }

    /**
     * Display message after played dies
     *
     * @param fontManaspace50 Font of size 50
     * @param fontManaspace18 Font of size 18
     */
    public void displayDeathMessage(Font fontManaspace50, Font fontManaspace18) {
        // Create game over text
        Label gameOverLabel = new Label("You Died!");
        gameOverLabel.setFont(fontManaspace50);
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setPadding(new Insets(10, 10, 10, 10));
        gameOverLabel.setBackground(new Background(new BackgroundFill(new Color(1, 1, 1, 0.5),
                new CornerRadii(0), new Insets(0, 0, 0, 0))));

        // Create label notifying player they are now in spectator mode
        Label spectatorHintLabel = new Label("Entered spectator mode");
        spectatorHintLabel.setFont(fontManaspace18);
        spectatorHintLabel.setTextFill(Color.BLACK);
        spectatorHintLabel.setPadding(new Insets(10, 10, 10, 10));
        spectatorHintLabel.setBackground(new Background(new BackgroundFill(new Color(1, 1, 1, 0.5),
                new CornerRadii(0), new Insets(0, 0, 0, 0))));

        // Add labels to VBox and display VBox
        VBox deathMessageBox = new VBox(gameOverLabel, spectatorHintLabel);
        deathMessageBox.setAlignment(Pos.CENTER);
        this.setCenter(deathMessageBox);
    }

    /**
     * Displays win message
     *
     * @param fontManaspace50 Font used by game of size 50
     * @param fontManaspace28 Font used by game of size 28
     * @param winningTeam     The winning team
     */
    public void displayWinMessage(Font fontManaspace50, Font fontManaspace28, Team winningTeam) {
        // Message already displayed, return
        if (this.getBottom() != null) {
            return;
        }

        // "Game Over" text
        Label gameWinLabel = new Label("Game Over!");
        gameWinLabel.setFont(fontManaspace50);
        gameWinLabel.setTextFill(Color.RED);
        gameWinLabel.setPadding(new Insets(10, 10, 10, 10));
        gameWinLabel.setBackground(new Background(new BackgroundFill(Color.BLACK,
                new CornerRadii(0), new Insets(0, 0, 0, 0))));

        // Label with which team won
        Label teamLabel = new Label("Winning team: " + winningTeam.toString());
        teamLabel.setFont(fontManaspace28);
        switch (winningTeam) {
            case RED:
                teamLabel.setTextFill(Constants.RED_TEAM_COLOR);
                break;
            case BLUE:
                teamLabel.setTextFill(Constants.BLUE_TEAM_COLOR);
                break;
            case GREEN:
                teamLabel.setTextFill(Constants.GREEN_TEAM_COLOR);
                break;
            case YELLOW:
                teamLabel.setTextFill(Constants.YELLOW_TEAM_COLOR);
                break;
            default:
                teamLabel.setTextFill(Color.BLACK);
        }
        teamLabel.setPadding(new Insets(10, 10, 10, 10));
        teamLabel.setBackground(new Background(new BackgroundFill(Color.BLACK,
                new CornerRadii(0), new Insets(0, 0, 0, 0))));

        // Add labels to VBox and display VBox
        VBox winMessage = new VBox(gameWinLabel, teamLabel);
        winMessage.setAlignment(Pos.CENTER);
        this.setBottom(winMessage);
    }

    /**
     * Close the death message
     */
    public void closeDeathMessage() {
        this.setCenter(null);
    }
}
