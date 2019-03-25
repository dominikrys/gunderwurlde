package server.engine.state;

import server.engine.state.effect.StatusEffect;

public interface HasEffect {
    public StatusEffect getEffect();
}
