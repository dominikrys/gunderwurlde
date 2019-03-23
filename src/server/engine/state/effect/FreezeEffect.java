package server.engine.state.effect;

import server.engine.state.entity.LivingEntity;
import shared.lists.EntityStatus;

public class FreezeEffect extends StatusEffect {
    public static long DEFAULT_DURATION = 2000;

    protected double oldMovementForce;

    public FreezeEffect() {
        this(DEFAULT_DURATION);
    }

    public FreezeEffect(long duration) {
        super(duration, EntityStatus.FROZEN);
        this.oldMovementForce = -1;
    }

    @Override
    public LivingEntity applyEffect(LivingEntity e) {
        e = super.applyEffect(e);
        if (e.getStatus() == status) {
            if (oldMovementForce == -1)
                oldMovementForce = e.getMovementForce();
            e.setMovementForce(oldMovementForce * 0.9);
        }
        return e;
    }

    @Override
    public LivingEntity clearEffect(LivingEntity e) {
        e.setMovementForce(oldMovementForce);
        return e;
    }

}
