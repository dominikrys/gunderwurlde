package client.input;

import java.util.LinkedHashSet;

import client.GameHandler;
import client.gui.Settings;
import javafx.scene.image.Image;
import shared.Pose;
import shared.view.TileView;
import shared.view.entity.ItemDropView;
import shared.view.entity.PlayerView;

public class Movement extends Action{
	
	private GameHandler handler;
	private PlayerView playerView;
	private Settings settings;
	private Collision collision;
	//private PickItem pickItem;

	public Movement(GameHandler handler, PlayerView playerView, Image pImage, TileView[][] tileMap, Settings settings, LinkedHashSet<ItemDropView> itemDropView) {
		super(handler, playerView);
		this.handler = handler;
		this.playerView = playerView;
		this.settings = settings;
		this.collision = new Collision(tileMap, pImage);
		//this.pickItem = new PickItem(handler, playerView, itemDropView);
	}
	
	public void move(String direction) {
		Pose pose = new Pose(playerView.getPose().getX(), playerView.getPose().getY(), playerView.getPose().getDirection());
        // System.out.println(pose.getX());
        // System.out.println(pose.getY());
        // System.out.println(pose.getDirection());
		//pickItem.checkPick();
		//String action = settings.getAction(key);
		int angle = -1;
		switch (direction) {
			case "up" :
				//pose.setY(pose.getY() - playerView.getMoveSpeed());
			        angle = 270;
				//this.handler.send(ActionList.MOVEMENT, direction);
            // System.out.println("up");
				break;
			case "left" :
				//pose.setX(pose.getX() - playerView.getMoveSpeed());
			        angle = 180;
				//this.handler.send(ActionList.MOVEMENT, direction);
            // System.out.println("left");
				break;
			case "down" :
				//pose.setY(pose.getY() + playerView.getMoveSpeed());
			        angle = 90;
				//this.handler.send(ActionList.MOVEMENT, direction);
            // System.out.println("down");
				break;
			case "right" :
				//pose.setX(pose.getX() + playerView.getMoveSpeed());
			        angle = 0;
				//this.handler.send(ActionList.MOVEMENT, direction);
            // System.out.println("right");
				break;
			case "upLeft" :
			        angle = 225;
				break;
			case "upRight" :
			        angle = 315;
				break;
			case "downLeft" :
			        angle = 135;
				break;
			case "downRight" :
			        angle = 45;
				break;
		}
		this.handler.send(ActionList.MOVEMENT, angle);
		/*
		if(collision.checkBoundary(pose.getX(), pose.getY())) {
			//playerView.setPose(pose);
			this.sender.send(direction);
			// TODO: send request here (send pose)
		}
		*/
	}
	
}
