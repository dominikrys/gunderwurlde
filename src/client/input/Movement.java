package client.input;

import client.Client;
import shared.view.entity.PlayerView;

/**
 * Movement class. This is the class for player movements.
 *
 * @author Mak Hong Lun Timothy
 */
public class Movement extends Action{
	
	/**
     * Client handler for sending requests
     */
	private Client handler;

	/**
     * Constructor
     *
     * @param handler Client handler
     * @param playerView Player view
     */
	public Movement(Client handler, PlayerView playerView) {
		super(handler, playerView);
		this.handler = handler;
	}
	
	/**
     * Method for sending movement requests
     * 
     *	@param direction Direction to move
     */
	public void move(String direction) {
		int angle = -1;
		switch (direction) {
			case "up" :
			        angle = 270;
				break;
			case "left" :
			        angle = 180;
				break;
			case "down" :
			        angle = 90;
				break;
			case "right" :
			        angle = 0;
				break;
			case "upLeft" :
			        angle = 225;
				break;
			case "upRight" :
			        angle = 315;
				break;
			case "downLeft" :
			        angle = 135;
				break;
			case "downRight" :
			        angle = 45;
				break;
		}
		this.handler.send(CommandList.MOVEMENT, angle);
	}
	
}
