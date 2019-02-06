package client;

public enum Sprites {
    // Menu items
    SOUND_OFF("assets/img/gui/sound_off"),
    SOUND_ON("assets/img/gui/sound_on"),

    // Map thumbnails
    MEADOW("assets/img/gui/meadow.png"),

    // Items
    PISTOL("assets/img/items/pistol.png"),

    // Mobs
    PLAYER("assets/img/mobs/player.png"),
    ZOMBIE("assets/img/mobs/zombie.png"),

    // Projectiles
    BASIC_BULLET("assets/img/projectiles/basic_bullet.png"),

    // Tiles
    GRASS_TILE("assets/img/tiles/grass.png"),
    WOOD_TILE("assets/img/tiles/wood.png"),

    // Other
    AMMO_CLIP("assets/img/other/ammo_clip.png"),
    HEART_FULL("assets/img/other/heart_full.png"),
    HEART_HALF("assets/img/other/heart_half.png"),
    HEART_LOST("assets/img/other/heart_lost.png");

    private String spritePath;

    Sprites(String spritePath) {
        this.spritePath = spritePath;
    }

    public String getPath() {
        return spritePath;
    }
}
