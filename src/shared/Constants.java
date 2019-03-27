package shared;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;

/**
 * Global constants used in various parts of the program
 */
public final class Constants {
    /**
     * Constants for renderer/GUI
     */
    public static final int TILE_SIZE = 32;
    public static final String DEFAULT_GRAPHIC_PATH = "file:assets/img/other/default.png";
    public static final String MANASPACE_FONT_PATH = "assets/fonts/manaspc.ttf";

    /**
     * Constants for game
     */
    public static final int MAX_PLAYERS = 4;

    /**
     * Most common screen resolutions as of the Steam Hardware survey January 2019
     */
    public static final int[][] SCREEN_RESOLUTIONS = new int[][]{{1024, 768}, {1280, 1024}, {1280, 720},
            {1280, 800}, {1360, 768}, {1440, 900}, {1536, 864}, {1600, 900}, {1680, 1050}, {1920, 1200}, {1920, 1080},
            {2560, 1080}, {2560, 1080}, {3440, 1440}, {3840, 2160}
    };

    /**
     * Constants for entities
     */
    public static final ColorAdjust RED_PLAYER_COLOR_ADJUST = new ColorAdjust(-0.3, 0, 0, 0);
    public static final ColorAdjust BLUE_PLAYER_COLOR_ADJUST = new ColorAdjust(-0.75, 0, 0, 0);
    public static final ColorAdjust GREEN_PLAYER_COLOR_ADJUST = new ColorAdjust(0.5, 0, 0, 0);
    public static final ColorAdjust YELLOW_PLAYER_COLOR_ADJUST = new ColorAdjust(0.15, 0, 0, 0);

    public static final ColorAdjust ZOMBIE_COLOR_ADJUST = new ColorAdjust(0.6, 0, 0, 0);
    public static final ColorAdjust SOLDIER_COLOR_ADJUST = new ColorAdjust(-1, 0, 0, 0);
    public static final ColorAdjust MIDGET_COLOR_ADJUST = new ColorAdjust(-0.5, 0, 0, 0);
    public static final ColorAdjust BOOMER_COLOR_ADJUST = new ColorAdjust(0.12, 0, 0, 0);
    public static final ColorAdjust MACHINE_GUNNER_COLOR_ADJUST = new ColorAdjust(0.8, 0, 0, 0);

    /**
     * Team colours
     */
    public static final Color RED_TEAM_COLOR = Color.rgb(255, 0, 47);
    public static final Color BLUE_TEAM_COLOR = Color.rgb(66, 173, 244);
    public static final Color GREEN_TEAM_COLOR = Color.rgb(90, 240, 41);
    public static final Color YELLOW_TEAM_COLOR = Color.rgb(232, 232, 0);
}
