package client.input;

import client.ClientHandler;
import server.engine.state.entity.player.Player;
import server.engine.state.item.Item;
import server.engine.state.item.weapon.gun.Gun;
import server.engine.state.item.weapon.gun.Pistol;
import shared.lists.AmmoList;
import shared.view.ItemView;
import shared.view.entity.PlayerView;

public class Reload extends Action{
	
	private ClientHandler handler;
	private PlayerView playerView;

	public Reload(ClientHandler handler, PlayerView playerView) {
		super(handler, playerView);
		this.handler = handler;
		this.playerView = playerView;
	}
	
	public void reload() {
		ItemView itemView = playerView.getCurrentItem();
		String itemName = itemView.getItemListName().toString();
		switch (itemName) {
			case "PISTOL" :
				gunReload(itemView, AmmoList.BASIC_AMMO);
				break;
		}
	}
	
	public void gunReload(ItemView itemView, AmmoList ammo) {
		if(itemView.getAmmoInClip() < itemView.getClipSize() && playerView.getAmmo().get(ammo) > 0) {
			// TODO: send reload request
			handler.send(ActionList.RELOAD);
		}
		else {
			System.out.println("Reload failed");
			// TODO: reload fail stuff here (sound...)
		}
	}

}
