package client.input;

import client.Client;
import javafx.scene.Scene;
import shared.view.GameView;

/**
 * UserInteraction class. This is the abstract class for input handlers.
 *
 * @author Mak Hong Lun Timothy
 */
public abstract class UserInteraction {
	
	/**
     * Client handler for sending requests
     */
	protected Client handler;
	
	/**
     * Scene where inputs are captured
     */
	protected Scene scene;
	
	/**
     * GameView that contains game info
     */
	protected GameView gameView;
	
	/**
     * Boolean whether this handler is active
     */
	protected boolean activated;
	
	/**
     * Constructor
     *
     */
	public UserInteraction() {
		this.handler = null;
		this.scene = null;
		this.gameView = null;
		this.activated = false;
	}
	
	/**
     * Setter for client handler
     * 
     * @param handler Client handler
     */
	public void setClientHandler(Client handler) {
		this.handler = handler;
	}
	
	/**
     * Setter for scene
     * 
     * @param scene Scene
     */
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	/**
     * Setter game view
     * 
     * @param gameView Game view
     */
	public void setGameView(GameView gameView) {
		this.gameView = gameView;
	}
	
	/**
     * Method for activating the handler
     *
     */
	public void activate() {
		this.activated = true;
	}
	
	/**
     * Method for deactivating the handler
     *
     */
	public void deactivate() {
		this.activated = false;
	}
	
	/**
     * Check if handler is activated
     *
     * @return Boolean whether the handler is activated
     */
	public boolean isActivated() {
		return this.activated;
	}

}
