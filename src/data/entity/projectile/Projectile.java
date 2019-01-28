package data.entity.projectile;

import data.Pose;
import data.entity.Entity;

public abstract class Projectile extends Entity {
    protected int speed;
    protected int damage;
    protected ProjectileList projectileType;

    Projectile(int speed, int damage, ProjectileList projectileType, Pose pose, int size) {
        super(pose, size);
        this.speed = speed;
        this.damage = damage;
        this.projectileType = projectileType;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public ProjectileList getProjectileType() {
        return projectileType;
    }

    public void setProjectileType(ProjectileList projectileType) {
        this.projectileType = projectileType;
    }

}
