package data.item;

public abstract class Item {
    protected final IsItem itemName;
    protected ItemList itemListName;

    protected Item(IsItem itemName, ItemList itemListName) {
        this.itemName = itemName;    
        this.itemListName = itemListName;
    }

    public ItemList getItemName() {
        return itemName.getItemListName();
    }

    public ItemType getItemListName() {
        return itemName.getItemType();
    }

}
