package data.item;

import data.entity.EntityList;
import data.item.weapon.gun.AmmoList;

public enum ItemList {
    PISTOL(EntityList.PISTOL),
    BASIC_AMMO(EntityList.AMMO_CLIP);

    private String spritePath;
    private EntityList entityListName;

    ItemList(EntityList entityListName) {
        this.entityListName = entityListName;
        this.spritePath = entityListName.getPath();
    }

    public AmmoList toAmmoList() { //only works if you know the item is definitely ammo TODO make this less error prone
        return AmmoList.valueOf(this.toString());
    }

    public String getSpritePath() {
        return spritePath;
    }

    public EntityList getEntityList() {
        return entityListName;
    }
}

