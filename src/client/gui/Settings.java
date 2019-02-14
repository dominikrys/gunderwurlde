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
    // Current screen resolution
    private Resolution screenResolution;

    public Settings() {
        // TODO: have these get loaded from a file

        // Initialise settings
        soundVolume = 70;
        musicVolume = 70;
        soundMute = false;
        musicMute = false;
        fullScreen = false;
        screenResolution = new Resolution("1280x720");
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

    public String getScreenResolutionString() {
        return screenResolution.toString();
    }

    public int getScreenHeight() {
        return screenResolution.getScreenHeight();
    }

    public int getScreenWidth() {
        return screenResolution.getScreenWidth();
    }

    public void setScreenResolution(String resolution) {
        screenResolution.setResolution(resolution);
    }

    // Screen resolutions
    private class Resolution {
        private int screenWidth;
        private int screenHeight;

        private Resolution(String resolution) {
            setResolution(resolution);
        }

        private void setResolution(String resolution) {
            this.screenWidth = Integer.parseInt(resolution.substring(0, resolution.indexOf('x')));
            this.screenHeight = Integer.parseInt(resolution.substring(resolution.indexOf('x') + 1));
        }

        @Override
        public String toString() {
            return screenWidth + "x" + screenHeight;
        }

        private int getScreenWidth(){
            return screenWidth;
        }

        private int getScreenHeight() {
            return screenHeight;
        }
    }
}
