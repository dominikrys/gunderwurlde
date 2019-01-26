package inputhandler;

import data.GameState;
import javafx.scene.Scene;

public class UserInteraction {
	protected Scene scene;
	protected GameState gameState;
	
	public UserInteraction(Scene scene, GameState gameState) {
		this.scene = scene;
		this.gameState = gameState;
		
		KeyboardHandler kbHandler = new KeyboardHandler(scene, gameState);
	    //MouseHandler mHandler = new MouseHandler(scene, gameState);
	}

}
