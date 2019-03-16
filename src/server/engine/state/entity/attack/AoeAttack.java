package server.engine.state.entity.attack;

import server.engine.state.entity.Entity;
import server.engine.state.physics.Force;
import server.engine.state.physics.Physics;
import shared.Location;
import shared.lists.EntityList;

//AOE - Area of Effect
public class AoeAttack extends Entity implements Attack {
    private static final int FORCE_PER_DAMAGE = 400;

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

    public Force getForce(Location location, Location backUp) {
        Location locationToUse = this.pose;
        double[] components = Physics.fromComponents(location.getX() - locationToUse.getX(), location.getY() - locationToUse.getY());
        double force = FORCE_PER_DAMAGE * (1 - (components[1] / this.size));
        if (components[1] == 0) {
            locationToUse = backUp;
        }
        components = Physics.fromComponents(location.getX() - locationToUse.getX(), location.getY() - locationToUse.getY());
        return new Force((int) components[0], force);
    }
}
