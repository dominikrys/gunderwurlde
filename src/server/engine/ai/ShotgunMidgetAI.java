package server.engine.ai;

import server.engine.state.entity.attack.AoeAttack;
import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Shotgun;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.Pose;
import shared.lists.ActionList;
import shared.lists.Teams;

import java.util.LinkedList;

public class ShotgunMidgetAI extends ZombieAI{

    private final int KNOCKBACK_AMOUT;
    private Shotgun shotgun = new Shotgun(3, 10, 1000);

    public ShotgunMidgetAI(int knockbackAmount){
        super();
        randomizePath = false;
        this.KNOCKBACK_AMOUT = knockbackAmount;
        attackDelay = MEDIUM_DELAY;
    }

    @Override
    public LinkedList<Attack> getAttacks() {
        LinkedList<Attack> attacks = new LinkedList<>();
        long now = System.currentTimeMillis();

        if ((now - beginAttackTime) >= attackDelay) {
            int attackAngle = (int) getAngle(pose, closestPlayer);
            attacks.add(new ProjectileAttack(shotgun.getShotProjectiles(new Pose(pose, attackAngle), Teams.ENEMY)));
            attacking = false;
            this.actionState = ActionList.NONE;
        }
        return attacks;
    }

    @Override
    public Force getForceFromAttack(double maxMovementForce) {
        if (!attacking) {
            int knockbackAngle = Pose.normaliseDirection((int) getAngle(pose, closestPlayer) + 180);
            return new Force(knockbackAngle, maxMovementForce + KNOCKBACK_AMOUT);
        }else{
            return new Force(0, 0);
        }
    }
}
