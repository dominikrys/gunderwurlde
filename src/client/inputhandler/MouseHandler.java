package client.inputhandler;

import client.data.entity.GameView;
import client.data.entity.PlayerView;
import javafx.animation.AnimationTimer;
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
	private ChangeItem changeItem;
	private double mouseX;
	private double mouseY;
	private double playerX;
	private double playerY;
	private double mouseDegree;
	private double playerDegree;
	private double toRotate;
	private AnimationTimer t;
	private boolean activated;
	
	public MouseHandler() {
		super();
		this.t = null;
	}
	
	public MouseHandler(Scene scene, Canvas mapCanvas, GameView gameView) {
		super(scene,gameView);
		this.scene = scene;
		this.mapCanvas = mapCanvas;
		this.gameView = gameView;
		// TODO: get name of client here
		for (PlayerView p : gameView.getPlayers()) {
            if(p.getName() == "Bob") {
            	this.playerView = p;
            	break;
            }
        }
		this.attack = new Attack(playerView);
		
		/*
		scene.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
			mouseMovement(e);
		});
		
		scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			if(e.isPrimaryButtonDown()) {
				mouseMovement(e);
				attack.attack();
			}
			else {
				mouseMovement(e);
			}
		});
		
		scene.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if(event.getDeltaY() > 0) {
					changeItem.previousItem();
				}
				else if(event.getDeltaY() < 0) {
					changeItem.nextItem();
				}
				System.out.println(playerView.getCurrentItemIndex());
				// TODO: send changes(item change) to server
			}
		});
		*/
		
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
		playerX = mapCanvas.getLayoutX() + playerView.getPose().getX() + 16;
		playerY = mapCanvas.getLayoutY() + playerView.getPose().getY() + 16;
		System.out.println("mouseX: "+ mouseX);
		System.out.println("mouseY: "+ mouseY);
		System.out.println("playerX: "+ playerX);
		System.out.println("playerY: "+ playerY);
		
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
	
	@Override
	public void setScene(Scene scene) {
		super.setScene(scene);
		
		scene.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
			mouseMovement(e);
		});
		
		scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			if(e.isPrimaryButtonDown()) {
				mouseMovement(e);
				attack.attack();
			}
			else {
				mouseMovement(e);
			}
		});
		
		scene.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if(event.getDeltaY() > 0) {
					changeItem.previousItem();
				}
				else if(event.getDeltaY() < 0) {
					changeItem.nextItem();
				}
				System.out.println(playerView.getCurrentItemIndex());
				// TODO: send changes(item change) to server
			}
		});
	}
	
	public void setCanvas(Canvas mapCanvas) {
		this.mapCanvas = mapCanvas;
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
		this.attack = new Attack(playerView);
	}

	@Override
	public void activate() {
		super.activate();
	}

	@Override
	public void deactivate() {
		super.deactivate();
	}

	@Override
	public boolean isActivated() {
		return super.isActivated();
	}
	
	

}
