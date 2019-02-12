package server.engine.state.item.weapon;

import server.engine.state.item.Item;
import shared.lists.ItemList;
import shared.lists.ItemType;

public abstract class Weapon extends Item {

    public Weapon(ItemList weaponName) {
        super(weaponName, ItemType.GUN);
    }

}
