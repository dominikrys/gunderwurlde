package shared;

public final class Constants {
    public static final int TILE_SIZE = 32;
    public static final String DEFAULT_GRAPHIC_PATH = "file:assets/img/other/default.png";
    public static final String MANASPACE_FONT_PATH = "assets/fonts/manaspc.ttf";
    public static final int MAX_PLAYERS = 4;

    // Most common screen resolutions as of the Steam Hardware survey January 2019 + 800x600 for compatibility's sake
    public static final int[][] SCREEN_RESOLUTIONS = new int[][]{{800, 600}, {1024, 768}, {1280, 1024}, {1280, 720},
            {1280, 800}, {1360, 768}, {1440, 900}, {1536, 864}, {1600, 900}, {1680, 1050}, {1920, 1200}, {1920, 1080},
            {2560, 1080}, {2560, 1080}, {3440, 1440}, {3840, 2160}
    };
}
