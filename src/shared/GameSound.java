package shared;

import javafx.scene.media.AudioClip;
import server.engine.state.item.weapon.gun.AssaultRifle;
import server.engine.state.item.weapon.gun.BuckshotShotgun;
import server.engine.state.item.weapon.gun.HeavyPistol;
import server.engine.state.item.weapon.gun.LaserPistol;
import server.engine.state.item.weapon.gun.MachineGun;
import server.engine.state.item.weapon.gun.Pistol;
import server.engine.state.item.weapon.gun.PlasmaPistol;
import server.engine.state.item.weapon.gun.RocketLauncher;
import server.engine.state.item.weapon.gun.Shotgun;
import server.engine.state.item.weapon.gun.Smg;
import server.engine.state.item.weapon.gun.SniperRifle;
import shared.lists.ActionList;
import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.SoundList;
import shared.view.entity.EnemyView;
import shared.view.ExplosionView;
import shared.view.GunView;
import shared.view.entity.EntityView;
import shared.view.entity.PlayerView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * GameSound class. Contains the settings for generating an in-game sound.
 *
 * @author Mak Hong Lun Timothy
 */
public class GameSound {
	/**
     * loadedGameSounds - Loaded game sounds
     */
	private HashMap<SoundList, AudioClip> loadedGameSounds;
	/**
     * client - View on the client
     */
	private PlayerView client;
	/**
     * entity - View on the sound source
     */
	private EntityView entity;
	/**
     * explosion - View on the explosion source
     */
	private ExplosionView explosion;
	/**
     * action - Action of the sound source
     */
	private ActionList action;
	/**
     * audio - Audio to be played
     */
	private AudioClip audio;
	/**
     * volume - Volume of audio
     */
	private double volume;
	/**
     * timer - Timer for task scheduling
     */
	private Timer timer;
	/**
     * checkReplay - TimerTask for indicating that this audio is replayable
     */
	private TimerTask checkReplay;
	/**
     * play - TimerTask for playing the audio
     */
	private TimerTask play;
	/**
     * replayable - Boolean whether the audio is replayable
     */
	private boolean replayable;
	/**
     * startDelay - Delay before playing the audio
     */
	private long startDelay;
	/**
     * hearableDistance - Maximum distance where sounds from source can be heard
     */
	private double hearableDistance = 500;
	
	/**
     * Constructor for sounds of entity
     * 
     * @param loadedGameSounds Loaded game sounds
     * @param client View on the client
     * @param entity View on the sound source
     * @param action Action of the sound source
     * @param volume Volume of audio
     */
	public GameSound(HashMap<SoundList, AudioClip> loadedGameSounds, PlayerView client, EntityView entity, ActionList action, double volume) {
		this.loadedGameSounds = loadedGameSounds;
		this.client = client;
		this.entity = entity;
		this.action = action;
		this.volume = volume;
		this.timer = new Timer();
		this.checkReplay = new TimerTask() {
			@Override
			public void run() {
				replayable = true;
				timer.cancel();
				timer.purge();
			}
		};
		this.play = new TimerTask() {
			@Override
			public void run() {
				audio.play();
			}
		};
		this.startDelay = 0;
		this.audio = getAudio(action);
		if(this.audio != null) {
			this.replayable = false;
			this.calculateSound(this.audio);
			this.timer.schedule(play, startDelay);
		}
		else {
			this.timer.cancel();
			this.timer.purge();
		}
	}
	
	/**
     * Constructor for sounds of explosion
     * 
     * @param loadedGameSounds Loaded game sounds
     * @param client View on the client
     * @param explosion View on the explosion
     * @param volume Volume of audio
     */
	public GameSound(HashMap<SoundList, AudioClip> loadedGameSounds, PlayerView client, ExplosionView explosion, double volume) {
		this.loadedGameSounds = loadedGameSounds;
		this.client = client;
		this.explosion = explosion;
		this.volume = volume;
		this.audio = loadedGameSounds.get(SoundList.SHOTGUN);
		this.hearableDistance = 1000;
		this.calculateSound(audio, explosion);
		this.audio.play();
	}
	
