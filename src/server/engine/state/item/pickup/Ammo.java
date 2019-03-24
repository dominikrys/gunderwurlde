package server.engine.state.item.pickup;

import server.engine.state.item.Item;
import shared.lists.AmmoList;
import shared.lists.ItemType;

public class Ammo extends Item {

    public Ammo(AmmoList ammo) {
        super(ammo.getItemListName(), ItemType.AMMO);
    }

}
