package data.entity.item;

import java.util.Optional;

import data.Location;

public abstract class Item {
	protected final ItemList itemID;
	protected final ItemType itemType;

	protected Item(ItemList itemID, ItemType itemType) {
		this.itemID = itemID;
		this.itemType = itemType;
	}

	public String getItemName() {
		return itemID.toString();
	}

	public ItemList getItemID() {
		return itemID;
	}

	public ItemType getItemType() {
		return itemType;
	}

}
