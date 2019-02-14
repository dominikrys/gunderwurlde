package shared;

public final class Constants {
    public static final int SCREEN_HEIGHT = 720;
    public static final int SCREEN_WIDTH = 1280;
    public static final int TILE_SIZE = 32;
    public static final String DEFAULT_GRAPHIC_PATH = "file:assets/img/other/default.png";
    public static final String MANASPACE_FONT_PATH = "assets/fonts/manaspc.ttf";
    public static final int MAX_PLAYERS = 4;

    // Most common screen resolutions as of the Steam Harware survey January 2019 + 800x600 for compatibility's sake
    public static final String[] SCREEN_RESOLUTIONS = new String[]{"800x600", "1024x768", "1280x1024", "1280x720", "1280x800", "1360x768",
            "1440x900", "1536x864", "1600x900", "1680x1050", "1920x1200", "1920x1080", "2560x1080", "3440x1440",
            "3840x2160"};
}
