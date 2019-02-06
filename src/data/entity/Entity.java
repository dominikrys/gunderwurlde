package data.entity;

import data.Location;
import data.Pose;
import data.map.tile.Tile;

// Class for renderable entities
public abstract class Entity {
    public static final int MAX_SIZE = (3 * Tile.TILE_SIZE);
    
    protected Pose pose;
    protected int sizeScaleFactor;

    protected Entity(Pose pose, int sizeScaleFactor) {
        this.pose = pose;
        this.sizeScaleFactor = sizeScaleFactor;
    }
    
    protected Entity(int sizeScaleFactor) {
        this.pose = new Pose();
        this.sizeScaleFactor = sizeScaleFactor;
    }

    protected Entity(Location location, int sizeScaleFactor) {
        this(new Pose(location), sizeScaleFactor);
    }

    protected Entity(Pose pose) {
        this(pose, 1);
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

    public int getSizeScaleFactor() {
        return sizeScaleFactor;
    }

    public void changeSize(int amount) {
        if (amount <= -this.sizeScaleFactor) {
            this.sizeScaleFactor = 1;
        } else if ((amount + this.sizeScaleFactor) > MAX_SIZE) {
            this.sizeScaleFactor = MAX_SIZE;
        } else {
            this.sizeScaleFactor += amount;
        }
    }

    public void setSizeScaleFactor(int sizeScaleFactor) {
        if (sizeScaleFactor <= 0) {
            this.sizeScaleFactor = 1;
        } else if (sizeScaleFactor > MAX_SIZE) {
            this.sizeScaleFactor = MAX_SIZE;
        } else {
            this.sizeScaleFactor = sizeScaleFactor;
        }
    }
    
}
