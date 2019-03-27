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
import shared.view.GunView;
import shared.view.entity.EntityView;
import shared.view.entity.PlayerView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class GameSound {
	
	private HashMap<SoundList, AudioClip> loadedGameSounds;
	private PlayerView client;
	private EntityView entity;
	private ActionList action;
	private AudioClip audio;
	private double volume;
	private Timer timer;
	private TimerTask checkReplay;
	private TimerTask play;
	private boolean replayable;
	private long startDelay;
	final double hearableDistance = 500	;
	
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
	}
	
	public EntityView getEntityView() {
		return this.entity;
	}
	
	public void setEntityView(EntityView entity) {
		this.entity = entity;
	}
	
	public ActionList getActionList() {
		return this.action;
	}
	
	public boolean getReplayable() {
		return this.replayable;
	}
	
	public boolean isPlaying() {
		if(this.audio != null) {
			return this.audio.isPlaying();
		}
		else {
			return false;
		}
	}
	
	public void replay() {
		if(this.audio != null) {
			if(this.replayable) {
				this.audio.play();
				this.replayable = false;
			}
		}
	}
	
	// for debugging
	public void setClient(PlayerView client) {
		this.client = client;
	}
	
	// for debugging
	public void play() {
		this.calculateSound(audio);
		this.audio.play();
	}
	
	public void stop() {
		if(this.audio != null && !this.action.equals(ActionList.ATTACKING)) {
			this.audio.stop();
		}
	}
	
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
	
	private void calculateSound(AudioClip audio) {
		double distance = Math.sqrt(Math.pow(this.entity.getPose().getX() - this.client.getPose().getX(), 2) + Math.pow(this.entity.getPose().getY() - this.client.getPose().getY(), 2));
		double balanceDistance = Math.sqrt(Math.pow(this.entity.getPose().getX() - this.client.getPose().getX(), 2));
		
		audio.setVolume(Math.max(0, 1 - Math.abs(distance/hearableDistance))*(Math.pow(this.volume/100, 2)));
		audio.setBalance((balanceDistance + 1) / hearableDistance);
		if(entity.getPose().getX() < client.getPose().getX()) {
			audio.setBalance(this.audio.getBalance()*-1);
		}
	}
	
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
								this.timer.schedule(checkReplay, BuckshotShotgun.DEFAULT_RELOAD_TIME);
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
							break;
						case MIDGET:
							audio = loadedGameSounds.get(SoundList.SHOTGUN2);
							this.timer.schedule(checkReplay, Shotgun.DEFAULT_COOL_DOWN);
							this.startDelay = 200;
							this.playShellsFall(startDelay);
							break;
						case BOOMER:
							break;
						case MACHINE_GUNNER:
							audio = loadedGameSounds.get(SoundList.MACHINE_GUN2);
							this.timer.schedule(checkReplay, MachineGun.DEFAULT_COOL_DOWN);
							//this.startDelay = 200;
							//this.playShellsFall(startDelay);
							break;
						case SNIPER:
							audio = loadedGameSounds.get(SoundList.SNIPER);
							this.timer.schedule(checkReplay, SniperRifle.DEFAULT_COOL_DOWN);
							//this.startDelay = 200;
							//this.playShellsFall(startDelay);
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
		}
		return audio;
	}

}
