package data.entity;

import data.Location;
import data.Pose;
import data.item.Item;
import data.item.ItemList;
import data.item.ItemType;

public class ItemDrop extends Entity implements HasID {
    public static final long DECAY_LENGTH = 10000; //10 seconds
    public static final int DROP_FREEZE = 1000; // drop freeze of 1 second

    private static int nextID = 0;

    private final Item item;
    private final int id;

    protected int quantity;
    protected long dropTime;

    public ItemDrop(Item item, Location location, int quantity) {
        super(new Pose(location), 1, item.getItemListName());
        this.item = item;
        this.quantity = quantity;
        this.id = nextID++;
        this.dropTime = System.currentTimeMillis();
    }

    public ItemDrop(Item item, Location location, EntityList entityListName) {
        this(item, location, 1, entityListName);
    }

    public long getDropTime() {
        return dropTime;
    }

    public ItemList getItemName() {
        return item.getItemName();
    }

    public ItemType getItemType() {
        return item.getItemListName();
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

    @Override
    public int getID() {
        return id;
    }

}
