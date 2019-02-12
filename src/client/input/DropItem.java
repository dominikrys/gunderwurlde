package client.input;

import client.ClientHandler;
import shared.view.entity.PlayerView;

public class DropItem extends Action{
	
	private ClientHandler handler;
	private PlayerView playerView;

	public DropItem(ClientHandler handler, PlayerView playerView) {
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
