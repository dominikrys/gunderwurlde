package inputhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import data.GameState;
import data.Pose;
import data.entity.item.Item;
import data.entity.item.weapon.gun.Pistol;
import data.entity.player.Player;
import data.map.GameMap;
import data.map.tile.Tile;
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
	private ArrayList<String> input = new ArrayList<String>();
	private boolean wPressed = false;
	private boolean aPressed = false;
	private boolean sPressed = false;
	private boolean dPressed = false;
	private boolean rPressed = false;
	private HashMap<Integer, ArrayList<Integer>> obstacle = new HashMap<Integer, ArrayList<Integer>>();
	
	public KeyboardHandler(Scene scene, GameState gameState) {
		super(scene, gameState);
		this.scene = scene;
		this.gameState = gameState;
		this.pImage = new Image("file:assets/img/player.png");
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
		getBoundary(gameState.getCurrentMap());
		for (Map.Entry<Integer, ArrayList<Integer>> entry : obstacle.entrySet()) {
		    String key = entry.getKey().toString();
		    String value = entry.getValue().toString();
		    System.out.println("key, " + key + " value " + value);
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
				if(wPressed || aPressed || sPressed || dPressed) {
					if(wPressed) {
						pose.setY(pose.getY() - player.getMoveSpeed());
					}
					if(aPressed) {
						pose.setX(pose.getX() - player.getMoveSpeed());
					}
					if(sPressed) {
						pose.setY(pose.getY() + player.getMoveSpeed());
					}
					if(dPressed) {
						pose.setX(pose.getX() + player.getMoveSpeed());
					}
					if(checkBoundary(pose.getX(), pose.getY())) {
						player.setPose(pose);
					}
					System.out.println("playerX: " + pose.getX());
					System.out.println("playerY: " + pose.getY());
				}
				if(rPressed) {
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
	
	private void getBoundary(GameMap map) {
		Tile[][] tileMap = map.getTileMap();
		ArrayList<Integer> columns = new ArrayList<Integer>();
		for(int row = 0 ; row < map.getXDim() ; row++) {
			columns = new ArrayList<Integer>();
			for(int column = 0 ; column < map.getYDim() ; column++) {
				if(tileMap[row][column].getState().toString().equals("SOLID")) {
					columns.add(column);
					System.out.println(column);
				}
			}
			if(!columns.isEmpty()) {
				obstacle.put(row, columns);
			}
		}
	}
	
	
	private boolean checkBoundary(int toGoX, int toGoY) {
		double top = toGoY - pImage.getHeight()/2;
		double down = toGoY + pImage.getHeight()/2;
		double left = toGoX - pImage.getWidth()/2;
		double right = toGoX + pImage.getWidth()/2;
		int[] upward = checkTile(toGoX, top);
		System.out.println(Arrays.toString(upward));
		int[] downward = checkTile(toGoX, down);
		System.out.println(Arrays.toString(downward));
		int[] leftward = checkTile(left, toGoY);
		System.out.println(Arrays.toString(leftward));
		int[] rightward = checkTile(right, toGoY);
		System.out.println(Arrays.toString(rightward));
		
		if(obstacle.containsKey(upward[1])) {
			if(obstacle.get(upward[1]).contains(upward[0])) {
				// bump
				System.out.println("upward bump");
				return false;
			}
			// move
		}
		else if(obstacle.containsKey(downward[1])) {
			if(obstacle.get(downward[1]).contains(downward[0])) {
				// bump
				System.out.println("downward bump");
				return false;
			}
			// move
		}
		else if(obstacle.containsKey(leftward[1])) {
			if(obstacle.get(leftward[1]).contains(leftward[0])) {
				// bump
				System.out.println("leftward bump");
				return false;
			}
			// move
		}
		else if(obstacle.containsKey(rightward[1])) {
			if(obstacle.get(rightward[1]).contains(rightward[0])) {
				// bump
				System.out.println("rightward bump");
				return false;
			}
			// move
		}
		// move
		return true;
	}
	
	
	private int[] checkTile(double x, double y) {
		int[] tile = {(int) (x/Tile.TILE_SIZE), (int) (y/Tile.TILE_SIZE)};
		return tile;
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
