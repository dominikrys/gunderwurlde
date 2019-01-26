package data.entity.item.weapon;

import data.entity.item.IsDroppable;
import data.entity.item.ItemList;
import data.entity.item.ItemType;

public enum GunList implements IsDroppable, IsWeapon {
    PISTOL;

    @Override
    public ItemType getItemType() {
        return ItemType.GUN;
    }

    @Override
    public ItemList toItemList() {
        return ItemList.valueOf(this.toString());
    }

}
