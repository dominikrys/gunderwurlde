package client.inputhandler;

import client.ClientHandler;
import client.data.entity.GameView;
import javafx.scene.Scene;

public abstract class UserInteraction {
	protected ClientHandler handler;
	protected Scene scene;
	protected GameView gameView;
	protected boolean activated;
	
	public UserInteraction() {
		this.handler = null;
		this.scene = null;
		this.gameView = null;
		this.activated = false;
	}
	
	public void setClientHandler(ClientHandler handler) {
		this.handler = handler;
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
