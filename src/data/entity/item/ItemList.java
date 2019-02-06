package data.entity.item;

import data.entity.EntityList;
import data.entity.item.weapon.gun.AmmoList;

public enum ItemList {
    PISTOL(EntityList.PISTOL.getPath()),
    BASIC_AMMO(EntityList.AMMO_CLIP.getPath());

    private String spritePath;

    ItemList(String spritePath) {
        this.spritePath = spritePath;
    }

    public AmmoList toAmmoList() { //only works if you know the item is definitely ammo TODO make this less error prone
        return AmmoList.valueOf(this.toString());
    }
}

