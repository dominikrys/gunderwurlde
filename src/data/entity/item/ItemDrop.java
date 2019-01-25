package data.entity.item;

import data.Location;
import data.Pose;
import data.entity.Entity;
import data.map.Tile;

import java.util.Locale;

public class ItemDrop extends Entity {
    public static final int DROP_SIZE = Tile.TILE_SIZE;

    private final ItemList itemName;

    public ItemDrop(ItemList itemName, Pose pose) {
        super(pose, DROP_SIZE);
        this.itemName = itemName;
    }

    public ItemDrop(ItemList itemName, Location location) {
        this(itemName, new Pose(location));
    }

    public ItemList getItemName() {
        return itemName;
    }

}
