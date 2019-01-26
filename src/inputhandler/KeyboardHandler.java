package inputhandler;

import java.util.ArrayList;
import java.util.Iterator;

import data.GameState;
import data.Location;
import data.Pose;
import data.entity.player.Player;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

public class KeyboardHandler {
	
	private Image pImage;
	//private ImageView pImageV;
	private GraphicsContext gc;
	private Scene scene;
	private GameState gameState;
	private Player player;
	private ArrayList<String> input = new ArrayList<String>();
	private boolean wPressed = false;
	private boolean aPressed = false;
	private boolean sPressed = false;
	private boolean dPressed = false;
	
	public KeyboardHandler(String imagePath, GraphicsContext gc, Scene scene, GameState gameState) {
		//super(scene, gameState);
		this.pImage = new Image(imagePath);
		this.gc = gc;
		//this.pImageV = new ImageView(pImage);
		this.scene = scene;
		this.gameState = gameState;
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
				}
			}
		});
		
		AnimationTimer t = new AnimationTimer() {
			@Override
			public void handle(long now) {
				Pose pose = player.getPose();
				//if(mouseDegree != playerDegree) {
				//}
				if(wPressed) {
					pose.setY(pose.getY() - player.getMoveSpeed());
					//pCoordinate.setY(pCoordinate.getY() - player.getMoveSpeed());
					gc.drawImage(pImage, pose.getX(), pose.getY() - 1);
					//pImageV.setY(center(pose, pImage).getY());
					player.setPose(pose);
					System.out.println("here");
				}
				if(aPressed) {
					pose.setX(pose.getX() - player.getMoveSpeed());
					//pCoordinate.setX(pCoordinate.getX() - player.getMoveSpeed());
					//pImageV.setX(center(pose, pImage).getX());
					player.setPose(pose);
				}
				if(sPressed) {
					pose.setY(pose.getY() - player.getMoveSpeed());
					//pCoordinate.setY(pCoordinate.getY() + player.getMoveSpeed());
					//pImageV.setY(center(pose, pImage).getY());
					player.setPose(pose);
				}
				if(dPressed) {
					pose.setX(pose.getX() - player.getMoveSpeed());
					//pCoordinate.setX(pCoordinate.getX() + player.getMoveSpeed());
					//pImageV.setX(center(pose, pImage).getX());
					player.setPose(pose);
				}
			}
		};
		
		t.start();
		
	}
	
	/*
	public void handle() {
		Pose pose = player.getPose();
		
		if(wPressed) {
			pose.setY(pose.getY() - player.getMoveSpeed());
			//pCoordinate.setY(pCoordinate.getY() - player.getMoveSpeed());
			gc.drawImage(pImage, pose.getX(), pose.getY() - 1);
			//pImageV.setY(center(pose, pImage).getY());
			player.setPose(pose);
		}
		if(aPressed) {
			pose.setX(pose.getX() - player.getMoveSpeed());
			//pCoordinate.setX(pCoordinate.getX() - player.getMoveSpeed());
			//pImageV.setX(center(pose, pImage).getX());
			player.setPose(pose);
		}
		if(sPressed) {
			pose.setY(pose.getY() - player.getMoveSpeed());
			//pCoordinate.setY(pCoordinate.getY() + player.getMoveSpeed());
			//pImageV.setY(center(pose, pImage).getY());
			player.setPose(pose);
		}
		if(dPressed) {
			pose.setX(pose.getX() - player.getMoveSpeed());
			//pCoordinate.setX(pCoordinate.getX() + player.getMoveSpeed());
			//pImageV.setX(center(pose, pImage).getX());
			player.setPose(pose);
		}
	}
	*/
	
	public static Pose center(Pose target, Image image) {
		double width = image.getWidth();
		double height = image.getHeight();
		double centerX = target.getX() - width/2;
		double centerY = target.getY() - height/2;
		Pose center = new Pose((int)centerX, (int)centerY);
		return center;
	}

}
