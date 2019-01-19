package data.item;

public abstract class Item {
	protected final String itemName;
	protected final int itemID;
	protected final ItemType itemType;
	
	protected Item(String itemName, int itemID, ItemType itemType) {
		this.itemName = itemName;
		this.itemID = itemID;
		this.itemType = itemType;
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

}
