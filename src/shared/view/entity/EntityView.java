package shared.view.entity;

import java.io.Serializable;

import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Status;

public abstract class EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Pose pose;
    protected EntityList entityListName;
    protected Status status;
    protected int sizeScaleFactor;
    protected boolean cloaked;

    protected EntityView(Pose pose, int size, EntityList entityListName, Boolean cloaked, Status status) {
        int radius = size;
        this.pose = new Pose(pose.getX() - radius, pose.getY() - radius, pose.getDirection());
        this.sizeScaleFactor = 1; // TODO sort out scaling
        this.entityListName = entityListName;
        this.cloaked = cloaked;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isCloaked() {
        return cloaked;
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
