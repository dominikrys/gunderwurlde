package client.render;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import shared.lists.EntityList;

import java.util.HashMap;
import java.util.Map;

public class SpriteLoader {
    private Map<EntityList, Image> loadedSprites;

    public SpriteLoader() {
        loadedSprites = new HashMap<>();
    }

    public void loadAllSprites() {
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

    public Image getSprite(EntityList entity) {
        return loadedSprites.get(entity);
    }
}
