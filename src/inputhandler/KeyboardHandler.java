package inputhandler;

import java.util.ArrayList;
import java.util.Iterator;

import data.GameState;
import data.Pose;
import data.entity.item.Item;
import data.entity.item.ItemList;
import data.entity.item.weapon.Pistol;
import data.entity.player.Player;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

public class KeyboardHandler extends UserInteraction{
	
	//private Image pImage;
	private Scene scene;
	private GameState gameState;
	private Player player;
	private ArrayList<String> input = new ArrayList<String>();
	private boolean wPressed = false;
	private boolean aPressed = false;
	private boolean sPressed = false;
	private boolean dPressed = false;
	private boolean rPressed = false;
	
	public KeyboardHandler(Scene scene, GameState gameState) {
		//this.pImage = new Image(imagePath);
		super(scene, gameState);
		this.scene = scene;
		this.gameState = gameState;
		Iterator<Player> playerIterator = gameState.getPlayers().iterator();
		for (Player p : gameState.getPlayers()) {
            if(p.getName() == "Player 1") {
            	this.player = p;
            	//System.out.println("X: " + p.getPose().getX());
            	//System.out.println("Y: " + p.getPose().getY());
            	//System.out.println("D: " + p.getPose().getDirection());
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
					switch(pressed) {
						case "W" :
							wPressed = true;
							break;
						case "A" :
							aPressed = true;
							break;
						case "S" :
							sPressed = true;
							break;
						case "D" :
							dPressed = true;
							break;
						case "R" :
							rPressed = true;
							break;
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
				switch(released) {
					case "W" :
						wPressed = false;
						break;
					case "A" :
						aPressed = false;
						break;
					case "S" :
						sPressed = false;
						break;
					case "D" :
						dPressed = false;
						break;
					case "R" :
						rPressed = false;
						break;
				}
			}
		});
		
		AnimationTimer t = new AnimationTimer() {
			@Override
			public void handle(long now) {
				Pose pose = player.getPose();
				if(wPressed) {
					pose.setY(pose.getY() - player.getMoveSpeed());
					player.setPose(pose);
				}
				if(aPressed) {
					pose.setX(pose.getX() - player.getMoveSpeed());
					player.setPose(pose);
				}
				if(sPressed) {
					pose.setY(pose.getY() - player.getMoveSpeed());
					player.setPose(pose);
				}
				if(dPressed) {
					pose.setX(pose.getX() - player.getMoveSpeed());
					player.setPose(pose);
				}
				if(rPressed) {
					ItemList iList = player.getCurrentItem().getItemID();
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
	
	public static Pose center(Pose target, Image image) {
		double width = image.getWidth();
		double height = image.getHeight();
		double centerX = target.getX() - width/2;
		double centerY = target.getY() - height/2;
		Pose center = new Pose((int)centerX, (int)centerY);
		return center;
	}

}
