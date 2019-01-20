package data.item;

import java.util.Optional;

import data.Location;

public abstract class Item {
	protected final String itemName;
	protected final int itemID;
	protected final ItemType itemType;
	protected Optional<Location> location; //for items on the map
	
	protected Item(String itemName, int itemID, ItemType itemType) {
		this.itemName = itemName;
		this.itemID = itemID;
		this.itemType = itemType;
	}
	
	protected Item(String itemName, int itemID, ItemType itemType, Location location) {
		this(itemName, itemID, itemType);
		this.location = Optional.of(location);
	}

	public String getItemName() {
		return itemName;
	}

	public int getItemID() {
		return itemID;
	}

	public ItemType getItemType() {
		return itemType;
	}
	
	public Location getLocation() {
		return location.get();
	}
	
	public void setLocation(Location location) {
		this.location = Optional.of(location);
	}

}
