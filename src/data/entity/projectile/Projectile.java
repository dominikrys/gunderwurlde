package data.entity.projectile;

import data.Pose;
import data.entity.Entity;

public abstract class Projectile extends Entity {
    protected int speed;
    protected int damage;
    protected ProjectileList projectileType;
    protected int range;

    Projectile(int speed, int damage, ProjectileList projectileType, Pose pose, int size, int range) {
        super(pose, size);
        this.speed = speed;
        this.damage = damage;
        this.projectileType = projectileType;
        this.range = range;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        if (range < 0) range = 0; //0 is considered infinite
        this.range = range;
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
