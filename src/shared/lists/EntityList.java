package shared.lists;

import javafx.scene.effect.ColorAdjust;

// List of all entities as well as renderable objects

// For coloradjust: hue, saturation, brightness, contrast. All between -1 and 1
public enum EntityList {
    // Item
    PISTOL("file:assets/img/entity/item/pistol.png"),
    SHOTGUN("file:assets/img/entity/item/shotgun.png"),

    // Player
    PLAYER("file:assets/img/entity/player/player.png"),
    PLAYER_RED("file:assets/img/entity/player/player.png", new ColorAdjust(-0.3, 0, 0, 0)),
    PLAYER_BLUE("file:assets/img/entity/player/player.png", new ColorAdjust(-0.75, 0, 0, 0)),
    PLAYER_GREEN("file:assets/img/entity/player/player.png", new ColorAdjust(0.5, 0, 0, 0)),
    PLAYER_YELLOW("file:assets/img/entity/player/player.png", new ColorAdjust(0.15, 0, 0, 0)),

    // Player holding gun
    PLAYER_WITH_GUN("file:assets/img/entity/player/player_gun.png"),
    PLAYER_WITH_GUN_RED("file:assets/img/entity/player/player_gun.png", new ColorAdjust(-0.3, 0, 0, 0)),
    PLAYER_WITH_GUN_BLUE("file:assets/img/entity/player/player_gun.png", new ColorAdjust(-0.75, 0, 0, 0)),
    PLAYER_WITH_GUN_GREEN("file:assets/img/entity/player/player_gun.png", new ColorAdjust(0.5, 0, 0, 0)),
    PLAYER_WITH_GUN_YELLOW("file:assets/img/entity/player/player_gun.png", new ColorAdjust(0.15, 0, 0, 0)),

    // Player holding gun recoil
    PLAYER_WITH_GUN_RECOIL("file:assets/img/entity/player/player_gun_recoil.png"),
    PLAYER_WITH_GUN_RECOIL_RED("file:assets/img/entity/player/player_gun_recoil.png", new ColorAdjust(-0.3, 0, 0, 0)),
    PLAYER_WITH_GUN_RECOIL_BLUE("file:assets/img/entity/player/player_gun_recoil.png", new ColorAdjust(-0.75, 0, 0, 0)),
    PLAYER_WITH_GUN_RECOIL_GREEN("file:assets/img/entity/player/player_gun_recoil.png", new ColorAdjust(0.5, 0, 0, 0)),
    PLAYER_WITH_GUN_RECOIL_YELLOW("file:assets/img/entity/player/player_gun_recoil.png", new ColorAdjust(0.15, 0, 0, 0)),

    // Enemy
    ZOMBIE("file:assets/img/entity/enemy/zombie.png"),
    RUNNER("file:assets/img/entity/enemy/zombie.png"),
    SOLDIER("file:assets/img/entity/enemy/zombie.png"),
    MIDGET("file:assets/img/entity/enemy/zombie.png"),

    // Projectile
    BASIC_BULLET("file:assets/img/entity/projectile/basic_bullet.png"),

    // Tiles
    GRASS_TILE("file:assets/img/tiles/grass.png"),
    WOOD_TILE("file:assets/img/tiles/wood.png"),
    DEFAULT("file:assets/img/tiles/default.png"),

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
    PLAYER_WALK_YELLOW("file:assets/img/entity/player/player_walk.png", new ColorAdjust(0.15, 0, 0, 0));

    private final String spritePath;
    private ColorAdjust colorAdjust;

    EntityList(String spritePath) {
        this.spritePath = spritePath;
        colorAdjust = null;
    }

    EntityList(String spritePath, ColorAdjust colorAdjust) {
        this(spritePath);
        this.colorAdjust = colorAdjust;
    }


    public String getPath() {
        return spritePath;
    }

    public ColorAdjust getColorAdjust() {
        return colorAdjust;
    }
}
