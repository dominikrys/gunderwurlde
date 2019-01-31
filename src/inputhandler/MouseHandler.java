package inputhandler;

import java.util.ArrayList;

import data.GameState;
import data.Location;
import data.Pose;
import data.entity.item.Item;
import data.entity.item.ItemList;
import data.entity.player.Player;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MouseHandler extends UserInteraction{
	
	//private Image pImage;
	private Scene scene;
	private Canvas mapCanvas;
	private GameState gameState;
	private Player player;
	private ArrayList<String> input = new ArrayList<String>();
	private double mouseX;
	private double mouseY;
	private double playerX;
	private double playerY;
	private double mouseDegree;
	private double playerDegree;
	double toRotate;
	
	public MouseHandler(Scene scene, Canvas mapCanvas, GameState gameState) {
		//this.pImage = new Image(imagePath);
		super(scene,gameState);
		this.scene = scene;
		this.mapCanvas = mapCanvas;
		this.gameState = gameState;
		// TODO: get name of client here
		for (Player p : gameState.getPlayers()) {
            if(p.getName() == "Player 1") {
            	this.player = p;
            	break;
            }
        }
		
		AnimationTimer T = new AnimationTimer() {
			@Override
			public void handle(long now) {
				System.out.println("shooting");
				// TODO: send shooting request here
			}
		};
		
		scene.addEventHandler(MouseEvent.ANY, e -> {
			if(e.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				if(e.getButton().toString().equals("PRIMARY")) {
					T.start();
				}
			}
			else if(e.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
				T.stop();
			}
			/*
			else if(e.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
				mouseX = e.getSceneX();
				mouseY = e.getSceneY();
				playerX = mapCanvas.getLayoutX() + player.getPose().getX();
				playerY = mapCanvas.getLayoutY() + player.getPose().getY();
				System.out.println("playerX: " + playerX);
				System.out.println("playerY: " + playerY);
				System.out.println("scenex: " + e.getSceneX() + ", sceney: " + e.getSceneY());
				
				toRotate = Math.toDegrees(Math.atan((mouseX - playerX)/(mouseY - playerY)));
				System.out.println("toRotate: " + toRotate);
				int quarter = quarter(playerX, playerY, mouseX, mouseY);
				System.out.println("quater: " + quarter);
				if(quarter == 1) {
					mouseDegree = 360 - toRotate;
				}
				else if(quarter == 2) {
					mouseDegree = 0 - toRotate;
				}
				else if(quarter == 3) {
					mouseDegree = 180 - toRotate;
				}
				else if(quarter == 4) {
					mouseDegree = 180 - toRotate;
				}
				if(mouseDegree == 360) {
					mouseDegree = 0;
				}
				playerDegree = mouseDegree;
				System.out.println("mouseDegree: " + mouseDegree);
				System.out.println("playerDegree: " + playerDegree);
				System.out.println(input.toString());
				// TODO: send changes(playerDegree) to server
			}
			*/
		});
		
		scene.addEventHandler(MouseEvent.ANY, e -> {
			if(e.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
				mouseX = e.getSceneX();
				mouseY = e.getSceneY();
				playerX = mapCanvas.getLayoutX() + player.getPose().getX();
				playerY = mapCanvas.getLayoutY() + player.getPose().getY();
				System.out.println("playerX: " + playerX);
				System.out.println("playerY: " + playerY);
				System.out.println("scenex: " + e.getSceneX() + ", sceney: " + e.getSceneY());
				
				toRotate = Math.toDegrees(Math.atan((mouseX - playerX)/(mouseY - playerY)));
				System.out.println("toRotate: " + toRotate);
				int quarter = quarter(playerX, playerY, mouseX, mouseY);
				System.out.println("quater: " + quarter);
				if(quarter == 1) {
					mouseDegree = 360 - toRotate;
				}
				else if(quarter == 2) {
					mouseDegree = 0 - toRotate;
				}
				else if(quarter == 3) {
					mouseDegree = 180 - toRotate;
				}
				else if(quarter == 4) {
					mouseDegree = 180 - toRotate;
				}
				if(mouseDegree == 360) {
					mouseDegree = 0;
				}
				playerDegree = mouseDegree;
				System.out.println("mouseDegree: " + mouseDegree);
				System.out.println("playerDegree: " + playerDegree);
				System.out.println(input.toString());
				// TODO: send changes(playerDegree) to server
			}
		});
		
		/*
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().toString().equals("PRIMARY")) {
					T.start();
				}
			}
		});
		
		scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				T.stop();
			}
		});

		scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseX = event.getSceneX();
				mouseY = event.getSceneY();
				playerX = mapCanvas.getLayoutX() + player.getPose().getX();
				playerY = mapCanvas.getLayoutY() + player.getPose().getY();
				System.out.println("playerX: " + playerX);
				System.out.println("playerY: " + playerY);
				System.out.println("scenex: " + event.getSceneX() + ", sceney: " + event.getSceneY());
				
				toRotate = Math.toDegrees(Math.atan((mouseX - playerX)/(mouseY - playerY)));
				System.out.println("toRotate: " + toRotate);
				int quarter = quarter(playerX, playerY, mouseX, mouseY);
				System.out.println("quater: " + quarter);
				if(quarter == 1) {
					mouseDegree = 360 - toRotate;
				}
				else if(quarter == 2) {
					mouseDegree = 0 - toRotate;
				}
				else if(quarter == 3) {
					mouseDegree = 180 - toRotate;
				}
				else if(quarter == 4) {
					mouseDegree = 180 - toRotate;
				}
				if(mouseDegree == 360) {
					mouseDegree = 0;
				}
				playerDegree = mouseDegree;
				System.out.println("mouseDegree: " + mouseDegree);
				System.out.println("playerDegree: " + playerDegree);
				System.out.println(input.toString());
				// TODO: send changes(playerDegree) to server
			}
		});
		
		*/
		
		scene.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if(event.getDeltaY() > 0) {
					player.previousItem();
				}
				else if(event.getDeltaY() < 0) {
					player.nextItem();
				}
				System.out.println(player.getCurrentItemIndex());
				// TODO: send changes(item change) to server
			}
		});
		
	}
	
	public static int quarter(double playerX, double playerY, double destinationX, double destinationY) {
		double xDif = destinationX - playerX;
		double yDif = destinationY - playerY;
		if(xDif <= 0 && yDif < 0) {
			return 1;
		}
		else if(xDif > 0 && yDif < 0) {
			return 2;
		}
		else if(xDif <=0 && yDif >= 0) {
			return 3;
		}
		else return 4;
	}

}
