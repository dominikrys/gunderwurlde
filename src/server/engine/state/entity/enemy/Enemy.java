package server.engine.state.entity.enemy;

import java.util.LinkedHashSet;

import server.engine.ai.EnemyAI;
import server.engine.state.entity.Entity;
import server.engine.state.entity.HasHealth;
import server.engine.state.entity.HasID;
import server.engine.state.entity.IsMovable;
import server.engine.state.physics.Force;
import server.engine.state.physics.HasPhysics;
import server.engine.state.physics.Velocity;
import shared.lists.ActionList;
import shared.lists.EntityList;

public abstract class Enemy extends Entity implements HasPhysics, HasHealth, IsMovable, HasID {
    private static int nextID = 0;

    protected final LinkedHashSet<Drop> drops;
    protected final int id;

    protected EntityList entityListName;
    protected ActionList currentAction;
    protected EnemyAI ai;
    protected int scoreOnKill;
    protected int health;
    protected int maxHealth;
    protected int moveSpeed;
    protected boolean takenDamage;
    protected boolean moving;
    protected Velocity velocity;
    protected Force resultantForce;
    protected double mass;

    Enemy(int maxHealth, int moveSpeed, EntityList entityListName, int size, LinkedHashSet<Drop> drops, int scoreOnKill, EnemyAI ai, double mass) {
        super(size, entityListName);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.moveSpeed = moveSpeed;
        this.drops = drops;
        this.entityListName = entityListName;
        this.id = nextID++;
        this.scoreOnKill = scoreOnKill;
        this.ai =ai;
        this.takenDamage = false;
        this.moving = false;
        this.currentAction = ActionList.NONE;
        this.velocity = new Velocity();
        this.resultantForce = new Force();
        this.mass = mass;
    }

    public ActionList getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(ActionList currentAction) {
        this.currentAction = currentAction;
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
    public boolean hasTakenDamage() {
        return takenDamage;
    }

    @Override
    public void setTakenDamage(boolean takenDamage) {
        this.takenDamage = takenDamage;
    }

    @Override
    public boolean isMoving() {
        return moving;
    }

    @Override
    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

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

    @Override
    public Velocity getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Velocity v) {
        this.velocity = v;
        this.resultantForce = new Force();
    }

    @Override
    public Force getResultantForce() {
        return resultantForce;
    }

    @Override
    public void addNewForce(Force f) {
        this.resultantForce.add(f);
    }

    @Override
    public double getMass() {
        return mass;
    }

}
