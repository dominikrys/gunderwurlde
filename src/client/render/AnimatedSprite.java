package client.render;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 * AnimatedSprite class. Handles spritesheet animations.
 * @author Dominik Rys
 */
public class AnimatedSprite {
    /**
     * Image containing spritesheet
     */
    private Image image;

    /**
     * Current frame being shown out of all the frames
     */
    private int currentFrame;

    /**
     * Timeline for cycling through the spritesheet's sprites
     */
    private Timeline timeline;

    /**
     * Type of animation from allowed types
     */
    private AnimationType animationType;

    /**
     * X coordinate of current image from spritesheet
     */
    private double xOffset;

    /**
     * Y coordinate of current image from spritesheet
     */
    private double yOffset;

    /**
     * Width of each sprite on spritesheet
     */
    private int individualImageWidth;

    /**
     * Height of each sprite on spritesheet
     */
    private int individualImageHeight;

    /**
     * Constructor
     * @param image Image containing spritesheet
     * @param individualImageHeight Height of each image in spritesheet
     * @param individualImageWidth Width of each image in spritesheet
     * @param frameCount Total amount of frames on spritesheet
     * @param timeBetweenFrames Time between frames in milliseonds
     * @param cycleCount How many times to cycle through animation
     * @param animationType Animation type according to AnimationType enum
     */
    AnimatedSprite(Image image, int individualImageHeight, int individualImageWidth, int frameCount,
                   int timeBetweenFrames, int cycleCount, AnimationType animationType) {
        // Initialise variables
        this.image = image;
        this.individualImageHeight = individualImageHeight;
        this.individualImageWidth = individualImageWidth;
        this.yOffset = 0;
        this.xOffset = 0;
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
                            xOffset += individualImageWidth;
                            currentFrame++;
                        } else if (cycleCount == 0) {
                            // Reached end of animation, reset xOffset and frame counter
                            xOffset = 0;
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

    /**
     * Blank constructor for initialising in hashmap in MapCanvas class
     */
    AnimatedSprite() {
        animationType = AnimationType.NONE;
    }

    /**
     * Constructor for static images
     * @param image Image
     * @param animationType Type of animation according to AnimationType enum
     */
    AnimatedSprite(Image image, AnimationType animationType) {
        this.image = image;
        this.animationType = animationType;

        this.currentFrame = 0;
        this.timeline = null;
        this.xOffset = 0;
        this.yOffset = 0;
        this.individualImageWidth = (int) image.getWidth();
        this.individualImageHeight = (int) image.getHeight();
    }

    /**
     * Get image
     * @return Image stored by this object
     */
    public Image getImage() {
        return image;
    }

    /**
     * Get current x offset
     * @return X offset
     */
    public double getXOffset() {
        return xOffset;
    }

    /**
     * Get current y offset
     * @return Y offset
     */
    public double getYOffset() {
        return yOffset;
    }

    /**
     * Get individual image width
     * @return Individual image width
     */
    public int getIndividualImageWidth() {
        return individualImageWidth;
    }

    /**
     * Get individual image height
     * @return Individual image height
     */
    public int getIndividualImageHeight() {
        return individualImageHeight;
    }

    /**
     * Get animation type
     * @return Animation type
     */
    public AnimationType getAnimationType() {
        return animationType;
    }

    /**
     * Get current frame
     * @return Current frame
     */
    public int getCurrentFrame() {
        return currentFrame;
    }
}
