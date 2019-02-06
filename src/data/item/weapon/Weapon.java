package data.item.weapon;

import data.item.Item;
import data.item.weapon.gun.IsWeapon;

public abstract class Weapon extends Item {

    public Weapon(IsWeapon weaponName) {
        super(weaponName);
    }

}
