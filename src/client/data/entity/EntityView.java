package client.data.entity;

import java.io.Serializable;

import data.Pose;
import data.entity.EntityList;

public abstract class EntityView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Pose pose;
    protected int sizeScaleFactor;
    protected EntityList entityName;

    protected EntityView(Pose pose, int sizeScaleFactor, EntityList entityName) {
        int radius = sizeScaleFactor / 2;
        this.pose = new Pose(pose.getX() - radius, pose.getY() - radius, pose.getDirection());
        this.sizeScaleFactor = 1; // TODO sort out scaling
        this.entityName = entityName;
    }

    public Pose getPose() {
        return pose;
    }

    public int getSizeScaleFactor() {
        return sizeScaleFactor;
    }

    public EntityList getEntityName() {
        return entityName;
    }

}
