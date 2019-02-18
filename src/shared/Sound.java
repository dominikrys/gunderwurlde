package shared;

import javafx.scene.media.AudioClip;
import shared.lists.ActionList;
import shared.lists.ItemList;
import shared.lists.SoundList;
import shared.view.entity.EntityView;
import shared.view.entity.PlayerView;

public class Sound {
	
	private EntityView entity;
	private ActionList action;
	private AudioClip audio;
	
	public Sound(EntityView entity, ActionList action) {
		this.entity = entity;
		this.action = action;
		this.audio = getAudio(action);
		if(this.audio != null) {
			this.audio.play();
		}
	}
	
	public EntityView getEntityView() {
		return this.entity;
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
	
	public void stop() {
		this.audio.stop();
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
						break;
					case SHOTGUN:
						audio = new AudioClip(SoundList.SHOTGUN.getPath());
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
