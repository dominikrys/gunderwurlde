package client.inputhandler;

import client.ClientHandler;
import client.data.entity.PlayerView;

public class ChangeItem extends Action{

	private ClientHandler handler;
	private PlayerView playerView;
	
	public ChangeItem(ClientHandler handler, PlayerView playerView) {
		super(handler, playerView);
		this.handler = handler;
		this.playerView = playerView;
	}
	
	public void previousItem() {
		// TODO: request to swap to previous item
	}
	
	public void nextItem() {
		// TODO: request to swap to next item
	}

}
