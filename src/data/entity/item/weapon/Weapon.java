package data.entity.item.weapon;

import data.entity.item.Item;
import data.entity.item.weapon.gun.IsWeapon;

public abstract class Weapon extends Item {

    public Weapon(IsWeapon weaponName) {
        super(weaponName);
    }

}
