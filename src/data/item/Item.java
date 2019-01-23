package data.item;

import java.util.Optional;

import data.HasLocation;
import data.Location;

public abstract class Item implements HasLocation{
	protected final ItemList itemName;
	protected final ItemType itemType;
	protected Optional<Location> location; //for items on the map
	
	protected Item(ItemList itemName, ItemType itemType) {
		this.itemName = itemName;
		this.itemType = itemType;
	}
	
	protected Item(ItemList itemID, ItemType itemType, Location location) {
		this(itemID, itemType);
		this.location = Optional.of(location);
	}

	public ItemList getItemName() {
		return itemName;
	}

	public ItemType getItemType() {
		return itemType;
	}
	
	public void pickedUp() {
		this.location = Optional.empty(); //Item location isn't tracked when in possession of something else.
	}
	
	@Override
	public Location getLocation() {
		return location.get();
	}
	
	@Override
	public void setLocation(Location location) {
		this.location = Optional.of(location);
	}

}
