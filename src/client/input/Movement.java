package client.input;

import java.util.LinkedHashSet;

import client.GameHandler;
import javafx.scene.image.Image;
import shared.Pose;
import shared.view.TileView;
import shared.view.entity.ItemDropView;
import shared.view.entity.PlayerView;

public class Movement extends Action{
	
	private GameHandler handler;
	private PlayerView playerView;
	private KeyboardSettings kbSettings;
	private Collision collision;
	private PickItem pickItem;

	public Movement(GameHandler handler, PlayerView playerView, Image pImage, TileView[][] tileMap, KeyboardSettings kbSettings,  LinkedHashSet<ItemDropView> itemDropView) {
		super(handler, playerView);
		this.handler = handler;
		this.playerView = playerView;
		this.kbSettings = kbSettings;
		this.collision = new Collision(tileMap, pImage);
		this.pickItem = new PickItem(handler, playerView, itemDropView);
	}
	
	public void move(String key) {
		Pose pose = new Pose(playerView.getPose().getX(), playerView.getPose().getY(), playerView.getPose().getDirection());
        // System.out.println(pose.getX());
        // System.out.println(pose.getY());
        // System.out.println(pose.getDirection());
		pickItem.checkPick();
		String action = kbSettings.getAction(key);
		int direction = -1;
		switch (action) {
			case "up" :
				pose.setY(pose.getY() - playerView.getMoveSpeed());
				direction = 0;
				this.handler.send(ActionList.MOVEMENT, direction);
            // System.out.println("up");
				break;
			case "left" :
				pose.setX(pose.getX() - playerView.getMoveSpeed());
				direction = 270;
				this.handler.send(ActionList.MOVEMENT, direction);
            // System.out.println("left");
				break;
			case "down" :
				pose.setY(pose.getY() + playerView.getMoveSpeed());
				direction = 180;
				this.handler.send(ActionList.MOVEMENT, direction);
            // System.out.println("down");
				break;
			case "right" :
				pose.setX(pose.getX() + playerView.getMoveSpeed());
				direction = 90;
				this.handler.send(ActionList.MOVEMENT, direction);
            // System.out.println("right");
				break;
		}
		/*
		if(collision.checkBoundary(pose.getX(), pose.getY())) {
			//playerView.setPose(pose);
			this.sender.send(direction);
			// TODO: send request here (send pose)
		}
		*/
	}
	
}
