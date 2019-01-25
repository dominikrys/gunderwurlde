package data.entity.item.weapon;

import data.entity.item.Item;
import data.entity.item.ItemList;
import data.entity.item.ItemType;

abstract class Weapon extends Item {

    Weapon(ItemList itemID, ItemType itemType) {
        super(itemID, itemType);
    }

}
