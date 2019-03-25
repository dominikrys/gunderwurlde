package client.input;

import client.Client;
import shared.view.entity.PlayerView;

/**
 * ChangeItem class. This is the class for player's item switching.
 *
 * @author Mak Hong Lun Timothy
 */
public class ChangeItem extends Action{

	/**
     * Client handler for sending requests
     */
	private Client handler;
	/**
     * PlayerView that contains player info
     */
	private PlayerView playerView;
	
	/**
     * Constructor
     *
     * @param handler  Client handler
     * @param playerView Player view
     */
	public ChangeItem(Client handler, PlayerView playerView) {
		super(handler, playerView);
		this.handler = handler;
		this.playerView = playerView;
	}
	
	/**
     * Force item index to always be within range 0 - 2 (3 item slots)
     *
     * @param oldItemIndex old item index before switch
     * @param newItemIndex new item index after switch
     * @return newItemIndex to switch to
     */
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
	
	/**
     * Send request to switch to previous item
     *
     */
	public void previousItem() {
		int oldItemIndex = playerView.getCurrentItemIndex();
		int newItemIndex = oldItemIndex - 1;
		
		newItemIndex = checkItemIndex(oldItemIndex, newItemIndex);
		if(newItemIndex != -1) {
			handler.send(CommandList.CHANGEITEM, newItemIndex);
		}
		// no item to switch to
	}
	
	/**
     * Send request to switch to next item
     *
     */
	public void nextItem() {
		int oldItemIndex = playerView.getCurrentItemIndex();
		int newItemIndex = oldItemIndex + 1;
		
		newItemIndex = checkItemIndex(oldItemIndex, newItemIndex);
		if(newItemIndex != -1) {
			handler.send(CommandList.CHANGEITEM, newItemIndex);
		}
		// no item to switch to
	}
	
	/**
     * Send request to switch to chosen item based on keyboard input (e.g. 1, 2, 3)
     *
     */
	public void changeTo(int number) {
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
