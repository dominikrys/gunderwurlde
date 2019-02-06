package client;

public enum Sprites {
    // Menu items
    SOUND_OFF("assets/img/gui/sound_off", false),
    SOUND_ON("assets/img/gui/sound_on", false),

    // Map thumbnails
    MEADOW("assets/img/gui/meadow.png", false),

    // Items
    PISTOL("assets/img/items/pistol.png", true),

    // Players
    PLAYER("assets/img/mobs/player_1.png", true),
    PLAYER_1("assets/img/mobs/player_1.png", true),
    PLAYER_2("assets/img/mobs/player_2.png", true),
    PLAYER_3("assets/img/mobs/player_3.png", true),
    PLAYER_4("assets/img/mobs/player_4.png", true),


    // Mobs
    ZOMBIE("assets/img/mobs/zombie.png", true),

    // Projectiles
    BASIC_BULLET("assets/img/projectiles/basic_bullet.png", true),

    // Tiles
    GRASS_TILE("assets/img/tiles/grass.png", true),
    WOOD_TILE("assets/img/tiles/wood.png", true),
    DEFAULT("assets/img/tiles/default.png", true),

    // Other
    AMMO_CLIP("assets/img/other/ammo_clip.png", true),
    HEART_FULL("assets/img/other/heart_full.png", true),
    HEART_HALF("assets/img/other/heart_half.png", true),
    HEART_LOST("assets/img/other/heart_lost.png", true);

    private String spritePath;

    // Whether this sprite is used in the game or not (as opposed to menus etc.)
    private boolean usedInGame;

    Sprites(String spritePath, boolean usedInGame) {
        this.spritePath = spritePath;
        this.usedInGame = usedInGame;
    }

    public String getPath() {
        return spritePath;
    }

    public boolean isUsedInGame() {
        return usedInGame;
    }
}
