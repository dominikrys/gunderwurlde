package data.entity;

import data.Location;
import data.Pose;
import data.map.tile.Tile;

// Class for renderable entities
public abstract class Entity {
    public static final int MAX_SIZE = (3 * Tile.TILE_SIZE);
    
    protected Pose pose;
    protected int sizeScaleFactor;
    protected EntityList entityName;

    protected Entity(Pose pose, int sizeScaleFactor, EntityList entityName) {
        this.pose = pose;
        this.sizeScaleFactor = sizeScaleFactor;
        this.entityName = entityName;
    }
    
    protected Entity(int sizeScaleFactor, EntityList entityName) {
        this (new Pose(), 1, entityName);
    }

    protected Entity(Location location, int sizeScaleFactor, EntityList entityName) {
        this(new Pose(location), sizeScaleFactor, entityName);
    }

    protected Entity(Pose pose, EntityList entityName) {
        this(pose, 1, entityName);
    }

    public Pose getPose() {
        return pose;
    }

    public EntityList ge

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
