package data.entity.enemy;

import data.HasHealth;
import data.IsMovable;
import data.Pose;
import data.entity.Entity;
import data.entity.item.Item;

import java.util.LinkedHashMap;

public abstract class Enemy extends Entity implements HasHealth, IsMovable {
    protected int health;
    protected int maxHealth;
    protected int moveSpeed;
    protected LinkedHashMap<Item, Double> drops;

    Enemy(int maxHealth, int moveSpeed, Pose pose, LinkedHashMap<Item, Double> drops) {
        super(pose);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.moveSpeed = moveSpeed;
        this.drops = drops;
    }

    Enemy(int maxHealth, int moveSpeed, Pose pose) {
        this(maxHealth, moveSpeed, pose, new LinkedHashMap<Item, Double>());
    }

    public LinkedHashMap<Item, Double> getDrops() {
        return drops;
    }

    @Override
    public int getMoveSpeed() {
        return moveSpeed;
    }

    @Override
    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        if (health < 0) health = 0;
        this.health = health;
    }

    @Override
    public boolean damage(int amount) {
        if (amount >= health) {
            health = 0;
            return true;
        } else {
            health -= amount;
            return false;
        }
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        if (maxHealth < 0) maxHealth = 0;
        this.maxHealth = maxHealth;
    }

}
