package client;

import client.input.KeyAction;

import java.io.*;
import java.util.HashMap;

/**
 * Settings class. Contains the game's settings
 *
 * @author Dominik Rys
 */
public class Settings implements Serializable {
    /**
     * Minimum sound and music volume
     */
    private final int MIN_VOLUME = 0;

    /**
     * Maximum sound and music volume
     */
    private final int MAX_VOLUME = 100;

    /**
     * Sound volume
     */
    private int soundVolume;

    /**
     * Music volume
     */
    private int musicVolume;

    /**
     * Whether sound is muted or not
     */
    private boolean soundMute;

    /**
     * Whether music is muted or not
     */
    private boolean musicMute;

    /**
     * Whether game is full screen or not
     */
    private boolean fullScreen;

    /**
     * Screen width
     */
    private int screenWidth;

    /**
     * Screen height
     */
    private int screenHeight;

    /**
     * Key mapping
     */
    private HashMap<KeyAction, String> keyMapping;

    /**
     * Constructor
     */
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
        mapDefaultKeys();
    }

    /**
     * Static method for loading settings file
     *
     * @return Loaded settings
     */
    public static Settings loadSettingsFromFile() {
        Settings settings = null;
        try (
                InputStream file = new FileInputStream("settings.ser");
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream(buffer)
        ) {
            // Deserialize the file
            settings = (Settings) input.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (IOException e) {
            return null;
        }

        return settings;
    }

    /**
     * Set key mapping to default
     */
    public void mapDefaultKeys() {
        keyMapping = new HashMap<>();
        keyMapping.put(KeyAction.UP, "W");
        keyMapping.put(KeyAction.DOWN, "S");
        keyMapping.put(KeyAction.LEFT, "A");
        keyMapping.put(KeyAction.RIGHT, "D");
        keyMapping.put(KeyAction.RELOAD, "R");
        keyMapping.put(KeyAction.DROP, "G");
        keyMapping.put(KeyAction.INTERACT, "E");
        keyMapping.put(KeyAction.ITEM1, "DIGIT1");
        keyMapping.put(KeyAction.ITEM2, "DIGIT2");
        keyMapping.put(KeyAction.ITEM3, "DIGIT3");
        keyMapping.put(KeyAction.ESC, "ESCAPE");
    }

    /**
     * Get sound volume
     *
     * @return Sound volume
     */
    public int getSoundVolume() {
        return soundVolume;
    }

    /**
     * Set sound volume, checking if within allowed bounds
     *
     * @param soundVolume Sound volume to set
     */
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

    /**
     * Get music volume
     *
     * @return Music volume
     */
    public int getMusicVolume() {
        return musicVolume;
    }

    /**
     * Set music volume, making sure it's within allowed bounds
     *
     * @param musicVolume
     */
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

    /**
     * Check if sound muted
     *
     * @return Whether sound muted
     */
    public boolean isSoundMute() {
        return soundMute;
    }

    /**
     * Set sound muted or unmuted
     *
     * @param soundMute True if muted, false if unmuted
     */
    public void setSoundMute(boolean soundMute) {
        this.soundMute = soundMute;
    }

    /**
     * Check if music is muted
     *
     * @return Whether music is muted
     */
    public boolean isMusicMute() {
        return musicMute;
    }

    /**
     * Set music mute
     *
     * @param musicMute Music mute: true if muted, false if not muted
     */
    public void setMusicMute(boolean musicMute) {
        this.musicMute = musicMute;
    }

    /**
     * Check if full screen
     *
     * @return Whether full screen
     */
    public boolean isFullScreen() {
        return fullScreen;
    }

    /**
     * Set full screen
     *
     * @param fullScreen Full screen boolean: true if fullscreen, false if not
     */
    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    /**
     * Get screen resolution with 'x' in the middle so it can be displayed legibly
     *
     * @return String containing the screen resolution with an 'x' between height and width
     */
    public String getScreenResolutionString() {
        return screenWidth + "x" + screenHeight;
    }

    /**
     * Get screen height
     *
     * @return Screen height
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * Set screen height
     *
     * @param screenHeight Value to set screen height to
     */
    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    /**
     * Get screen width
     *
     * @return Screen width
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * Set screen width
     *
     * @param screenWidth Value to set screen width to
     */
    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    /**
     * Get fullscreen boolean
     *
     * @return Fullscreen if true
     */
    public boolean getFullscreen() {
        return fullScreen;
    }

    /**
     * Get key mapping as key
     *
     * @param action The binding of which action to return
     * @return Binding of specified action
     */
    public String getKey(KeyAction action) {
        return keyMapping.get(action);
    }

    /**
     * Set key binding of action to one specified
     *
     * @param action Action to set key binding of
     * @param newKey New binding for specified action
     */
    public void setKey(KeyAction action, String newKey) {
        keyMapping.put(action, newKey);
    }

    /**
     * Serialise settings object and save to the disk
     */
    public void saveToDisk() {
        try (
                OutputStream file = new FileOutputStream("settings.ser");
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer)
        ) {
            output.writeObject(this);
        } catch (IOException e) {
            System.out.println("Can't write settings to disk:" + e.getMessage());
        }
    }
}

