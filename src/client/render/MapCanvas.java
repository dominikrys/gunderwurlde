package client.render;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.AmmoList;
import shared.lists.EntityList;
import shared.view.GameView;
import shared.view.ItemView;
import shared.view.entity.*;

import java.util.HashMap;
import java.util.Map;

/**
 * MapCanvas class. This is what the map and all entities are rendered on.
 *
 * @author Dominik Rys
 */
public class MapCanvas extends Canvas {
    /**
     * GraphicsContext used by this canvas
     */
    private GraphicsContext mapGC;

    /**
     * Hashmap for storing player animations
     */
    private Map<Integer, AnimatedSprite> playersOnMapAnimations;

    /**
     * HashMap for storing enemy animations
     */
    private Map<Integer, AnimatedSprite> enemiesOnMapAnimations;

    /**
     * Player locations in last gameview object - used for checking if any players died
     */
    private Map<Integer, Pose> lastPlayerLocations;

    /**
     * Enemy locations in last gameview object - used for checking if any enemies died
     */
    private Map<Integer, Pose> lastEnemyLocations;

    /**
     * Constructor
     *
     * @param width  Width of canvas
     * @param height Height of canvas
     */
    public MapCanvas(int width, int height) {
        // Initialise canvas
        super(width, height);
        mapGC = this.getGraphicsContext2D();

        // Initialise animation hashmaps
        playersOnMapAnimations = new HashMap<>();
        enemiesOnMapAnimations = new HashMap<>();

        // Initialise last location hashmaps for animations
        lastPlayerLocations = new HashMap<>();
        lastEnemyLocations = new HashMap<>();
    }

