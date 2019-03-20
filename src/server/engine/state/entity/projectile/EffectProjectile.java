package server.engine.state.entity.projectile;

import server.engine.state.effect.StatusEffect;
import shared.Pose;
import shared.lists.EntityList;
import shared.lists.Team;

public abstract class EffectProjectile extends Projectile {
    protected StatusEffect effect;

    EffectProjectile(int speed, int damage, EntityList entityListName, int size, int max_range, StatusEffect effect) {
        super(speed, damage, entityListName, size, max_range);
        this.effect = effect;
    }

    EffectProjectile(int speed, int damage, EntityList entityListName, int size, int max_range, Pose pose, Team team, StatusEffect effect) {
        super(speed, damage, entityListName, size, max_range, pose, team);
        this.effect = effect;
    }

    public StatusEffect getEffect() {
        return effect;
    }
}
