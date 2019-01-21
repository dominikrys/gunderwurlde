package data.item;

import java.util.Optional;

import data.Location;

public abstract class Item {
	protected final ItemList itemID;
	protected final ItemType itemType;
	protected Optional<Location> location; //for items on the map
	
	protected Item(ItemList itemID, ItemType itemType) {
		this.itemID = itemID;
		this.itemType = itemType;
	}
	
	protected Item(ItemList itemID, ItemType itemType, Location location) {
		this(itemID, itemType);
		this.location = Optional.of(location);
	}

	public String getItemName() {
		return itemID.toString();
	}

	public int getItemID() {
		return itemID.getID();
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
