package data.entity.item.weapon.gun;

import data.entity.item.IsDroppable;
import data.entity.item.ItemList;
import data.entity.item.ItemType;

public enum AmmoList implements IsDroppable {
    BASIC_AMMO, NONE/* Use none for items with infinite ammo*/;

    @Override
    public ItemType getItemType() {
        return ItemType.AMMO;
    }

    @Override
    public ItemList toItemList() {
        return ItemList.valueOf(this.toString());
    }
}
