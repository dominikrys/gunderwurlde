package inputhandler;

import java.util.ArrayList;
import java.util.Iterator;
import data.GameState;
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
	private GameState gameState;
	private Player player;
	private Image pImage;
	private Collision collision;
	private ArrayList<String> input = new ArrayList<String>();
	private KeyboardSettings kbSettings = new KeyboardSettings();
	private boolean upPressed = false;
	private boolean leftPressed = false;
	private boolean downPressed = false;
	private boolean rightPressed = false;
	private boolean reloadPressed = false;
	
	public KeyboardHandler(Scene scene, GameState gameState) {
		super(scene, gameState);
		this.scene = scene;
		this.gameState = gameState;
		this.pImage = new Image("file:assets/img/player.png");
		this.collision = new Collision(gameState.getCurrentMap().getTileMap(), pImage);
		Iterator<Player> playerIterator = gameState.getPlayers().iterator();
		for (Player p : gameState.getPlayers()) {
            if(p.getName() == "Player 1") {
            	this.player = p;
            	break;
            }
        }
		
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
			}
		});
		
		AnimationTimer t = new AnimationTimer() {
			@Override
			public void handle(long now) {
				Pose pose = new Pose(player.getPose().getX(), player.getPose().getY(), player.getPose().getDirection());
				if(upPressed || leftPressed || downPressed || rightPressed) {
					if(upPressed) {
						pose.setY(pose.getY() - player.getMoveSpeed());
					}
					if(leftPressed) {
						pose.setX(pose.getX() - player.getMoveSpeed());
					}
					if(downPressed) {
						pose.setY(pose.getY() + player.getMoveSpeed());
					}
					if(rightPressed) {
						pose.setX(pose.getX() + player.getMoveSpeed());
					}
					if(collision.checkBoundary(pose.getX(), pose.getY())) {
						player.setPose(pose);
					}
				}
				if(reloadPressed) {
					Item cItem = player.getCurrentItem();
					if(cItem instanceof Pistol) {
						Pistol currentItem = (Pistol)cItem;
						if(currentItem.attemptReload()) {
							// TODO: send reload request here
						}
						else {
							// TODO: reload fail stuff here
						}
					}
				}
				
				// TODO: send changes(player location, reload) to server
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
