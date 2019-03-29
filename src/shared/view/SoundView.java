package shared.view;

import client.Settings;
import javafx.scene.media.AudioClip;
import shared.GameSound;
import shared.lists.ActionList;
import shared.lists.SoundList;
import shared.view.entity.EnemyView;
import shared.view.entity.EntityView;
import shared.view.entity.PlayerView;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * SoundView class. Contains the view of all sounds in game.
 *
 * @author Mak Hong Lun Timothy
 */
public class SoundView {
	/**
     * clientID - ID of client
     */
	protected int clientID;
	/**
     * client - View on the client
     */
	protected PlayerView client;
	/**
     * gameView - View of game
     */
	protected GameView gameView;
	/**
     * settings - Settings of the client
     */
	protected Settings settings;
	/**
     * pPlaying - HashMap containing sounds generated from each player
     */
	protected HashMap<Integer, GameSound> pPlaying;
	/**
     * ePlaying - HashMap containing sounds generated from each enemy
     */
	protected HashMap<Integer, GameSound> ePlaying;
	/**
     * loadedGameSounds - Loaded game sounds
     */
	protected HashMap<SoundList, AudioClip> loadedGameSounds;
	/**
	 * timer - Timer for task scheduling
	 */
	private Timer timer;

	/**
	 * task - TimerTask for continuously checking the game view and play sounds
	 */
	private TimerTask task;
	
	/**
     * Constructor
     * 
     * @param clientID ID of client
     * @param gameView View of game
     * @param settings Settings of the client
     */
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
	}
	
	/**
     * Setter for gameView
     * 
     * @param gameView View of game
     */
	public void setGameView(GameView gameView) {
		this.gameView = gameView;
		for(PlayerView c : gameView.getPlayers()) {
			if(c.getID() == this.clientID) {
				this.client = c;
			}
		}
	}
	
	/**
     * Load all game sounds
     */
	public void loadGameSounds() {
		this.loadedGameSounds = new HashMap<SoundList, AudioClip>();
		EnumSet.allOf(SoundList.class).forEach(SoundList -> loadedGameSounds.put(SoundList, new AudioClip(SoundList.getPath())));
	}
	
	/**
     * Activate the timer and schedule it with task
     */
	public void activate() {
		this.timer = new Timer();
		
		this.task = new TimerTask() {
			@Override
			public void run() {
				if(!settings.isSoundMute()) {
					playSounds();
				}
			}
		};
		
		timer.scheduleAtFixedRate(task, 0, 1);
	}
	
	/**
     * Deactivate the timer
     */
	public void deactivate() {
		this.timer.cancel();
		this.timer.purge();
	}
	
	/**
     * Check the player, enemy and explosion view and play sounds according to their actions
     */
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
			else if(p.hasTakenDamage()) {
				if(pPlaying.containsKey(p.getID())) {
					pPlaying.get(p.getID()).playDamaged(this.client, p);
				}
				else {
					pPlaying.put(p.getID(), new GameSound(loadedGameSounds, this.client, p, ActionList.NONE, this.settings.getSoundVolume()));
				}
			}
		}
		
		for(EnemyView e : gameView.getEnemies()) {
			if(!e.getCurrentAction().equals(ActionList.NONE) && !e.getCurrentAction().equals(ActionList.DEAD)) {
				if(ePlaying.containsKey(e.getID())) {
					ePlaying.get(e.getID()).setEntityView(e);
					if(!ePlaying.get(e.getID()).getActionList().equals(e.getCurrentAction())) {
						ePlaying.get(e.getID()).stop();
						ePlaying.put(e.getID(), new GameSound(loadedGameSounds, this.client, e, e.getCurrentAction(), this.settings.getSoundVolume()));
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
			else if(e.hasTakenDamage()) {
				if(ePlaying.containsKey(e.getID())) {
					ePlaying.get(e.getID()).playDamaged(this.client, e);
				}
				else {
					ePlaying.put(e.getID(), new GameSound(loadedGameSounds, this.client, e, ActionList.NONE, this.settings.getSoundVolume()));
				}
			}
		}
		
		for(ExplosionView ex : gameView.getExplosions()) {
			GameSound explosion = new GameSound(loadedGameSounds, this.client, ex, this.settings.getSoundVolume());
		}
	}

}