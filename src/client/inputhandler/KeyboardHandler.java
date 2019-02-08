package client.inputhandler;

import java.util.ArrayList;

import client.ClientSender;
import client.data.entity.EntityView;
import client.data.entity.GameView;
import client.data.entity.PlayerView;
import data.GameState;
import data.Pose;
import data.entity.EntityList;
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
	private DropItem dropItem;
	private PickItem pickItem;
	private ArrayList<String> input = new ArrayList<String>();
	private KeyboardSettings kbSettings = new KeyboardSettings();
	private boolean upPressed = false;
	private boolean leftPressed = false;
	private boolean downPressed = false;
	private boolean rightPressed = false;
	private boolean reloadPressed = false;
	private boolean dropPressed = false;
	private boolean interactPressed = false;
	private AnimationTimer t;
	private boolean activated;
	ClientSender sender;
	
	public KeyboardHandler() {
		super();
		this.t = null;
	}
	
	public KeyboardHandler(ClientSender sender, Scene scene, GameView gameView) {
		super(scene, gameView);
		this.sender = sender;
		this.scene = scene;
		this.gameView = gameView;
		/*
		// TODO: get player here
		for (PlayerView p : gameView.getPlayers()) {
            if(p.getName() == "Player 1") {
            	this.playerView = p;
            	break;
            }
        }
		this.pImage = new Image(playerView.getPathToGraphic());
		this.movement = new Movement(playerView, pImage, gameView.getTileMap(), kbSettings, gameView.getItemDrops());
		this.reload = new Reload(playerView);
		this.dropItem = new DropItem(playerView);
		
		
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
		*/
		
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
					dropItem.drop();
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
	
	@Override
	public void setScene(Scene scene) {
		this.scene = scene;
		
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
	}
	
	@Override
	public void setGameView(GameView gameView) {
		super.setGameView(gameView);
		
		for (PlayerView p : gameView.getPlayers()) {
            if(p.getName() == "Bob") {
            	this.playerView = p;
            	break;
            }
        }
		this.pImage = new Image(EntityList.PLAYER.getPath());
		this.movement = new Movement(sender, playerView, pImage, gameView.getTileMap(), kbSettings, gameView.getItemDrops());
		this.reload = new Reload(playerView);
		this.dropItem = new DropItem(playerView);
	}
	
	
	
	@Override
	public void activate() {
		super.activate();
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
					dropItem.drop();
				}
				if(interactPressed) {
					
				}
			}
		};
		
		t.start();
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
		t.stop();
	}
	
	@Override
	public boolean isActivated() {
		return super.isActivated();
	}
	
	public void setClientSender(ClientSender sender) {
		this.sender = sender;
	}

}
