package client.input;

import client.Client;
import client.GameHandler;
import shared.view.ItemView;
import shared.view.entity.PlayerView;

public class Attack extends Action {

    private Client handler;
    private PlayerView playerView;

    public Attack(Client handler, PlayerView playerView) {
        super(handler, playerView);
        this.handler = handler;
        this.playerView = playerView;
    }

    public void attack() {
        ItemView itemView = playerView.getCurrentItem();
        String itemName = itemView.getItemListName().toString();
        shoot(itemView);
    }

    public void shoot(ItemView itemView) {
        handler.send(ActionList.ATTACK);
        /*if (itemView.getAmmoInClip() != 0) {
            // TODO: send shooting request
            handler.send(ActionList.ATTACK);
        } else {
            //System.out.println("No ammo");
            // TODO: shooting fail stuff here (sound...)
        }*/
    }

}
