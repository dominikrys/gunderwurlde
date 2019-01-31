package inputhandler;

import data.GameState;
import javafx.scene.Scene;

public abstract class UserInteraction {
	protected Scene scene;
	protected GameState gameState;
	
	public UserInteraction(Scene scene, GameState gameState) {
		this.scene = scene;
		this.gameState = gameState;
	}

}
