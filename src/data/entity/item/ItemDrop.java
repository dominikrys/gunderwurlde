package data.entity.item;

import data.Location;
import data.Pose;
import data.entity.Entity;

import java.util.Locale;

public class ItemDrop extends Entity {
    private final ItemList itemID;

    public ItemDrop(ItemList itemID, Pose pose) {
        super(pose);
        this.itemID = itemID;
    }

    public ItemDrop(ItemList itemID, Location location) {
        super(location);
        this.itemID = itemID;
    }
}
