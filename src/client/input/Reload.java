package client.input;

import client.GameHandler;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import shared.lists.AmmoList;
import shared.lists.SoundList;
import shared.view.ItemView;
import shared.view.entity.PlayerView;

public class Reload extends Action {

    private GameHandler handler;
    private PlayerView playerView;
    private AudioClip reload_mag = new AudioClip(SoundList.RELOAD_MAG.getPath());

    public Reload(GameHandler handler, PlayerView playerView) {
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
            reload_mag.play();
        } else {
            //System.out.println("Reload failed");
            // TODO: reload fail stuff here (sound...)
        }
    }

}
