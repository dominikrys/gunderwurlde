package data.item.weapon.gun;

import data.item.IsDroppable;
import data.item.ItemList;
import data.item.ItemType;

public enum AmmoList implements IsDroppable {
    BASIC_AMMO, NONE/* Use none for items with infinite ammo*/;

    @Override
    public ItemType getItemType() {
        return ItemType.AMMO;
    }

    @Override
    public ItemList getItemListName() {
        return ItemList.valueOf(this.toString());
    }
}
