package client.input;

import client.Client;
import shared.view.GunView;
import shared.view.ItemView;
import shared.view.entity.PlayerView;

/**
 * Attack class. This is the class for player attacks.
 *
 * @author Mak Hong Lun Timothy
 */
public class Attack extends Action {

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
    public Attack(Client handler, PlayerView playerView) {
        super(handler, playerView);
        this.handler = handler;
        this.playerView = playerView;
    }

    /**
     * Send attack request based on current held item
     *
     * @param distance Distance between player and cursor
     */
    public void attack(double distance) {
    	switch(playerView.getCurrentItem().getItemType()) {
    		case GUN:
    			GunView gunView = (GunView)playerView.getCurrentItem();
    			if(gunView.getAmmoInClip() != 0) {
    				handler.send(CommandList.ATTACK);
    			}
    			break;
    		case CONSUMEABLE:
    			handler.send(CommandList.CONSUMABLE, (int)distance);
    			break;
    		case MELEE_WEAPON:
    			break;
    	}
    }
    
}
