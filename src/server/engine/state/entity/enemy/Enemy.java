package server.engine.state.entity.enemy;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import server.engine.ai.EnemyAI;
import server.engine.state.entity.Entity;
import server.engine.state.entity.HasHealth;
import server.engine.state.entity.HasID;
import server.engine.state.entity.IsMovable;
import server.engine.state.entity.attack.Attack;
import shared.lists.EntityList;

public abstract class Enemy extends Entity implements HasHealth, IsMovable, HasID {
    private static int nextID = 0;
    protected final LinkedHashSet<Drop> drops;
    protected int scoreOnKill;
    protected int health;
    protected int maxHealth;
    protected int moveSpeed;
    protected EntityList entityListName;
    protected EnemyAI ai;
    private int id;
    protected LinkedList<Attack> attacksToDo;

    Enemy(int maxHealth, int moveSpeed, EntityList entityListName, int size, LinkedHashSet<Drop> drops, int scoreOnKill, EnemyAI ai) {
        super(size, entityListName);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.moveSpeed = moveSpeed;
        this.drops = drops;
        this.entityListName = entityListName;
        this.id = nextID++;
        this.scoreOnKill = scoreOnKill;
        this.ai =ai;
        this.attacksToDo = new LinkedList<>();
    }
    
    public void addAttack(Attack attack) {
        attack.start();
        // TODO freeze enemy based on time to carryout
        this.attacksToDo.add(attack);
    }

    public LinkedList<Attack> getReadyAttacks() {
        LinkedList<Attack> readyAttacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        for (Iterator<Attack> i = attacksToDo.iterator(); i.hasNext();) {
            Attack a = i.next();
            if ((a.getTimeToCarryOut() - (now - a.getStartTime())) <= 0) {
                readyAttacks.add(a);
                i.remove();
            }
        }

        return readyAttacks;
    }

    public EnemyAI getAI() {
        return ai;
    }
    
    public void setAI(EnemyAI ai) {
        this.ai = ai;
    }

    public int getScoreOnKill() {
        return scoreOnKill;
    }

    public LinkedHashSet<Drop> getDrops() {
        return drops;
    }

    public EntityList getEntityListName() {
        return entityListName;
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

    public abstract Enemy makeCopy();

}
