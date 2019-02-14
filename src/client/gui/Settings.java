package client.gui;

import java.util.HashMap;
import java.util.Map;

// Object for storing game settings
public class Settings {
    // Constants
    private final int MIN_VOLUME = 0;
    private final int MAX_VOLUME = 100;
    Map<String, Resolution> screenResolutions;
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

        // Populate screen resolutions - most common screen resolutions as of January 2019 Steam Hardware Survey + 800x600
        screenResolutions = new HashMap<>();
        screenResolutions.put("800x600", new Resolution(800, 600));
        screenResolutions.put("1024x768", new Resolution(1024, 768));
        screenResolutions.put("1280x1024", new Resolution(1280, 1024));
        screenResolutions.put("1280x720", new Resolution(1280, 720));
        screenResolutions.put("1280x800", new Resolution(1280, 800));
        screenResolutions.put("1360x768", new Resolution(1360, 768));
        screenResolutions.put("1366x768", new Resolution(1366, 768));
        screenResolutions.put("1440x900", new Resolution(1440, 900));
        screenResolutions.put("1536x864", new Resolution(1536, 864));
        screenResolutions.put("1600x900", new Resolution(1600, 900));
        screenResolutions.put("1680x1050", new Resolution(1680, 1050));
        screenResolutions.put("1920x1200", new Resolution(1920, 1200));
        screenResolutions.put("1920x1080", new Resolution(1920, 1080));
        screenResolutions.put("2560x1080", new Resolution(2560, 1080));
        screenResolutions.put("2560x1440", new Resolution(2560, 1440));
        screenResolutions.put("3440x1440", new Resolution(3440, 1440));
        screenResolutions.put("3840x2160", new Resolution(3840, 2160));
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

    // Screen resolutions
    private class Resolution {
        private int screenWidth;
        private int screenHeight;

        private Resolution(int screenWidth, int screenHeight) {
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
        }

        public int getScreenWidth() {
            return screenWidth;
        }

        public int getScreenHeight() {
            return screenHeight;
        }
    }
}
