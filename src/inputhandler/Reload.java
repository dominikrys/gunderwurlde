package inputhandler;

import client.data.ItemView;
import client.data.PlayerView;
import data.entity.item.Item;
import data.entity.item.weapon.gun.AmmoList;
import data.entity.item.weapon.gun.Gun;
import data.entity.item.weapon.gun.Pistol;
import data.entity.player.Player;

public class Reload extends Action{
	
	private PlayerView playerView;

	public Reload(PlayerView playerView) {
		super(playerView);
		this.playerView = playerView;
	}
	
	public void reload() {
		ItemView itemView = playerView.getCurrentItem();
		String itemName = itemView.getName().toString();
		switch (itemName) {
			case "PISTOL" :
				gunReload(itemView, AmmoList.BASIC_AMMO);
				break;
		}
	}
	
	public void gunReload(ItemView itemView, AmmoList ammo) {
		if(itemView.getAmmoInClip() < itemView.getClipSize() && playerView.getAmmo().get(ammo) > 0) {
			// TODO: send reload request
		}
		else {
			System.out.println("Reload failed");
			// TODO: reload fail stuff here (sound...)
		}
	}

}
