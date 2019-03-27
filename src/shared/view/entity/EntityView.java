package shared.view.entity;

import java.io.Serializable;

import shared.Pose;
import shared.lists.EntityList;
import shared.lists.EntityStatus;

public abstract class EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final Pose pose;
    protected final EntityList entityListName;
    protected final EntityStatus status;
    protected final int sizeScaleFactor;
    protected final boolean cloaked;

    protected EntityView(Pose pose, int size, EntityList entityListName, Boolean cloaked, EntityStatus status) {
        int radius = size;
        this.pose = new Pose(pose.getX() - radius, pose.getY() - radius, pose.getDirection());
        this.sizeScaleFactor = 1; // TODO have size scaling support for entities
        this.entityListName = entityListName;
        this.cloaked = cloaked;
        this.status = status;
    }

    public EntityStatus getStatus() {
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
