package data.entity.item;

import data.Location;
import data.Pose;
import data.entity.Entity;
import data.map.tile.Tile;

public class ItemDrop extends Entity {
    public static final int DROP_SIZE = Tile.TILE_SIZE;

    private final Item item;

    protected int quantity;

    public ItemDrop(Item item, Location location, int quantity) {
        super(new Pose(location), DROP_SIZE);
        this.item = item;
        this.quantity = quantity;
    }

    public ItemDrop(Item item, Location location) {
        this(item, location, 1);
    }

    public ItemList getItemName() {
        return item.getItemName();
    }

    public ItemType getItemType() {
        return item.getItemType();
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

    public Item getItem() {
        return item;
    }

}
