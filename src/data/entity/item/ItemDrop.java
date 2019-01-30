package data.entity.item;

import data.Location;
import data.Pose;
import data.entity.Entity;
import data.entity.HasID;
import data.map.tile.Tile;

public class ItemDrop extends Entity implements HasID {
    public static final int DROP_SIZE = Tile.TILE_SIZE;
    public static final long DECAY_LENGTH = 10000; //10 seconds
    
    private static int nextID = 0;

    private final Item item;   
    private final int id;

    protected int quantity;
    protected long dropTime;

    public ItemDrop(Item item, Location location, int quantity) {
        super(new Pose(location), DROP_SIZE);
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
    
    @Override
    public int getID() {
        return id;
    }

}
