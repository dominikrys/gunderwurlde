package shared.lists;

// List of all entities as well as renderable objects
public enum EntityList {
    // Item
    PISTOL("file:assets/img/entity/item/pistol.png"),
    SHOTGUN("file:assets/img/entity/item/shotgun.png"),

    // Player
    PLAYER("file:assets/img/entity/player/player.png"),
    PLAYER_RED("file:assets/img/entity/player/player_red.png"),
    PLAYER_GREEN("file:assets/img/entity/player/player_green.png"),
    PLAYER_YELLOW("file:assets/img/entity/player/player_yellow.png"),
    PLAYER_BLUE("file:assets/img/entity/player/player_blue.png"),

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
    CROSSHAIR("file:assets/img/gui/crosshair.png");

    private final String spritePath;

    EntityList(String spritePath) {
        this.spritePath = spritePath;
    }

    public String getPath() {
        return spritePath;
    }
}
