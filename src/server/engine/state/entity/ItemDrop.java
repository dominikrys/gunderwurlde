package server.engine.state.entity;

import server.engine.state.item.Item;
import server.engine.state.map.tile.Tile;
import server.engine.state.physics.Force;
import server.engine.state.physics.HasPhysics;
import server.engine.state.physics.Velocity;
import shared.Location;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.ItemList;
import shared.lists.ItemType;

public class ItemDrop extends Entity implements HasID, HasPhysics {
    public static final long DECAY_LENGTH = 10000; //10 seconds
    public static final int DROP_FREEZE = 300; // drop freeze of 0.3 seconds
    public static final double DEFAULT_MASS = 0.3;

    private static int nextID = 0;

    private final int id;

    protected int quantity;
    protected long dropTime;
    protected Velocity velocity;
    protected Force resultantForce;
    protected Item item;

    public ItemDrop(Item item, Location location, Velocity velocity, int quantity) {
        super(new Pose(location), Tile.TILE_SIZE / 2, item.getItemListName().getEntityList()); // TODO change size to match item
        this.item = item;
        this.quantity = quantity;
        this.id = nextID++;
        this.dropTime = System.currentTimeMillis();
        this.velocity = velocity;
        this.resultantForce = new Force();
    }

    public ItemDrop(Item item, Location location, Velocity velocity) {
        this(item, location, velocity, 1);
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

    public void setItem(Item i) {
        this.item = i;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public Entity makeCopy() {
        return new ItemDrop(item, pose, velocity, quantity);
    }

    @Override
    public Velocity getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Velocity v) {
        this.velocity = v;
        this.resultantForce = new Force();
    }

    @Override
    public Force getResultantForce() {
        return resultantForce;
    }

    @Override
    public void addNewForce(Force f) {
        System.out.println("Force:" + f.getForce());
        this.resultantForce.add(f);
    }

    @Override
    public double getMass() {
        return DEFAULT_MASS;
    }

}
