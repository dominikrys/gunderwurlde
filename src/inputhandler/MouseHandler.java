package inputhandler;

import java.util.ArrayList;

import client.data.GameView;
import client.data.PlayerView;
import data.GameState;
import data.entity.item.Item;
import data.entity.item.ItemList;
import data.entity.item.weapon.gun.Pistol;
import data.entity.player.Player;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MouseHandler extends UserInteraction{
	
	private Scene scene;
	private Canvas mapCanvas;
	private GameView gameView;
	private PlayerView playerView;
	private Attack attack;
	private double mouseX;
	private double mouseY;
	private double playerX;
	private double playerY;
	private double mouseDegree;
	private double playerDegree;
	private double toRotate;
	
	public MouseHandler(Scene scene, Canvas mapCanvas, GameView gameView) {
		super(scene,gameView);
		this.scene = scene;
		this.mapCanvas = mapCanvas;
		this.gameView = gameView;
		// TODO: get name of client here
		for (PlayerView p : gameView.getPlayers()) {
            if(p.getName() == "Player 1") {
            	this.playerView = p;
            	break;
            }
        }
		this.attack = new Attack(playerView);
		
		scene.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
			mouseMovement(e);
		});
		
		scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			mouseMovement(e);
			attack.attack();
		});
		
		scene.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if(event.getDeltaY() > 0) {
					playerView.previousItem();
				}
				else if(event.getDeltaY() < 0) {
					playerView.nextItem();
				}
				System.out.println(playerView.getCurrentItemIndex());
				// TODO: send changes(item change) to server
			}
		});
		
	}
	
	private static int quarter(double playerX, double playerY, double destinationX, double destinationY) {
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
	
	private void mouseMovement(MouseEvent e) {
		mouseX = e.getSceneX();
		mouseY = e.getSceneY();
		playerX = mapCanvas.getLayoutX() + playerView.getPose().getX();
		playerY = mapCanvas.getLayoutY() + playerView.getPose().getY();
		
		toRotate = Math.toDegrees(Math.atan((mouseX - playerX)/(mouseY - playerY)));
		int quarter = quarter(playerX, playerY, mouseX, mouseY);
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
		System.out.println("playerDegree: " + playerDegree);
		// TODO: send changes(playerDegree) to server
	}

}
