package client.input;

import client.Client;
import client.GameHandler;
import shared.view.entity.PlayerView;

public class ChangeItem extends Action{

	private Client handler;
	private PlayerView playerView;
	
	public ChangeItem(Client handler, PlayerView playerView) {
		super(handler, playerView);
		this.handler = handler;
		this.playerView = playerView;
	}
	
	public int checkItemIndex(int oldItemIndex, int newItemIndex) {
		if(newItemIndex == -1) {
			newItemIndex = playerView.getItems().size() - 1;
		}
		else if(newItemIndex == playerView.getItems().size()) {
			newItemIndex = 0;
		}
		if(oldItemIndex == newItemIndex) {
			return -1;
		}
		else
			return newItemIndex;
	}
	
	public void previousItem() {
		int oldItemIndex = playerView.getCurrentItemIndex();
		int newItemIndex = oldItemIndex - 1;
		
		newItemIndex = checkItemIndex(oldItemIndex, newItemIndex);
		if(newItemIndex != -1) {
			handler.send(CommandList.CHANGEITEM, newItemIndex);
		}
		// no item to switch to
	}
	
	public void nextItem() {
		int oldItemIndex = playerView.getCurrentItemIndex();
		int newItemIndex = oldItemIndex + 1;
		
		newItemIndex = checkItemIndex(oldItemIndex, newItemIndex);
		if(newItemIndex != -1) {
			handler.send(CommandList.CHANGEITEM, newItemIndex);
		}
		// no item to switch to
	}
	
	public void changeTo(int number) {
		System.out.println(playerView.getItems().size());
		if(playerView.getCurrentItemIndex() + 1 == number) {
			// do nothing
		}
		else if(playerView.getItems().size() < number) {
			// do nothing
		}
		else {
			handler.send(CommandList.CHANGEITEM, number - 1);
		}
	}

}
