package shared.lists;

import javafx.scene.effect.ColorAdjust;
import shared.Constants;

// List of all entities as well as renderable objects

// For coloradjust: hue, saturation, brightness, contrast. All between -1 and 1
public enum EntityList {
    // Item
    PISTOL("file:assets/img/entity/item/pistol.png", Constants.TILE_SIZE),
    SHOTGUN("file:assets/img/entity/item/shotgun.png", Constants.TILE_SIZE),
    SMG("file:assets/img/entity/item/smg.png", Constants.TILE_SIZE),

    // Player
    PLAYER("file:assets/img/entity/player/player.png", Constants.TILE_SIZE),
    PLAYER_RED("file:assets/img/entity/player/player.png", Constants.redPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_BLUE("file:assets/img/entity/player/player.png", Constants.bluePlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_GREEN("file:assets/img/entity/player/player.png", Constants.greenPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_YELLOW("file:assets/img/entity/player/player.png", Constants.yellowPlayerColorAdjust, Constants.TILE_SIZE),

    // Player holding gun
    PLAYER_WITH_GUN("file:assets/img/entity/player/player_gun.png"),
    PLAYER_WITH_GUN_RED("file:assets/img/entity/player/player_gun.png", Constants.redPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_BLUE("file:assets/img/entity/player/player_gun.png", Constants.bluePlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_GREEN("file:assets/img/entity/player/player_gun.png", Constants.greenPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_YELLOW("file:assets/img/entity/player/player_gun.png", Constants.yellowPlayerColorAdjust, Constants.TILE_SIZE),

    // Player holding gun recoil
    PLAYER_WITH_GUN_RECOIL("file:assets/img/entity/player/player_gun_recoil.png", Constants.TILE_SIZE),
    PLAYER_WITH_GUN_RECOIL_RED("file:assets/img/entity/player/player_gun_recoil.png", Constants.redPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_RECOIL_BLUE("file:assets/img/entity/player/player_gun_recoil.png", Constants.bluePlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_RECOIL_GREEN("file:assets/img/entity/player/player_gun_recoil.png", Constants.greenPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_WITH_GUN_RECOIL_YELLOW("file:assets/img/entity/player/player_gun_recoil.png", Constants.yellowPlayerColorAdjust, Constants.TILE_SIZE),

    // Player reload
    PLAYER_RELOAD("file:assets/img/entity/player/player_reload.png", Constants.TILE_SIZE),
    PLAYER_RELOAD_RED("file:assets/img/entity/player/player_reload.png", Constants.redPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_RELOAD_BLUE("file:assets/img/entity/player/player_reload.png", Constants.bluePlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_RELOAD_GREEN("file:assets/img/entity/player/player_reload.png", Constants.greenPlayerColorAdjust, Constants.TILE_SIZE),
    PLAYER_RELOAD_YELLOW("file:assets/img/entity/player/player_reload.png", Constants.yellowPlayerColorAdjust, Constants.TILE_SIZE),

    // Enemy
    ZOMBIE("file:assets/img/entity/enemy/zombie.png", Constants.zombieColorAdjust, Constants.TILE_SIZE),
    RUNNER("file:assets/img/entity/enemy/zombie.png", Constants.TILE_SIZE),
    SOLDIER("file:assets/img/entity/enemy/zombie.png", Constants.soldierColorAdjust, Constants.TILE_SIZE),
    MIDGET("file:assets/img/entity/enemy/zombie.png", Constants.midgetColorAdjust, Constants.TILE_SIZE),

    // Enemy walking
    ZOMBIE_WALK("file:assets/img/entity/enemy/zombie_walk.png", Constants.zombieColorAdjust),
    RUNNER_WALK("file:assets/img/entity/enemy/zombie_walk.png"),
    SOLDIER_WALK("file:assets/img/entity/enemy/zombie_walk.png", Constants.soldierColorAdjust),
    MIDGET_WALK("file:assets/img/entity/enemy/zombie_walk.png", Constants.midgetColorAdjust),

    // Projectile
    BASIC_BULLET("file:assets/img/entity/projectile/basic_bullet.png", 4),

    // Tiles
    GRASS_TILE("file:assets/img/tiles/grass.png", Constants.TILE_SIZE),
    WOOD_TILE("file:assets/img/tiles/wood.png", Constants.TILE_SIZE),
    DEFAULT("file:assets/img/tiles/default.png", Constants.TILE_SIZE),

    // Other/Not real entities - some applicable to Item
    AMMO_CLIP("file:assets/img/other/ammo_clip.png"),
    HEART_FULL("file:assets/img/other/heart_full.png"),
    HEART_HALF("file:assets/img/other/heart_half.png"),
    HEART_LOST("file:assets/img/other/heart_lost.png"),
    CROSSHAIR("file:assets/img/gui/crosshair.png"),

    // Animations TODO: maybe move this out to another enum/class

    // Walk
    PLAYER_WALK("file:assets/img/entity/player/player_walk.png"),
    PLAYER_WALK_RED("file:assets/img/entity/player/player_walk.png", new ColorAdjust(-0.3, 0, 0, 0)),
    PLAYER_WALK_BLUE("file:assets/img/entity/player/player_walk.png", new ColorAdjust(-0.75, 0, 0, 0)),
    PLAYER_WALK_GREEN("file:assets/img/entity/player/player_walk.png", new ColorAdjust(0.5, 0, 0, 0)),
    PLAYER_WALK_YELLOW("file:assets/img/entity/player/player_walk.png", new ColorAdjust(0.15, 0, 0, 0)),

    // Smoke cloud
    SMOKE_CLOUD("file:assets/img/misc_animations/smoke.png");

    private final String spritePath;
    private final ColorAdjust colorAdjust;
    private final int size;

    EntityList(String spritePath, ColorAdjust colorAdjust, int size) {
        this.spritePath = spritePath;
        this.colorAdjust = colorAdjust;
        this.size = size;
    }

    EntityList(String spritePath) {
        this(spritePath, null, 0);
    }

    EntityList(String spritePath, int size) {
        this(spritePath, null, size);
    }

    EntityList(String spritePath, ColorAdjust colorAdjust) {
        this (spritePath, colorAdjust, 0);
    }

    public String getPath() {
        return spritePath;
    }

    public ColorAdjust getColorAdjust() {
        return colorAdjust;
    }

    public int getSize() {
        return size;
    }
}
