package shared;

import javafx.scene.effect.ColorAdjust;

public final class Constants {
    // Constants for renderer/GUI
    public static final int TILE_SIZE = 32;
    public static final String DEFAULT_GRAPHIC_PATH = "file:assets/img/other/default.png";
    public static final String MANASPACE_FONT_PATH = "assets/fonts/manaspc.ttf";

    // Constants for game
    public static final int MAX_PLAYERS = 4;

    // Most common screen resolutions as of the Steam Hardware survey January 2019 + 800x600 for compatibility's sake
    public static final int[][] SCREEN_RESOLUTIONS = new int[][]{{800, 600}, {1024, 768}, {1280, 1024}, {1280, 720},
            {1280, 800}, {1360, 768}, {1440, 900}, {1536, 864}, {1600, 900}, {1680, 1050}, {1920, 1200}, {1920, 1080},
            {2560, 1080}, {2560, 1080}, {3440, 1440}, {3840, 2160}
    };

    // Constants for entities
    public static final ColorAdjust redPlayerColorAdjust = new ColorAdjust(-0.3, 0, 0, 0);
    public static final ColorAdjust bluePlayerColorAdjust = new ColorAdjust(-0.3, 0, 0, 0);
    public static final ColorAdjust greenPlayerColorAdjust = new ColorAdjust(-0.3, 0, 0, 0);
    public static final ColorAdjust yellowPlayerColorAdjust = new ColorAdjust(-0.3, 0, 0, 0);

    public static final ColorAdjust zombieColorAdjust = new ColorAdjust(0.6, 0, 0, 0);
}
