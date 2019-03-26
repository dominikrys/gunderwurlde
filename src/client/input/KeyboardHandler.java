package client.input;

import client.Client;
import client.Settings;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import shared.view.GameView;
import shared.view.entity.PlayerView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * KeyboardHandler class. This is the class for keyboard inputs.
 *
 * @author Mak Hong Lun Timothy
 */
public class KeyboardHandler extends UserInteraction {
	
	/**
     * PlayerID for identification
     */
    private int playerID;
    
    /**
     * Client handler for sending requests
     */
    private Client handler;
    
    /**
     * PlayerView that contains player info
     */
    private PlayerView playerView;
    
    /**
     * Movement class for player movement
     */
    private Movement movement;
    
    /**
     * Reload class for player reload
     */
    private Reload reload;
    
    /**
     * DropItem class for dropping items
     */
    private DropItem dropItem;
    
    /**
     * ChangeItem class for changing items
     */
    private ChangeItem changeItem;

    /**
     * Pause class for pausing the game
     */
    private Pause pauseGame;
    /**
     * ArrayList for storing pressed keys
     */
    private ArrayList<String> input = new ArrayList<String>();
    
    /**
     * Booleans for whether functional keys are pressed
     */
    private boolean upPressed = false;
    private boolean leftPressed = false;
    private boolean downPressed = false;
    private boolean rightPressed = false;
    private boolean reloadPressed = false;
    private boolean dropPressed = false;
    private boolean interactPressed = false;
    private boolean pausePressed = false;
    private boolean currentlyPaused = false;
    private boolean resumePressed = false;
    
    /**
     * Timer that loops, checks for requests and sends them
     */
    private Timer timer;
    
    /**
     * TimerTask for timer behaviour
     */
    private TimerTask task;
    
    /**
     * Boolean whether an item can be dropped
     */
    private boolean dropCoolDown;
    
    /**
     * TimerTask for item dropping cooldown
     */
    private TimerTask checkDropCoolDown;
    
    /**
     * Boolean whether this keyboard handler is active
     */
    private boolean activated;

    /**
     * Settings that contains keys mapping
     */
    private Settings settings;

