package data.entity.item;

import data.Location;
import data.Pose;
import data.entity.Entity;

import java.util.Locale;

public class ItemDrop extends Entity {
    private final ItemList itemName;

    public ItemDrop(ItemList itemName, Pose pose) {
        super(pose);
        this.itemName = itemName;
    }

    public ItemDrop(ItemList itemName, Location location) {
        super(location);
        this.itemName = itemName;
    }

    public ItemList getItemName() {
        return itemName;
    }

}
