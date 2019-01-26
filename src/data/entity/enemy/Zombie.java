package data.entity.enemy;

import data.HasContactDamage;
import data.Pose;
import data.map.tile.Tile;

public class Zombie extends Enemy implements HasContactDamage {
    public static final int DEFAULT_HEALTH = 2;
    public static final int DEFAULT_MOVESPEED = 6;
    public static final int DEFAULT_SIZE = 1;

    protected int contactDamage = 1;

    public Zombie(int maxHealth, int moveSpeed, Pose pose, int size) {
        super(maxHealth, moveSpeed, pose, EnemyList.ZOMBIE, size);
        pathToGraphic = "file:assets/img/mobs/zombie.png";
    }

    public Zombie(Pose pose) {
        this(DEFAULT_HEALTH, DEFAULT_MOVESPEED, pose, DEFAULT_SIZE);
    }

    @Override
    public int getContactDamage() {
        return contactDamage;
    }

    @Override
    public void setContactDamage(int contactDamage) {
        this.contactDamage = contactDamage;
    }

}
