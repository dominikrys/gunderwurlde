package client.input;

import client.GameHandler;
import shared.view.entity.PlayerView;

public class ChangeItem extends Action{

	private GameHandler handler;
	private PlayerView playerView;
	
	public ChangeItem(GameHandler handler, PlayerView playerView) {
		super(handler, playerView);
		this.handler = handler;
		this.playerView = playerView;
	}
	
	public boolean checkItemIndex(int oldItemIndex, int newItemIndex) {
		if(newItemIndex == -1) {
			newItemIndex = playerView.getItems().size() - 1;
		}
		if(oldItemIndex == newItemIndex) {
			return false;
		}
		else
			return true;
	}
	
	public void previousItem() {
		int oldItemIndex = playerView.getCurrentItemIndex();
		int newItemIndex = oldItemIndex - 1;
		if(newItemIndex == -1) {
			newItemIndex = playerView.getItems().size() - 1;
		}
		if(oldItemIndex != newItemIndex) {
			handler.send(ActionList.CHANGEITEM, newItemIndex);
		}
		// no item to switch to
	}
	
	public void nextItem() {
		int oldItemIndex = playerView.getCurrentItemIndex();
		int newItemIndex = oldItemIndex + 1;
		if(newItemIndex == playerView.getItems().size()) {
			newItemIndex = 0;
		}
		if(oldItemIndex != newItemIndex) {
			handler.send(ActionList.CHANGEITEM, newItemIndex);
		}
		// no item to switch to
	}

}
