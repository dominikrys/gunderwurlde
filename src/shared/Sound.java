package shared;

import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.media.AudioClip;
import server.engine.state.item.weapon.gun.Pistol;
import server.engine.state.item.weapon.gun.Shotgun;
import shared.lists.ActionList;
import shared.lists.ItemList;
import shared.lists.SoundList;
import shared.view.entity.EntityView;
import shared.view.entity.PlayerView;

public class Sound {
	
	private EntityView entity;
	private ActionList action;
	private AudioClip audio;
	private double volume;
	private Timer timer;
	private TimerTask checkReplay;
	private boolean replayable;
	
	public Sound(EntityView entity, ActionList action, double volume) {
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
	
	private AudioClip getAudio(ActionList action) {
		audio = null;
		switch(action) {
			case RELOADING:
				if(entity instanceof PlayerView) {
					ItemList item = ((PlayerView) entity).getCurrentItem().getItemListName();
					switch(item) {
						case PISTOL:
							audio = new AudioClip(SoundList.RELOAD_MAG.getPath());
							break;
						case SHOTGUN:
							audio = new AudioClip(SoundList.SHOTGUN_SINGLE_RELOAD.getPath());
							audio.setCycleCount(Shotgun.DEFAULT_CLIP_SIZE - ((PlayerView) entity).getCurrentItem().getAmmoInClip());
							break;
					}
				}
				break;
			case ATTACKING:
				if(entity instanceof PlayerView) {
					ItemList item = ((PlayerView) entity).getCurrentItem().getItemListName();
					switch(item) {
					case PISTOL:
						audio = new AudioClip(SoundList.PISTOL.getPath());
						this.timer.scheduleAtFixedRate(checkReplay, Pistol.DEFAULT_COOL_DOWN, Pistol.DEFAULT_COOL_DOWN);
						break;
					case SHOTGUN:
						audio = new AudioClip(SoundList.SHOTGUN.getPath());
						this.timer.scheduleAtFixedRate(checkReplay, Shotgun.DEFAULT_COOL_DOWN, Shotgun.DEFAULT_COOL_DOWN);
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
