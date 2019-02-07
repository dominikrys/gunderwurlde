package client.data.entity;

import data.Pose;
import data.entity.EntityList;

import java.io.Serializable;

public class ProjectileView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    public ProjectileView(Pose pose, int sizeScaleFactor, EntityList name) {
        super(pose, sizeScaleFactor, name);

    }
}
