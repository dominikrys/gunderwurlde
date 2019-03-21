package server.engine.state.entity;

import server.engine.state.effect.StatusEffect;
import server.engine.state.physics.Force;
import server.engine.state.physics.HasPhysics;
import server.engine.state.physics.Velocity;
import shared.lists.ActionList;
import shared.lists.EntityList;
import shared.lists.EntityStatus;

public abstract class LivingEntity extends Entity implements HasPhysics, HasHealth, IsMovable, HasID {
    private static int nextID = 0;

    protected final int id;
    protected EntityList entityListName;
    protected ActionList currentAction;
    protected int health;
    protected int maxHealth;
    protected double acceleration;
    protected boolean takenDamage;
    protected boolean moving;
    protected Velocity velocity;
    protected Force resultantForce;
    protected double mass;
    protected StatusEffect effect;

    protected LivingEntity(int maxHealth, double acceleration, EntityList entityListName, int size, double mass) {
        super(size, entityListName);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.acceleration = acceleration;
        this.entityListName = entityListName;
        this.id = nextID++;
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

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
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

    public void addEffect(StatusEffect effect) {
        this.effect = effect;
    }

    public boolean hasEffect() {
        return (effect != null);
    }

    public StatusEffect getEffect() {
        return effect;
    }

    public void clearStatusEffect() {
        this.effect = null;
        this.status = EntityStatus.NONE;
    }

}
