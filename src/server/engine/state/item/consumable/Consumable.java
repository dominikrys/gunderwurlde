package server.engine.state.item.consumable;

import server.engine.state.item.Item;
import server.engine.state.item.Limited;
import shared.lists.ItemList;
import shared.lists.ItemType;

public abstract class Consumable extends Item implements Limited {

    protected int quantity;
    protected int maxQuantity;
    
    protected Consumable(ItemList itemListName, ItemType itemType) {
        this(itemListName, itemType, -1);
    }

    protected Consumable(ItemList itemListName, ItemType itemType, int maxQuantity) {
        this(itemListName, itemType, maxQuantity, 1);
    }

    protected Consumable(ItemList itemListName, ItemType itemType, int maxQuantity, int quantity) {
        super(itemListName, itemType);
        this.quantity = quantity;
    }

    public boolean isRemoved() {
        return (quantity <= 0);
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public void replenish() {
        quantity = maxQuantity;
    }

    @Override
    public void replenish(int amount) {
        quantity += amount;
    }

    @Override
    public void empty() {
        quantity = 0;
    }

    @Override
    public void empty(int amount) {
        quantity -= amount;
        if (quantity < 0) {
            quantity = 0;
        }
    }

}
