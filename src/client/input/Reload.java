package client.input;

import client.Client;
import client.GameHandler;
import shared.lists.AmmoList;
import shared.view.ItemView;
import shared.view.entity.PlayerView;

public class Reload extends Action {

    private Client handler;
    private PlayerView playerView;

    public Reload(Client handler, PlayerView playerView) {
        super(handler, playerView);
        this.handler = handler;
        this.playerView = playerView;
    }

    public void reload() {
        ItemView itemView = playerView.getCurrentItem();
        gunReload(itemView, itemView.getAmmoType());
    }

    public void gunReload(ItemView itemView, AmmoList ammo) {
        if (itemView.getAmmoInClip() < itemView.getClipSize() && playerView.getAmmo().get(ammo) > 0) {
            // TODO: send reload request
            handler.send(ActionList.RELOAD);
        } else {
            //System.out.println("Reload failed");
            // TODO: reload fail stuff here (sound...)
        }
    }

}
