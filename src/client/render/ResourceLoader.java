package client.render;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import shared.Constants;
import shared.lists.EntityList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoader {
    private Map<EntityList, Image> loadedSprites;
    private Font fontManaspace28;
    private Font fontManaspace18;

    public ResourceLoader() {
        loadedSprites = new HashMap<>();
        fontManaspace18 = null;
        fontManaspace28 = null;
    }

    public void loadAllSprites() {
        // Iterate over all entitity lists and put into hashmap
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
    }

    public void loadFonts() {
        // Load fonts
        try {
            fontManaspace28 = Font.loadFont(new FileInputStream(new File(Constants.MANASPACE_FONT_PATH)), 28);
            fontManaspace18 = Font.loadFont(new FileInputStream(new File(Constants.MANASPACE_FONT_PATH)), 18);
        } catch (FileNotFoundException e) {
            System.out.println("Loading default font, font not found in " + Constants.MANASPACE_FONT_PATH);
            fontManaspace28 = new Font("Consolas", 28);
            fontManaspace18 = new Font("Consolas", 18);
        }
    }

    public Image getSprite(EntityList entity) {
        return loadedSprites.get(entity);
    }

    public Font getFontManaspace28() {
        return fontManaspace28;
    }

    public Font getFontManaspace18() {
        return fontManaspace18;
    }
}
