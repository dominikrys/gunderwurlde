package server.engine.state.entity;

import server.engine.state.item.Item;
import server.engine.state.map.tile.Tile;
import shared.Location;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.ItemList;
import shared.lists.ItemType;

public class ItemDrop extends Entity implements HasID {
    public static final long DECAY_LENGTH = 10000; //10 seconds
    public static final int DROP_FREEZE = 1000; // drop freeze of 1 second

    private static int nextID = 0;

    private final Item item;
    private final int id;

    protected int quantity;
    protected long dropTime;

    public ItemDrop(Item item, Location location, int quantity) {
        super(new Pose(location), Tile.TILE_SIZE, item.getItemListName().getEntityList());
        this.item = item;
        this.quantity = quantity;
        this.id = nextID++;
        this.dropTime = System.currentTimeMillis();
    }

    public ItemDrop(Item item, Location location) {
        this(item, location, 1);
    }

    public long getDropTime() {
        return dropTime;
    }

    public ItemList getItemName() {
        return item.getItemListName();
    }

    public ItemType getItemType() {
        return item.getItemType();
    }

    public EntityList getEntityListName() {
        return item.getItemListName().getEntityList();
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
