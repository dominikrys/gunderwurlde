package shared.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import client.gui.Settings;
import javafx.animation.AnimationTimer;
import javafx.scene.media.AudioClip;
import shared.Location;
import shared.Sound;
import shared.lists.ActionList;
import shared.lists.SoundList;
import shared.view.entity.EntityView;
import shared.view.entity.PlayerView;

public class SoundView {
	
	protected GameView gameView;
	protected Settings settings;
	protected HashMap<EntityView, Sound> playing;
	protected AudioClip audioClip;
	protected AnimationTimer t;
	
	
	public SoundView(GameView gameView, Settings settings) {
		this.gameView = gameView;
		this.settings = settings;
		this.playing = new HashMap<EntityView, Sound>();
		this.t = null;
	}
	
	public void setGameView(GameView gameView) {
		this.gameView = gameView;
	}
	
	public void activate() {
		this.t = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if(!settings.isSoundMute()) {
					analyze();
				}
				
			}
		};
		this.t.start();
	}
	
	public void deactivate() {
		this.t.stop();
	}
	
	public void analyze() {
		this.clear();
		for(PlayerView p : gameView.getPlayers()) {
			if(!p.getCurrentAction().equals(ActionList.NONE)) {
				if(!playing.containsKey(p)) {
					playing.put(p, new Sound(p, p.getCurrentAction()));
				}
				else if(!playing.get(p).getActionList().equals(p.getCurrentAction())) {
					playing.get(p).stop();
					playing.put(p, new Sound(p, p.getCurrentAction()));
				}
			}
			else {
				if(playing.containsKey(p)) {
					playing.get(p).stop();
				}
			}
		}
	}
	
	public void clear() {
		Iterator it = playing.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<EntityView, Sound> pair = (Map.Entry<EntityView, Sound>) it.next();
			if(!pair.getValue().isPlaying()) {
				it.remove();
			}
		}
	}

}

/*
switch(this.name) {

	case GUN_COCK :
		this.audioClip = new AudioClip(SoundList.GUN_COCK.getPath());
		break;
	case HEAVY_MACHINE_GUN :
		this.audioClip = new AudioClip(SoundList.HEAVY_MACHINE_GUN.getPath());
		break;
	case HEAVY_PISTOL :
		this.audioClip = new AudioClip(SoundList.HEAVY_PISTOL.getPath());
		break;
	case LASER :
		this.audioClip = new AudioClip(SoundList.LASER.getPath());
		break;
	case LASER2 :
		this.audioClip = new AudioClip(SoundList.LASER2.getPath());
		break;
	case LASER3 :
		this.audioClip = new AudioClip(SoundList.LASER3.getPath());
		break;
	case MACHINE_GUN :
		this.audioClip = new AudioClip(SoundList.MACHINE_GUN.getPath());
		break;
	case MACHINE_GUN2 :
		this.audioClip = new AudioClip(SoundList.MACHINE_GUN2.getPath());
		break;
	case MACHINE_GUN3 :
		this.audioClip = new AudioClip(SoundList.MACHINE_GUN3.getPath());
		break;
	case MISSLE :
		this.audioClip = new AudioClip(SoundList.MISSLE.getPath());
		break;
	case PISTOL :
		this.audioClip = new AudioClip(SoundList.PISTOL.getPath());
		break;
	case RELOAD_MAG :
		this.audioClip = new AudioClip(SoundList.RELOAD_MAG.getPath());
		break;
	case RIFLE :
		this.audioClip = new AudioClip(SoundList.RIFLE.getPath());
		break;
	case RIFLE2 :
		this.audioClip = new AudioClip(SoundList.RIFLE2.getPath());
		break;
	case RIFLE3 :
		this.audioClip = new AudioClip(SoundList.RIFLE3.getPath());
		break;
	case SHELLS_FALL :
		this.audioClip = new AudioClip(SoundList.SHELLS_FALL.getPath());
		break;
	case SHOTGUN :
		this.audioClip = new AudioClip(SoundList.SHOTGUN.getPath());
		break;
	case SHOTGUN_SINGLE_RELOAD :
		this.audioClip = new AudioClip(SoundList.SHOTGUN_SINGLE_RELOAD.getPath());
		break;
	case SHOTGUN_SINGLE_RELOAD2 :
		this.audioClip = new AudioClip(SoundList.SHOTGUN_SINGLE_RELOAD2.getPath());
		break;
	case SHOTGUN2 :
		this.audioClip = new AudioClip(SoundList.SHOTGUN2.getPath());
		break;
	case SMG :
		this.audioClip = new AudioClip(SoundList.SMG.getPath());
		break;
	case SNIPER :
		this.audioClip = new AudioClip(SoundList.SNIPER.getPath());
		break;
}
this.audioClip.setVolume(mainVolume);
this.audioClip.play();
*/