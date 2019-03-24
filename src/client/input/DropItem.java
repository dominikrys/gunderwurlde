package client.input;

import client.Client;
import client.GameHandler;
import shared.view.entity.PlayerView;

public class DropItem extends Action{
	
	private Client handler;
	private PlayerView playerView;

	public DropItem(Client handler, PlayerView playerView) {
		super(handler, playerView);
		this.handler = handler;
		this.playerView = playerView;
	}
	
	public void drop() {
		if(!playerView.getCurrentItem().equals(null)) {
			// TODO: send drop request
			handler.send(ActionList.DROPITEM);
		}
	}

}
