package data.item.weapon;

import data.Location;
import data.item.Item;
import data.item.ItemList;
import data.item.ItemType;

import java.util.Optional;

abstract class Weapon extends Item {

    Weapon(ItemList itemID, ItemType itemType, Optional<Location> location) {
        super(itemID, itemType, location);
    }


}
