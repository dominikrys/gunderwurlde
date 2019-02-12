package shared.view.entity;

import java.io.Serializable;

import shared.Pose;
import shared.lists.EntityList;

public class EnemyView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    public EnemyView(Pose pose, int size, EntityList name) {
        super(pose, size, name);
    }

}
