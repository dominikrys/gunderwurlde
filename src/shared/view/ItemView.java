package shared.view;

import java.io.Serializable;

import shared.lists.ItemList;
import shared.lists.ItemType;

public class ItemView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final ItemList name;
    protected final ItemType type;
    protected int quantity;

    public ItemView(ItemList name, ItemType type) {
        this.name = name;
        this.type = type;
        this.quantity = 1;
    }

    public ItemView(ItemList name, ItemType type, int quantity) {
        this.name = name;
        this.type = type;
        this.quantity = quantity;
    }

    public ItemList getItemListName() {
        return name;
    }

    public ItemType getItemType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

}
