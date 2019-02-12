package server.engine.state.entity.attack;

import shared.Location;

public class AoeAttack extends Attack {
    protected int damage;

    public AoeAttack(Location attackLocation, int attackSize, AttackType attackType, int delay, int damage) {
        super(attackLocation, attackSize, attackType, delay);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}
