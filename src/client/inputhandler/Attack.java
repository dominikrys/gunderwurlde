package client.inputhandler;

import client.data.ItemView;
import client.data.entity.PlayerView;
import data.item.weapon.gun.AmmoList;

public class Attack extends Action{
	
	private PlayerView playerView;

	public Attack(PlayerView playerView) {
		super(playerView);
		this.playerView = playerView;
	}
	
	public void attack() {
		ItemView itemView = playerView.getCurrentItem();
		String itemName = itemView.getItemListName().toString();
		switch(itemName) {
			case "PISTOL" :
				shoot(itemView);
		}
	}
	
	public void shoot(ItemView itemView) {
		if(itemView.getAmmoInClip() != 0) {
			// TODO: send shooting request
		}
		else {
			System.out.println("No ammo");
			// TODO: shooting fail stuff here (sound...)
		}
	}

}
