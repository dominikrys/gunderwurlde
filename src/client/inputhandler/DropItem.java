package client.inputhandler;

import client.data.entity.PlayerView;

public class DropItem extends Action{
	
	private PlayerView playerView;

	public DropItem(PlayerView playerView) {
		super(playerView);
		this.playerView = playerView;
	}
	
	public void drop() {
		if(!playerView.getCurrentItem().equals(null)) {
			// TODO: send drop request
		}
	}

}
