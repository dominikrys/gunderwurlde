package client.input;

import client.GameHandler;
import shared.view.entity.PlayerView;

public class DropItem extends Action{
	
	private GameHandler handler;
	private PlayerView playerView;

	public DropItem(GameHandler handler, PlayerView playerView) {
		super(handler, playerView);
		this.handler = handler;
		this.playerView = playerView;
	}
	
	public void drop() {
		if(!playerView.getCurrentItem().equals(null)) {
			// TODO: send drop request
			handler.send(CommandList.DROPITEM);
		}
	}

}
