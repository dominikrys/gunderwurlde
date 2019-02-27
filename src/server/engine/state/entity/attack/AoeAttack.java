package server.engine.state.entity.attack;

import server.engine.state.entity.Entity;
import shared.Location;
import shared.lists.EntityList;

//AOE - Area of Effect
public class AoeAttack extends Entity implements Attack {
    protected int damage;

    public AoeAttack(Location attackLocation, int attackSize, int damage) {
        super(attackLocation, attackSize, EntityList.DEFAULT);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public AttackType getAttackType() {
        return AttackType.AOE;
    }

    @Override
    public Entity makeCopy() {
        return new AoeAttack(pose, size, damage);
    }
}
