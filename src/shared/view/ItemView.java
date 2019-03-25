package shared.view;

import java.io.Serializable;

import shared.lists.ItemList;
import shared.lists.ItemType;

public class ItemView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final ItemList name;
    protected final ItemType type;

    public ItemView(ItemList name, ItemType type) {
        this.name = name;
        this.type = type;
    }

    public ItemList getItemListName() {
        return name;
    }

    public ItemType getItemType() {
        return type;
    }

}
