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

public class MouseHandler {
	
	private Image pImage;
	private Scene scene;
	private Canvas mapCanvas;
	private GameState gameState;
	private Player player;
	private ArrayList<String> input = new ArrayList<String>();
	private boolean leftClicked = false;
	private boolean rightClicked = false;
	private double mouseX;
	private double mouseY;
	private double playerX;
	private double playerY;
	private double mouseDegree;
	private double playerDegree;
	double toRotate;
	
	public MouseHandler(String imagePath, Scene scene, Canvas mapCanvas, GameState gameState) {
		this.pImage = new Image(imagePath);
		this.scene = scene;
		this.mapCanvas = mapCanvas;
		this.gameState = gameState;
		for (Player p : gameState.getPlayers()) {
            if(p.getName() == "Player 1") {
            	this.player = p;
            	break;
            }
        }
		
		/*
		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				String pressed = event.getButton().toString();
				System.out.println("clicked:" + pressed);
				if(!input.contains(pressed)) {
					input.add(pressed);
					System.out.println(input.toString());
					switch(pressed) {
						case "PRIMARY" :
							leftClicked = true;
							break;
						case "SECONDARY" :
							rightClicked = true;
							break;
					}
				}
			}
		});
		*/
		
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				String pressed = event.getButton().toString();
				System.out.println("clicked:" + pressed);
				if(!input.contains(pressed)) {
					input.add(pressed);
					System.out.println(input.toString());
					switch(pressed) {
						case "PRIMARY" :
							leftClicked = true;
							break;
						case "SECONDARY" :
							rightClicked = true;
							break;
					}
				}
			}
		});
		
		/*
		scene.setOnMouseDragReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				String released = event.getButton().toString();
				System.out.println("released:" + released);
				input.remove(released);
				System.out.println(input.toString());
				switch(released) {
					case "PRIMARY" :
						leftClicked = false;
						break;
					case "SECONDARY" :
						rightClicked = false;
						break;
				}
			}
		});
		*/
		
		/*
		scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				String released = event.getButton().toString();
				System.out.println("released:" + released);
				input.remove(released);
				System.out.println(input.toString());
				switch(released) {
					case "PRIMARY" :
						System.out.println("herherherherheh");
						leftClicked = false;
						break;
					case "SECONDARY" :
						System.out.println("herherherherheh");
						rightClicked = false;
						break;
				}
			}
		});
		*/
		
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
		
		AnimationTimer t = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if(leftClicked) {
					//System.out.println("left holding");
				}
				if(rightClicked) {
					//System.out.println("right holding");
				}
			}
		};
		
		t.start();
		
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
