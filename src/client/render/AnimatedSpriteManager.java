package client.render;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class AnimatedSpriteManager {
    private Image image;
    private int individualImageHeight;
    private int individualImageWidth;
    private int imageCount;
    private int timeBetweenFrames;
    private Timeline timeline;

    AnimatedSpriteManager(Image image, int individualImageHeight, int individualImageWidth, int imageCount, int timeBetweenFrames) {
        this.image = image;
        this.individualImageHeight = individualImageHeight;
        this.individualImageWidth = individualImageWidth;
        this.imageCount = imageCount;
        this.timeBetweenFrames = timeBetweenFrames;

        if (imageCount != 1) {
            timeline = new Timeline(new KeyFrame(Duration.millis(timeBetweenFrames),
                    e -> {

                    }));

            timeline.setCycleCount(imageCount);
            timeline.play();
        }
    }

    AnimatedSpriteManager(Image image) {
        this.image = image;
        this.individualImageHeight = 0;
        this.individualImageWidth = 0;
        this.imageCount = 1;
        this.timeBetweenFrames = 0;
        this.timeline = new Timeline();
    }

    Image getCurrentImage() {
        if (imageCount == 1) {
            return image;
        } else {

        }
    }
}
