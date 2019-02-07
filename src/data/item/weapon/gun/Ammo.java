package data.item.weapon.gun;

import data.item.Item;
import data.item.ItemList;
import data.item.ItemType;

public class Ammo extends Item {

    public Ammo(AmmoList ammo) {
        super(ItemList.BASIC_AMMO, ItemType.AMMO);
    }

}
