package shared.lists;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;

// List of all entities as well as renderable objects

// For coloradjust: hue, saturation, brightness, contrast. All between -1 and 1
public enum EntityList {
    // Item
    PISTOL("file:assets/img/entity/item/pistol.png"),
    SHOTGUN("file:assets/img/entity/item/shotgun.png"),

    // Player
    PLAYER("file:assets/img/entity/player/player.png"),
    PLAYER_RED("file:assets/img/entity/player/player.png", new ColorAdjust(-0.3, 0.25, 0, -0.1)),
    PLAYER_BLUE("file:assets/img/entity/player/player.png", new ColorAdjust(-0.75, 0.25, 0, -0.1)),
    PLAYER_GREEN("file:assets/img/entity/player/player.png", new ColorAdjust(0.5, 0.25, 0, -0.1)),
    PLAYER_YELLOW("file:assets/img/entity/player/player.png", new ColorAdjust(0.15, 0.25, 0, -0.1)),


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
    PLAYER_WALK("file:assets/img/entity/player/player_walk.png");

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
