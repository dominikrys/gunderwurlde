package server.engine.state.entity.attack;

import server.engine.state.entity.Entity;
import shared.Location;
import shared.lists.EntityList;

public class Attack extends Entity {
    protected AttackType attackType;
    protected long timeToCarryOut;
    protected long startTime;

    public Attack(Location attackLocation, int attackSize, AttackType attackType, int delay) {
        super(attackLocation, attackSize, EntityList.DEFAULT);
        this.attackType = attackType;
        this.timeToCarryOut = delay;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getTimeToCarryOut() {
        return timeToCarryOut;
    }

}
