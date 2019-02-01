package client.data;

import data.Pose;
import data.entity.projectile.ProjectileList;

public class ProjectileView extends EntityView {
    protected ProjectileList name;

    protected ProjectileView(Pose pose, int size, ProjectileList name) {
        super(pose, size);
        this.name = name;

        switch (name) {
        case SMALLBULLET:
            this.pathToGraphic = "file:assets/img/projectiles/bullet.png";
            break;
        }
    }

    public ProjectileList getName() {
        return name;
    }

}
