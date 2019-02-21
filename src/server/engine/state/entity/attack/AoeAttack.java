package server.engine.state.entity.attack;

import shared.Location;

//AOE - Area of Effect
public class AoeAttack extends Attack {
    protected int damage;

    public AoeAttack(Location attackLocation, int attackSize, int damage) {
        super(attackLocation, attackSize, AttackType.AOE);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}
