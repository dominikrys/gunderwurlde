package server.engine.state.entity.attack;

import server.engine.state.entity.Entity;
import shared.Location;
import shared.lists.EntityList;

public class Attack extends Entity {
    protected AttackType attackType;

    public Attack(Location attackLocation, int attackSize, AttackType attackType) {
        super(attackLocation, attackSize, EntityList.DEFAULT);
        this.attackType = attackType;
    }

    public AttackType getAttackType() {
        return attackType;
    }

}
