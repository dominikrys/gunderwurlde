package client.gui;

import java.util.HashMap;
import java.util.Map;

// Object for storing game settings
public class Settings {
    // Constants
    private final int MIN_VOLUME = 0;
    private final int MAX_VOLUME = 100;
    private final Map<String, Resolution> SCREEN_RESOLUTIONS;
    // Sound variables
    private int soundVolume;
    private int musicVolume;
    private boolean soundMute;
    private boolean musicMute;
    // Screen variables
    private boolean fullScreen;
    // Current screen resolution
    private Resolution screenResolution;

    public Settings() {
        // TODO: have these get loaded from a file

        // Populate screen resolutions - most common screen resolutions as of January 2019 Steam Hardware Survey + 800x600
        SCREEN_RESOLUTIONS = new HashMap<>();
        SCREEN_RESOLUTIONS.put("800x600", new Resolution(800, 600));
        SCREEN_RESOLUTIONS.put("1024x768", new Resolution(1024, 768));
        SCREEN_RESOLUTIONS.put("1280x1024", new Resolution(1280, 1024));
        SCREEN_RESOLUTIONS.put("1280x720", new Resolution(1280, 720));
        SCREEN_RESOLUTIONS.put("1280x800", new Resolution(1280, 800));
        SCREEN_RESOLUTIONS.put("1360x768", new Resolution(1360, 768));
        SCREEN_RESOLUTIONS.put("1366x768", new Resolution(1366, 768));
        SCREEN_RESOLUTIONS.put("1440x900", new Resolution(1440, 900));
        SCREEN_RESOLUTIONS.put("1536x864", new Resolution(1536, 864));
        SCREEN_RESOLUTIONS.put("1600x900", new Resolution(1600, 900));
        SCREEN_RESOLUTIONS.put("1680x1050", new Resolution(1680, 1050));
        SCREEN_RESOLUTIONS.put("1920x1200", new Resolution(1920, 1200));
        SCREEN_RESOLUTIONS.put("1920x1080", new Resolution(1920, 1080));
        SCREEN_RESOLUTIONS.put("2560x1080", new Resolution(2560, 1080));
        SCREEN_RESOLUTIONS.put("2560x1440", new Resolution(2560, 1440));
        SCREEN_RESOLUTIONS.put("3440x1440", new Resolution(3440, 1440));
        SCREEN_RESOLUTIONS.put("3840x2160", new Resolution(3840, 2160));

        // Initialise settings
        soundVolume = 70;
        musicVolume = 70;
        soundMute = false;
        musicMute = false;
        fullScreen = false;
        screenResolution = SCREEN_RESOLUTIONS.get("1280x720");
    }

    public int getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(int soundVolume) {
        // Check that the sound volume is within allowed values
        if (soundVolume < MIN_VOLUME) {
            this.soundVolume = MIN_VOLUME;
        } else if (soundVolume > MAX_VOLUME) {
            this.soundVolume = MAX_VOLUME;
        } else {
            this.soundVolume = soundVolume;
        }
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        // Check that the music is within allowed values
        if (musicVolume < MIN_VOLUME) {
            this.musicVolume = MIN_VOLUME;
        } else if (musicVolume > MAX_VOLUME) {
            this.musicVolume = MAX_VOLUME;
        } else {
            this.musicVolume = musicVolume;
        }
    }

    public boolean isSoundMute() {
        return soundMute;
    }

    public void setSoundMute(boolean soundMute) {
        this.soundMute = soundMute;
    }

    public boolean isMusicMute() {
        return musicMute;
    }

    public void setMusicMute(boolean musicMute) {
        this.musicMute = musicMute;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    public String getScreenResolution() {
        return screenResolution.toString();
    }

    public void setScreenResolution(String resolution) {
        screenResolution = SCREEN_RESOLUTIONS.get(resolution);
    }

    // Screen resolutions
    private class Resolution {
        private int screenWidth;
        private int screenHeight;

        private Resolution(int screenWidth, int screenHeight) {
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
        }

        @Override
        public String toString() {
            return screenWidth + "x" + screenHeight;
        }
    }
}
