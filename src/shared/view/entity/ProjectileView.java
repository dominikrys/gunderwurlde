package shared.view.entity;

import java.io.Serializable;

import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Status;

public class ProjectileView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    public ProjectileView(Pose pose, int size, EntityList name, boolean cloaked, Status status) {
        super(pose, size, name, cloaked, status);

    }
}
