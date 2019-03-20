package server.engine.state.effect;

import server.engine.state.entity.LivingEntity;

public abstract class StatusEffect {
    protected long duration;
    protected long startTime;

    protected StatusEffect(long duration) {
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
    }

    public LivingEntity applyEffect(LivingEntity e) {
        if (this.startTime + duration < System.currentTimeMillis()) {
            e.clearStatusEffect();
        }
        return e;
    }

}
