package shared.view.entity;

import java.io.Serializable;

import shared.Pose;
import shared.lists.EntityList;

public abstract class EntityView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected Pose pose;
    protected int sizeScaleFactor;
    protected EntityList entityListName;

    protected EntityView(Pose pose, int size, EntityList entityListName) {
        int radius = size / 2;
        this.pose = new Pose(pose.getX() - radius, pose.getY() - radius, pose.getDirection());
        this.sizeScaleFactor = 1; // TODO sort out scaling
        this.entityListName = entityListName;
    }

    public Pose getPose() {
        return pose;
    }

    public int getSizeScaleFactor() {
        return sizeScaleFactor;
    }

    public EntityList getEntityListName() {
        return entityListName;
    }

}
