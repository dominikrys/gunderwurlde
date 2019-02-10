package client.inputhandler;

import client.ClientSender;
import client.data.entity.GameView;
import data.GameState;
import javafx.scene.Scene;

public abstract class UserInteraction {
	ClientSender sender;
	protected Scene scene;
	protected GameView gameView;
	protected boolean activated;
	
	public UserInteraction() {
		this.sender = null;
		this.scene = null;
		this.gameView = null;
		this.activated = false;
	}
	
	public UserInteraction(Scene scene, GameView gameView) {
		this.scene = scene;
		this.gameView = gameView;
	}
	
	public void setClientSender(ClientSender sender) {
		this.sender = sender;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	public void setGameView(GameView gameView) {
		this.gameView = gameView;
	}
	
	public void activate() {
		this.activated = true;
	}
	
	public void deactivate() {
		this.activated = false;
	}
	
	public boolean isActivated() {
		return this.activated;
	}

}
