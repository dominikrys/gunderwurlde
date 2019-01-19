package data.item.weapon;

import data.item.Item;
import data.item.ItemType;

abstract class Weapon extends Item {

	Weapon(String itemName, int itemID, ItemType itemType) {
		super(itemName, itemID, itemType);
	}
	
	

}
