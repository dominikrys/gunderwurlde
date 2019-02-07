package data.item.weapon;

import data.entity.EntityList;
import data.item.Item;
import data.item.ItemList;
import data.item.ItemType;
import data.item.weapon.gun.IsWeapon;

public abstract class Weapon extends Item {

    public Weapon(ItemList weaponName) {
        super(weaponName, ItemType.GUN);
    }

}
