package server.engine.ai.enemyAI;

import server.engine.state.entity.attack.Attack;
import server.engine.state.entity.attack.ProjectileAttack;
import server.engine.state.item.weapon.gun.Shotgun;
import server.engine.state.physics.Force;
import shared.Constants;
import shared.Pose;
import shared.lists.Team;

public class ShotgunMidgetAI extends ZombieAI{

    private final int KNOCKBACK_AMOUNT;
    private Shotgun shotgun = new Shotgun(3, 10, 1000);

    public ShotgunMidgetAI(int knockbackAmount){
        super(Constants.TILE_SIZE * 3);
        randomizePath = false;
        this.KNOCKBACK_AMOUNT = knockbackAmount;
        attackDelay = MEDIUM_DELAY;
    }

    public Force getForceFromAttack(double maxMovementForce) {
        if (!attacking) {
            int knockbackAngle = Pose.normaliseDirection(getAngle(pose, closestPlayer) + 180);
            return new Force(knockbackAngle, maxMovementForce + KNOCKBACK_AMOUNT);
        }else{
            return new Force(pose.getDirection(), 0);
        }
    }


    @Override
    protected Attack getAttackObj() {
        int attackAngle = getAngle(pose, closestPlayer);
        return new ProjectileAttack(shotgun.getShotProjectiles(new Pose(pose, attackAngle), Team.ENEMY));
    }
}
