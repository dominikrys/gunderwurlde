package data.entity;

import data.Location;
import data.Pose;

public abstract class Entity {
    protected Pose pose;

    protected Entity(Pose pose) {
        this.pose = pose;
    }

    protected Entity(Location location) {
        this.pose = new Pose(location);
    }

    public Pose getPose() {
        return pose;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }
}
