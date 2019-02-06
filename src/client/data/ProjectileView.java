package client.data;

import java.io.Serializable;

import data.Pose;
import data.entity.projectile.ProjectileList;

public class ProjectileView extends EntityView implements Serializable {
    private static final long serialVersionUID = 1L;
    protected ProjectileList name;

    public ProjectileView(Pose pose, int size, ProjectileList name) {
        super(pose, size);
        this.name = name;

        switch (name) {
        case SMALLBULLET:
            break;
        }
    }

    public ProjectileList getName() {
        return name;
    }

}
