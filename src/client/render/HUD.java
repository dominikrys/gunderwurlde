package client.render;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import shared.Constants;
import shared.Pose;
import shared.lists.AmmoList;
import shared.lists.EntityList;
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

        // Add minimap to top right of HUD
        miniMapPane.getChildren().add(miniMapRectangle);
        this.setRight(miniMapPane);

        // Add playerinfo elements to top left of hud
        playerInfoBox.getChildren().addAll(playerLabel, heartPane, playerScoreLabel, playerScoreNumber, heldItems, ammoBox);
        this.setLeft(playerInfoBox);
    }

    /**
     * Update HUD with new data
     *
     * @param currentPlayer          PlayerView for current player
     * @param rendererResourceLoader Resources for renderer
     * @param fontManaspace28        Font of size 28
     * @param fontManaspace18        Font of size 18
     * @param playerPose             Pose of player for whom the HUD is
     * @param mapWidth               Width of the map, in pixels
     * @param mapHeight              Height of the map, in pixels
     */
    public void updateHUD(PlayerView currentPlayer, RendererResourceLoader rendererResourceLoader, Font fontManaspace28, Font fontManaspace18,
                          Pose playerPose, int mapWidth, int mapHeight) {
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

        // Add ammo amount to hud if the item has ammo
        if (currentItem.getAmmoType() != AmmoList.NONE) {
            // Make label for current ammo in item
            Label ammoInGun = new Label(Integer.toString(currentItem.getAmmoInClip()),
                    new ImageView(rendererResourceLoader.getSprite(EntityList.AMMO_CLIP)));
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

        // Make indicator for player location on minimap
        int playerRectangleSize = 4;
        Rectangle playerRectangle = new Rectangle(playerRectangleSize, playerRectangleSize);
        playerRectangle.setFill(Color.WHITE);

        // Set correct position of player location on minimap
        AnchorPane playerRectanglePane = new AnchorPane(playerRectangle);
        AnchorPane.setLeftAnchor(playerRectangle, (playerPose.getX() + Constants.TILE_SIZE / 2) *
                (miniMapRectangle.getWidth() / mapWidth) - playerRectangleSize / 2);
        AnchorPane.setTopAnchor(playerRectangle, (playerPose.getY() + Constants.TILE_SIZE / 2) *
                (miniMapRectangle.getHeight() / mapHeight) - playerRectangleSize / 2);

        // Update pane
        miniMapPane.getChildren().clear();
        miniMapPane.getChildren().addAll(miniMapRectangle, playerRectanglePane);
        miniMapPane.setAlignment(Pos.TOP_RIGHT);
    }

    /**
     * Display message after played dies
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
     * Close the death message
     */
    public void closeDeathMessage() {
        this.setCenter(null);
    }
}