	/**
     * Getter for entity
     * 
     * @return entity
     */
	public EntityView getEntityView() {
		return this.entity;
	}
	
	/**
     * Setter for entity
     * 
     * @param entity View on the sound source
     */
	public void setEntityView(EntityView entity) {
		this.entity = entity;
	}
	
	/**
     * Getter for action
     * 
     * @return action
     */
	public ActionList getActionList() {
		return this.action;
	}
	
	/**
     * Getter for replayable
     * 
     * @return replayable
     */
	public boolean getReplayable() {
		return this.replayable;
	}
	
	/**
     * Check whether this audio is still playing
     * 
     * @return true if yes, otherwise false
     */
	public boolean isPlaying() {
		if(this.audio != null) {
			return this.audio.isPlaying();
		}
		else {
			return false;
		}
	}
	
	/**
     * Replay this audio
     */
	public void replay() {
		if(this.audio != null) {
			if(this.replayable) {
				this.audio.play();
				this.replayable = false;
			}
		}
	}
	
	/**
     * Calculate the volume and play this audio
     */
	public void play() {
		this.calculateSound(audio);
		this.audio.play();
	}
	
	/**
     * Stop this audio
     */
	public void stop() {
		if(this.audio != null && !this.action.equals(ActionList.ATTACKING)) {
			this.audio.stop();
		}
	}
	
