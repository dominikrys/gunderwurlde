package data.item.weapon;

import data.item.Item;
import data.item.ItemList;
import data.item.ItemType;

public abstract class Weapon extends Item {

    public Weapon(ItemList weaponName) {
        super(weaponName, ItemType.GUN);
    }

}
