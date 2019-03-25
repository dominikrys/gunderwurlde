package client.input;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import shared.Constants;
import shared.lists.ItemType;
import shared.view.GameView;
import shared.view.GunView;
import shared.view.entity.PlayerView;

public class MouseHandler extends UserInteraction {

	private int playerID;
    private Canvas mapCanvas;
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
    private boolean hold;
    private double distance;

    public MouseHandler(int playerID) {
        super();
        this.t = null;
        this.playerID = playerID;
        this.hold = false;
    }

    private static int quarter(double playerX, double playerY, double destinationX, double destinationY) {
        double xDif = destinationX - playerX;
        double yDif = destinationY - playerY;
        if (xDif <= 0 && yDif < 0) {
            return 1;
        } else if (xDif > 0 && yDif < 0) {
            return 2;
        } else if (xDif <= 0 && yDif >= 0) {
            return 3;
        } else return 4;
    }

    private void mouseMovement(MouseEvent e) {
        mouseX = e.getSceneX();
        mouseY = e.getSceneY();
        playerX = mapCanvas.getTranslateX() + playerView.getPose().getX() + Constants.TILE_SIZE / 2;
        playerY = mapCanvas.getTranslateY() + playerView.getPose().getY() + Constants.TILE_SIZE / 2;
        // System.out.println("mouseX: " + mouseX);
        // System.out.println("mouseY: " + mouseY);
        // System.out.println("playerX: " + playerX);
        // System.out.println("playerY: " + playerY);

        toRotate = Math.toDegrees(Math.atan((mouseX - playerX) / (mouseY - playerY)));
        int quarter = quarter(playerX, playerY, mouseX, mouseY);
        if (quarter == 1) {
            mouseDegree = 360 - toRotate;
        } else if (quarter == 2) {
            mouseDegree = 0 - toRotate;
        } else if (quarter == 3) {
            mouseDegree = 180 - toRotate;
        } else if (quarter == 4) {
            mouseDegree = 180 - toRotate;
        }
        if (mouseDegree == 360) {
            mouseDegree = 0;
        }
        playerDegree = mouseDegree;
        // System.out.println("playerDegree: " + playerDegree);
        // TODO: send changes(playerDegree) to server
        handler.send(CommandList.TURN, (int) playerDegree - 90);

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
				distance = Math.sqrt((mouseX - playerX)*(mouseX - playerX) + (mouseY - playerY)*(mouseY - playerY));
				attack.attack(distance);
				this.hold = true;
			}
			else {
				mouseMovement(e);
			}
		});
		
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			if(e.isPrimaryButtonDown()) {
				distance = Math.sqrt((mouseX - playerX)*(mouseX - playerX) + (mouseY - playerY)*(mouseY - playerY));
				attack.attack(distance);
				this.hold = true;
			}
		});
		
		scene.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
			if(e.getButton().toString().equals("PRIMARY")) {
				this.hold = false;
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
				//System.out.println(playerView.getCurrentItemIndex());
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
            if (p.getID() == this.playerID) {
                this.playerView = p;
                break;
            }
        }
        this.attack = new Attack(handler, playerView);
        this.changeItem = new ChangeItem(handler, playerView);
    }

    @Override
    public void activate() {
        super.activate();
        this.t = new AnimationTimer() {
			@Override
			public void handle(long now) {
				
				if(hold == true) {
					//if(playerView.getCurrentItem().isAutoFire()) {
						distance = Math.sqrt((mouseX - playerX)*(mouseX - playerX) + (mouseY - playerY)*(mouseY - playerY));
						attack.attack(distance);
					//}
				}
			}
		};
		this.t.start();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        this.t.stop();
    }

    @Override
    public boolean isActivated() {
        return super.isActivated();
    }


}
