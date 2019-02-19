package shared.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import client.gui.Settings;
import javafx.animation.AnimationTimer;
import javafx.scene.media.AudioClip;
import shared.GameSound;
import shared.lists.ActionList;
import shared.view.entity.EntityView;
import shared.view.entity.PlayerView;

public class SoundView {
	
	protected GameView gameView;
	protected Settings settings;
	protected HashMap<Integer, GameSound> playing;
	protected AudioClip audioClip;
	protected AnimationTimer t;
	
	
	public SoundView(GameView gameView, Settings settings) {
		this.gameView = gameView;
		this.settings = settings;
		this.playing = new HashMap<Integer, GameSound>();
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
					playSounds();
				}
				
			}
		};
		this.t.start();
	}
	
	public void deactivate() {
		this.t.stop();
	}
	
	public void playSounds() {
		for(PlayerView p : gameView.getPlayers()) {
			if(!p.getCurrentAction().equals(ActionList.NONE)) {
				if(playing.containsKey(p.getID())) {
					playing.get(p.getID()).setEntityView(p);
					if(!playing.get(p.getID()).getActionList().equals(p.getCurrentAction())) {
						playing.get(p.getID()).stop();
						playing.put(p.getID(), new GameSound(p, p.getCurrentAction(), this.settings.getSoundVolume()));
					}
					else if(p.getCurrentAction().equals(ActionList.ATTACKING)) {
						playing.get(p.getID()).replay();
					}
				}
				else {
					playing.put(p.getID(), new GameSound(p, p.getCurrentAction(), this.settings.getSoundVolume()));
				}
			}
		}
	}
	
	// NOT USED
	public void clear() {
		Iterator it = playing.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<EntityView, GameSound> pair = (Map.Entry<EntityView, GameSound>) it.next();
			if(!pair.getValue().isPlaying()) {
				it.remove();
			}
		}
	}

}