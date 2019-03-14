package shared.view.entity;

import shared.Pose;
import shared.lists.EntityList;
import shared.lists.EntityStatus;

import java.io.Serializable;

public class ProjectileView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    public ProjectileView(Pose pose, int size, EntityList name, boolean cloaked, EntityStatus status) {
        super(pose, size, name, cloaked, status);

    }
}
