package shared.lists;

import javafx.scene.effect.ColorAdjust;
import shared.Constants;

/**
 * EntityList enum. Holds sprites and sizes of all renderable entities
 * <p>
 * For coloradjust 'hue', 'saturation', 'brightness', 'contrast' all take values between -1 and 1
 */
public enum EntityList {
    /**
     * Items
     */
    PISTOL("file:assets/img/entity/item/pistol.png", Constants.TILE_SIZE),
    SHOTGUN("file:assets/img/entity/item/shotgun.png", Constants.TILE_SIZE),
    SMG("file:assets/img/entity/item/smg.png", Constants.TILE_SIZE),
    SNIPER_RIFLE("file:assets/img/entity/item/smg.png", Constants.TILE_SIZE),

    /**
     * Player standing
     */
    PLAYER("file:assets/img/entity/player/player.png", Constants.TILE_SIZE - 3), // TODO: remove the -3?
    PLAYER_RED("file:assets/img/entity/player/player.png", Constants.redPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_BLUE("file:assets/img/entity/player/player.png", Constants.bluePlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_GREEN("file:assets/img/entity/player/player.png", Constants.greenPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_YELLOW("file:assets/img/entity/player/player.png", Constants.yellowPlayerColorAdjust, Constants.TILE_SIZE),

    /**
     * Player standing with a gun
     */
    PLAYER_WITH_GUN("file:assets/img/entity/player/player_gun.png"),
    PLAYER_WITH_GUN_RED("file:assets/img/entity/player/player_gun.png", Constants.redPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_BLUE("file:assets/img/entity/player/player_gun.png", Constants.bluePlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_GREEN("file:assets/img/entity/player/player_gun.png", Constants.greenPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_YELLOW("file:assets/img/entity/player/player_gun.png", Constants.yellowPlayerColorAdjust, Constants.TILE_SIZE),

    /**
     * Player recoiling due to shooting a gun
     */
    PLAYER_WITH_GUN_RECOIL("file:assets/img/entity/player/player_gun_recoil.png", Constants.TILE_SIZE),
    PLAYER_WITH_GUN_RECOIL_RED("file:assets/img/entity/player/player_gun_recoil.png", Constants.redPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_RECOIL_BLUE("file:assets/img/entity/player/player_gun_recoil.png", Constants.bluePlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_RECOIL_GREEN("file:assets/img/entity/player/player_gun_recoil.png", Constants.greenPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_RECOIL_YELLOW("file:assets/img/entity/player/player_gun_recoil.png", Constants.yellowPlayerColorAdjust, Constants.TILE_SIZE),

    /**
     * Player reloading
     */
    PLAYER_RELOAD("file:assets/img/entity/player/player_reload.png", Constants.TILE_SIZE),
    PLAYER_RELOAD_RED("file:assets/img/entity/player/player_reload.png", Constants.redPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_RELOAD_BLUE("file:assets/img/entity/player/player_reload.png", Constants.bluePlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_RELOAD_GREEN("file:assets/img/entity/player/player_reload.png", Constants.greenPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_RELOAD_YELLOW("file:assets/img/entity/player/player_reload.png", Constants.yellowPlayerColorAdjust, Constants.TILE_SIZE),

    /**
     * Player walking animation
     */
    PLAYER_WALK("file:assets/img/entity/player/player_walk.png"),
    PLAYER_WALK_RED("file:assets/img/entity/player/player_walk.png", new ColorAdjust(-0.3, 0, 0, 0)),
    PLAYER_WALK_BLUE("file:assets/img/entity/player/player_walk.png", new ColorAdjust(-0.75, 0, 0, 0)),
    PLAYER_WALK_GREEN("file:assets/img/entity/player/player_walk.png", new ColorAdjust(0.5, 0, 0, 0)),
    PLAYER_WALK_YELLOW("file:assets/img/entity/player/player_walk.png", new ColorAdjust(0.15, 0, 0, 0)),

    /**
     * Enemy standing
     */
    ZOMBIE("file:assets/img/entity/enemy/zombie.png", Constants.zombieColorAdjust, Constants.TILE_SIZE),
    RUNNER("file:assets/img/entity/enemy/zombie.png", Constants.TILE_SIZE),
    SOLDIER("file:assets/img/entity/enemy/zombie.png", Constants.soldierColorAdjust, Constants.TILE_SIZE),
    MIDGET("file:assets/img/entity/enemy/zombie.png", Constants.midgetColorAdjust, Constants.TILE_SIZE),
    BOOMER("file:assets/img/entity/enemy/zombie.png", Constants.zombieColorAdjust, Constants.TILE_SIZE),
    MACHINE_GUNNER("file:assets/img/entity/enemy/zombie.png", Constants.machineGunnerColorAdjust, Constants.TILE_SIZE),

    /**
     * Enemy walking
     */
    ZOMBIE_WALK("file:assets/img/entity/enemy/zombie_walk.png", Constants.zombieColorAdjust),
    RUNNER_WALK("file:assets/img/entity/enemy/zombie_walk.png"),
    SOLDIER_WALK("file:assets/img/entity/enemy/zombie_walk.png", Constants.soldierColorAdjust),
    MIDGET_WALK("file:assets/img/entity/enemy/zombie_walk.png", Constants.midgetColorAdjust),
    BOOMER_WALK("file:assets/img/entity/enemy/zombie_walk.png", Constants.boomerColorAdjust),
    MACHINE_GUNNER_WALK("file:assets/img/entity/enemy/zombie_walk.png", Constants.machineGunnerColorAdjust),

    /**
     * Projectiles
     */
    BASIC_BULLET("file:assets/img/entity/projectile/basic_bullet.png", 4),

    /**
     * Tiles
     */
    GRASS_TILE("file:assets/img/tiles/grass.png", Constants.TILE_SIZE),
    WOOD_TILE("file:assets/img/tiles/wood.png", Constants.TILE_SIZE),
    DEFAULT("file:assets/img/tiles/default.png", Constants.TILE_SIZE),

    /**
     * Other renderable entities - some are items
     */
    AMMO_CLIP("file:assets/img/other/ammo_clip.png"),
    HEART_FULL("file:assets/img/other/heart_full.png"),
    HEART_HALF("file:assets/img/other/heart_half.png"),
    HEART_LOST("file:assets/img/other/heart_lost.png"),
    CROSSHAIR("file:assets/img/gui/crosshair.png"),

    /**
     * Smoke cloud animation
     */
    SMOKE_CLOUD("file:assets/img/misc_animations/smoke_cloud.png"),

    /**
     * Blood explosion animation
     */
    BLOOD_EXPLOSION("file:assets/img/misc_animations/blood_explosion.png");

    /**
     * Path to sprite
     */
    private final String spritePath;

    /**
     * ColorAdjust to apply to sprite
     */
    private final ColorAdjust colorAdjust;

    /**
     * Size of entity
     */
    private final int size;

    /**
     * Constructor
     *
     * @param spritePath  Sprite path
     * @param colorAdjust Coloradjust
     * @param size        Size of entity
     */
    EntityList(String spritePath, ColorAdjust colorAdjust, int size) {
        this.spritePath = spritePath;
        this.colorAdjust = colorAdjust;
        this.size = size;
    }

    /**
     * Constructor
     *
     * @param spritePath Sprite path
     */
    EntityList(String spritePath) {
        this(spritePath, null, 0);
    }

    /**
     * Constructor
     *
     * @param spritePath Sprite path
     * @param size       Size of entity
     */
    EntityList(String spritePath, int size) {
        this(spritePath, null, size);
    }

    /**
     * Constructor
     *
     * @param spritePath  Sprite path
     * @param colorAdjust ColorAdjust
     */
    EntityList(String spritePath, ColorAdjust colorAdjust) {
        this(spritePath, colorAdjust, 0);
    }

    /**
     * Get sprite path
     *
     * @return Sprite path
     */
    public String getPath() {
        return spritePath;
    }

    /**
     * Get ColorAdjust
     *
     * @return ColorAdjust
     */
    public ColorAdjust getColorAdjust() {
        return colorAdjust;
    }

    /**
     * Get size of entity
     *
     * @return Size of entity
     */
    public int getSize() {
        return size;
    }
}
