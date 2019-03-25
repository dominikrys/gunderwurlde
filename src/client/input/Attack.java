package client.input;

import client.GameHandler;
import shared.view.ItemView;
import shared.view.entity.PlayerView;

public class Attack extends Action {

    private GameHandler handler;
    private PlayerView playerView;

    public Attack(GameHandler handler, PlayerView playerView) {
        super(handler, playerView);
        this.handler = handler;
        this.playerView = playerView;
    }

    public void attack(double distance) {
        ItemView itemView = playerView.getCurrentItem();
        String itemName = itemView.getItemListName().toString();
        shoot(itemView);
    }

    public void shoot(ItemView itemView) {
        if (itemView.getAmmoInClip() != 0) {
            // TODO: send shooting request
            handler.send(CommandList.ATTACK);
        } else {
            //System.out.println("No ammo");
            // TODO: shooting fail stuff here (sound...)
        }
    }

}
