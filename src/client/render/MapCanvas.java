package client.render;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.EntityList;
import shared.lists.EntityStatus;
import shared.lists.ItemType;
import shared.view.*;
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
     * Entity locations in last gameview object - used for spawn animation
     */
    private Map<Integer, Pose> lastEntityLocations;

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
        lastEntityLocations = new HashMap<>();
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

        // Iterate through the map, rending each tileList on canvas
        for (int x = 0; x < mapX; x++) {
            for (int y = 0; y < mapY; y++) {
                // Get tileList graphic
                Image tileImage = rendererResourceLoader.getSprite(gameView.getTileMap()[x][y].getTileList().getEntityListName());

                // Add tileList to canvas
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
        // Render enemy and player spawn animations
        renderEntitySpawns(gameView, rendererResourceLoader);

        // Render items
        for (ItemDropView currentItem : gameView.getItemDrops()) {
            // Make item flicker when timer on it is expiring
            if (currentItem.getTimeLeft() < 6000 && currentItem.getTimeLeft() >= 3500) {
                if (currentItem.getTimeLeft() % 400 < 200) {
                    renderEntityView(currentItem, rendererResourceLoader);
                }
            } else if (currentItem.getTimeLeft() < 3500 && currentItem.getTimeLeft() >= 1500) {
                if (currentItem.getTimeLeft() % 250 < 125) {
                    renderEntityView(currentItem, rendererResourceLoader);
                }
            } else if (currentItem.getTimeLeft() < 1500 && currentItem.getTimeLeft() >= 0) {
                if (currentItem.getTimeLeft() % 100 < 50) {
                    renderEntityView(currentItem, rendererResourceLoader);
                }
            } else {
                renderEntityView(currentItem, rendererResourceLoader);
            }
        }

        // Render players
        for (PlayerView currentPlayer : gameView.getPlayers()) {
            // Check if dead and if do, render death animation
            if (currentPlayer.getStatus() == EntityStatus.DEAD) {
                renderDeathAnimation(rendererResourceLoader, currentPlayer.getPose());
            }

            renderPlayerView(playerID, rendererResourceLoader, currentPlayer);
        }

        // Render enemies
        for (EnemyView currentEnemy : gameView.getEnemies()) {
            // Check if dead and if do, render death animation
            if (currentEnemy.getStatus() == EntityStatus.DEAD) {
                renderDeathAnimation(rendererResourceLoader, currentEnemy.getPose());
            }

            renderEnemyView(rendererResourceLoader, currentEnemy);
        }

        // Render projectiles
        for (ProjectileView currentProjectile : gameView.getProjectiles()) {
            renderEntityView(currentProjectile, rendererResourceLoader);
        }

        // Render lasers
        for (LaserView currentLaser : gameView.getLasers()) {
            renderLaser(currentLaser);
        }

        // Render explosions
        for (ExplosionView currentExplosion : gameView.getExplosions()) {
            renderExplosion(currentExplosion, rendererResourceLoader);
        }
    }

    /**
     * Render explosion on canvas
     *
     * @param explosionView          Explosion to render
     * @param rendererResourceLoader Renderer resources
     */
    private void renderExplosion(ExplosionView explosionView, RendererResourceLoader rendererResourceLoader) {
        // Calculate where to render explosion - center it at the specified location
        Pose poseToRenderAt = new Pose(explosionView.getLocation().getX() - (double) explosionView.getSize() / 2,
                explosionView.getLocation().getY() - (double) explosionView.getSize() / 2);

        // Resize explosion spritesheet to render
        int explosionScaleFactor = explosionView.getSize() / Constants.TILE_SIZE;
        Image animationToRender = resampleImage(rendererResourceLoader.getSprite(EntityList.EXPLOSION), explosionScaleFactor);

        // Start thread for rendering the animation
        new AnimationTimer() {
            int frameCount = 40;
            AnimatedSprite deathAnimation = new AnimatedSprite(
                    animationToRender, (int) animationToRender.getHeight(), (int) animationToRender.getHeight(),
                    frameCount, 20, 1, AnimationType.NONE);

            @Override
            public void handle(long now) {
                // Check if animation still running - if not, stop animation
                if (renderOneOffAnimation(deathAnimation, poseToRenderAt, frameCount)) {
                    this.stop();
                }
            }
        }.start();
    }

    /**
     * Render laser to canvas
     *
     * @param laserView Laser to render
     */
    private void renderLaser(LaserView laserView) {
        // Save state of canvas
        mapGC.save();

        // Set laser and shadow colour
        DropShadow dropShadow;
        switch (laserView.getTeam()) {
            case RED:
                mapGC.setStroke(Constants.RED_TEAM_COLOR);
                dropShadow = new DropShadow(10, Constants.RED_TEAM_COLOR);
                break;
            case BLUE:
                mapGC.setStroke(Constants.BLUE_TEAM_COLOR);
                dropShadow = new DropShadow(10, Constants.BLUE_TEAM_COLOR);
                break;
            case GREEN:
                mapGC.setStroke(Constants.GREEN_TEAM_COLOR);
                dropShadow = new DropShadow(10, Constants.GREEN_TEAM_COLOR);
                break;
            case YELLOW:
                mapGC.setStroke(Constants.YELLOW_TEAM_COLOR);
                dropShadow = new DropShadow(10, Constants.YELLOW_TEAM_COLOR);
                break;
            default:
                mapGC.setStroke(Color.GREY);
                dropShadow = new DropShadow(10, Color.GREY);
                break;
        }

        // Add bloom adn set effect
        dropShadow.setInput(new Bloom(0));
        mapGC.setEffect(dropShadow);

        // Set laser width and render on canvas
        mapGC.setLineWidth(laserView.getWidth());
        mapGC.strokeLine(laserView.getStart().getX(), laserView.getStart().getY(),
                laserView.getEnd().getX(), laserView.getEnd().getY());

        // Restore canvas
        mapGC.restore();
    }

    /**
     * Method for rendering death animation at a certain position
     *
     * @param rendererResourceLoader Renderer resources
     * @param pose                   Pose to render animation at
     */
    private void renderDeathAnimation(RendererResourceLoader rendererResourceLoader, Pose pose) {
        // Set up animationtimer that will handle the animation
        new AnimationTimer() {
            int frameCount = 32;
            AnimatedSprite deathAnimation = new AnimatedSprite(
                    rendererResourceLoader.getSprite(EntityList.BLOOD_EXPLOSION), 32, 32,
                    frameCount, 25, 1, AnimationType.NONE);

            @Override
            public void handle(long now) {
                // Check if animation still running - if not, stop animation
                if (renderOneOffAnimation(deathAnimation, pose, frameCount)) {
                    this.stop();
                }
            }
        }.start();
    }

    /**
     * Render enemy off of enemyview + healthbar
     *
     * @param rendererResourceLoader Renderer resources
     * @param currentEnemy           Enemy to be rendered
     */
    private void renderEnemyView(RendererResourceLoader rendererResourceLoader, EnemyView currentEnemy) {
        // Update animation hashmap to track entity
        if (!enemiesOnMapAnimations.containsKey(currentEnemy.getID())) {
            enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite());
        }

        // Check if enemy moving and is so, choose right animation
        if (currentEnemy.isMoving()) {
            // Check if already in the hashmap for enemies on the map
            if (enemiesOnMapAnimations.get(currentEnemy.getID()).getAnimationType() != AnimationType.MOVE) {
                // Add appropriate animation to animation hashmap
                switch (currentEnemy.getEntityListName()) {
                    case RUNNER:
                        enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                                rendererResourceLoader.getSprite(EntityList.RUNNER_WALK), 32, 32,
                                17, 25, 0, AnimationType.MOVE));
                        break;
                    case SOLDIER:
                        enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                                rendererResourceLoader.getSprite(EntityList.SOLDIER_WALK), 34, 34,
                                8, 90, 0, AnimationType.MOVE));
                        break;
                    case MIDGET:
                        enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                                rendererResourceLoader.getSprite(EntityList.MIDGET_WALK), 24, 24,
                                8, 30, 0, AnimationType.MOVE));
                        break;
                    case BOOMER:
                        enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                                rendererResourceLoader.getSprite(EntityList.BOOMER_WALK), 40, 40,
                                8, 70, 0, AnimationType.MOVE));
                        break;
                    case MACHINE_GUNNER:
                        enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                                rendererResourceLoader.getSprite(EntityList.MACHINE_GUNNER_WALK), 34, 34,
                                8, 35, 0, AnimationType.MOVE));
                        break;
                    case SNIPER:
                        enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                                rendererResourceLoader.getSprite(EntityList.SNIPER_WALK), 32, 32,
                                4, 50, 0, AnimationType.MOVE));
                        break;
                    case MAGE:
                        enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                                rendererResourceLoader.getSprite(EntityList.MAGE_WALK), 38, 38,
                                8, 90, 0, AnimationType.MOVE));
                        break;
                    case THEBOSS:
                        enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                                rendererResourceLoader.getSprite(EntityList.THEBOSS_WALK), 160, 160,
                                8, 85, 0, AnimationType.MOVE));
                        break;
                    case ZOMBIE:
                    default:
                        enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                                rendererResourceLoader.getSprite(EntityList.ZOMBIE_WALK), 32, 32,
                                6, 70, 0, AnimationType.MOVE));
                        break;
                }
            }
        } else {
            // Enemy standing, render standing image
            enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSprite(
                    rendererResourceLoader.getSprite(currentEnemy.getEntityListName()), AnimationType.STAND));
        }

        // Render animation - Animation now in playerOnMap map so just render in appropriate location
        renderAnimationSpriteOnMap(enemiesOnMapAnimations, currentEnemy.getID(), currentEnemy.getPose());

        // Render healthbar
        renderHealthBar(currentEnemy.getPose(), currentEnemy.getHealth(), currentEnemy.getMaxHealth(),
                currentEnemy.getEntityListName().getSize());

        // Render status effect
        renderStatusEffect(currentEnemy.getPose(), currentEnemy.getEntityListName().getSize(),
                currentEnemy.getStatus(), rendererResourceLoader);

        // Put enemy into enemy locations hashmap
        lastEntityLocations.put(currentEnemy.getID(), currentEnemy.getPose());
    }

    /**
     * Render player off of PlayerView
     *
     * @param playerID               ID of player to render
     * @param rendererResourceLoader Renderer resources
     * @param currentPlayer          PlayerView to render
     */
    private void renderPlayerView(int playerID, RendererResourceLoader rendererResourceLoader, PlayerView currentPlayer) {
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
                        5, ((GunView) currentPlayer.getCurrentItem()).getReloadTime() / 5,
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
                if (iv.getItemType() == ItemType.GUN) {
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
        renderHealthBar(currentPlayer.getPose(), currentPlayer.getHealth(), currentPlayer.getMaxHealth(),
                Constants.TILE_SIZE);

        // Render status effect
        renderStatusEffect(currentPlayer.getPose(), currentPlayer.getEntityListName().getSize(),
                currentPlayer.getStatus(), rendererResourceLoader);

        // Put player into player locations hashmap
        lastEntityLocations.put(currentPlayer.getID(), currentPlayer.getPose());
    }

    /**
     * Render entity spawn animations
     *
     * @param gameView               GameView storing data
     * @param rendererResourceLoader Renderer resource loader
     */
    private void renderEntitySpawns(GameView gameView, RendererResourceLoader rendererResourceLoader) {
        // Make a hashmap of all entities on map to ease calculations and find dead entities
        Map<Integer, Pose> gameViewEntityPoses = new HashMap<>();
        for (PlayerView playerView : gameView.getPlayers()) {
            gameViewEntityPoses.put(playerView.getID(), playerView.getPose());
        }

        for (EnemyView enemyView : gameView.getEnemies()) {
            gameViewEntityPoses.put(enemyView.getID(), enemyView.getPose());
        }

        // Get the new entities
        Map<Integer, Pose> newEntities = mapDifference(gameViewEntityPoses, lastEntityLocations);

        // Go through list of new entities
        for (Map.Entry<Integer, Pose> entry : newEntities.entrySet()) {
            // Set up an animationtimer with a one-off animation for every entity
            new AnimationTimer() {
                Pose pose = entry.getValue();
                int frameCount = 32;
                AnimatedSprite spawnAnimation = new AnimatedSprite(
                        rendererResourceLoader.getSprite(EntityList.WHITE_SMOKE_CLOUD), 32, 32,
                        frameCount, 25, 1, AnimationType.NONE);

                @Override
                public void handle(long now) {
                    // Check if animation still running - if not, stop animation
                    if (renderOneOffAnimation(spawnAnimation, pose, frameCount)) {
                        this.stop();
                    }
                }
            }.start();
        }
    }

    /**
     * See if one off animation should still run, in which case update it, otherwise return true
     *
     * @param animation  Running animation to check
     * @param pose       Pose to render animation at
     * @param frameCount Total amount of frames for animation
     * @return True if animation finished
     */
    private boolean renderOneOffAnimation(AnimatedSprite animation, Pose pose, int frameCount) {
        // Check if animation finished
        if (animation.getCurrentFrame() < frameCount - 1) {
            drawRotatedImageFromSpritesheet(animation.getImage(), 0, pose.getX(),
                    pose.getY(), animation.getXOffset(), animation.getYOffset(),
                    animation.getIndividualImageWidth(), animation.getIndividualImageHeight());

            // Animation ongoing, return false
            return false;
        }

        // Animation ended, return true
        return true;
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
     * @param enemySize     Size of enemy
     */
    private void renderHealthBar(Pose pose, int currentHealth, int maxHealth, int enemySize) {
        // Variables for calculations
        int healthBarHeight = 5;
        int verticalOffset = 16;
        double healthLeftPercentage = (double) currentHealth / (double) maxHealth;

        // Render current health portion
        mapGC.setFill(Color.LIME);
        mapGC.fillRect(pose.getX(), pose.getY() - verticalOffset,
                enemySize * healthLeftPercentage, healthBarHeight);

        // Render lost health portion
        mapGC.setFill(Color.RED);
        mapGC.fillRect(pose.getX() + enemySize * healthLeftPercentage, pose.getY() - verticalOffset,
                enemySize * (1 - healthLeftPercentage), healthBarHeight);
    }

    /**
     * Render status effect above enemu
     *
     * @param pose                   Pose to render at
     * @param entitySize             Size of enemy to center effect
     * @param effect                 Effect to render
     * @param rendererResourceLoader Renderer resources
     */
    private void renderStatusEffect(Pose pose, int entitySize, EntityStatus effect, RendererResourceLoader rendererResourceLoader) {
        // Get the image to render
        Image imageToRender;
        switch (effect) {
            case SLOWED:
                imageToRender = rendererResourceLoader.getSprite(EntityList.SLOWED);
                break;
            case FROZEN:
                imageToRender = rendererResourceLoader.getSprite(EntityList.BURNING);
                break;
            case BURNING:
                imageToRender = rendererResourceLoader.getSprite(EntityList.SLOWED);
                break;
            case FUSED:
                imageToRender = rendererResourceLoader.getSprite(EntityList.FUSED);
                break;
            default:
                // No status effect needed, return
                return;
        }

        // How far above the entity to render the status effect
        int verticalDebuffOffset = 54;

        // Draw debuff image centered above the entity
        mapGC.drawImage(imageToRender, pose.getX() + (double) (entitySize - EntityList.BURNING.getSize()) / 2,
                pose.getY() - verticalDebuffOffset);
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
        // Check if scale factor is 1, in which case don't resize
        if (scaleFactor == 1) {
            return inputImage;
        }

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
}
