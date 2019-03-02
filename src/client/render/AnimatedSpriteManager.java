package client.render;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class AnimatedSpriteManager {
    private Image image;
    private int frameCount;
    private int currentFrame;
    private int timeBetweenFrames;
    private Timeline timeline;

    private double sx;
    private double sy;
    private int individualImageWidth;
    private int individualImageHeight;

    /*
    For use with GraphicsContext::drawImage()

    Parameters:
    img - the image to be drawn or null.
    sx - the source rectangle's X coordinate position.
    sy - the source rectangle's Y coordinate position.
    sw - the source rectangle's width.
    sh - the source rectangle's height.
    dx - the destination rectangle's X coordinate position.
    dy - the destination rectangle's Y coordinate position.
    dw - the destination rectangle's width.
    dh - the destination rectangle's height.
     */

    AnimatedSpriteManager(Image image, int individualImageHeight, int individualImageWidth, int frameCount, int timeBetweenFrames) {
        this.image = image;
        this.individualImageHeight = individualImageHeight;
        this.individualImageWidth = individualImageWidth;
        this.frameCount = frameCount;
        this.timeBetweenFrames = timeBetweenFrames;
        this.sy = 0;
        this.currentFrame = 0;

        if (frameCount != 1) {
            timeline = new Timeline(new KeyFrame(Duration.millis(timeBetweenFrames),
                    e -> {
                        if (currentFrame < frameCount) {
                            sx += individualImageWidth;
                            currentFrame++;
                        } else {
                            sx = 0;
                            currentFrame = 0;
                        }

                    }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    AnimatedSpriteManager(Image image) {
        this.image = image;
        this.individualImageHeight = (int) image.getHeight();
        this.individualImageWidth = (int) image.getWidth();
        this.frameCount = 1;
        this.timeBetweenFrames = 0;
        this.currentFrame = 0;
        this.timeline = new Timeline();
        this.sx = 0;
        this.sy = 0;
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
}
