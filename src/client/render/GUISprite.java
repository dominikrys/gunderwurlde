package client.render;

/**
 * GUISprite enum. Holds Sprites used in the GUI
 * @author Dominik Rys
 */
public enum GUISprite {
    /**
     * Sound off graphic
     */
    SOUND_OFF("assets/img/gui/sound_off"),

    /**
     * Sound on graphic
     */
    SOUND_ON("assets/img/gui/sound_on"),

    /**
     * Meadow map thumbnail
     */
    MEADOW("assets/img/gui/meadow.png");

    /**
     * Path to sprite
     */
    private String spritePath;

    /**
     * Constructor
     * @param spritePath Path to sprite
     */
    GUISprite(String spritePath) {
        this.spritePath = spritePath;
    }

    /**
     * Get sprite path
     * @return Sprite path
     */
    public String getPath() {
        return spritePath;
    }
}
