package server.engine.state.item;

import shared.lists.IsItem;
import shared.lists.ItemList;
import shared.lists.ItemType;

public abstract class Item implements IsItem{
    protected ItemList itemListName;
    protected ItemType itemType;

    protected Item(ItemList itemListName, ItemType itemType) {
        this.itemListName = itemListName;
        this.itemType = itemType;
    }

    public ItemList getItemListName() {
        return itemListName;
    }

    public ItemType getItemType() {
        return itemType;
    }

}
