package data.item;

public enum ItemList {
	PISTOL(0);
	
	private int itemID;
	
	private ItemList(int id) {
		this.itemID = id;
	}
	
	public int getID() {
		return itemID;
	}
}
