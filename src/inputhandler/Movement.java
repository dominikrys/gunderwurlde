package inputhandler;

import client.data.PlayerView;
import client.data.TileView;
import data.Pose;
import data.map.tile.Tile;
import javafx.scene.image.Image;

public class Movement extends Action{
	
	private PlayerView playerView;
	private Image pImage;
	private KeyboardSettings kbSettings;
	private Collision collision;

	public Movement(PlayerView playerView, Image pImage, TileView[][] tileMap, KeyboardSettings kbSettings) {
		super(playerView);
		this.playerView = playerView;
		this.kbSettings = kbSettings;
		this.collision = new Collision(tileMap, pImage);
	}
	
	public void move(String key) {
		Pose pose = new Pose(playerView.getPose().getX(), playerView.getPose().getY(), playerView.getPose().getDirection());
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
			// TODO: send request here (send pose)
		}
	}
	
}
