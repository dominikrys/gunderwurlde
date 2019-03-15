package client.render;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import shared.Constants;
import shared.lists.AmmoList;
import shared.lists.EntityList;
import shared.view.ItemView;
import shared.view.entity.PlayerView;

public class HUD extends VBox {
    private Label playerScoreNumber;
    private FlowPane heldItems;
    private FlowPane heartBox;
    private VBox ammoBox;

    public HUD() {
        // Initialize HUD elements
        playerScoreNumber = null;
        heartBox = null;
        heldItems = null;
        ammoBox = null;
    }

    // Create HUD
    public void createHUD(PlayerView currentPlayer, ResourceLoader resourceLoader, Font fontManaspace28, Font fontManaspace18) {
        // Make HUD
        this.setPadding(new Insets(5, 5, 5, 5));
        this.setMaxWidth(Constants.TILE_SIZE * 6);
        this.setMaxHeight(300);
        this.setSpacing(5);

        // If for some reason the player hasn't been found, return an empty HUD
        if (currentPlayer == null) {
            return;
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
                this.setStyle("-fx-background-color: rgba(255, 0, 47, 0.4); -fx-background-radius: 0 0 165 0;");
                this.setEffect(new DropShadow(25, Color.rgb(255, 0, 47)));
                break;
            case BLUE:
                this.setStyle("-fx-background-color: rgba(66, 173, 244, 0.4); -fx-background-radius: 0 0 165 0;");
                this.setEffect(new DropShadow(25, Color.rgb(66, 173, 244)));
                break;
            case GREEN:
                this.setStyle("-fx-background-color: rgba(90, 240, 41, 0.4); -fx-background-radius: 0 0 165 0;");
                this.setEffect(new DropShadow(25, Color.rgb(90, 240, 41)));
                break;
            case YELLOW:
                this.setStyle("-fx-background-color: rgba(232, 232, 0, 0.4); -fx-background-radius: 0 0 165 0;");
                this.setEffect(new DropShadow(25, Color.rgb(232, 232, 0)));
                break;
            default:
                this.setStyle("-fx-background-color: rgba(178, 177, 169, 0.65); -fx-background-radius: 0 0 165 0;");
                this.setEffect(new DropShadow(25, Color.rgb(178, 177, 169)));
                break;
        }

        // Add elements of HUD for player to HUD
        this.getChildren().addAll(playerLabel, heartBox, playerScoreLabel, playerScoreNumber, heldItems, ammoBox);
    }

    // Update all HUD elements
    public void updateHUD(PlayerView currentPlayer, ResourceLoader resourceLoader, Font fontManaspace28, Font fontManaspace18){
        // Update score
        playerScoreNumber.setText(Integer.toString(currentPlayer.getScore()));

        // Update hearts
        heartBox.getChildren().clear();
        int halfHearts = currentPlayer.getHealth() % 2;
        int wholeHearts = currentPlayer.getHealth() / 2;
        int lostHearts = (currentPlayer.getMaxHealth() - currentPlayer.getHealth()) / 2;

        // Populate heart box in GUI
        for (int i = 0; i < wholeHearts; i++) {
            heartBox.getChildren().add(new ImageView(resourceLoader.getSprite(EntityList.HEART_FULL)));
        }
        // Populate half heart
        for (int i = 0; i < halfHearts; i++) {
            heartBox.getChildren().add(new ImageView(resourceLoader.getSprite(EntityList.HEART_HALF)));
        }
        // Populate lost hearts
        for (int i = 0; i < lostHearts; i++) {
            heartBox.getChildren().add(new ImageView(resourceLoader.getSprite(EntityList.HEART_LOST)));
        }

        // Update held items
        heldItems.getChildren().clear();

        int currentItemIndex = 0; // Keep track of current item since iterator is used

        for (ItemView currentItem : currentPlayer.getItems()) {
            // Make image view out of graphic
            ImageView itemImageView = new ImageView(resourceLoader.getSprite(currentItem.getItemListName().getEntityList()));

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
                    new ImageView(resourceLoader.getSprite(EntityList.AMMO_CLIP)));
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
}
