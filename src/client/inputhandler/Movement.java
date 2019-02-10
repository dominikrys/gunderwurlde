package client.inputhandler;

import java.util.LinkedHashSet;

import client.ClientSender;
import client.data.entity.ItemDropView;
import client.data.entity.PlayerView;
import client.data.TileView;
import data.Pose;
import javafx.scene.image.Image;

public class Movement extends Action{
	
	ClientSender sender;
	private PlayerView playerView;
	private KeyboardSettings kbSettings;
	private Collision collision;
	private PickItem pickItem;

	public Movement(ClientSender sender, PlayerView playerView, Image pImage, TileView[][] tileMap, KeyboardSettings kbSettings,  LinkedHashSet<ItemDropView> itemDropView) {
		super(playerView);
		this.sender = sender;
		this.playerView = playerView;
		this.kbSettings = kbSettings;
		this.collision = new Collision(tileMap, pImage);
		this.pickItem = new PickItem(playerView, itemDropView);
	}
	
	public void move(String key) {
		Pose pose = new Pose(playerView.getPose().getX(), playerView.getPose().getY(), playerView.getPose().getDirection());
		System.out.println(pose.getX());
		System.out.println(pose.getY());
		System.out.println(pose.getDirection());
		pickItem.checkPick();
		String action = kbSettings.getAction(key);
		int direction = -1;
		switch (action) {
			case "up" :
				pose.setY(pose.getY() - playerView.getMoveSpeed());
				direction = 0;
				this.sender.send(direction);
				System.out.println("up");
				break;
			case "left" :
				pose.setX(pose.getX() - playerView.getMoveSpeed());
				direction = 270;
				this.sender.send(direction);
				System.out.println("left");
				break;
			case "down" :
				pose.setY(pose.getY() + playerView.getMoveSpeed());
				direction = 180;
				this.sender.send(direction);
				System.out.println("down");
				break;
			case "right" :
				pose.setX(pose.getX() + playerView.getMoveSpeed());
				direction = 90;
				this.sender.send(direction);
				System.out.println("right");
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
