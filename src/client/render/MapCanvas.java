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

public class MapCanvas extends Canvas {
    // Graphics context for canvas
    private GraphicsContext mapGC;

    // Animation hashmaps
    private Map<Integer, AnimatedSpriteManager> playersOnMapAnimations;
    private Map<Integer, AnimatedSpriteManager> enemiesOnMapAnimations;

    // Last location of players and enemies hashmaps - used for spawning and death animations
    private Map<Integer, Pose> lastPlayerLocations;
    private Map<Integer, Pose> lastEnemyLocations;

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

    // Render map from tiles
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

    // Render entities to the map canvas
    public void renderEntitiesFromGameViewToCanvas(GameView gameView, int playerID, RendererResourceLoader rendererResourceLoader) {
        // Render items
        for (ItemDropView currentItem : gameView.getItemDrops()) {
            renderEntityView(currentItem, rendererResourceLoader);
        }

        // Render players
        for (PlayerView currentPlayer : gameView.getPlayers()) {
            // Update animation hashmap to track entity
            if (!playersOnMapAnimations.containsKey(currentPlayer.getID())) {
                playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager());
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
                    playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
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
                    playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
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
                playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
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
                playersOnMapAnimations.put(currentPlayer.getID(), new AnimatedSpriteManager(
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
                enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager());
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
                    enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager(
                            rendererResourceLoader.getSprite(entityToRender), 32, 32,
                            6, 75, 0, AnimationType.MOVE));
                }
            }
            // Enemy standing, render standing image
            else {
                enemiesOnMapAnimations.put(currentEnemy.getID(), new AnimatedSpriteManager(
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
        renderEntityDeaths(GameRenderer.EntityDeathAnimation.ENEMY, gameView, rendererResourceLoader);
        renderEntityDeaths(GameRenderer.EntityDeathAnimation.PLAYER, gameView, rendererResourceLoader);
    }

    // Check which entities have died and display death animation
    private void renderEntityDeaths(GameRenderer.EntityDeathAnimation entityType, GameView gameView,
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
                AnimatedSpriteManager deathSpriteManager = new AnimatedSpriteManager(
                        rendererResourceLoader.getSprite(EntityList.BLOOD_EXPLOSION), 32, 32,
                        frameCount, 25, 1, AnimationType.NONE);

                @Override
                public void handle(long now) {
                    // Check if animation still running - if not, stop animation
                    if (deathSpriteManager.getCurrentFrame() < frameCount - 1) {
                        drawRotatedImageFromSpritesheet(deathSpriteManager.getImage(), 0, pose.getX(),
                                pose.getY(), deathSpriteManager.getSx(), deathSpriteManager.getSy(),
                                deathSpriteManager.getImageWidth(), deathSpriteManager.getImageHeight());
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

    // Find difference between two maps - first argument is the hashmap whose extras will be returned
    private <K, V> Map<K, V> mapDifference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
        Map<K, V> difference = new HashMap<>();
        difference.putAll(left);
        difference.putAll(right);
        difference.entrySet().removeAll(right.entrySet());
        return difference;
    }

    // Render image according to info from its animation
    private void renderAnimationSpriteOnMap(Map<Integer, AnimatedSpriteManager> entitiesOnMap, int id, Pose pose) {
        AnimatedSpriteManager thisSpriteManager = entitiesOnMap.get(id);

        drawRotatedImageFromSpritesheet(thisSpriteManager.getImage(), pose.getDirection(), pose.getX(),
                pose.getY(), thisSpriteManager.getSx(), thisSpriteManager.getSy(),
                thisSpriteManager.getImageWidth(), thisSpriteManager.getImageHeight());
    }

    // Render healthbar above entity
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

    private void renderEntityView(EntityView entityView, RendererResourceLoader rendererResourceLoader) {
        // Get image from loaded sprites
        Image imageToRender = rendererResourceLoader.getSprite(entityView.getEntityListName());

        // Render image
        renderEntity(entityView, imageToRender);
    }

    // Render entity onto the map canvas
    private void renderEntity(EntityView entity, Image image) {
        // If entity's sizeScaleFactor isn't zero, enlarge the graphic
        if (entity.getSizeScaleFactor() != 1) {
            image = resampleImage(image, entity.getSizeScaleFactor());
        }

        // Render entity to specified location on canvas
        drawRotatedImage(image, entity.getPose().getDirection(), entity.getPose().getX(),
                entity.getPose().getY());
    }

    // Draw rotated image
    private void drawRotatedImage(Image image, double angle, double tlpx, double tlpy) {
        mapGC.save(); // Saves the current state on stack, including the current transform for later
        rotate(angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
        mapGC.drawImage(image, tlpx, tlpy);
        mapGC.restore(); // Back to original state (before rotation)
    }

    // Draw rotated image with spritesheet support - can specify which bit of image to render
    private void drawRotatedImageFromSpritesheet(Image image, double angle, double tlpx,
                                                 double tlpy, double sx, double sy, double sw, double sh) {
        mapGC.save(); // Saves the current state on stack, including the current transform for later
        rotate(angle, tlpx + sw / 2, tlpy + sh / 2);
        mapGC.drawImage(image, sx, sy, sw, sh, tlpx, tlpy, sw, sh);
        mapGC.restore(); // Back to original state (before rotation)
    }

    // Set transform for the GraphicsContext to rotate around a pivot point.
    private void rotate(double angle, double xPivotCoordinate, double yPivotCoordinate) {
        Rotate r = new Rotate(angle, xPivotCoordinate, yPivotCoordinate);
        mapGC.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    // Scale image by integer value through resampling - useful for large enemies/powerups
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
}
