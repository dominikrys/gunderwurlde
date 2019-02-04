package inputhandler;

import java.util.ArrayList;
import java.util.Iterator;

import client.data.GameView;
import client.data.PlayerView;
import data.Pose;
import data.entity.item.Item;
import data.entity.item.weapon.gun.Pistol;
import data.entity.player.Player;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

public class KeyboardHandler extends UserInteraction{
	
	private Scene scene;
	private GameView gameView;
	private PlayerView playerView;
	private Image pImage;
	private Movement movement;
	private Reload reload;
	private ArrayList<String> input = new ArrayList<String>();
	private KeyboardSettings kbSettings = new KeyboardSettings();
	private boolean upPressed = false;
	private boolean leftPressed = false;
	private boolean downPressed = false;
	private boolean rightPressed = false;
	private boolean reloadPressed = false;
	private boolean dropPressed = false;
	private boolean interactPressed = false;
	
	public KeyboardHandler(Scene scene, GameView gameView) {
		super(scene, gameView);
		this.scene = scene;
		this.gameView = gameView;
		// TODO: get player here
		for (PlayerView p : gameView.getPlayers()) {
            if(p.getName() == "Player 1") {
            	this.playerView = p;
            	break;
            }
        }
		this.pImage = new Image(playerView.getPathToGraphic());
		this.movement = new Movement(playerView, pImage, gameView.getTileMap(), kbSettings);
		this.reload = new Reload(playerView);
		
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String pressed = event.getCode().toString();
				if (!input.contains(pressed)) {
					input.add(pressed);
					System.out.println(input.toString());
					if (kbSettings.getKey("up").equals(pressed)) {
						upPressed = true;
					}
					if (kbSettings.getKey("left").equals(pressed)) {
						leftPressed = true;
					}
					if (kbSettings.getKey("down").equals(pressed)) {
						downPressed = true;
					}
					if (kbSettings.getKey("right").equals(pressed)) {
						rightPressed = true;
					}
					if (kbSettings.getKey("reload").equals(pressed)) {
						reloadPressed = true;
					}
					if (kbSettings.getKey("drop").equals(pressed)) {
						dropPressed = true;
					}
					if (kbSettings.getKey("interact").equals(pressed)) {
						interactPressed = true;
					}
				}
			}
		});
		
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String released = event.getCode().toString();
				input.remove(released);
				System.out.println(input.toString());
				if (kbSettings.getKey("up").equals(released)) {
					upPressed = false;
				}
				if (kbSettings.getKey("left").equals(released)) {
					leftPressed = false;
				}
				if (kbSettings.getKey("down").equals(released)) {
					downPressed = false;
				}
				if (kbSettings.getKey("right").equals(released)) {
					rightPressed = false;
				}
				if (kbSettings.getKey("reload").equals(released)) {
					reloadPressed = false;
				}
				if (kbSettings.getKey("drop").equals(released)) {
					dropPressed = false;
				}
				if (kbSettings.getKey("interact").equals(released)) {
					interactPressed = false;
				}
			}
		});
		
		AnimationTimer t = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if(upPressed || leftPressed || downPressed || rightPressed) {
					for(int i = 0 ; i < input.size() ; i++) {
						String key = input.get(i);
						if(key.equals(kbSettings.getKey("up")) ||
								key.equals(kbSettings.getKey("left")) ||
								key.equals(kbSettings.getKey("down")) ||
								key.equals(kbSettings.getKey("right"))) {
							movement.move(key);
						}
					}
				}
				if(reloadPressed) {
					reload.reload();
				}
				if(dropPressed) {
					
				}
				if(interactPressed) {
					
				}
			}
		};
		
		t.start();
		
	}
	
	// NOT USED
	public static Pose center(Pose target, Image image) {
		double width = image.getWidth();
		double height = image.getHeight();
		double centerX = target.getX() - width/2;
		double centerY = target.getY() - height/2;
		Pose center = new Pose((int)centerX, (int)centerY);
		return center;
	}

}
