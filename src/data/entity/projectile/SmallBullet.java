package data.entity.projectile;

import data.Pose;

public class SmallBullet extends Projectile {
    public static final int DEFAULT_SPEED = 60;
    public static final int DEFAULT_DAMAGE = 1;
    public static final int DEFAULT_SIZE = 1;

    public SmallBullet(Pose pose) {
        this(DEFAULT_SPEED, DEFAULT_DAMAGE, pose, DEFAULT_SIZE);
    }

    public SmallBullet(int speed, int damage, Pose pose, int size) {
        super(speed, damage, ProjectileList.SMALLBULLET, pose, size);
    }

}
