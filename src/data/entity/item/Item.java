package data.entity.item;

public abstract class Item {
    protected final ItemList itemName;
    protected final ItemType itemType;

    protected Item(ItemList itemName, ItemType itemType) {
        this.itemName = itemName;
        this.itemType = itemType;
    }

    public ItemList getItemName() {
        return itemName;
    }

    public ItemType getItemType() {
        return itemType;
    }

}