    /**
     * Render map tiles
     *
     * @param gameView               GameView to get map data from
     * @param rendererResourceLoader Renderer resources
     */
    public void renderMap(GameView gameView, RendererResourceLoader rendererResourceLoader) {
        // Get map X and Y dimensions
        int mapX = gameView.getXDim();
        int mapY = gameView.getYDim();

        // Iterate through the map, rending each tile on canvas
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                // Get tile graphic
                Image tileImage = rendererResourceLoader.getSprite(gameView.getTileMap()[x][y].getTileType().getEntityListName());

                // Add tile to canvas
                mapGC.drawImage(tileImage, x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE,
                        Constants.TILE_SIZE);
            }
        }
    }

    /**
     * Render entities to canvas
     *
     * @param gameView               GameView to get data from
     * @param playerID               ID of player that can see this canvas
     * @param rendererResourceLoader Renderer resources
     */
    public void renderEntitiesFromGameViewToCanvas(GameView gameView, int playerID, RendererResourceLoader rendererResourceLoader) {
        // Render items
        for (ItemDropView currentItem : gameView.getItemDrops()) {
            // Make item flicker when timer on it is expiring
            if (currentItem.getTimeLeft() < 4000  && currentItem.getTimeLeft() >= 2500) {
                if (currentItem.getTimeLeft() % 500 < 250) {
                    renderEntityView(currentItem, rendererResourceLoader);
                }
            }
            else if (currentItem.getTimeLeft() < 2500  && currentItem.getTimeLeft() >= 1000) {
                if (currentItem.getTimeLeft() % 250 < 125) {
                    renderEntityView(currentItem, rendererResourceLoader);
                }
            }
            else if (currentItem.getTimeLeft() < 1000  && currentItem.getTimeLeft() >= 0) {
                if (currentItem.getTimeLeft() % 100 < 50) {
                    renderEntityView(currentItem, rendererResourceLoader);
                }
            } else {
                renderEntityView(currentItem, rendererResourceLoader);
            }
        }

        // Render players
        for (PlayerView currentPlayer : gameView.getPlayers()) {
            // Update animation hashmap to track entity
            if (!playersOnMapAnimations.containsKey(currentPlayer.getID())) {
                playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSprite());
            }

            // Check correct animation
            if (currentPlayer.isMoving()) {
                // TODO: have this go through a scale factor check
                // Check if in map of currently tracked players
                if (playersOnMapAnimations.get(playerID).getAnimationType() != AnimationType.MOVE) {
                    // Get the correct sprite
                    EntityList entityToRender = EntityList.PLAYER_WALK;
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            entityToRender = EntityList.PLAYER_WALK_RED;
                            break;
                        case BLUE:
                            entityToRender = EntityList.PLAYER_WALK_BLUE;
                            break;
                        case GREEN:
                            entityToRender = EntityList.PLAYER_WALK_GREEN;
                            break;
                        case YELLOW:
                            entityToRender = EntityList.PLAYER_WALK_YELLOW;
                            break;
                    }

                    // Add animation to animationhashmap
                    playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSprite(
                            rendererResourceLoader.getSprite(entityToRender), 32, 32,
                            6, 75, 0, AnimationType.MOVE));
                }
            }
            // Check if player reloading
            else if (currentPlayer.getCurrentAction() == ActionList.RELOADING) {
                // Check if in map of currently tracked players
                if (playersOnMapAnimations.get(playerID).getAnimationType() != AnimationType.RELOAD) {
                    // Get the correct sprite
                    EntityList entityToRender = EntityList.PLAYER_RELOAD;
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            entityToRender = EntityList.PLAYER_RELOAD_RED;
                            break;
                        case BLUE:
                            entityToRender = EntityList.PLAYER_RELOAD_BLUE;
                            break;
                        case GREEN:
                            entityToRender = EntityList.PLAYER_RELOAD_GREEN;
                            break;
                        case YELLOW:
                            entityToRender = EntityList.PLAYER_RELOAD_YELLOW;
                            break;
                    }

                    // Add animation to animationhashmap
                    playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSprite(
                            rendererResourceLoader.getSprite(entityToRender), 32, 45,
                            5, currentPlayer.getCurrentItem().getReloadTime() / 5,
                            0, AnimationType.RELOAD));
                }
            }
            // Check if player attacking
            else if (currentPlayer.getCurrentAction() == ActionList.ATTACKING) {
                // Get correct sprite
                EntityList entityToRender = EntityList.PLAYER_WITH_GUN_RECOIL;
                switch (currentPlayer.getTeam()) {
                    case RED:
                        entityToRender = EntityList.PLAYER_WITH_GUN_RECOIL_RED;
                        break;
                    case GREEN:
                        entityToRender = EntityList.PLAYER_WITH_GUN_RECOIL_GREEN;
                        break;
                    case YELLOW:
                        entityToRender = EntityList.PLAYER_WITH_GUN_RECOIL_YELLOW;
                        break;
                    case BLUE:
                        entityToRender = EntityList.PLAYER_WITH_GUN_RECOIL_BLUE;
                        break;
                }

                // Add sprite to animation hashmap
                playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSprite(
                        rendererResourceLoader.getSprite(entityToRender), AnimationType.ATTACK));
            }
            // If standing, render standing image
            else {
                // Check if player has item that takes bullets - if so, render player with a gun
                boolean hasGun = false;

                for (ItemView iv : currentPlayer.getItems()) {
                    if (iv.getAmmoType() != AmmoList.NONE) {
                        hasGun = true;
                        break;
                    }
                }

                // Get correct graphic
                EntityList entityToRender;
                if (hasGun) {
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            entityToRender = EntityList.PLAYER_WITH_GUN_RED;
                            break;
                        case BLUE:
                            entityToRender = EntityList.PLAYER_WITH_GUN_BLUE;
                            break;
                        case GREEN:
                            entityToRender = EntityList.PLAYER_WITH_GUN_GREEN;
                            break;
                        case YELLOW:
                            entityToRender = EntityList.PLAYER_WITH_GUN_YELLOW;
                            break;
                        default:
                            entityToRender = EntityList.PLAYER_WITH_GUN;
                            break;
                    }
                } else {
                    switch (currentPlayer.getTeam()) {
                        case RED:
                            entityToRender = EntityList.PLAYER_RED;
                            break;
                        case BLUE:
                            entityToRender = EntityList.PLAYER_BLUE;
                            break;
                        case GREEN:
                            entityToRender = EntityList.PLAYER_GREEN;
                            break;
                        case YELLOW:
                            entityToRender = EntityList.PLAYER_YELLOW;
                            break;
                        default:
                            entityToRender = EntityList.PLAYER;
                            break;
                    }
                }

                // Add sprite to animation hashmap
                playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSprite(
                        rendererResourceLoader.getSprite(entityToRender), AnimationType.STAND));
            }

            // Animation now in playerOnMap map so just render in appropriate location
            renderAnimationSpriteOnMap(playersOnMapAnimations, currentPlayer.getID(), currentPlayer.getPose());

            // Render healthbar
            renderHealthBar(currentPlayer.getPose(), currentPlayer.getHealth(), currentPlayer.getMaxHealth());

            // Put player into player locations hashmap
            lastPlayerLocations.put(currentPlayer.getID(), currentPlayer.getPose());
        }

        // Render enemies
        for (EnemyView currentEnemy : gameView.getEnemies()) {
            // Update animation hashmap to track entity
            if (!enemiesOnMapAnimations.containsKey(currentEnemy.getID())) {
                enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite());
            }

            // Check if enemy moving and is so, choose right animation
            if (currentEnemy.isMoving()) {
                // Check if already in the hashmap for enemies on the map
                if (enemiesOnMapAnimations.get(currentEnemy.getID()).getAnimationType() != AnimationType.MOVE) {
                    //TODO: add support for larger enemies/those that don't use the default zombie skin

                    // Get correct sprite
                    EntityList entityToRender = EntityList.ZOMBIE_WALK;
                    switch (currentEnemy.getEntityListName()) {
                        case ZOMBIE:
                            entityToRender = EntityList.ZOMBIE_WALK;
                            break;
                        case RUNNER:
                            entityToRender = EntityList.RUNNER_WALK;
                            break;
                        case SOLDIER:
                            entityToRender = EntityList.SOLDIER_WALK;
                            break;
                        case MIDGET:
                            entityToRender = EntityList.MIDGET_WALK;
                            break;
                        case BOOMER:
                            entityToRender = EntityList.BOOMER_WALK;
                            break;
                        case MACHINE_GUNNER:
                            entityToRender = EntityList.MACHINE_GUNNER_WALK;
                            break;
                    }

                    // Add animation to animation hashmap
                    enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                            rendererResourceLoader.getSprite(entityToRender), 32, 32,
                            6, 75, 0, AnimationType.MOVE));
                }
            }
            // Enemy standing, render standing image
            else {
                enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                        rendererResourceLoader.getSprite(currentEnemy.getEntityListName()), AnimationType.STAND));
            }

            // Render animation - Animation now in playerOnMap map so just render in appropriate location
            renderAnimationSpriteOnMap(enemiesOnMapAnimations, currentEnemy.getID(), currentEnemy.getPose());

            // Render healthbar
            renderHealthBar(currentEnemy.getPose(), currentEnemy.getHealth(), currentEnemy.getMaxHealth());

            // Put enemy into enemy locations hashmap
            lastEnemyLocations.put(currentEnemy.getID(), currentEnemy.getPose());
        }

        // Render projectiles
        for (ProjectileView currentProjectile : gameView.getProjectiles()) {
            renderEntityView(currentProjectile, rendererResourceLoader);
        }

        // Render enemy and player death animations
        renderEntityDeaths(EntityDeathAnimation.ENEMY, gameView, rendererResourceLoader);
        renderEntityDeaths(EntityDeathAnimation.PLAYER, gameView, rendererResourceLoader);
    }

    /**
     * Check which entities have died relative to the previous gameview object and display their death animations
     *
     * @param entityType             EntityType to check deaths for
     * @param gameView               GameView to get data from
     * @param rendererResourceLoader Renderer resources
     */
    private void renderEntityDeaths(EntityDeathAnimation entityType, GameView gameView,
                                    RendererResourceLoader rendererResourceLoader) {
        // Make a hashmap of all entities  on map to ease calculations and find dead entities
        Map<Integer, Pose> gameViewEntityPoses = new HashMap<>();
        HashMap<Integer, Pose> deadEntities = new HashMap<>();
        switch (entityType) {
            case PLAYER:
                for (PlayerView playerView : gameView.getPlayers()) {
                    gameViewEntityPoses.put(playerView.getID(), playerView.getPose());
                }

                deadEntities = (HashMap<Integer, Pose>) mapDifference(lastPlayerLocations, gameViewEntityPoses);
                break;
            case ENEMY:
                for (EnemyView enemyView : gameView.getEnemies()) {
                    gameViewEntityPoses.put(enemyView.getID(), enemyView.getPose());
                }

                deadEntities = (HashMap<Integer, Pose>) mapDifference(lastEnemyLocations, gameViewEntityPoses);
                break;
        }

        // Go through lit of dead enemies
        for (Map.Entry<Integer, Pose> entry : deadEntities.entrySet()) {
            // Set up an animationtimer with a one-off animation for every enemy
            new AnimationTimer() {
                Pose pose = entry.getValue();
                int frameCount = 32;
                AnimatedSprite deathSpriteManager = new AnimatedSprite(
                        rendererResourceLoader.getSprite(EntityList.BLOOD_EXPLOSION), 32, 32,
                        frameCount, 25, 1, AnimationType.NONE);

                @Override
                public void handle(long now) {
                    // Check if animation still running - if not, stop animation
                    if (deathSpriteManager.getCurrentFrame() < frameCount - 1) {
                        drawRotatedImageFromSpritesheet(deathSpriteManager.getImage(), 0, pose.getX(),
                                pose.getY(), deathSpriteManager.getXOffset(), deathSpriteManager.getYOffset(),
                                deathSpriteManager.getIndividualImageWidth(), deathSpriteManager.getIndividualImageHeight());
                    } else {
                        this.stop();
                    }
                }
            }.start();

            // Remove entry from last locations so it isn't played again
            switch (entityType) {
                case PLAYER:
                    lastPlayerLocations.remove(entry.getKey());
                    break;
                case ENEMY:
                    lastEnemyLocations.remove(entry.getKey());
                    break;
            }
        }
    }

    /**
     * Find difference between two maps
     *
     * @param map1 Map whose extras will be returned
     * @param map2 Map to compare to the first map
     * @param <K>  Type of keys in input maps
     * @param <V>  Type of values in input maps
     * @return Map containing the differences between the two maps
     */
    private <K, V> Map<K, V> mapDifference(Map<? extends K, ? extends V> map1, Map<? extends K, ? extends V> map2) {
        Map<K, V> difference = new HashMap<>();
        difference.putAll(map1);
        difference.putAll(map2);
        difference.entrySet().removeAll(map2.entrySet());
        return difference;
    }

    /**
     * Render image according to info from its animation
     *
     * @param entitiesOnMap Entities currently on the map
     * @param id            ID of animation
     * @param pose          Pose of animation to get location to render to from
     */
    private void renderAnimationSpriteOnMap(Map<Integer, AnimatedSprite> entitiesOnMap, int id, Pose pose) {
        AnimatedSprite thisSpriteManager = entitiesOnMap.get(id);

        drawRotatedImageFromSpritesheet(thisSpriteManager.getImage(), pose.getDirection(), pose.getX(),
                pose.getY(), thisSpriteManager.getXOffset(), thisSpriteManager.getYOffset(),
                thisSpriteManager.getIndividualImageWidth(), thisSpriteManager.getIndividualImageHeight());
    }

    /**
     * Render healthbar above entity
     *
     * @param pose          Pose of animation to get location to render to from
     * @param currentHealth Current health of the entity
     * @param maxHealth     Max health of the entity
     */
    private void renderHealthBar(Pose pose, int currentHealth, int maxHealth) {
        // Variables for calculations
        int healthBarHeight = 5;
        int verticalOffset = 12;
        double healthLeftPercentage = (double) currentHealth / (double) maxHealth;

        // Render current health portion
        mapGC.setFill(Color.LIME);
        mapGC.fillRect(pose.getX(), pose.getY() - verticalOffset,
                Constants.TILE_SIZE * healthLeftPercentage, healthBarHeight);

        // Render lost health portion
        mapGC.setFill(Color.RED);
        mapGC.fillRect(pose.getX() + Constants.TILE_SIZE * healthLeftPercentage, pose.getY() - verticalOffset,
                Constants.TILE_SIZE * (1 - healthLeftPercentage), healthBarHeight);
    }

    /**
     * Render entity from its EntityView object
     *
     * @param entityView             EntityView to render
     * @param rendererResourceLoader Renderer resources
     */
    private void renderEntityView(EntityView entityView, RendererResourceLoader rendererResourceLoader) {
        // Get image from loaded sprites
        Image imageToRender = rendererResourceLoader.getSprite(entityView.getEntityListName());

        // Render image
        renderEntity(entityView, imageToRender);
    }

    /**
     * Render entity to map
     *
     * @param entityView Entity to render
     * @param image      Image to render
     */
    private void renderEntity(EntityView entityView, Image image) {
        // If entity's sizeScaleFactor isn't zero, enlarge the graphic
        if (entityView.getSizeScaleFactor() != 1) {
            image = resampleImage(image, entityView.getSizeScaleFactor());
        }

        // Render entity to specified location on canvas
        drawRotatedImage(image, entityView.getPose().getDirection(), entityView.getPose().getX(),
                entityView.getPose().getY());
    }

    /**
     * Draw rotated image on canvas
     *
     * @param image Image to draw
     * @param angle Angle to draw image at
     * @param x     X coordinate to render image to
     * @param y     Y coordinate to render image to
     */
    private void drawRotatedImage(Image image, double angle, double x, double y) {
        mapGC.save(); // Saves the current state on stack, including the current transform for later
        rotate(angle, x + image.getWidth() / 2, y + image.getHeight() / 2);
        mapGC.drawImage(image, x, y);
        mapGC.restore(); // Back to original state (before rotation)
    }

    /**
     * Draw rotated image with spritesheet support - can specify which bit of image to render
     *
     * @param image   Image to render
     * @param angle   Angle to render image at
     * @param x       X coordinate to render image at
     * @param y       Y coordinate to render image at
     * @param xOffset X offset on spritesheet to render image from
     * @param yOffset Y offset on spritesheet to render image from
     * @param width   Width of image to render
     * @param height  Height of image to render
     */
    private void drawRotatedImageFromSpritesheet(Image image, double angle, double x, double y, double xOffset,
                                                 double yOffset, double width, double height) {
        mapGC.save(); // Saves the current state on stack, including the current transform for later
        rotate(angle, x + width / 2, y + height / 2);
        mapGC.drawImage(image, xOffset, yOffset, width, height, x, y, width, height);
        mapGC.restore(); // Back to original state (before rotation)
    }

    /**
     * Set transform for the GraphicsContext to rotate around a pivot point.
     *
     * @param angle            Angle to rotate GraphicsContext by
     * @param xPivotCoordinate Coordinate of rotation x pivot
     * @param yPivotCoordinate Coordinate of rotation y pivot
     */
    private void rotate(double angle, double xPivotCoordinate, double yPivotCoordinate) {
        Rotate r = new Rotate(angle, xPivotCoordinate, yPivotCoordinate);
        mapGC.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    /**
     * Scale image by integer value through resampling - useful for large enemies/powerups
     *
     * @param inputImage  Image to enlarge
     * @param scaleFactor Scalefactor to enlarge image by
     * @return Enlarged image
     */
    private Image resampleImage(Image inputImage, int scaleFactor) {
        final int inputImageWidth = (int) inputImage.getWidth();
        final int inputImageHeight = (int) inputImage.getHeight();

        // Set up output image
        WritableImage outputImage = new WritableImage(inputImageWidth * scaleFactor,
                inputImageHeight * scaleFactor);

        // Set up pixel reader and writer
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        // Resample image
        for (int y = 0; y < inputImageHeight; y++) {
            for (int x = 0; x < inputImageWidth; x++) {
                final int argb = reader.getArgb(x, y);
                for (int dy = 0; dy < scaleFactor; dy++) {
                    for (int dx = 0; dx < scaleFactor; dx++) {
                        writer.setArgb(x * scaleFactor + dx, y * scaleFactor + dy, argb);
                    }
                }
            }
        }
        return outputImage;
    }

    /**
     * Create a WritableImage object from a given colour
     *
     * @param color Color to make image from
     * @return Image made from input colour
     */
    private Image createImageFromColor(Color color) {
        WritableImage image = new WritableImage(1, 1);
        image.getPixelWriter().setColor(0, 0, color);
        return image;
    }

    /**
     * Enum containing entities that have death animations for the MapCanvas class
     */
    enum EntityDeathAnimation {
        PLAYER, ENEMY
    }
}
