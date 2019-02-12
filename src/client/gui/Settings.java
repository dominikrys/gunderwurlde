package client.gui;

// Object for storing game settings
public class Settings {
    // Constants
    private final int MIN_VOLUME = 0;
    private final int MAX_VOLUME = 100;

    // Sound variables
    private int soundVolume;
    private int musicVolume;
    private boolean soundMute;
    private boolean musicMute;

    // Screen variables
    private boolean fullScreen;

    public Settings() {
        // TODO: have these get loaded from a file

        // Initialise settings
        soundVolume = 70;
        musicVolume = 70;
        soundMute = false;
        musicMute = false;
        fullScreen = false;
    }
}
