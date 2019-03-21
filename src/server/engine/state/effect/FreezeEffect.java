package server.engine.state.effect;

import server.engine.state.entity.LivingEntity;
import shared.lists.EntityStatus;

public class FreezeEffect extends StatusEffect {
    public static long DEFAULT_DURATION = 2000;

    protected double oldAcceration;

    public FreezeEffect() {
        this(DEFAULT_DURATION);
    }

    public FreezeEffect(long duration) {
        super(duration, EntityStatus.FROZEN);
        this.oldAcceration = -1;
    }

    @Override
    public LivingEntity applyEffect(LivingEntity e) {
        e = super.applyEffect(e);
        if (e.getStatus() == status) {
            if (oldAcceration == -1)
                oldAcceration = e.getAcceleration();
            e.setAcceleration(oldAcceration * 0.9);
        }
        return e;
    }

    @Override
    public LivingEntity clearEffect(LivingEntity e) {
        e.setAcceleration(oldAcceration);
        return e;
    }

}
