package server.engine.state.effect;

import server.engine.state.entity.LivingEntity;
import shared.lists.EntityStatus;

/**
 * 
 * @author Richard
 *
 */
public class FreezeEffect extends StatusEffect {
    public static long DEFAULT_DURATION = 3000;

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
                oldMovementForce = e.getMovementForceAddition();
            e.setMovementForceAddition(oldMovementForce * 0.99);
        }
        return e;
    }

    @Override
    public LivingEntity clearEffect(LivingEntity e) {
        e.setMovementForceAddition(oldMovementForce);
        return e;
    }

}
