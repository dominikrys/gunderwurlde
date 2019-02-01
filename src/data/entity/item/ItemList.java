package data.entity.item;

import data.entity.item.weapon.gun.AmmoList;

public enum ItemList {
    PISTOL, BASIC_AMMO;
    
    public AmmoList toAmmoList() { //only works if you know the item is definitely ammo TODO make this less error prone
        return AmmoList.valueOf(this.toString());
    }
}

