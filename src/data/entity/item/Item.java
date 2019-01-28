package data.entity.item;

public abstract class Item {
    protected final IsItem itemName;

    protected Item(IsItem itemName) {
        this.itemName = itemName;
    }

    public ItemList getItemName() {
        return itemName.toItemList();
    }

    public ItemType getItemType() {
        return itemName.getItemType();
    }

}
