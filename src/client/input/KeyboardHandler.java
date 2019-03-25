package client.input;

import client.GameHandler;
import client.Settings;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import shared.Pose;
import shared.lists.EntityList;
import shared.view.GameView;
import shared.view.entity.PlayerView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class KeyboardHandler extends UserInteraction {
	private int playerID;
    private GameHandler handler;
    private PlayerView playerView;
    private Image pImage;
    private Movement movement;
    private Reload reload;
    private DropItem dropItem;
    private ChangeItem changeItem;
    private ArrayList<String> input = new ArrayList<String>();
    private boolean upPressed = false;
    private boolean leftPressed = false;
    private boolean downPressed = false;
    private boolean rightPressed = false;
    private boolean reloadPressed = false;
    private boolean dropPressed = false;
    private boolean interactPressed = false;
    private AnimationTimer t;
    private Timer timer;
    private TimerTask task;
    private boolean activated;

    // Settings
    private Settings settings;

    public KeyboardHandler(int playerID, Settings settings) {
        super();
        this.t = null;
        this.playerID = playerID;
        this.settings = settings;
    }

    // NOT USED
    public static Pose center(Pose target, Image image) {
        double width = image.getWidth();
        double height = image.getHeight();
        double centerX = target.getX() - width / 2;
        double centerY = target.getY() - height / 2;
        Pose center = new Pose((int) centerX, (int) centerY);
        return center;
    }

    @Override
    public void setGameHandler(GameHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String pressed = event.getCode().toString();
                if (!input.contains(pressed)) {
                    input.add(pressed);
                    // System.out.println(input.toString());
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
                    if(settings.getKey(KeyAction.ITEM1).equals(pressed)) {
                        changeItem.changeTo(1);
                    }
                    if(settings.getKey(KeyAction.ITEM2).equals(pressed)) {
                        changeItem.changeTo(2);
                    }
                    if(settings.getKey(KeyAction.ITEM3).equals(pressed)) {
                        changeItem.changeTo(3);
                    }
                    if(settings.getKey(KeyAction.ESC).equals(pressed)) {
                        // TODO: escape menu
                    }
                }
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String released = event.getCode().toString();
                input.remove(released);
                // System.out.println(input.toString());
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
            }
        });
    }

    @Override
    public void setGameView(GameView gameView) {
        super.setGameView(gameView);

        for (PlayerView p : gameView.getPlayers()) {
            //System.out.print(p.getID() + " " + p.getName());
            if (p.getID() == this.playerID) {
                this.playerView = p;
                break;
            }
        }
        this.pImage = new Image(EntityList.PLAYER.getPath());
        this.movement = new Movement(handler, playerView, pImage, gameView.getTileMap(), settings, gameView.getItemDrops());
        this.reload = new Reload(handler, playerView);
        this.dropItem = new DropItem(handler, playerView);
        this.changeItem = new ChangeItem(handler, playerView);
    }

    @Override
    public void activate() {
        super.activate();
        
        this.timer = new Timer();
        this.task = new TimerTask() {
			@Override
			public void run() {
				if (upPressed || leftPressed || downPressed || rightPressed) {
                	if((upPressed && !leftPressed && !downPressed && !rightPressed) || (upPressed && leftPressed && !downPressed && rightPressed)) {
                		movement.move("up");
                	}
                	else if((!upPressed && leftPressed && !downPressed && !rightPressed) || (upPressed && leftPressed && downPressed && !rightPressed)) {
                		movement.move("left");
                	}
                	else if((!upPressed && !leftPressed && downPressed && !rightPressed) || (!upPressed && leftPressed && downPressed && rightPressed)) {
                		movement.move("down");
                	}
                	else if((!upPressed && !leftPressed && !downPressed && rightPressed) || (upPressed && !leftPressed && downPressed && rightPressed)) {
                		movement.move("right");
                	}
                	else if(upPressed && leftPressed && !downPressed && !rightPressed) {
                		movement.move("upLeft");
                	}
                	else if(upPressed && !leftPressed && !downPressed && rightPressed) {
                		movement.move("upRight");
                	}
                	else if(!upPressed && leftPressed && downPressed && !rightPressed) {
                		movement.move("downLeft");
                	}
                	else if(!upPressed && !leftPressed && downPressed && rightPressed) {
                		movement.move("downRight");
                	}
                	else {
                		// do nothing
                	}
                }
                if(reloadPressed) {
                    reload.reload();
                }
                if(dropPressed) {
                    dropItem.drop();
                }
                if(interactPressed) {

                }
			}
		};
		
		timer.scheduleAtFixedRate(task, 0, 1);
    }

    @Override
    public void deactivate() {
        super.deactivate();
        this.timer.cancel();
    }

    @Override
    public boolean isActivated() {
        return super.isActivated();
    }

}
