package data.entity;

import data.Location;
import data.Pose;
import data.map.tile.Tile;

public abstract class Entity {
    public static final int MAX_SIZE = (3 * Tile.TILE_SIZE);
    
    protected Pose pose;
    protected int size;

    protected Entity(Pose pose, int size) {
        this.pose = pose;
    }

    protected Entity(Location location, int size) {
        this(new Pose(location), size);
    }

    public Pose getPose() {
        return pose;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }

    public Location getLocation() {
        return pose;
    }

    public void setLocation(Location location) {
        this.pose = new Pose(location, this.pose.getDirection());
    }

    public int getSize() {
        return size;
    }

    public void changeSize(int amount) {
        if (amount <= -this.size) {
            this.size = 1;
        } else if ((amount + this.size) > MAX_SIZE) {
            this.size = MAX_SIZE;
        } else {
            this.size += amount;
        }
    }

    public void setSize(int size) {
        if (size <= 0) {
            this.size = 1;
        } else if (size > MAX_SIZE) {
            this.size = MAX_SIZE;
        } else {
            this.size = size;
        }
    }

}
