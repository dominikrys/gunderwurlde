package shared;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.media.AudioClip;
import server.engine.state.item.weapon.gun.Pistol;
import server.engine.state.item.weapon.gun.Shotgun;
import shared.lists.ActionList;
import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.SoundList;
import shared.view.entity.EntityView;
import shared.view.entity.PlayerView;

public class GameSound {
	
	private HashMap<SoundList, AudioClip> loadedGameSounds;
	private EntityView entity;
	private ActionList action;
	private AudioClip audio;
	private double volume;
	private Timer timer;
	private TimerTask checkReplay;
	private boolean replayable;
	
	public GameSound(HashMap<SoundList, AudioClip> loadedGameSounds, EntityView entity, ActionList action, double volume) {
		this.loadedGameSounds = loadedGameSounds;
		this.entity = entity;
		this.action = action;
		this.volume = volume;
		this.timer = new Timer();
		this.checkReplay = new TimerTask() {
			@Override
			public void run() {
				replayable = true;
			}
		};
		this.audio = getAudio(action);
		if(this.audio != null) {
			this.replayable = false;
			this.audio.setVolume(volume/100);
			this.audio.play();
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
	
	public void stop() {
		if(this.audio != null && !this.action.equals(ActionList.ATTACKING)) {
			this.audio.stop();
		}
	}
	
	private void playShellsFall() {
		Timer t = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				AudioClip shells = new AudioClip(SoundList.SHELLS_FALL.getPath());
				shells.setVolume(volume/100);
				shells.play();
			}
		};
		t.schedule(task, 700);
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
							if(((PlayerView) entity).getAmmo().get(AmmoList.SHOTGUN_ROUND) > 0 && ((PlayerView) entity).getCurrentItem().getAmmoInClip() + 1 != 8) {
								this.timer.schedule(checkReplay, Shotgun.DEFAULT_RELOAD_TIME + 50);
							}
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
						this.playShellsFall();
						break;
					case SHOTGUN:
						audio = loadedGameSounds.get(SoundList.SHOTGUN);
						this.timer.schedule(checkReplay, Shotgun.DEFAULT_COOL_DOWN - 15);
						this.playShellsFall();
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
