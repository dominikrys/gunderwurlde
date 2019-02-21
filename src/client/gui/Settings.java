package client.gui;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// Object for storing game settings
public class Settings implements Serializable {
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
    private int screenWidth;
    private int screenHeight;
    // Key mapping
    private HashMap<String,String> keyMapping = new HashMap<String,String>();

    public Settings() {
        // Initialise sound settings
        soundVolume = 70;
        musicVolume = 70;
        soundMute = false;
        musicMute = false;

        // Initialise screen settings
        fullScreen = false;
        screenWidth = 1280;
        screenHeight = 720;

        // Initialise controls settings
        keyMapping.put("up", "W");
        keyMapping.put("down", "S");
        keyMapping.put("left", "A");
        keyMapping.put("right", "D");
        keyMapping.put("reload", "R");
        keyMapping.put("drop", "G");
        keyMapping.put("interact", "E");
        keyMapping.put("item1", "DIGIT1");
        keyMapping.put("item2", "DIGIT2");
        keyMapping.put("item3", "DIGIT3");
        keyMapping.put("esc", "ESCAPE");
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
        return screenWidth + "x" + screenHeight;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public void setScreenHeight (int screenHeight) {
        this.screenHeight = screenHeight;
    }

    // Get key mapping as a key
    public String getKey(String action) {
        return keyMapping.get(action);
    }

    // Get key action
    public String getAction(String key) {
        for(Map.Entry<String, String> entry : keyMapping.entrySet()) {
            if(entry.getValue().equals(key)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Change key mapping
    public void changeKey(String action, String newKey) {
        keyMapping.put(action, newKey);
    }

    // Method for serialising this object and saving to the disk
    public void saveToDisk() {
        try (
                OutputStream file = new FileOutputStream("settings.ser");
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer)
        ) {
            output.writeObject(this);
        } catch (IOException ex) {
            System.out.println("Cannot perform output." + ex.getMessage());
        }
    }
}
