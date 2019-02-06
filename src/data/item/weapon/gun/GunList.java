package data.item.weapon.gun;

import data.item.IsDroppable;
import data.item.ItemList;
import data.item.ItemType;

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
