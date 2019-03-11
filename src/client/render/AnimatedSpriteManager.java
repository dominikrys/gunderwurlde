package client.render;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class AnimatedSpriteManager {
    private Image image;
    private int currentFrame;
    private Timeline timeline;
    private AnimationType animationType;

    private double sx;
    private double sy;
    private int individualImageWidth;
    private int individualImageHeight;

    AnimatedSpriteManager(Image image, int individualImageHeight, int individualImageWidth, int frameCount,
                          int timeBetweenFrames, int cycleCount, AnimationType animationType) {
        // Initialise variables
        this.image = image;
        this.individualImageHeight = individualImageHeight;
        this.individualImageWidth = individualImageWidth;
        this.sy = 0;
        this.sx = 0;
        this.animationType = animationType;
        this.currentFrame = 0;

        // Check if animation - frame count has to be higher than 1
        if (frameCount != 1) {
            // Create timeline that will loop over the parts of the spritesheet
            timeline = new Timeline(new KeyFrame(Duration.millis(timeBetweenFrames),
                    e -> {
                        //TODO: this only supports once cycle - make it support more if necessary?
                        // Check if not reached end of frames
                        if (currentFrame < frameCount - 1) {
                            // Increment position x of current frame with the frame width
                            sx += individualImageWidth;
                            currentFrame++;
                        } else if (cycleCount == 0) {
                            // Reached end of animation, reset sx and frame counter
                            sx = 0;
                            currentFrame = 0;
                        }
                    }));

            // Set amount of times for animation to cycle: 0 means indefinite
            if (cycleCount == 0) {
                timeline.setCycleCount(Timeline.INDEFINITE);
            } else {
                timeline.setCycleCount(cycleCount * frameCount);
            }

            timeline.play();
        }
    }

    // Blank constructor for initialising in hashmap in renderer
    AnimatedSpriteManager() {
        animationType = AnimationType.NONE;
    }

    // Constructor which takes image and animationtype for static images
    AnimatedSpriteManager(Image image, AnimationType animationType) {
        this.image = image;
        this.animationType = animationType;

        this.currentFrame = 0;
        this.timeline = null;
        this.sx = 0;
        this.sy = 0;
        this.individualImageWidth = (int) image.getWidth();
        this.individualImageHeight = (int) image.getHeight();
    }

    public Image getImage() {
        return image;
    }

    public double getSx() {
        return sx;
    }

    public double getSy() {
        return sy;
    }

    public int getImageWidth() {
        return individualImageWidth;
    }

    public int getImageHeight() {
        return individualImageHeight;
    }

    public AnimationType getAnimationType() {
        return animationType;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}
