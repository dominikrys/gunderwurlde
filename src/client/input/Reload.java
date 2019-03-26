package client.input;

import client.Client;
import shared.lists.AmmoList;
import shared.lists.ItemType;
import shared.view.GunView;
import shared.view.ItemView;
import shared.view.entity.PlayerView;

/**
 * Reload class. This is the class for player reloads.
 *
 * @author Mak Hong Lun Timothy
 */
public class Reload extends Action {

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
     * @param handler Client handler
     * @param playerView Player view
     */
    public Reload(Client handler, PlayerView playerView) {
        super(handler, playerView);
        this.handler = handler;
        this.playerView = playerView;
    }

    /**
     * Method for gun reload only, ignore non-gun items
     *
     */
    public void reload() {
        ItemView itemView = playerView.getCurrentItem();
        if (itemView.getItemType() == ItemType.GUN)
            gunReload((GunView) itemView, ((GunView) itemView).getAmmoType());
    }

    /**
     * Method for sending gun reload requests
     *
     */
    public void gunReload(GunView itemView, AmmoList ammo) {
        if (itemView.getAmmoInClip() < itemView.getClipSize() && playerView.getAmmo().getOrDefault(ammo, 0) > 0) {
            handler.send(CommandList.RELOAD);
            System.out.println("Sending reload request");
        } else {
            // TODO: reload fail stuff here (sound...)
        }
    }

}
