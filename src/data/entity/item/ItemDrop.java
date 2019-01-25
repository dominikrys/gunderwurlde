package data.entity.item;

import data.Location;
import data.Pose;
import data.entity.Entity;
import data.map.Tile;

public class ItemDrop extends Entity {
    public static final int DROP_SIZE = Tile.TILE_SIZE;

    private final IsDroppable itemName;

    protected int quantity;

    public ItemDrop(IsDroppable itemName, Location location, int quantity) {
        super(new Pose(location), DROP_SIZE);
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public ItemDrop(IsDroppable itemName, Location location) {
        this(itemName, location, 1);
    }

    public ItemList getItemName() {
        return itemName.toItemList();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            this.quantity = 0;
        } else {
            this.quantity = quantity;
        }
    }

}
