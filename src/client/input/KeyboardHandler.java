package client.input;

import java.util.ArrayList;

import client.GameHandler;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import shared.Pose;
import shared.lists.EntityList;
import shared.view.GameView;
import shared.view.entity.PlayerView;

public class KeyboardHandler extends UserInteraction {

    private GameHandler handler;
    private Scene scene;
    private GameView gameView;
    private PlayerView playerView;
    private Image pImage;
    private Movement movement;
    private Reload reload;
    private DropItem dropItem;
    private PickItem pickItem;
    private ChangeItem changeItem;
    private ArrayList<String> input = new ArrayList<String>();
    private KeyboardSettings kbSettings = new KeyboardSettings();
    private boolean upPressed = false;
    private boolean leftPressed = false;
    private boolean downPressed = false;
    private boolean rightPressed = false;
    private boolean reloadPressed = false;
    private boolean dropPressed = false;
    private boolean interactPressed = false;
    private AnimationTimer t;
    private boolean activated;

    public KeyboardHandler() {
        super();
        this.t = null;
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
                    if (kbSettings.getKey("up").equals(pressed)) {
                        upPressed = true;
                    }
                    if (kbSettings.getKey("left").equals(pressed)) {
                        leftPressed = true;
                    }
                    if (kbSettings.getKey("down").equals(pressed)) {
                        downPressed = true;
                    }
                    if (kbSettings.getKey("right").equals(pressed)) {
                        rightPressed = true;
                    }
                    if (kbSettings.getKey("reload").equals(pressed)) {
                        reloadPressed = true;
                    }
                    if (kbSettings.getKey("drop").equals(pressed)) {
                        dropPressed = true;
                    }
                    if (kbSettings.getKey("interact").equals(pressed)) {
                        interactPressed = true;
                    }
                    if(kbSettings.getKey("item1").equals(pressed)) {
                    	changeItem.changeTo(1);
                    }
                    if(kbSettings.getKey("item2").equals(pressed)) {
                    	changeItem.changeTo(2);
                    }
                    if(kbSettings.getKey("item3").equals(pressed)) {
                    	changeItem.changeTo(3);
                    }
                    if(kbSettings.getKey("esc").equals(pressed)) {
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
                if (kbSettings.getKey("up").equals(released)) {
                    upPressed = false;
                }
                if (kbSettings.getKey("left").equals(released)) {
                    leftPressed = false;
                }
                if (kbSettings.getKey("down").equals(released)) {
                    downPressed = false;
                }
                if (kbSettings.getKey("right").equals(released)) {
                    rightPressed = false;
                }
                if (kbSettings.getKey("reload").equals(released)) {
                    reloadPressed = false;
                }
                if (kbSettings.getKey("drop").equals(released)) {
                    dropPressed = false;
                }
                if (kbSettings.getKey("interact").equals(released)) {
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
            if (p.getID() == 0) {
                this.playerView = p;
                break;
            }
        }
        this.pImage = new Image(EntityList.PLAYER.getPath());
        this.movement = new Movement(handler, playerView, pImage, gameView.getTileMap(), kbSettings, gameView.getItemDrops());
        this.reload = new Reload(handler, playerView);
        this.dropItem = new DropItem(handler, playerView);
        this.changeItem = new ChangeItem(handler, playerView);
    }

    @Override
    public void activate() {
        super.activate();
        this.t = new AnimationTimer() {
            @Override
            public void handle(long now) {
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
