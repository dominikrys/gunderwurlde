package shared.view;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import client.gui.Settings;
import javafx.animation.AnimationTimer;
import javafx.scene.media.AudioClip;
import shared.GameSound;
import shared.lists.ActionList;
import shared.lists.SoundList;
import shared.view.entity.EnemyView;
import shared.view.entity.EntityView;
import shared.view.entity.PlayerView;

public class SoundView {
	
	protected int clientID;
	protected PlayerView client;
	protected GameView gameView;
	protected Settings settings;
	protected HashMap<Integer, GameSound> pPlaying;
	protected HashMap<Integer, GameSound> ePlaying;
	protected HashMap<SoundList, AudioClip> loadedGameSounds;
	protected AnimationTimer t;
	
	
	public SoundView(int clientID, GameView gameView, Settings settings) {
		this.clientID = clientID;
		for(PlayerView c : gameView.getPlayers()) {
			if(c.getID() == this.clientID) {
				this.client = c;
			}
		}
		this.gameView = gameView;
		this.settings = settings;
		this.pPlaying = new HashMap<Integer, GameSound>();
		this.ePlaying = new HashMap<Integer, GameSound>();
		this.loadGameSounds();
		this.t = null;
	}
	
	public void setGameView(GameView gameView) {
		this.gameView = gameView;
		for(PlayerView c : gameView.getPlayers()) {
			if(c.getID() == this.clientID) {
				this.client = c;
			}
		}
	}
	
	public void loadGameSounds() {
		this.loadedGameSounds = new HashMap<SoundList, AudioClip>();
		EnumSet.allOf(SoundList.class).forEach(SoundList -> loadedGameSounds.put(SoundList, new AudioClip(SoundList.getPath())));
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
			if(!p.getCurrentAction().equals(ActionList.NONE) && !p.getCurrentAction().equals(ActionList.DEAD)) {
				if(pPlaying.containsKey(p.getID())) {
					pPlaying.get(p.getID()).setEntityView(p);
					if(!pPlaying.get(p.getID()).getActionList().equals(p.getCurrentAction())) {
						pPlaying.get(p.getID()).stop();
						pPlaying.put(p.getID(), new GameSound(loadedGameSounds, this.client, p, p.getCurrentAction(), this.settings.getSoundVolume()));
					}
					else if(p.getCurrentAction().equals(ActionList.ATTACKING) || p.getCurrentAction().equals(ActionList.RELOADING)) {
						if(pPlaying.get(p.getID()).getReplayable()) {
							pPlaying.put(p.getID(), new GameSound(loadedGameSounds, this.client, p, p.getCurrentAction(), this.settings.getSoundVolume()));
						}
					}
				}
				else {
					pPlaying.put(p.getID(), new GameSound(loadedGameSounds, this.client, p, p.getCurrentAction(), this.settings.getSoundVolume()));
				}
			}
			else if(p.getCurrentAction().equals(ActionList.DEAD)) {
				//pPlaying.get(p.getID()).stop();
			}
		}
		
		for(EnemyView e : gameView.getEnemies()) {
			if(!e.getCurrentAction().equals(ActionList.NONE) && !e.getCurrentAction().equals(ActionList.DEAD)) {
				if(ePlaying.containsKey(e.getID())) {
					ePlaying.get(e.getID()).setEntityView(e);
					if(!ePlaying.get(e.getID()).getActionList().equals(e.getCurrentAction())) {
						pPlaying.get(e.getID()).stop();
						pPlaying.put(e.getID(), new GameSound(loadedGameSounds, this.client, e, e.getCurrentAction(), this.settings.getSoundVolume()));
					}
					else if(e.getCurrentAction().equals(ActionList.ATTACKING) || e.getCurrentAction().equals(ActionList.RELOADING)) {
						if(ePlaying.get(e.getID()).getReplayable()) {
							ePlaying.put(e.getID(), new GameSound(loadedGameSounds, this.client, e, e.getCurrentAction(), this.settings.getSoundVolume()));
						}
					}
				}
				else {
					ePlaying.put(e.getID(), new GameSound(loadedGameSounds, this.client, e, e.getCurrentAction(), this.settings.getSoundVolume()));
				}
			}
			else if(e.getCurrentAction().equals(ActionList.DEAD)) {
				//ePlaying.get(e.getID()).stop();
			}
		}
	}
	
	// NOT USED
	public void clear() {
		Iterator it = pPlaying.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<EntityView, GameSound> pair = (Map.Entry<EntityView, GameSound>) it.next();
			if(!pair.getValue().isPlaying()) {
				it.remove();
			}
		}
	}

}