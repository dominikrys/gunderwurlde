package data.entity;

import data.Location;
import data.Pose;
import data.map.Tile;

public abstract class Entity {
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
        } else if ((amount + this.size) > (3 * Tile.TILE_SIZE)) {
            this.size = (3 * Tile.TILE_SIZE);
        } else {
            this.size += amount;
        }
    }

    public void setSize(int size) {
        if (size <= 0) {
            this.size = 1;
        } else if (size > (3 * Tile.TILE_SIZE)) {
            this.size = (3 * Tile.TILE_SIZE);
        } else {
            this.size = size;
        }
    }

}
