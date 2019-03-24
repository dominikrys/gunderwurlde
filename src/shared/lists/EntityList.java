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
    SNIPER_RIFLE("file:assets/img/entity/item/sniper_rifle.png", Constants.TILE_SIZE),
    PLASMA_PISTOL("file:assets/img/entity/item/plasma_pistol.png", Constants.TILE_SIZE),
    MACHINE_GUN("file:assets/img/entity/item/machine_gun.png", Constants.TILE_SIZE),
    ASSAULT_RIFLE("file:assets/img/entity/item/assault_rifle.png", Constants.TILE_SIZE),
    CRYSTAL_LAUNCHER("file:assets/img/entity/item/crystal_launcher.png", Constants.TILE_SIZE),
    FIRE_GUN("file:assets/img/entity/item/fire_gun.png", Constants.TILE_SIZE),
    ICE_GUN("file:assets/img/entity/item/pistol.png", Constants.TILE_SIZE),
    RING_OF_DEATH("file:assets/img/entity/item/ring_of_death.png", Constants.TILE_SIZE),
    HEAVY_PISTOL("file:assets/img/entity/item/heavy_pistol.png", Constants.TILE_SIZE),
    BUCKSHOT_SHOTGUN("file:assets/img/entity/item/sawedoff.png", Constants.TILE_SIZE),
    ROCKET_LAUNCHER("file:assets/img/entity/item/rpg.png", Constants.TILE_SIZE),

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
    SNIPER("file:assets/img/entity/enemy/zombie.png", Constants.machineGunnerColorAdjust, Constants.TILE_SIZE),
    THEBOSS ("file:assets/img/entity/enemy/zombie.png", Constants.machineGunnerColorAdjust, Constants.TILE_SIZE * 5),

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
    BASIC_BULLET("file:assets/img/entity/projectile/basic_bullet.png", 6),
    HEAVY_BULLET("file:assets/img/entity/projectile/heavy_bullet.png", 8),
    PLASMA_BULLET("file:assets/img/entity/projectile/plasma_bullet.png", 6),
    FIRE_BULLET("file:assets/img/entity/projectile/fire_bullet.png", 6),
    ICE_BULLET("file:assets/img/entity/projectile/ice_bullet.png", 6),
    CRYSTAL_BULLET("file:assets/img/entity/projectile/crystal_bullet.png", 6),
    BOUNCE_BULLET("file:assets/img/entity/projectile/bounce_bullet.png", 6),
    ROCKET("file:assets/img/entity/projectile/rocket.png", 6),
    PHANTOM_BULLET("file:assets/img/entity/projectile/phantom_bullet.png", 6),
    STICKY_BULLET("file:assets/img/entity/projectile/sticky_bullet.png", 6),
    

    /**
     * Tiles
     */
    DEFAULT("file:assets/img/tiles/default.png", Constants.TILE_SIZE),
    GRASS_TILE("file:assets/img/tiles/grass.png", Constants.TILE_SIZE),
    WOOD_TILE("file:assets/img/tiles/wood.png", Constants.TILE_SIZE),
    DOOR_TILE("file:assets/img/tiles/door.png", Constants.TILE_SIZE),
    RUINS_DOOR("file:assets/img/tiles/ruins_door.png", Constants.TILE_SIZE),
    RUINS_DOOR_OPEN("file:assets/img/tiles/ruins_door_open.png", Constants.TILE_SIZE),
    RUINS_FLOOR("file:assets/img/tiles/ruins_floor.png", Constants.TILE_SIZE),
    RUINS_ORNATE_BLOCK("file:assets/img/tiles/ruins_ornate_block.png", Constants.TILE_SIZE),
    RUINS_SOLID_BLOCK_DARK("file:assets/img/tiles/ruins_solid_block_dark.png", Constants.TILE_SIZE),
    RUINS_SOLID_BLOCK_LIGHT("file:assets/img/tiles/ruins_solid_block_light.png", Constants.TILE_SIZE),
    RUINS_WALL_DARK("file:assets/img/tiles/ruins_wall_dark.png", Constants.TILE_SIZE),
    RUINS_WALL_MID("file:assets/img/tiles/ruins_wall_mid.png", Constants.TILE_SIZE),
    RUINS_WALL_LIGHT("file:assets/img/tiles/ruins_wall_light.png", Constants.TILE_SIZE),
    WATER("file:assets/img/tiles/water.png", Constants.TILE_SIZE),
    SAND("file:assets/img/tiles/sand.png", Constants.TILE_SIZE),
    RED_GROUND("file:assets/img/tiles/red_ground.png", Constants.TILE_SIZE),
    MARBLE_FLOOR("file:assets/img/tiles/marble_floor.png", Constants.TILE_SIZE),
    DIRT("file:assets/img/tiles/dirt.png", Constants.TILE_SIZE),

    /**
     * Other renderable entities - some are items
     */
    AMMO_CLIP("file:assets/img/other/ammo_clip.png"),
    ENERGY("file:assets/img/other/energy.png"),
    MAGIC_ESSENCE("file:assets/img/other/magic_essence.png"),
    HEAVY_AMMO("file:assets/img/other/heavy_ammo_clip.png"),
    ROCKET_AMMO("file:assets/img/other/rocket_ammo.png"),
    HEART_FULL("file:assets/img/other/heart_full.png"),
    HEART_HALF("file:assets/img/other/heart_half.png"),
    HEART_LOST("file:assets/img/other/heart_lost.png"),
    CROSSHAIR("file:assets/img/gui/crosshair.png"),
    PAUSE_BG("file:assets/img/gui/pause_bg.png"),

    /**
     * Animations
     */
    SMOKE_CLOUD("file:assets/img/misc_animations/smoke_cloud.png"),
    BLOOD_EXPLOSION("file:assets/img/misc_animations/blood_explosion.png"),
    WHITE_SMOKE_CLOUD("file:assets/img/misc_animations/white_smoke_cloud.png"),
    EXPLOSION("file:assets/img/misc_animations/explosion.png");

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
