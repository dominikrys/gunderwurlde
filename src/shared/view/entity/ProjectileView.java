package shared.view.entity;

import java.io.Serializable;

import shared.Pose;
import shared.lists.EntityList;

public class ProjectileView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    public ProjectileView(Pose pose, int sizeScaleFactor, EntityList name) {
        super(pose, sizeScaleFactor, name);

    }
}