	/**
     * Play shells falling sound
     * 
     * @param startDelay Delay before playing
     */
	private void playShellsFall(long startDelay) {
		Timer shellTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				AudioClip shells = new AudioClip(SoundList.SHELLS_FALL.getPath());
				calculateSound(shells);
				shells.play();
				shellTimer.cancel();
				shellTimer.purge();
			}
		};
		shellTimer.schedule(task, 700 + startDelay);
	}
	
	/**
     * Play damage taken sound
     * 
     * @param client View of the client
     * @param entity View of the sound source
     */
	public void playDamaged(PlayerView client, EntityView entity) {
		this.client = client;
		this.entity = entity;
		AudioClip ouch;
		if(entity instanceof PlayerView) {
			ouch = new AudioClip(SoundList.OUCH.getPath());
		}
		else {
			ouch = new AudioClip(SoundList.OUCH2.getPath());
		}
		this.audio = ouch;
		calculateSound(ouch);
		ouch.play();
	}
	
	/**
     * Calculate the volume for the audio
     * 
     * @param audio Audio to be calculated
     */
	private void calculateSound(AudioClip audio) {
		double distance = Math.sqrt(Math.pow(this.entity.getPose().getX() - this.client.getPose().getX(), 2) + Math.pow(this.entity.getPose().getY() - this.client.getPose().getY(), 2));
		double balanceDistance = Math.sqrt(Math.pow(this.entity.getPose().getX() - this.client.getPose().getX(), 2));
		
		audio.setVolume(Math.max(0, 1 - Math.abs(distance/hearableDistance))*(Math.pow(this.volume/100, 2)));
		audio.setBalance((balanceDistance + 1) / hearableDistance);
		if(entity.getPose().getX() < client.getPose().getX()) {
			audio.setBalance(this.audio.getBalance()*-1);
		}
	}
	
	/**
     * Calculate the volume for an explosion
     * 
     * @param audio Audio of explosion to be calculated
     * @param explosion View of explosion
     */
	private void calculateSound(AudioClip audio, ExplosionView explosion) {
		double distance = Math.sqrt(Math.pow(explosion.getLocation().getX() - this.client.getPose().getX(), 2) + Math.pow(explosion.getLocation().getY() - this.client.getPose().getY(), 2));
		double balanceDistance = Math.sqrt(Math.pow(explosion.getLocation().getX() - this.client.getPose().getX(), 2));
		
		audio.setVolume(Math.max(0, 1 - Math.abs(distance/hearableDistance))*(Math.pow(this.volume/100, 2)));
		audio.setBalance((balanceDistance + 1) / hearableDistance);
		if(explosion.getLocation().getX() < client.getPose().getX()) {
			audio.setBalance(this.audio.getBalance()*-1);
		}
	}
	
	/**
     * Get the right audio file and set up delays
     * 
     * @param action Action of the sound source
     */
	private AudioClip getAudio(ActionList action) {
		audio = null;
		switch(action) {
			case RELOADING:
				if(entity instanceof PlayerView) {
					ItemList item = ((PlayerView) entity).getCurrentItem().getItemListName();
					switch(item) {
						case PISTOL:
							audio = loadedGameSounds.get(SoundList.RELOAD_MAG);
							this.timer.schedule(checkReplay, Pistol.DEFAULT_RELOAD_TIME + 50);
							break;
						case SHOTGUN:
							audio = loadedGameSounds.get(SoundList.SHOTGUN_SINGLE_RELOAD);
							if(((PlayerView) entity).getAmmo().get(AmmoList.SHOTGUN_ROUND) > 0 && ((GunView)((PlayerView) entity).getCurrentItem()).getAmmoInClip() + 1 != 8) {
								this.timer.schedule(checkReplay, Shotgun.DEFAULT_RELOAD_TIME + 50);
							}
							break;
						case SMG:
							audio = loadedGameSounds.get(SoundList.RELOAD_MAG);
							this.timer.schedule(checkReplay, Smg.DEFAULT_RELOAD_TIME + 50);
							break;
						case SNIPER_RIFLE:
							audio = loadedGameSounds.get(SoundList.RELOAD_MAG);
							this.timer.schedule(checkReplay, SniperRifle.DEFAULT_RELOAD_TIME);
							break;
						case PLASMA_PISTOL:
							break;
						case MACHINE_GUN:
							audio = loadedGameSounds.get(SoundList.RELOAD_MAG);
							this.timer.schedule(checkReplay, MachineGun.DEFAULT_RELOAD_TIME);
							break;
						case CRYSTAL_LAUNCHER:
							break;
						case FIRE_GUN:
							break;
						case ICE_GUN:
							break;
						case RING_OF_DEATH:
							break;
						case HEAVY_PISTOL:
							audio = loadedGameSounds.get(SoundList.RELOAD_MAG);
							this.timer.schedule(checkReplay, HeavyPistol.DEFAULT_RELOAD_TIME);
							break;
						case ASSAULT_RIFLE:
							audio = loadedGameSounds.get(SoundList.RELOAD_MAG);
							this.timer.schedule(checkReplay, AssaultRifle.DEFAULT_RELOAD_TIME);
							break;
						case BUCKSHOT_SHOTGUN:
							audio = loadedGameSounds.get(SoundList.SHOTGUN_SINGLE_RELOAD2);
							if(((PlayerView) entity).getAmmo().get(AmmoList.SHOTGUN_ROUND) > 0 && ((GunView)((PlayerView) entity).getCurrentItem()).getAmmoInClip() + 1 != 2) {
								this.timer.schedule(checkReplay, BuckshotShotgun.DEFAULT_RELOAD_TIME + 300);
							}
							break;
						case ROCKET_LAUNCHER:
							break;
						case LASER_PISTOL:
							break;
						case LASER_CANNON:
							break;
					}
				}
				break;
			case ATTACKING:
				if(entity instanceof PlayerView) {
					ItemList item = ((PlayerView) entity).getCurrentItem().getItemListName();
					switch(item) {
						case PISTOL:
							audio = loadedGameSounds.get(SoundList.PISTOL);
							this.timer.schedule(checkReplay, Pistol.DEFAULT_COOL_DOWN - 15);
							this.playShellsFall(0);
							break;
						case SHOTGUN:
							audio = loadedGameSounds.get(SoundList.SHOTGUN);
							this.timer.schedule(checkReplay, Shotgun.DEFAULT_COOL_DOWN - 15);
							this.playShellsFall(0);
							break;
						case SMG:
							audio = loadedGameSounds.get(SoundList.SMG);
							this.timer.schedule(checkReplay, Smg.DEFAULT_COOL_DOWN - 5);
							this.playShellsFall(0);
							break;
						case SNIPER_RIFLE:
							audio = loadedGameSounds.get(SoundList.SNIPER);
							this.timer.schedule(checkReplay, SniperRifle.DEFAULT_COOL_DOWN);
							this.playShellsFall(0);
							break;
						case PLASMA_PISTOL:
							audio = loadedGameSounds.get(SoundList.LASER3);
							this.timer.schedule(checkReplay, PlasmaPistol.DEFAULT_COOL_DOWN);
							break;
						case MACHINE_GUN:
							audio = loadedGameSounds.get(SoundList.MACHINE_GUN);
							this.timer.schedule(checkReplay, MachineGun.DEFAULT_COOL_DOWN);
							this.playShellsFall(0);
							break;
						case CRYSTAL_LAUNCHER:
							break;
						case FIRE_GUN:
							break;
						case ICE_GUN:
							break;
						case RING_OF_DEATH:
							break;
						case HEAVY_PISTOL:
							audio = loadedGameSounds.get(SoundList.HEAVY_PISTOL);
							this.timer.schedule(checkReplay, HeavyPistol.DEFAULT_COOL_DOWN);
							this.playShellsFall(0);
							break;
						case ASSAULT_RIFLE:
							audio = loadedGameSounds.get(SoundList.RIFLE3);
							this.timer.schedule(checkReplay, AssaultRifle.DEFAULT_COOL_DOWN);
							this.playShellsFall(0);
							break;
						case BUCKSHOT_SHOTGUN:
							audio = loadedGameSounds.get(SoundList.SHOTGUN2);
							this.timer.schedule(checkReplay, BuckshotShotgun.DEFAULT_COOL_DOWN);
							this.playShellsFall(0);
							break;
						case ROCKET_LAUNCHER:
							audio = loadedGameSounds.get(SoundList.MISSLE);
							this.timer.schedule(checkReplay, RocketLauncher.DEFAULT_COOL_DOWN);
							break;
						case LASER_PISTOL:
							audio = loadedGameSounds.get(SoundList.LASER2);
							this.timer.schedule(checkReplay, LaserPistol.DEFAULT_COOL_DOWN);
							break;
						case LASER_CANNON:
							break;
						case GRENADE:
							audio = loadedGameSounds.get(SoundList.SHOTGUN);
							this.startDelay = 3000;
							this.timer.schedule(checkReplay, 0);
							break;
					}
				}
				else if(entity instanceof EnemyView) {
					switch(entity.getEntityListName()) {
						case ZOMBIE:
							break;
						case RUNNER:
							break;
						case SOLDIER:
							System.out.println("HERE");
							audio = loadedGameSounds.get(SoundList.MISSLE);
							this.timer.schedule(checkReplay, 500);
							break;
						case MIDGET:
							audio = loadedGameSounds.get(SoundList.SHOTGUN2);
							this.timer.schedule(checkReplay, Shotgun.DEFAULT_COOL_DOWN);
							this.startDelay = 300;
							this.playShellsFall(startDelay);
							break;
						case BOOMER:
							break;
						case MACHINE_GUNNER:
							audio = loadedGameSounds.get(SoundList.MACHINE_GUN4);
							this.timer.schedule(checkReplay, 1400);
							this.startDelay = 850;
							break;
						case SNIPER:
							audio = loadedGameSounds.get(SoundList.SNIPER);
							this.timer.schedule(checkReplay, SniperRifle.DEFAULT_COOL_DOWN);
							break;
						case THEBOSS:
							break;
					}
				}
				
				break;
			case RAGDOLL:
				break;
			case ITEM_SWITCH:
				break;
			case SPECIAL_ATTACK_1:
				break;
			case SPECIAL_ATTACK_2:
				break;
			case NONE:
				break;
		}
		return audio;
	}

}
