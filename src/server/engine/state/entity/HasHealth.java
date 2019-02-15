package server.engine.state.entity;

public interface HasHealth {
    public abstract int getHealth();

    public abstract void setHealth(int health);

    public abstract boolean damage(int amount);

    public abstract int getMaxHealth();

    public abstract void setMaxHealth(int maxHealth);

    public abstract boolean hasTakenDamage();

    public abstract void setTakenDamage(boolean takenDamage);
}
