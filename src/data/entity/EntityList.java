package data.entity;

public enum EntityList {
    // Item
    PISTOL("assets/img/items/pistol.png"),

    // Player
    PLAYER("assets/img/mobs/player_1.png"),
    PLAYER_1("assets/img/mobs/player_1.png"),
    PLAYER_2("assets/img/mobs/player_2.png"),
    PLAYER_3("assets/img/mobs/player_3.png"),
    PLAYER_4("assets/img/mobs/player_4.png"),

    // Enemy
    ZOMBIE("assets/img/mobs/zombie.png"),

    // Projectile
    BASIC_BULLET("assets/img/projectiles/basic_bullet.png"),

    // Tiles
    GRASS_TILE("assets/img/tiles/grass.png"),
    WOOD_TILE("assets/img/tiles/wood.png"),
    DEFAULT("assets/img/tiles/default.png"),

    // Other - some applicable to Item
    AMMO_CLIP("assets/img/other/ammo_clip.png"),
    HEART_FULL("assets/img/other/heart_full.png"),
    HEART_HALF("assets/img/other/heart_half.png"),
    HEART_LOST("assets/img/other/heart_lost.png");

    private String spritePath;

    EntityList(String spritePath) {
        this.spritePath = spritePath;
    }

    public String getPath() {
        return spritePath;
    }
}
