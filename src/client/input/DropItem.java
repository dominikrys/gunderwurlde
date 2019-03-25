package client.input;

import client.Client;
import shared.view.entity.PlayerView;

/**
 * DropItem class. This is the class for dropping items.
 *
 * @author Mak Hong Lun Timothy
 */
public class DropItem extends Action{
	
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
	public DropItem(Client handler, PlayerView playerView) {
		super(handler, playerView);
		this.handler = handler;
		this.playerView = playerView;
	}
	
	/**
     * Send request to drop current held item
     *
     */
	public void drop() {
		if(!playerView.getCurrentItem().equals(null)) {
			// TODO: send drop request
			handler.send(CommandList.DROPITEM);
		}
	}

}
