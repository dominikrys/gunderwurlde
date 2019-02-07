package client;

public enum SpritesNonGame {
    // Menu items
    SOUND_OFF("assets/img/gui/sound_off"),
    SOUND_ON("assets/img/gui/sound_on"),

    // Map thumbnails
    MEADOW("assets/img/gui/meadow.png");

    private String spritePath;

    SpritesNonGame(String spritePath) {
        this.spritePath = spritePath;
    }

    public String getPath() {
        return spritePath;
    }
}
