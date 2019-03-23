package server.engine.state.effect;

import server.engine.state.entity.LivingEntity;
import shared.lists.EntityStatus;

public class FireEffect extends StatusEffect {
    public static long DEFAULT_DURATION = 4000;
    public static int DEFAULT_DAMAGE = 1;
    public static int DEAFULT_TIME_BETWEEN_DAMAGE = 1000;

    protected int damage;
    protected int timeBetweenDamage;
    protected long lastDamageTime;

    public FireEffect() {
        this(DEFAULT_DURATION, DEFAULT_DAMAGE, DEAFULT_TIME_BETWEEN_DAMAGE);
    }

    public FireEffect(long duration, int damage, int timeBetweenDamage) {
        super(duration, EntityStatus.BURNING);
        this.damage = damage;
        this.timeBetweenDamage = timeBetweenDamage;
        this.lastDamageTime = startTime;
    }

    @Override
    public LivingEntity applyEffect(LivingEntity e) {
        e = super.applyEffect(e);
        long now = System.currentTimeMillis();
        if (lastDamageTime + timeBetweenDamage < now) {
            lastDamageTime = now;
            e.damage(damage);
        }
        return e;
    }

    @Override
    public LivingEntity clearEffect(LivingEntity e) {
        return e;
    }

}
