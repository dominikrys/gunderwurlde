package server.engine.state.entity.attack;

import shared.Pose;

//AOE - Area of Effect
public class AoeAttack extends Attack {
    protected int damage;

    public AoeAttack(Pose player, int attackSize, AttackType attackType, int damage) {
        super(player, attackSize, attackType);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}
