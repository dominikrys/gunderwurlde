package client.data.entity;

import data.Pose;
import data.entity.EntityList;

import java.io.Serializable;

public class EnemyView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;

    public EnemyView(Pose pose, int size, EntityList name) {
        super(pose, size, name);
    }

}
