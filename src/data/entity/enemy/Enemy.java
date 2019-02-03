package data.entity.enemy;

import data.HasHealth;
import data.IsMovable;
import data.entity.Entity;
import data.entity.HasID;
import data.entity.enemy.EnemyList;

import java.util.LinkedHashSet;

public abstract class Enemy extends Entity implements HasHealth, IsMovable, HasID {
    private static int nextID = 0;

    private int id;

    protected final LinkedHashSet<Drop> drops;

    protected int health;
    protected int maxHealth;
    protected int moveSpeed;
    protected EnemyList enemyName;

    Enemy(int maxHealth, int moveSpeed, EnemyList enemyName, int size, LinkedHashSet<Drop> drops) {
        super(size);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.moveSpeed = moveSpeed;
        this.drops = drops;
        this.enemyName = enemyName;
        this.id = nextID++;
    }

    Enemy(int maxHealth, int moveSpeed, EnemyList enemyName, int size) {
        this(maxHealth, moveSpeed, enemyName, size, new LinkedHashSet<Drop>());
    }

    public LinkedHashSet<Drop> getDrops() {
        return drops;
    }

    public EnemyList getEnemyName() {
        return enemyName;
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
        if (health < 0)
            health = 0;
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
        if (maxHealth < 0)
            maxHealth = 0;
        this.maxHealth = maxHealth;
    }

    @Override
    public int getID() {
        return id;
    }

}
