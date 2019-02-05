package inputhandler;

import client.data.GameView;
import data.GameState;
import javafx.scene.Scene;

public abstract class UserInteraction {
	protected Scene scene;
	protected GameView gameView;
	
	public UserInteraction(Scene scene, GameView gameView) {
		this.scene = scene;
		this.gameView = gameView;
	}

}
