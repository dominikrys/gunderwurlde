package server.engine.state.effect;

import server.engine.state.entity.LivingEntity;
import shared.lists.EntityStatus;

/**
 * Status effect class sued to process status effects for the applied
 * LivingEntity
 * 
 * @author Richard
 *
 */
public abstract class StatusEffect {
    protected long duration;
    protected long startTime;
    protected EntityStatus status;

    protected StatusEffect(long duration, EntityStatus status) {
        this.duration = duration;
        this.status = status;
        this.startTime = System.currentTimeMillis();
    }

    public LivingEntity applyEffect(LivingEntity e) {
        if (startTime + duration < System.currentTimeMillis()) {
            e = clearEffect(e);
            e.clearStatusEffect();
        } else {
            e.setStatus(status);
        }
        return e;
    }

    public abstract LivingEntity clearEffect(LivingEntity e);

}
