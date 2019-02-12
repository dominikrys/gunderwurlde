package server.engine.state.item.weapon.gun;

import server.engine.state.item.Item;
import shared.lists.AmmoList;
import shared.lists.ItemList;
import shared.lists.ItemType;

public class Ammo extends Item {

    public Ammo(AmmoList ammo) {
        super(ItemList.BASIC_AMMO, ItemType.AMMO);
    }

}
