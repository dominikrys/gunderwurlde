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
	private Image pImage;
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
		pickItem.checkPick();
		String action = kbSettings.getAction(key);
		switch (action) {
			case "up" :
				pose.setY(pose.getY() - playerView.getMoveSpeed());
				System.out.println("up");
				break;
			case "left" :
				pose.setX(pose.getX() - playerView.getMoveSpeed());
				System.out.println("left");
				break;
			case "down" :
				pose.setY(pose.getY() + playerView.getMoveSpeed());
				System.out.println("down");
				break;
			case "right" :
				pose.setX(pose.getX() + playerView.getMoveSpeed());
				System.out.println("right");
				break;
		}
		if(collision.checkBoundary(pose.getX(), pose.getY())) {
			//playerView.setPose(pose);
			this.sender.send(pose);
			// TODO: send request here (send pose)
		}
	}
	
}
