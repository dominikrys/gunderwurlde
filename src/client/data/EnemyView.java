package client.data;

import java.io.Serializable;

import data.Pose;
import data.entity.enemy.EnemyList;

public class EnemyView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected EnemyList name;

    public EnemyView(Pose pose, int size, EnemyList name) {
        super(pose, size);
        this.name = name;

        switch (name) {
        case ZOMBIE:
            this.pathToGraphic = "file:assets/img/mobs/zombie.png";
            break;
        }
    }

    public EnemyList getName() {
        return name;
    }

}
