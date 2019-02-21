package server.engine.state.entity.attack;

import shared.Location;

public class ProjectileAttack extends Attack {
    public ProjectileAttack(Location attackLocation, int attackSize, AttackType attackType) {
        super(attackLocation, attackSize, attackType);
    }
}
