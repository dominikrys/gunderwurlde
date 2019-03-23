package shared.lists;

/**
 * SoundsList enum. Contains all sounds that can be played in the game
 */
public enum SoundList {
	GUN_COCK("file:assets/sound/gun/gun_cock.wav"),
	HEAVY_MACHINE_GUN("file:assets/sound/gun/heavy_machine_gun.wav"),
	HEAVY_PISTOL("file:assets/sound/gun/heavy_pistol.wav"),
	LASER("file:assets/sound/gun/laser.wav"),
	LASER2("file:assets/sound/gun/laser2.wav"),
	LASER3("file:assets/sound/gun/laser3.wav"),
	MACHINE_GUN("file:assets/sound/gun/machine_gun.wav"),
	MACHINE_GUN2("file:assets/sound/gun/machine_gun2.wav"),
	MACHINE_GUN3("file:assets/sound/gun/machine_gun3.wav"),
	MISSLE("file:assets/sound/gun/missle.wav"),
	PISTOL("file:assets/sound/gun/pistol.wav"),
	RELOAD_MAG("file:assets/sound/gun/reload_mag.wav"),
	RIFLE("file:assets/sound/gun/rifle.wav"),
	RIFLE_CHAMBER("file:assets/sound/gun/rifle_chamber.wav"),
	RIFLE2("file:assets/sound/gun/rifle2.wav"),
	RIFLE3("file:assets/sound/gun/rifle3.wav"),
	SHELLS_FALL("file:assets/sound/gun/shells_fall.wav"),
	SHOTGUN("file:assets/sound/gun/shotgun.wav"),
	SHOTGUN_SINGLE_RELOAD("file:assets/sound/gun/shotgun_single_reload.wav"),
	SHOTGUN_SINGLE_RELOAD2("file:assets/sound/gun/shotgun_single_reload2.wav"),
	SHOTGUN2("file:assets/sound/gun/shotgun2.wav"),
	SMG("file:assets/sound/gun/smg.wav"),
	SNIPER("file:assets/sound/gun/sniper.wav");

	/**
	 * Path to sound file
	 */
	private String soundPath;

	/**
	 * Constructor
	 * @param soundPath Path to sound file
	 */
	SoundList(String soundPath) {
        this.soundPath = soundPath;
    }

	/**
	 * Get path to sound file
	 * @return Path to sound file
	 */
	public String getPath() {
        return soundPath;
    }

}
