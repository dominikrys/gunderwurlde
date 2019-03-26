package client.input;

import client.Client;
import client.Settings;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import shared.lists.ItemType;
import shared.view.GameView;
import shared.view.entity.PlayerView;
import shared.view.GunView;

/**
 * MouseHandler class. This is the class for mouse inputs.
 *
 * @author Mak Hong Lun Timothy
 */
public class MouseHandler extends UserInteraction {

	/**
     * PlayerID for identification
     */
	private int playerID;
	
	/**
     * Map canvas for location calculation
     */
    
    /**
     * PlayerView that contains player info
     */
    private PlayerView playerView;
    
    /**
     * Attack class for player attacks
     */
    private Attack attack;
    
    /**
     * ChangeItem class for changing items
     */
    private ChangeItem changeItem;
    
    private Canvas mapCanvas;
    
    private Settings settings;
    
    /**
     * Mouse cursor X coordinate on screen
     */
    private double mouseX;
    
    /**
     * Mouse cursor Y coordinate on screen
     */
    private double mouseY;
    
    /**
     * Player X coordinate on screen
     */
    private double playerX;
    
    /**
     * Player Y coordinate on screen
     */
    private double playerY;
    
    /**
     * Angle between player and mouse cursor
     */
    private double mouseDegree;
    
    /**
     * Angle player is facing
     */
    private double playerDegree;
    
    /**
     * Amount to rotate when mouse cursor moves
     */
    private double toRotate;
    
    /**
     * AnimationTimer that loops, checks for requests and sends them 
     */
    private AnimationTimer t;
    
    /**
     * Boolean whether this mouse handler is active
     */
    private boolean activated;
    
    /**
     * Boolean whether the primary mouse button is being pressed
     */
    private boolean hold;
    
    /**
     * Distance between player and cursor
     */
    private double distance;

    /**
     * Constructor
     *
     * @param playerID Player ID
     */
    public MouseHandler(int playerID, Settings settings) {
        super();
        this.settings = settings;
        this.t = null;
        this.playerID = playerID;
        this.hold = false;
    }
    
    /**
     * Setter for client handler
     *
     * @param handler Client handler
     */
    public void setGameHandler(Client handler) {
        this.handler = handler;
    }

    /**
     * Calculate which quarter of the screen is the mouse cursor at
     *
     * @param playerX Player X coordinate on screen
     * @param playerY Player Y coordinate on screen
     * @param mouseX Mouse cursor X coordinate on screen
     * @param mouseY Mouse cursor Y coordinate on screen
     * @return 1 = top left, 2 = top right, 3 = bottom left, 4 = bottom right
     */
    private static int quarter(double playerX, double playerY, double mouseX, double mouseY) {
        double xDif = mouseX - playerX;
        double yDif = mouseY - playerY;
        if (xDif <= 0 && yDif < 0) {
            return 1;
        } else if (xDif > 0 && yDif < 0) {
            return 2;
        } else if (xDif <= 0 && yDif >= 0) {
            return 3;
        } else return 4;
    }

    /**
     * Method for converting mouse movement to player rotation requests
     *
     * @param e MouseEvent
     */
    private void mouseMovement(MouseEvent e) {
        mouseX = e.getSceneX();
        mouseY = e.getSceneY();
        playerX = (double) settings.getScreenWidth() / 2;
        playerY = (double) settings.getScreenHeight() / 2;
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
        handler.send(CommandList.TURN, (int) playerDegree - 90);
    }

    /**
     * Setter for all mouse captures on scene
     *
     * @param scene scene
     */
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
			}
		});
    }

    /**
     * Setter for mapCanvas
     *
     * @param mapCanvas Map canvas
     */
    public void setCanvas(Canvas mapCanvas) {
        this.mapCanvas = mapCanvas;
    }

    /**
     * Setter for game view and initialize actions
     *
     * @param gameView Game view
     */
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

    /**
     * Method for activating the timer for capturing mouse inputs
     *
     */
    @Override
    public void activate() {
        super.activate();
        this.t = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (hold == true) {
                    if (playerView.getCurrentItem().getItemType() == ItemType.GUN && ((GunView) playerView.getCurrentItem()).isAutoFire()) {
                        attack.attack(distance);
                    }
                }
            }
        };
        this.t.start();
    }

    /**
     * Method for deactivating the timer
     *
     */
    @Override
    public void deactivate() {
        super.deactivate();
        this.t.stop();
    }

    /**
     * Check if timer is activated
     *
     * @return Boolean whether the timer is activated
     */
    @Override
    public boolean isActivated() {
        return super.isActivated();
    }


}