    /**
     * Constructor
     *
     * @param playerID Player ID
     * @param settings Game settings with keys mapping
     */
    public KeyboardHandler(int playerID, Settings settings) {
        super();
        this.playerID = playerID;
        this.settings = settings;
        this.dropCoolDown = false;
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
     * Setter for all key captures on scene
     *
     * @param scene scene
     */
    @Override
    public void setScene(Scene scene) {
        this.scene = scene;

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String pressed = event.getCode().toString();
                if (!input.contains(pressed)) {
                    input.add(pressed);
                    if (settings.getKey(KeyAction.UP).equals(pressed)) {
                        upPressed = true;
                    }
                    if (settings.getKey(KeyAction.LEFT).equals(pressed)) {
                        leftPressed = true;
                    }
                    if (settings.getKey(KeyAction.DOWN).equals(pressed)) {
                        downPressed = true;
                    }
                    if (settings.getKey(KeyAction.RIGHT).equals(pressed)) {
                        rightPressed = true;
                    }
                    if (settings.getKey(KeyAction.RELOAD).equals(pressed)) {
                        reloadPressed = true;
                    }
                    if (settings.getKey(KeyAction.DROP).equals(pressed)) {
                        dropPressed = true;
                    }
                    if (settings.getKey(KeyAction.INTERACT).equals(pressed)) {
                        interactPressed = true;
                    }
                    if (settings.getKey(KeyAction.ITEM1).equals(pressed)) {
                        changeItem.changeTo(1);
                    }
                    if (settings.getKey(KeyAction.ITEM2).equals(pressed)) {
                        changeItem.changeTo(2);
                    }
                    if (settings.getKey(KeyAction.ITEM3).equals(pressed)) {
                        changeItem.changeTo(3);
                    }
                    if (settings.getKey(KeyAction.ESC).equals(pressed)) {
                        if(currentlyPaused){
                            resumePressed = true;
                            currentlyPaused = false;
                        }
                        else {
                            pausePressed = true;
                            currentlyPaused = true;
                        }
                    }
                }
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String released = event.getCode().toString();
                input.remove(released);
                if (settings.getKey(KeyAction.UP).equals(released)) {
                    upPressed = false;
                }
                if (settings.getKey(KeyAction.LEFT).equals(released)) {
                    leftPressed = false;
                }
                if (settings.getKey(KeyAction.DOWN).equals(released)) {
                    downPressed = false;
                }
                if (settings.getKey(KeyAction.RIGHT).equals(released)) {
                    rightPressed = false;
                }
                if (settings.getKey(KeyAction.RELOAD).equals(released)) {
                    reloadPressed = false;
                }
                if (settings.getKey(KeyAction.DROP).equals(released)) {
                    dropPressed = false;
                }
                if (settings.getKey(KeyAction.INTERACT).equals(released)) {
                    interactPressed = false;
                }
                if(settings.getKey(KeyAction.ESC).equals(released)){
                    if(currentlyPaused) {
                        pausePressed = false;
                    }
                    else{
                        resumePressed = false;
                    }
                }
            }
        });
    }

    /**
     * Setter for game view
     *
     * @param gameView Game view and initialize actions
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
        this.movement = new Movement(handler, playerView);
        this.reload = new Reload(handler, playerView);
        this.dropItem = new DropItem(handler, playerView);
        this.changeItem = new ChangeItem(handler, playerView);
        this.pauseGame = new Pause(handler, playerView);
    }

    /**
     * Method for activating the timer for capturing key inputs
     *
     */
    @Override
    public void activate() {
        super.activate();
        
        this.timer = new Timer();
        this.task = new TimerTask() {
            @Override
            public void run() {
                if (upPressed || leftPressed || downPressed || rightPressed) {
                    if ((upPressed && !leftPressed && !downPressed && !rightPressed) || (upPressed && leftPressed && !downPressed && rightPressed)) {
                        movement.move("up");
                    } else if ((!upPressed && leftPressed && !downPressed && !rightPressed) || (upPressed && leftPressed && downPressed && !rightPressed)) {
                        movement.move("left");
                    } else if ((!upPressed && !leftPressed && downPressed && !rightPressed) || (!upPressed && leftPressed && downPressed && rightPressed)) {
                        movement.move("down");
                    } else if ((!upPressed && !leftPressed && !downPressed && rightPressed) || (upPressed && !leftPressed && downPressed && rightPressed)) {
                        movement.move("right");
                    } else if (upPressed && leftPressed && !downPressed && !rightPressed) {
                        movement.move("upLeft");
                    } else if (upPressed && !leftPressed && !downPressed && rightPressed) {
                        movement.move("upRight");
                    } else if (!upPressed && leftPressed && downPressed && !rightPressed) {
                        movement.move("downLeft");
                    } else if (!upPressed && !leftPressed && downPressed && rightPressed) {
                        movement.move("downRight");
                    }
                    else {
                        // do nothing
                    }
                }
                if (reloadPressed) {
                    reload.reload();
                }
                if(pausePressed){
                    pauseGame.pause();
                }
                if(resumePressed){
                    pauseGame.resume();
                }
                if (dropPressed && !dropCoolDown) {
                    dropItem.drop();
                    dropCoolDown = true;
                    checkDropCoolDown = new TimerTask() {
            			@Override
            			public void run() {
            				dropCoolDown = false;
            			}
            		};
                    timer.schedule(checkDropCoolDown, 1000);
                }
                if (interactPressed) {

                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1);
    }

    public void unpause(){
        if(currentlyPaused){
            resumePressed = true;
            currentlyPaused = false;
        }
        else {
            pausePressed = true;
            currentlyPaused = true;
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(currentlyPaused) {
            pausePressed = false;
        }
        else{
            resumePressed = false;
        }
    }

    /**
     * Method for deactivating the timer
     *
     */
    @Override
    public void deactivate() {
        super.deactivate();
        this.timer.cancel();
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
